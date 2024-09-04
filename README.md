<div align=center>
  
# Mobs Info 1.7.10 GTNH FORK
  
### Please open any mod-related PRs [upstream](https://github.com/kuba6000/Mobs-Info).
### If you want to change something GTNH-specific please try to do it in the configs or [KubaTech](https://github.com/GTNewHorizons/GT5-Unofficial/blob/master/src/main/java/kubatech/loaders/MobHandlerLoader.java).

</div>

## About

This mod is an addon to NEI that adds a new tab with all of the information about the mobs. Now also includes villager trades!

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

![1](https://github.com/kuba6000/Mobs-Info/assets/53441451/771b0adc-8b4d-450c-b7c5-2c2bd6b0b3fd)

## Villager trades

Find out which villager sells the stuff that you need! Also supports custom villagers (like forestry ones)
![image](https://github.com/user-attachments/assets/bdd10784-04f0-46bd-998b-85d777596e54)


## Hidden mode

You can turn hidden mode on in the configuration file located in `/config/mobsinfo/mobsinfo.cfg`.
Hidden mode will make all mobs hidden in NEI until you kill them.
<details>
  <summary>Screenshots in game</summary>

  ![image](https://github.com/kuba6000/Mobs-Info/assets/53441451/165b8b65-4fda-4084-8e28-ea86c3c086e4)

</details>

## Compatibility

This mod is compatible with many mods including:
- Edit Mob Drops
- MineTweaker
- Ender IO
- Infernal Mobs
- And many others...

<details>
  <summary>Screenshots in game</summary>

  ![image](https://github.com/kuba6000/Mobs-Info/assets/53441451/8f7e3b5c-2085-4512-b753-1894a28bf986)
  ![image](https://github.com/kuba6000/Mobs-Info/assets/53441451/765a72c2-6e73-4621-99e9-bc10b50f22c2)
  ![image](https://github.com/kuba6000/Mobs-Info/assets/53441451/21ea707f-5d5b-4180-bfb6-7f825c4cb563)

</details>

If you find any mod that adds its own drops to the game and they don't show in NEI then feel free to open an issue!

## Configuration

There are two configuration files `mobsinfo.cfg` and `MobOverrides.json` in `/config/mobsinfo/` directory that should generate at first launch.
There will also be third file called `MobRecipeLoader.cache` but it is to make your game start faster next time, so dont edit it!
You can turn various features on and off in there including:
- Hidden mode
- Infernal drops NEI page
- Show all EnderIO spawners in NEI!
- Mob blacklist

## Override Config
Sometimes there are cases where it is impossible to generate loot that the mob gives.
In this cases you can manually edit the information on the page with the override config.

You can find it in .minecraft\config\MobsInfo\MobOverrides.json

Example file should generate at first start

## License

MobsInfo - Minecraft addon
Copyright (C) 2023-2024  kuba6000

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
