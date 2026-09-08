package com.github.sachin.lootin.listeners;

import com.github.sachin.lootin.utils.LConstants;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataType;

public class SurvivalPlacedListener extends BaseListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        if (plugin.isBlackListWorld(event.getBlock().getWorld())) {
            return;
        }

        BlockState state = event.getBlockPlaced().getState();
        if (!(state instanceof Lootable lootable) || lootable.getLootTable() == null) {
            return;
        }
        if (!(state instanceof TileState tileState)) {
            return;
        }

        tileState.getPersistentDataContainer().set(LConstants.SURVIVAL_PLACED_KEY, PersistentDataType.BYTE, (byte) 1);
        tileState.update(true, false);
    }
}
