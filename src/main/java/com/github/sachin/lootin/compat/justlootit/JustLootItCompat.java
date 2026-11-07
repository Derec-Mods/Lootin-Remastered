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

    public static String getLootTable(World world, PersistentDataContainer container) {
        if (container == null) {
            return null;
        }
        if (container.has(JLI_TABLE_KEY_KEY, PersistentDataType.STRING)) {
            String key = container.get(JLI_TABLE_KEY_KEY, PersistentDataType.STRING);
            String namespace = container.has(JLI_TABLE_NAMESPACE_KEY, PersistentDataType.STRING)
                    ? container.get(JLI_TABLE_NAMESPACE_KEY, PersistentDataType.STRING)
                    : "minecraft";
            if (key != null && !key.isEmpty()) {
                return namespace + ":" + key;
            }
        }
        long id = getJLIId(container);
        if (id < 0) {
            return null;
        }
        String fromPlugin = getLootTableFromPlugin(world, id);
        if (fromPlugin != null && !fromPlugin.isEmpty()) {
            return fromPlugin;
        }
        return getLootTableFromStorageFiles(world, id);
    }

    private static String getLootTableFromPlugin(World world, long id) {
        try {
            org.bukkit.plugin.Plugin jli = Bukkit.getPluginManager().getPlugin("JustLootIt");
            if (jli == null || !jli.isEnabled()) {
                return null;
            }
            java.lang.reflect.Method versionHandlerMethod = jli.getClass().getMethod("versionHandler");
            Object versionHandler = versionHandlerMethod.invoke(jli);
            java.lang.reflect.Method getLevelMethod = versionHandler.getClass().getMethod("getLevel", World.class);
            Object level = getLevelMethod.invoke(versionHandler, world);
            Class<?> storageCapClass = Class.forName("me.lauriichan.spigot.justlootit.capability.StorageCapability");
            java.lang.reflect.Method getCapMethod = level.getClass().getMethod("getCapability", Class.class);
            Object optCap = getCapMethod.invoke(level, storageCapClass);
            if (optCap instanceof java.util.Optional<?> optional && optional.isPresent()) {
                Object cap = optional.get();
                java.lang.reflect.Method storageMethod = cap.getClass().getMethod("storage");
                Object storage = storageMethod.invoke(cap);
                java.lang.reflect.Method getMethod = storage.getClass().getMethod("get", long.class);
                Object stored = getMethod.invoke(storage, id);
                if (stored != null) {
                    java.lang.reflect.Method valueMethod = stored.getClass().getMethod("value");
                    Object val = valueMethod.invoke(stored);
                    if (val != null) {
                        try {
                            java.lang.reflect.Method getLootTableKeyMethod = val.getClass().getMethod("getLootTableKey");
                            Object keyObj = getLootTableKeyMethod.invoke(val);
                            if (keyObj instanceof NamespacedKey nk) {
                                return nk.toString();
                            }
                        } catch (NoSuchMethodException ignored) {
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static String getLootTableFromStorageFiles(World world, long id) {
        File folder = getJLIStorageFolder(world);
        if (folder == null || !folder.exists()) {
            return null;
        }
        int fileId = (int) (id >> 10);
        int valueId = (int) (id & 1023);
        String hexName = Integer.toHexString(fileId);
        File[] candidates = new File[] {
            new File(folder, hexName + ".jlti"),
            new File(folder, hexName + ".vjli"),
            new File(folder, hexName + ".jli"),
            new File(folder, "rafstorage.jlti"),
            new File(folder, "rafstorage.jli")
        };
        for (File file : candidates) {
            if (!file.exists() || !file.isFile()) {
                continue;
            }
            String result = readLootTableFromFile(file, valueId);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static String readLootTableFromFile(File file, int valueId) {
        try (java.io.RandomAccessFile raf = new java.io.RandomAccessFile(file, "r")) {
            long lookupOffset = 5L + (8L * valueId);
            if (raf.length() < lookupOffset + 8L) {
                return null;
            }
            raf.seek(lookupOffset);
            long dataOffset = raf.readLong();
            if (dataOffset <= 0 || dataOffset >= raf.length()) {
                return null;
            }
            raf.seek(dataOffset);
            int typeId = raf.readUnsignedShort();
            int version = raf.readUnsignedShort();
            int dataLength = raf.readInt();
            if (dataLength <= 0 || dataOffset + 8L + dataLength > raf.length()) {
                return null;
            }
            if (typeId == 16 || typeId == 18) {
                int nsLen = raf.readInt();
                if (nsLen <= 0 || nsLen > 256) {
                    return null;
                }
                byte[] nsBytes = new byte[nsLen];
                raf.readFully(nsBytes);
                String namespace = new String(nsBytes, java.nio.charset.StandardCharsets.UTF_8);

                int keyLen = raf.readInt();
                if (keyLen <= 0 || keyLen > 256) {
                    return null;
                }
                byte[] keyBytes = new byte[keyLen];
                raf.readFully(keyBytes);
                String key = new String(keyBytes, java.nio.charset.StandardCharsets.UTF_8);

                return namespace + ":" + key;
            }
        } catch (Throwable ignored) {
        }
        return null;
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
