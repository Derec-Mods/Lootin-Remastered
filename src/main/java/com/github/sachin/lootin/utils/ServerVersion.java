package com.github.sachin.lootin.utils;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerVersion {

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?");
    private static final ServerVersion CURRENT = parse(Bukkit.getVersion());

    private final int major;
    private final int minor;
    private final int patch;

    private ServerVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static ServerVersion current() {
        Bukkit.getLogger().info(CURRENT.toString());
        return CURRENT;
    }

    public boolean isAtLeast(int major, int minor) {
        return isAtLeast(major, minor, 0);
    }

    public boolean isAtLeast(int major, int minor, int patch) {
        if (this.major != major) {
            return this.major > major;
        }
        if (this.minor != minor) {
            return this.minor > minor;
        }
        return this.patch >= patch;
    }

    public boolean equals(int major, int minor, int patch) {
        return this.major == major && this.minor == minor && this.patch == patch;
    }

    private static ServerVersion parse(String raw) {
        if (raw == null) {
            return new ServerVersion(0, 0, 0);
        }
        Matcher matcher = VERSION_PATTERN.matcher(raw);
        if (!matcher.find()) {
            return new ServerVersion(0, 0, 0);
        }
        int major = parseInt(matcher.group(1));
        int minor = parseInt(matcher.group(2));
        int patch = parseInt(matcher.group(3));
        return new ServerVersion(major, minor, patch);
    }

    private static int parseInt(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
