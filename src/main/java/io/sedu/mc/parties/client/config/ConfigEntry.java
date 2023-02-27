package io.sedu.mc.parties.client.config;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class ConfigEntry {
    ArrayList<EntryObject> entries;

    public ConfigEntry() {
        entries = new ArrayList<>();
    }

    public void addEntry(String name, Object value) {
        entries.add(new EntryObject(name, value));
    }

    public ArrayList<EntryObject> getEntries() {
        return entries;
    }





    public void forEachEntry(BiConsumer<String, Object> action) {
        entries.forEach(entryObject -> action.accept(entryObject.name, entryObject.value));
    }


    static class EntryObject {
        String name;
        Object value;

        public EntryObject(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }
}
