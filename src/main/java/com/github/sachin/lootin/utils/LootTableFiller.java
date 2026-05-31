package com.github.sachin.lootin.utils;

import com.github.sachin.lootin.Lootin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

import java.util.Collection;
import java.util.Random;

/**
 * Utility to fill inventories from Bukkit/Paper loot tables without NMS.
 */
public final class LootTableFiller {

    private static final Lootin plugin = Lootin.getPlugin();

    private LootTableFiller() {}

    public static void fill(Player player, Lootable container, String lootTableKey, boolean resetSeed) {
        if (lootTableKey == null || lootTableKey.isEmpty()) return;

        try {
            NamespacedKey key = null;
            try {
                key = NamespacedKey.fromString(lootTableKey);
            } catch (Throwable t) {
                // fallback: parse manually
                if (lootTableKey.contains(":")){
                    String[] parts = lootTableKey.split(":",2);
                    try {
                        key = new NamespacedKey(parts[0], parts[1]);
                    } catch (Throwable ignored){
                        key = new NamespacedKey(Lootin.getPlugin(), lootTableKey);
                    }
                } else {
                    key = new NamespacedKey(Lootin.getPlugin(), lootTableKey);
                }
            }

            LootTable lootTable = Bukkit.getLootTable(key);
            if (lootTable == null) return;

            if (!(container instanceof InventoryHolder)) return;
            Inventory inv = ((InventoryHolder) container).getInventory();

            Location loc = null;
            if (container instanceof org.bukkit.block.BlockState) loc = ((org.bukkit.block.BlockState) container).getLocation();
            else if (container instanceof Entity) loc = ((Entity) container).getLocation();

            long seed = System.nanoTime();
            if (resetSeed && loc != null) {
                long worldSeed = loc.getWorld().getSeed();
                seed = worldSeed ^ ((long)loc.getBlockX() * 73428767L + (long)loc.getBlockY() * 9127L + (long)loc.getBlockZ() * 1361L);
            }
            Random random = new Random(seed);

            LootContext.Builder builder;
            if (loc != null) builder = new LootContext.Builder(loc);
            else builder = new LootContext.Builder(player.getLocation());

            LootContext context = builder.build();

            // Try to use fillInventory if available, otherwise populateLoot
            try {
                lootTable.fillInventory(inv, random, context);
                return;
            } catch (NoSuchMethodError | AbstractMethodError ignored) {
                // fall through to populateLoot
            }

            // populateLoot returns a list in this API - add returned items
            Collection<ItemStack> items = lootTable.populateLoot(random, context);
            if (items != null && !items.isEmpty()) inv.addItem(items.toArray(new ItemStack[0]));

        } catch (Throwable t) {
            plugin.getLogger().warning("LootTableFiller failed to fill loot: " + t.getMessage());
        }
    }
}
