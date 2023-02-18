package io.sedu.mc.parties.client.config;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ConfigEntry {
    private static final HashMap<String, HashMap<String, Object>> entries = new HashMap<>();

    public static void clearEntries() {
        entries.clear();
    }

    public static Object getEntry(String renderName, String entryName) {
        return entries.get(renderName) == null ? null : entries.get(renderName).get(entryName);

    }

    public static void setEntry(String renderName, String entryName, Object value) {
        entries.computeIfAbsent(renderName, k -> new HashMap<>());
        entries.get(renderName).put(entryName, value);
    }

    public static void forEachInItem(String renderName, BiConsumer<String, Object> action) {
        Objects.requireNonNull(action);
        entries.computeIfPresent(renderName, (name, map) -> {
            map.forEach(action);
            return map;
        });
    }


}
