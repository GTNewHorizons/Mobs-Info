# MobsInfo 1.7.10 FORK

### Please open any mod-related PRs [upstream](https://github.com/kuba6000/Mobs-Info).
### If you want to change something GTNH-specific please try to do it in the configs or [KubaTech](https://github.com/GTNewHorizons/KubaTech/blob/master/src/main/java/kubatech/loaders/MobHandlerLoader.java).

## About

This mod is an addon to NEI that adds a new tab with all of the information about the mobs.

These informations includes:
- How does it look (it is rendered and always looks at your cursor)
- Mob name
- Mob registry name (shown only with F3+H and shift)
- Which mod added the mob
- Max health
- Can it be spawned as infernal (only with Infernal-Mobs mod)
- Is it a boss
- Is it allowed on peaceful
- Can it be captured with a vial (only with EnderIO)
- Mob loot:
  - Normal drops
  - Rare drops
  - Additional drops (armor, equipment)

<details>
  <summary>Screenshots in game</summary>
  
  ![1](https://github.com/kuba6000/Mobs-Info/assets/53441451/771b0adc-8b4d-450c-b7c5-2c2bd6b0b3fd)
  
</details>

## Override Config
Sometimes there are cases where it is impossible to generate loot that the mob gives.
In this cases you can manually edit the information on the page with the override config.

You can find it in .minecraft\config\MobsInfo\MobOverrides.json

Example file should generate at first start


## License

MobsInfo - Minecraft addon
Copyright (C) 2023  kuba6000

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this library. If not, see <https://www.gnu.org/licenses/>.
