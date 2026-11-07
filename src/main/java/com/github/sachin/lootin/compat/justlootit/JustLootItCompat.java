package com.github.sachin.lootin.compat.justlootit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JustLootItCompat {

    public static final NamespacedKey JLI_IDENTITY_KEY = NamespacedKey.fromString("justlootit:id");
    public static final NamespacedKey JLI_CHEST_OFFSET_KEY = NamespacedKey.fromString("justlootit:chest_offset_v2");
    public static final NamespacedKey JLI_CHEST_OFFSET_V1_KEY = NamespacedKey.fromString("justlootit:chest_offset");
    public static final NamespacedKey JLI_LEGACY_CHEST_OFFSET_KEY = NamespacedKey.fromString("justlootit:chest");
    public static final NamespacedKey JLI_BREAK_KEY = NamespacedKey.fromString("justlootit:break");
    public static final NamespacedKey JLI_TABLE_TYPE_KEY = NamespacedKey.fromString("justlootit:table/type");
    public static final NamespacedKey JLI_TABLE_NAMESPACE_KEY = NamespacedKey.fromString("justlootit:table/namespace");
    public static final NamespacedKey JLI_TABLE_KEY_KEY = NamespacedKey.fromString("justlootit:table/key");

    private static final Set<Location> convertingLocations = ConcurrentHashMap.newKeySet();

    private JustLootItCompat() {
    }

    public static boolean isJLIContainer(PersistentDataContainer container) {
        if (container == null) {
            return false;
        }
        return container.has(JLI_IDENTITY_KEY, PersistentDataType.LONG);
    }

    public static long getJLIId(PersistentDataContainer container) {
        if (container == null || !container.has(JLI_IDENTITY_KEY, PersistentDataType.LONG)) {
            return -1L;
        }
        Long id = container.get(JLI_IDENTITY_KEY, PersistentDataType.LONG);
        return id != null ? id : -1L;
    }

    public static void cleanJLIKeys(PersistentDataContainer container) {
        if (container == null) {
            return;
        }
        container.remove(JLI_IDENTITY_KEY);
        container.remove(JLI_CHEST_OFFSET_KEY);
        container.remove(JLI_CHEST_OFFSET_V1_KEY);
        container.remove(JLI_LEGACY_CHEST_OFFSET_KEY);
        container.remove(JLI_BREAK_KEY);
        container.remove(JLI_TABLE_TYPE_KEY);
        container.remove(JLI_TABLE_NAMESPACE_KEY);
        container.remove(JLI_TABLE_KEY_KEY);
    }

    public static boolean isConverting(Location location) {
        if (location == null) {
            return false;
        }
        return convertingLocations.contains(location);
    }

    public static boolean startConverting(Location location) {
        if (location == null) {
            return false;
        }
        return convertingLocations.add(location);
    }

    public static void stopConverting(Location location) {
        if (location != null) {
            convertingLocations.remove(location);
        }
    }

    public static File getJLIStorageFolder(World world) {
        if (world == null) {
            return null;
        }
        File baseDir = world.getWorldFolder();
        File direct = new File(baseDir, "justlootit/containers");
        if (direct.exists()) {
            return direct;
        }
        if (world.getEnvironment() == World.Environment.NORMAL) {
            File overworld26 = new File(baseDir, "dimensions/minecraft/overworld/justlootit/containers");
            if (overworld26.exists()) {
                return overworld26;
            }
        } else if (world.getEnvironment() == World.Environment.NETHER) {
            File nether26 = new File(baseDir, "dimensions/minecraft/the_nether/justlootit/containers");
            if (nether26.exists()) {
                return nether26;
            }
            File dimNeg1 = new File(baseDir, "DIM-1/justlootit/containers");
            if (dimNeg1.exists()) {
                return dimNeg1;
            }
        } else if (world.getEnvironment() == World.Environment.THE_END) {
            File end26 = new File(baseDir, "dimensions/minecraft/the_end/justlootit/containers");
            if (end26.exists()) {
                return end26;
            }
            File dim1 = new File(baseDir, "DIM1/justlootit/containers");
            if (dim1.exists()) {
                return dim1;
            }
        }
        if (!Bukkit.getWorlds().isEmpty()) {
            File mainWorld = Bukkit.getWorlds().get(0).getWorldFolder();
            if (world.getEnvironment() == World.Environment.NETHER) {
                File mainNether26 = new File(mainWorld, "dimensions/minecraft/the_nether/justlootit/containers");
                if (mainNether26.exists()) {
                    return mainNether26;
                }
                File mainDimNeg1 = new File(mainWorld, "DIM-1/justlootit/containers");
                if (mainDimNeg1.exists()) {
                    return mainDimNeg1;
                }
            } else if (world.getEnvironment() == World.Environment.THE_END) {
                File mainEnd26 = new File(mainWorld, "dimensions/minecraft/the_end/justlootit/containers");
                if (mainEnd26.exists()) {
                    return mainEnd26;
                }
                File mainDim1 = new File(mainWorld, "DIM1/justlootit/containers");
                if (mainDim1.exists()) {
                    return mainDim1;
                }
            }
        }
        File separate = new File(Bukkit.getWorldContainer(), world.getName() + "/justlootit/containers");
        if (separate.exists()) {
            return separate;
        }
        return direct;
    }
}
