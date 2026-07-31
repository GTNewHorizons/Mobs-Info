#!/usr/bin/env python3
"""Merge extra pull request entries into GitHub-generated release notes."""

from __future__ import annotations

import argparse
from pathlib import Path


WHATS_CHANGED_HEADING = "## What's Changed"
FULL_CHANGELOG_MARKER = "**Full Changelog**"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Insert additional entries into the What's Changed section."
    )
    parser.add_argument("native_notes", type=Path)
    parser.add_argument("additional_entries", type=Path)
    return parser.parse_args()


def merge_notes(native_notes: str, additional_entries: str) -> str:
    entries = additional_entries.strip()
    if not entries:
        return native_notes

    heading_start = native_notes.find(WHATS_CHANGED_HEADING)
    if heading_start == -1:
        full_changelog_start = native_notes.find(FULL_CHANGELOG_MARKER)
        insertion_point = (
            len(native_notes) if full_changelog_start == -1 else full_changelog_start
        )
        before = native_notes[:insertion_point].rstrip()
        after = native_notes[insertion_point:].lstrip("\n")
        prefix = f"{before}\n\n" if before else ""
        suffix = f"\n\n{after}" if after else "\n"
        return f"{prefix}{WHATS_CHANGED_HEADING}\n{entries}{suffix}"

    section_boundaries = [len(native_notes)]
    next_heading = native_notes.find("\n## ", heading_start + len(WHATS_CHANGED_HEADING))
    if next_heading != -1:
        section_boundaries.append(next_heading)
    full_changelog_start = native_notes.find(
        FULL_CHANGELOG_MARKER, heading_start + len(WHATS_CHANGED_HEADING)
    )
    if full_changelog_start != -1:
        section_boundaries.append(full_changelog_start)
    section_end = min(section_boundaries)

    before = native_notes[:section_end].rstrip()
    after = native_notes[section_end:].lstrip("\n")
    separator = "\n\n" if after else "\n"
    return f"{before}\n{entries}{separator}{after}"


def main() -> int:
    args = parse_args()
    native_notes = args.native_notes.read_text(encoding="utf-8")
    additional_entries = args.additional_entries.read_text(encoding="utf-8")
    print(merge_notes(native_notes, additional_entries), end="")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
