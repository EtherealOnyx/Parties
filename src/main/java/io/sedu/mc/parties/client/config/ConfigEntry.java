package io.sedu.mc.parties.client.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

    public JsonObject getJsonEntries(Gson gson) {
        JsonObject json = new JsonObject();
        entries.forEach(e -> json.add(e.name, gson.toJsonTree(e.value)));
        return json;
    }





    public void forEachEntry(BiConsumer<String, Object> action) {
        entries.forEach(entryObject -> action.accept(entryObject.name, entryObject.value));
    }

    public void forEachKey(Consumer<String> action) {
        entries.forEach(entryObject -> action.accept(entryObject.name));
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
