# Lootin Remastered

![Replace this with a description](https://cdn.modrinth.com/data/cached_images/a777d78fa432f6fd5d28750e04aba6e70e86b525_0.webp)

This plugin was originally created by sachingorkar102 for 1.16.5, who has permitted its posting provided that the source code remains available. This project is a revival fork of the original Lootin plugin, maintained as a continuation of the original work to provide modern version support for newer APIs, and is distributed under the [original project's license](https://github.com/sachingorkar102/Lootin-plugin/blob/main/LICENSE). 

## Description 

Per player loot, perfect for survival servers! Lootin Remastered is a Spigot/Paper plugin that ensures container loot is unique for every player on the server, similar to [Lootr](https://modrinth.com/mod/lootr) and [JustLootit](https://modrinth.com/plugin/justlootit). 
When a player opens a naturally generated chest, barrel, or minecart, they receive their own personal loot drop, preventing structures from being cleared out by the first explorer.

## Features
* **Per-Player Loot:** Every naturally generated chest, barrel, or storage minecart generates independent contents per player, so no smp will ever have the problem of already-looted dungeons ever again
* **Still Feels Vanilla:** Players will open and close chests with the same sounds and animations, just like in vanilla Minecraft
* **Automated Loot Refills:** Supports configurable loot replenishment routines per world (managed via `worlds.yml`). When enabled, containers automatically restock their loot pools after a designated cooldown window. Requires the `lootin.autoreplenish` permission.
* **Vanilla Structural Integration:** Directly interprets Minecraft's native loot table formats. Specific structures can be exempted via the `blacklist-structures` blocklist array in the plugin configuration.
* **Anti-Griefing & Explosion Safeguards:** Special loot containers are protected against accidental breaking or structural damage. Regular extraction requires players to sneak while breaking the block. Complete block-breaking bypass requires the `lootin.breakchest.bypass` permission. Lootin containers are also protected against TNT and Creeper detonations (toggled via `config.yml`).
* **Sculk Sensor Interactions:** Interacting with a Lootin container fires typical spatial vibrations matching vanilla mechanics.
* **Per-Player Elytras:** Elytra frames inside End City ships generate uniquely per player. Has added compatibility for the [Elytra Vaults plugin](https://modrinth.com/datapack/elytra-vaults-atlasplays), letting that handle elytras logic instead. NOTE - Per player elytras are buggy on newer versions due to NMS support being gradually phased out
* **Third-Party Ecosystem Hooks:** Features built-in compatibility for *WorldGuard* (using the `lootin-container-access` flag), *PlaceholderAPI*, and custom world generators including *Terra*, *OhTheDungeonsYou'llGo*, *BetterStructures*, and *TerraFormGenerator*.
** Note that *CustomStructures* support has been dropped
* Support for JustLootIt, letting you easily switch to that plugin for your loot containers!

## Upcoming Features
* Please suggest them on the github page!!
* Support for converting JustLootIt containers back, if servers want to switch (they have a useful utility to convert Lootin containers to JustLootIt form)

## Design Choice

This plugin, instead of listening for loot chest and container generation on world gen, instead listens for when players open a loot chest for the first time, thus converting it into a plugin managed loot table with none the wiser! This implementation allows for mapmakers to modify existing chunks with new loot table chests, and supports new structures being pasted in existing chunks via commands, structure blocks, or [WorldEdit](https://modrinth.com/plugin/worldedit/versions).

## Why was this made?

As of late 2025, the original developer announced they could no longer keep the plugin updated for newer Minecraft versions past 1.21.7, releasing a [Lootin Uninstaller](https://www.spigotmc.org/resources/lootin-1-16-1-21-7-no-more-already-looted-chests.90453/update?update=614717) to safely revert containers. 

## ❤️ Credits
* **Original Author:** [sachingorkar102](https://github.com/sachingorkar102)
* **Contributors:** [DerexXD](https://github.com/DereC4), [PrometheuzzZ](https://github.com/PrometheuzzZ), [Lauriichan](https://github.com/Lauriichan)
* **Photo Credits**: [Enginee404](https://www.curseforge.com/members/enginee404/projects)

This project is open-source and licensed under the **GNU General Public License v3.0 (GPL-3.0)**. You are free to modify and distribute this software under the terms of the same license.
