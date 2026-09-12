package com.github.sachin.lootin.utils;

import com.github.sachin.lootin.Lootin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Random;

/**
 * Utility to fill inventories from loot tables. 
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
            Inventory inv = container instanceof Chest
                    ? ((Chest) container).getBlockInventory()
                    : ((InventoryHolder) container).getInventory();

            Location loc = null;
            if (container instanceof BlockState) loc = ((BlockState) container).getLocation();
            else if (container instanceof Entity) loc = ((Entity) container).getLocation();

            long seed = System.nanoTime();
            if (resetSeed && loc != null) {
                long worldSeed = loc.getWorld().getSeed();
                seed = worldSeed ^ ((long)loc.getBlockX() * 73428767L + (long)loc.getBlockY() * 9127L + (long)loc.getBlockZ() * 1361L);
            }
            Random random = new Random(seed);

            if (tryFillLikeVanilla(player, container, lootTable, inv, seed, lootTableKey)) {
                return;
            }

            LootContext.Builder builder;
            if (loc != null) builder = new LootContext.Builder(loc);
            else builder = new LootContext.Builder(player.getLocation());
            if (player != null) {
                builder.killer(player);
                builder.lootedEntity(player);
            }

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
            plugin.getLogger().warning("LootTableFiller failed to fill loot for '" + lootTableKey + "': " + t.getMessage());
        }
    }

    private static boolean tryFillLikeVanilla(Player player, Lootable container, LootTable lootTable, Inventory inv, long seed, String lootTableKey) {
        try {
            try {
                container.setLootTable(lootTable, seed);
            } catch (NoSuchMethodError ignored) {
                container.setLootTable(lootTable);
                container.setSeed(seed);
            }
            if (container instanceof BlockState) ((BlockState) container).update();

            Object nmsPlayer = invokeNoArg(player, "getHandle");
            Object nmsContainer = container instanceof BlockState
                    ? invokeNoArg(container, "getBlockEntity", "getTileEntity")
                    : invokeNoArg(container, "getHandle");
            if (nmsContainer == null) nmsContainer = invokeNoArg(inv, "getInventory", "getHandle");

            Method unpack = null;
            if (nmsContainer != null) {
                for (Method method : nmsContainer.getClass().getMethods()) {
                    if (!"unpackLootTable".equals(method.getName())) continue;
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == 2 && params[1] == boolean.class) {
                        unpack = method;
                        break;
                    }
                    if (params.length == 1) unpack = method;
                }
            }
            if (unpack == null) {
                plugin.getLogger().info("LootTableFiller vanilla unpack for '" + lootTableKey + "': unpackLootTable not found, falling back");
                container.setLootTable(null);
                if (container instanceof BlockState) ((BlockState) container).update();
                return false;
            }
            if (unpack.getParameterCount() == 2) unpack.invoke(nmsContainer, nmsPlayer, true);
            else unpack.invoke(nmsContainer, nmsPlayer);

            int filled = countItems(inv);
            if (filled == 0) {
                plugin.getLogger().info("LootTableFiller vanilla unpack for '" + lootTableKey + "': ran " + unpack.getParameterCount() + "-arg unpack on " + nmsContainer.getClass().getSimpleName() + " but inventory was empty, falling back");
                container.setLootTable(null);
                if (container instanceof BlockState) ((BlockState) container).update();
                return false;
            }

            plugin.getLogger().info("LootTableFiller vanilla unpack for '" + lootTableKey + "': filled " + filled + " stacks via " + unpack.getParameterCount() + "-arg unpack on " + nmsContainer.getClass().getSimpleName());
            if (container instanceof BlockState) container.setLootTable(null);
            return true;
        } catch (Throwable t) {
            plugin.getLogger().info("LootTableFiller vanilla unpack for '" + lootTableKey + "': failed (" + t.getClass().getSimpleName() + ": " + t.getMessage() + "), falling back");
            try {
                container.setLootTable(null);
                if (container instanceof BlockState) ((BlockState) container).update();
            } catch (Throwable ignored2) {
            }
            return false;
        }
    }

    private static int countItems(Inventory inv) {
        int count = 0;
        if (inv == null) return 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && !item.getType().isAir()) count++;
        }
        return count;
    }

    private static Object invokeNoArg(Object target, String... names) {
        if (target == null) return null;
        for (String name : names) {
            try {
                Method method = target.getClass().getMethod(name);
                method.setAccessible(true);
                return method.invoke(target);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            for (String name : names) {
                try {
                    Method method = type.getDeclaredMethod(name);
                    method.setAccessible(true);
                    return method.invoke(target);
                } catch (ReflectiveOperationException ignored) {
                }
            }
        }
        return null;
    }
}
