#!/usr/bin/env python3
"""Generate Markdown entries for upstream pull requests between two Git tags."""

from __future__ import annotations

import argparse
import json
import os
import subprocess
import sys
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Find upstream pull requests associated with commits between two tags."
    )
    parser.add_argument("--upstream", required=True, metavar="OWNER/REPO")
    parser.add_argument("--previous-tag", required=True)
    parser.add_argument("--current-tag", required=True)
    parser.add_argument("--api-url", default="https://api.github.com")
    parser.add_argument(
        "--token",
        default=os.environ.get("GH_TOKEN") or os.environ.get("GITHUB_TOKEN"),
    )
    return parser.parse_args()


def commits_between(previous_tag: str, current_tag: str) -> list[str]:
    result = subprocess.run(
        ["git", "rev-list", "--reverse", f"{previous_tag}..{current_tag}"],
        check=True,
        capture_output=True,
        text=True,
    )
    return result.stdout.splitlines()


def has_complete_history(previous_tag: str, current_tag: str) -> bool:
    result = subprocess.run(
        ["git", "merge-base", "--is-ancestor", previous_tag, current_tag],
        capture_output=True,
        text=True,
    )
    return result.returncode == 0


def associated_pull_requests(
    api_url: str, upstream: str, commit: str, token: str | None
) -> list[dict[str, object]]:
    url = f"{api_url.rstrip('/')}/repos/{upstream}/commits/{commit}/pulls"
    headers = {
        "Accept": "application/vnd.github+json",
        "X-GitHub-Api-Version": "2022-11-28",
        "User-Agent": "upstream-release-notes",
    }
    if token:
        headers["Authorization"] = f"Bearer {token}"

    request = Request(url, headers=headers)
    try:
        with urlopen(request, timeout=30) as response:
            payload = json.load(response)
    except HTTPError as error:
        if error.code in (404, 422):
            return []
        raise
    if not isinstance(payload, list):
        raise ValueError(f"GitHub returned an unexpected response for commit {commit}")
    return payload


def render_notes(pull_requests: list[dict[str, object]]) -> str:
    if not pull_requests:
        return ""

    lines: list[str] = []
    for pull_request in pull_requests:
        user = pull_request.get("user")
        login = user.get("login") if isinstance(user, dict) else None
        author = f" by @{login}" if login else ""
        lines.append(
            f"* {pull_request['title']}{author} in {pull_request['html_url']}"
        )
    return "\n".join(lines) + "\n"


def main() -> int:
    args = parse_args()
    if not has_complete_history(args.previous_tag, args.current_tag):
        print(
            "Skipping upstream release notes because the commit range is not "
            "available in the shallow checkout.",
            file=sys.stderr,
        )
        return 0

    discovered: list[dict[str, object]] = []
    seen_urls: set[str] = set()

    try:
        for commit in commits_between(args.previous_tag, args.current_tag):
            for pull_request in associated_pull_requests(
                args.api_url, args.upstream, commit, args.token
            ):
                base = pull_request.get("base")
                base_repo = base.get("repo") if isinstance(base, dict) else None
                full_name = (
                    base_repo.get("full_name") if isinstance(base_repo, dict) else None
                )
                url = pull_request.get("html_url")
                if (
                    pull_request.get("merged_at")
                    and isinstance(full_name, str)
                    and full_name.casefold() == args.upstream.casefold()
                    and isinstance(url, str)
                    and url not in seen_urls
                ):
                    seen_urls.add(url)
                    discovered.append(pull_request)
    except (subprocess.CalledProcessError, HTTPError, URLError, ValueError) as error:
        print(f"Failed to generate upstream release notes: {error}", file=sys.stderr)
        return 1

    sys.stdout.write(render_notes(discovered))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
