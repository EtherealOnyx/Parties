package io.sedu.mc.parties.client.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.arsnoveau.ANCompatManager;
import io.sedu.mc.parties.api.thirstmod.TMCompatManager;
import io.sedu.mc.parties.api.toughasnails.TANCompatManager;
import io.sedu.mc.parties.client.overlay.GeneralOptions;
import io.sedu.mc.parties.client.overlay.RenderItem;
import io.sedu.mc.parties.data.ClientConfigData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.sedu.mc.parties.data.ClientConfigData.*;

public class Config {
    public static final Path DEFAULT_PRESET_PATH = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).resolve(Parties.MODID).resolve("presets");
    public static final Path PRESET_PATH = FMLPaths.CONFIGDIR.get().resolve(Parties.MODID).resolve("presets");
    public static void init() {
        try {
            Files.createDirectories(DEFAULT_PRESET_PATH);
            Files.createDirectories(PRESET_PATH);
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve(Parties.MODID).resolve("dims"));
        } catch (IOException e) {
            Parties.LOGGER.error("Error trying to create config paths!", e);
        }

    }

    public static boolean saveCompletePreset(String name, String desc, HashMap<String, RenderItem.Getter> itemGetter) {
        JsonObject json = new JsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        json.addProperty("description", desc);
        json.add("general", GeneralOptions.INSTANCE.getCurrentValues(itemGetter).getJsonEntries(gson));
        RenderItem.items.forEach((itemName, item) -> json.add(itemName, item.getCurrentValues(itemGetter).getJsonEntries(gson)));
        try (FileWriter writer = new FileWriter(new File(PRESET_PATH.toFile(), name + ".json"))){
            writer.write(gson.toJson(json));
            writer.flush();
        } catch (IOException e) {
            Parties.LOGGER.error("Error trying to create preset for " + name + "!", e);
            return false;
        }
        return true;
    }

    public static void saveDefaultPreset(String name, String desc) {
        JsonObject json = new JsonObject();
        HashMap<String, RenderItem.Getter> itemGetter = new HashMap<>();
        RenderItem.initGetter(itemGetter);
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        json.addProperty("description", desc);
        json.add("general", RenderItem.getGeneralValues().getJsonEntries(gson));
        RenderItem.items.forEach((itemName, item) -> json.add(itemName, item.getCurrentValues(itemGetter).getJsonEntries(gson)));
        try(FileWriter writer = new FileWriter(new File(DEFAULT_PRESET_PATH.toFile(), name + ".json"))) {
            writer.write(gson.toJson(json));
            writer.flush();
        } catch (IOException e) {
            Parties.LOGGER.error("Error trying to save default preset!", e);
        }
    }

    public static JsonObject savePresetToObject() {
        JsonObject json = new JsonObject();
        HashMap<String, RenderItem.Getter> itemGetter = new HashMap<>();
        RenderItem.initGetter(itemGetter);
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        json.add("general", RenderItem.getGeneralValues().getJsonEntries(gson));
        RenderItem.items.forEach((itemName, item) -> json.add(itemName, item.getCurrentValues(itemGetter).getJsonEntries(gson)));
        return json;
    }

    public static void getDefaultPresets(BiConsumer<String, String> action) {
        getPresets(DEFAULT_PRESET_PATH, action);
    }

    public static void getCustomPresets(BiConsumer<String, String> action) {
        getPresets(PRESET_PATH, action);
    }

    public static boolean loadPreset(String file, boolean isDefault, HashMap<String, RenderItem.Update> updater) {
        try (Reader reader = new FileReader((isDefault? DEFAULT_PRESET_PATH : PRESET_PATH).resolve(file + ".json").toFile())) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            //General values
            JsonElement element = jsonObject.get("general");
            updateValues(GeneralOptions.INSTANCE, element, updater);
            RenderItem.items.forEach((name, item) -> updateValues(item, jsonObject.get(name), updater));
        } catch (IOException e) {
            Parties.LOGGER.warn("Error trying to load a preset! Refreshing list", e);
            Parties.LOGGER.warn("Refreshing preset list...", e);
            return false;
        }
        return true;
    }

    private static void updateValues(RenderItem item, JsonElement element, HashMap<String, RenderItem.Update> updater) {
        if (element instanceof JsonObject jsonObject) {
            item.getDefaults().forEachKey(entryName -> {
                if (jsonObject.get(entryName) != null && updater.get(entryName) != null)
                    updater.get(entryName).onUpdate(item, getPrimitiveValue(jsonObject.get(entryName)));
                else {
                    Parties.LOGGER.warn("Entry '" + entryName + "' is not defined. Perhaps json file is from a different version?");
                }
            });
            return;
        }
        Parties.LOGGER.warn("Failed to parse an element properly. Perhaps json file is from a different version?");
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

    public static boolean deletePreset(String file) {
        return PRESET_PATH.resolve(file + ".json").toFile().delete();
    }

    public static void copyPreset(Minecraft minecraft, HashMap<String, RenderItem.Getter> getter) {
        minecraft.keyboardHandler.setClipboard(getPresetString(getter));
    }

    public static boolean pastePreset(Minecraft minecraft, HashMap<String, RenderItem.Update> updater) {
        assert minecraft.player != null;
        try {
            String bits = minecraft.keyboardHandler.getClipboard();
            if (Integer.parseInt(bits.substring(0, bits.indexOf('|'))) != Parties.ENCODE_VERSION) {
                minecraft.player.sendMessage(new TranslatableComponent("messages.sedparties.config.loadfail").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), minecraft.player.getUUID());
                minecraft.player.sendMessage(new TranslatableComponent("messages.sedparties.config.loadfailv").append("(v. " + Parties.ENCODE_VERSION + ")").withStyle(ChatFormatting.GRAY), minecraft.player.getUUID());
                return false;
            }
            bits = bits.substring(bits.indexOf('|') + 1);
            int parse = Integer.parseInt(bits.substring(0, bits.indexOf('|')));
            bits = new BigInteger(1, Base64.decodeBase64(bits.substring(bits.indexOf('|') + 1))).toString(2);
            if (parse > 0) {
                bits = bits.substring(parse);
            } else {
                bits = String.format("%" + (bits.length() + Math.abs(parse)) + "s", bits).replace(' ', '0');
            }
            parseBinaryString(bits.toCharArray(), updater);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    private static void parseBinaryString(char[] bits, HashMap<String, RenderItem.Update> updater) {
        int index = 0;
        index = GeneralOptions.INSTANCE.getDefaults().readBits(index, bits, (name, value) -> updater.get(name).onUpdate(GeneralOptions.INSTANCE, value));
        for (RenderItem item : RenderItem.items.values()) {
            index += item.getDefaults().readBits(index, bits, (name, value) -> updater.get(name).onUpdate(item, value));
        }
    }

    public static String getPresetString(HashMap<String, RenderItem.Getter> getter) {
        StringBuilder bits = new StringBuilder();
        bits.append(GeneralOptions.INSTANCE.getCurrentValues(getter).getBits());
        RenderItem.items.values().forEach((item) -> bits.append(item.getCurrentValues(getter).getBits()));
        BigInteger temp = new BigInteger(bits.toString(), 2);
        int sizeDiff = temp.toString(2).length() - bits.length();
        return Parties.ENCODE_VERSION + "|" + sizeDiff + "|" + Base64.encodeBase64String(temp.toByteArray());
    }

    public static void saveDefaultDims(List<DimConfig.DimEntryConfig> entries) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        try (FileWriter writer = new FileWriter(new File(FMLPaths.CONFIGDIR.get().resolve(Parties.MODID).resolve("dims").toFile(), "default.json"))){
            writer.write(gson.toJson(entries));
            writer.flush();
        } catch (IOException e) {
            Parties.LOGGER.error("Error trying to save default dimension entries !", e);
        }
    }

    public static void forEachDimFile(Consumer<List<DimConfig.DimEntryConfig>> action) {
        Gson gson = new Gson();
        Type dimListType = new TypeToken<ArrayList<DimConfig.DimEntryConfig>>(){}.getType();
        Path master = FMLPaths.CONFIGDIR.get().resolve(Parties.MODID).resolve("dims");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(master)) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    String pathName = path.getFileName().toString();
                    if (pathName.endsWith(".json") && !pathName.equals("default.json")) {
                        try (Reader reader = new FileReader(master.resolve(path).toFile())) {
                            List<DimConfig.DimEntryConfig> l = gson.fromJson(reader, dimListType);
                            if (l != null) action.accept(l);
                        }
                    }
                }
            }
        } catch (IOException e) {
            Parties.LOGGER.error("Error trying to load dimension entries!", e);
        }
    }

    public static void saveDefaultPresetFromString(HashMap<String, RenderItem.Update> updater, String fileName, String description, String load) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(load, JsonObject.class);
        //General values
        JsonElement element = jsonObject.get("general");
        updateValues(GeneralOptions.INSTANCE, element, updater);
        RenderItem.items.forEach((name, item) -> updateValues(item, jsonObject.get(name), updater));
        saveDefaultPreset(fileName, description);
    }

    public static void loadPresetFromObject(JsonObject jsonObject, HashMap<String, RenderItem.Update> updater) {
        JsonElement element = jsonObject.get("general");
        updateValues(GeneralOptions.INSTANCE, element, updater);
        RenderItem.items.forEach((name, item) -> updateValues(item, jsonObject.get(name), updater));
    }

    public static void reloadClientConfigs() {
        OverlayRegistry.enableOverlay(ForgeIngameGui.POTION_ICONS_ELEMENT, renderPotionEffects.get());
        OverlayRegistry.enableOverlay(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, renderXPBar.get());
        OverlayRegistry.enableOverlay(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, renderPlayerHealth.get());
        OverlayRegistry.enableOverlay(ForgeIngameGui.ARMOR_LEVEL_ELEMENT, renderPlayerArmor.get());
        OverlayRegistry.enableOverlay(ForgeIngameGui.FOOD_LEVEL_ELEMENT, renderHunger.get());
        TMCompatManager.getHandler().setThirstRender(renderThirst.get());
        //CSCompatManager.getHandler().setTempRender(renderTemperature.get()); //I cri
        ANCompatManager.getHandler().setManaRender(renderMana.get());
        TANCompatManager.getHandler().setRenderers(renderThirst.get(), renderTemperature.get());
    }

    public static void loadDefaultPreset() {
        Minecraft minecraft = Minecraft.getInstance();
        assert minecraft.player != null;
        HashMap<String, RenderItem.Update> updater = new HashMap<>();
        RenderItem.initUpdater(updater);
        try {

            String bits = ClientConfigData.defaultPreset.get();
            if (bits.equals("") || Integer.parseInt(bits.substring(0, bits.indexOf('|'))) != Parties.ENCODE_VERSION) {
                saveDefaultPresetString(updater);
                return;
            }
            bits = bits.substring(bits.indexOf('|') + 1);
            int parse = Integer.parseInt(bits.substring(0, bits.indexOf('|')));
            bits = new BigInteger(1, Base64.decodeBase64(bits.substring(bits.indexOf('|') + 1))).toString(2);
            if (parse > 0) {
                bits = bits.substring(parse);
            } else {
                bits = String.format("%" + (bits.length() + Math.abs(parse)) + "s", bits).replace(' ', '0');
            }
            parseBinaryString(bits.toCharArray(), updater);
        } catch (Exception e) {
            saveDefaultPresetString(updater);
        }
    }

    private static void saveDefaultPresetString(HashMap<String, RenderItem.Update> updater) {
        Parties.LOGGER.info("Filling the default preset in the client configuration and using it.");
        if (ModList.get().isLoaded("ars_nouveau")) {
            if (!Config.loadPreset("standard-mana", true, updater))
                RenderItem.setDefaultValues();
        } else {
            RenderItem.setDefaultValues();
        }
        saveCurrentPresetAsDefault();
    }

    private static void saveCurrentPresetAsDefault() {
        HashMap<String, RenderItem.Getter> getter = new HashMap<>();
        RenderItem.initGetter(getter);
        saveCurrentPresetAsDefault(getter);
    }

    public static void saveCurrentPresetAsDefault(HashMap<String, RenderItem.Getter> getter) {
        ClientConfigData.defaultPreset.set(getPresetString(getter));
        Parties.LOGGER.debug("Updated default preset for this instance.");
    }
}
