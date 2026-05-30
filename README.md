# Lootin Remastered

Per player loot, perfect for survival servers! Lootin Remastered is a Spigot/Paper plugin that ensures container loot is unique for every player on the server. 
When a player opens a naturally generated chest, barrel, or minecart, they receive their own personal loot drop, preventing structures from being cleared out by the first explorer.

This project is a revival fork of the original [Lootin-plugin by sachingorkar102](https://github.com/sachingorkar102/Lootin-plugin) with additional code by PrometheuzzZ and
Lauriichan.


## Features
* **Per-Player Loot Distribution:** Every naturally generated chest, barrel, or storage minecart generates independent contents per player, mimicking a single-player progression experience.
* **Automated Loot Refills:** Supports configurable loot replenishment routines per world (managed via `worlds.yml`). When enabled, containers automatically restock their loot pools after a designated cooldown window. Requires the `lootin.autoreplenish` permission.
* **Vanilla Structural Integration:** Directly interprets Minecraft's native loot table formats. Specific structures can be exempted via the `blacklist-structures` blocklist array in the plugin configuration.
* **Anti-Griefing & Explosion Safeguards:** Special loot containers are protected against accidental breaking or structural damage. Regular extraction requires players to sneak while breaking the block. Complete block-breaking bypass requires the `lootin.breakchest.bypass` permission. Lootin containers are also protected against TNT and Creeper detonations (toggled via `config.yml`).
* **Sculk Sensor Interactions:** Interacting with a Lootin container fires typical spatial vibrations matching vanilla mechanics.
* **Per-Player Elytras:** Elytra frames inside End City ships generate uniquely per player. Has added compatibility for the [Elytra Vaults plugin](https://modrinth.com/datapack/elytra-vaults-atlasplays), letting that handle elytras logic instead.
* **Third-Party Ecosystem Hooks:** Features built-in compatibility for *WorldGuard* (using the `lootin-container-access` flag), *PlaceholderAPI*, and custom world generators including *Terra*, *CustomStructures*, *OhTheDungeonsYou'llGo*, *BetterStructures*, and *TerraFormGenerator*.

## Credits & License
* **Original Author:** sachingorkar102
* **Awesome developers:** [PrometheuzzZ](https://github.com/PrometheuzzZ), [Lauriichan](https://github.com/Lauriichan)

This project is open-source and licensed under the **GNU General Public License v3.0 (GPL-3.0)**. You are free to modify and distribute this software under the terms of the same license.