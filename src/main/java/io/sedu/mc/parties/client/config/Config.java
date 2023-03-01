package io.sedu.mc.parties.client.config;

import com.google.gson.*;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.overlay.GeneralOptions;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class Config {
    public static final Path DEFAULT_PRESET_PATH = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).resolve(Parties.MODID).resolve("presets");
    public static final Path PRESET_PATH = FMLPaths.CONFIGDIR.get().resolve(Parties.MODID).resolve("presets");
    public static void init() {
        try {
            Files.createDirectories(DEFAULT_PRESET_PATH);
            Files.createDirectories(PRESET_PATH);
        } catch (IOException e) {
            Parties.LOGGER.error("Error trying to create config paths!", e);
        }

    }

    public static void saveCompletePreset(String name, String desc) {
        JsonObject json = new JsonObject();
        HashMap<String, RenderItem.Getter> itemGetter = new HashMap<>();
        RenderItem.initGetter(itemGetter);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        json.addProperty("description", desc);
        json.add("general", RenderItem.getGeneralValues().getJsonEntries(gson));
        RenderItem.items.forEach((itemName, item) -> json.add(itemName, item.getCurrentValues(itemGetter, true).getJsonEntries(gson)));
        try (FileWriter writer = new FileWriter(new File(PRESET_PATH.toFile(), name + ".json"))){
            writer.write(gson.toJson(json));
            writer.flush();
        } catch (IOException e) {
            Parties.LOGGER.error("Error trying to create preset for " + name + "!", e);
        }
    }

    public static void saveDefaultPreset(String name, String desc) {
        JsonObject json = new JsonObject();
        HashMap<String, RenderItem.Getter> itemGetter = new HashMap<>();
        RenderItem.initGetter(itemGetter);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        json.addProperty("description", desc);
        json.add("general", RenderItem.getGeneralValues().getJsonEntries(gson));
        RenderItem.items.forEach((itemName, item) -> json.add(itemName, item.getCurrentValues(itemGetter, true).getJsonEntries(gson)));
        try(FileWriter writer = new FileWriter(new File(DEFAULT_PRESET_PATH.toFile(), name + ".json"))) {
            writer.write(gson.toJson(json));
            writer.flush();
        } catch (IOException e) {
            Parties.LOGGER.error("Error trying to save default preset!", e);
        }
    }

    public static void getDefaultPresets(BiConsumer<String, String> action) {
        getPresets(DEFAULT_PRESET_PATH, action);
    }

    public static void getCustomPresets(BiConsumer<String, String> action) {
        getPresets(PRESET_PATH, action);
    }

    public static void loadPreset(String file, boolean isDefault, HashMap<String, RenderItem.Update> updater) {
        try (Reader reader = new FileReader((isDefault? DEFAULT_PRESET_PATH : PRESET_PATH).resolve(file + ".json").toFile())) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            //General values
            JsonElement element = jsonObject.get("general");
            updateValues(new GeneralOptions(""), element, updater);
            RenderItem.items.forEach((name, item) -> updateValues(item, jsonObject.get(name), updater));
        } catch (IOException e) {
            Parties.LOGGER.error("Error trying to load a preset!", e);
        }
    }

    private static void updateValues(RenderItem item, JsonElement element, HashMap<String, RenderItem.Update> updater) {
        if (element instanceof JsonObject jsonObject) {
            item.getDefaults().forEachKey(entryName -> {
                if (jsonObject.get(entryName) != null)
                    updater.get(entryName).onUpdate(item, getPrimitiveValue(jsonObject.get(entryName)));
            });
        }
    }

    private static Object getPrimitiveValue(JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return null;
        } else if (jsonElement instanceof JsonPrimitive p) {
            if (p.isString()) {
                return p.getAsString();
            } else if (p.isBoolean()) {
                return p.getAsBoolean();
            } else if (p.isNumber()) {
                return p.getAsNumber().intValue(); //Only use ints for presets
            }
        }
        return jsonElement;
    }

    public static void getPresets(Path presetPath, BiConsumer<String, String> action) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(presetPath)) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    String pathName = path.getFileName().toString();
                    if (pathName.endsWith(".json")) {
                        pathName = pathName.substring(0, pathName.length() - 5);
                        String desc;
                        try (Stream<String> lines = Files.lines(presetPath.resolve(path.getFileName()))) {
                            desc = lines.skip(1).findFirst().get();
                            action.accept(pathName, desc.substring(desc.indexOf('"', desc.indexOf(':'))+1, desc.length()-2));
                        }
                    }


                }
            }
        } catch (IOException e) {
            Parties.LOGGER.error("Error trying to load default presets!", e);
        }
    }
}
