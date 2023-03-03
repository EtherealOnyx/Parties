package io.sedu.mc.parties.client.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConfigEntry {
    ArrayList<EntryObject> entries;
    int totalBits;

    public ConfigEntry() {
        entries = new ArrayList<>();
    }

    public void addEntry(String name, Object value, int bits) {
        entries.add(new EntryObject(name, value, bits));
        totalBits += bits;
    }

    public void addEntry(EntryObject obj, Object value) {
        entries.add(new EntryObject(obj.name, value, obj.bitSize));
        totalBits += obj.bitSize;
    }

    public ArrayList<EntryObject> getEntries() {
        return entries;
    }

    public JsonObject getJsonEntries(Gson gson) {
        JsonObject json = new JsonObject();
        entries.forEach(e -> json.add(e.name, gson.toJsonTree(e.value)));
        return json;
    }

    public String getBits() {
        StringBuilder bits = new StringBuilder();
        entries.forEach(e -> bits.append(e.getBits()));
        return bits.toString();
    }


    public int readBits(int index, char[] bits, BiConsumer<String, Object> action) {
        for (EntryObject entry : entries) {
            entry.updateValue(bits, index);
            action.accept(entry.name, entry.value);
            index += entry.bitSize;
        }
        return totalBits;
    }





    public void forEachEntry(BiConsumer<EntryObject, Object> action) {
        entries.forEach(entryObject -> action.accept(entryObject, entryObject.value));
    }

    public void forEachKey(Consumer<String> action) {
        entries.forEach(entryObject -> action.accept(entryObject.name));
    }


    public static class EntryObject {
        String name;
        Object value;
        int bitSize;

        EntryObject(String name, Object value, int bitSize) {
            this.name = name;
            this.value = value;
            this.bitSize = bitSize;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        String getBits() {
            String bits = "";
            if (value instanceof Integer i) {
                bits = String.format("%" + bitSize + "s", Integer.toBinaryString(i)).replace(' ', '0');
            } else if (value instanceof Boolean b) {
                if (b) bits = "1";
                else bits = "0";
            }
            return bits;
        }

        //Returns number of bits used.
        void updateValue(char[] bits, int index) {
            if (value instanceof Boolean) {
                value = bits[index] == '1';
            } else if (value instanceof Integer) {
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < bitSize; i++) {
                    s.append(bits[index+i]);
                }
                this.value = Integer.parseInt(s.toString(), 2);
            }
        }

    }
}
