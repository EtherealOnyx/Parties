package io.sedu.mc.parties.client.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.mod.arsnoveau.ANCompatManager;
import io.sedu.mc.parties.api.mod.feathers.FCompatManager;
import io.sedu.mc.parties.api.mod.spellsandshields.SSCompatManager;
import io.sedu.mc.parties.api.mod.thirstmod.TMCompatManager;
import io.sedu.mc.parties.api.mod.toughasnails.TANCompatManager;
import io.sedu.mc.parties.client.overlay.GeneralOptions;
import io.sedu.mc.parties.client.overlay.PHead;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
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
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static void init() {
        try {
            Files.createDirectories(DEFAULT_PRESET_PATH);
            Files.createDirectories(PRESET_PATH);
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve(Parties.MODID).resolve("dims"));
        } catch (IOException e) {
            Parties.LOGGER.error("[Parties] Error trying to create config paths!", e);
        }

    }

    public static boolean saveCompletePreset(String name, String desc, HashMap<String, RenderItem.Getter> itemGetter) {
        JsonObject json = new JsonObject();
        json.addProperty("description", desc);
        json.add("general", GeneralOptions.INSTANCE.getCurrentValues(itemGetter).getJsonEntries(GSON));
        RenderItem.items.forEach((itemName, item) -> json.add(itemName, item.getCurrentValues(itemGetter).getJsonEntries(GSON)));
        try (FileWriter writer = new FileWriter(new File(PRESET_PATH.toFile(), name + ".json"))){
            writer.write(GSON.toJson(json));
            writer.flush();
        } catch (IOException e) {
            Parties.LOGGER.error("[Parties] Error trying to create preset for " + name + "!", e);
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
            Parties.LOGGER.error("[Parties] Error trying to save default preset!", e);
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
            Parties.LOGGER.warn("[Parties] Error trying to load a preset! Refreshing list", e);
            Parties.LOGGER.warn("[Parties] Refreshing preset list...", e);
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
                    Parties.LOGGER.warn("[Parties] Entry '" + entryName + "' is not defined. Perhaps json file is from a different version?");
                }
            });
            return;
        }
        Parties.LOGGER.warn("[Parties] Failed to parse an element properly. Perhaps json file is from a different version?");
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
            Parties.LOGGER.error("[Parties] Error trying to load default presets!", e);
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
            if (!applyPresetString(minecraft.keyboardHandler.getClipboard().toCharArray(), updater)) {
                parseError();
                return false;
            }
        } catch (Exception e) {
            minecraft.player.sendMessage(new TranslatableComponent("messages.sedparties.config.loadfail").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), minecraft.player.getUUID());
            minecraft.player.sendMessage(new TranslatableComponent("messages.sedparties.config.loadfailv").withStyle(ChatFormatting.GRAY), minecraft.player.getUUID());
            Parties.LOGGER.error("[Parties] Failed to load preset.", e);
            return false;
        }
        return true;
    }

    public static void parseError() {
        Parties.LOGGER.warn("[Parties] Failed to load preset.");
    }


    private static boolean applyPresetString(char[] bits, HashMap<String, RenderItem.Update> updater)  {
        StringBuilder bitBuilder = new StringBuilder();
        boolean newEntry = true;
        boolean hasZeroes = false;
        boolean validPreset = false;
        int eleId = -1;
        int eleVer = 0;
        String elementBits = "";
        ArrayList<RenderItem> itemList = new ArrayList<>(RenderItem.items.values());

        for (char bit : bits) {
            if (bit == ':') { //bitBuilder contains ID.
                newEntry = false; //We are grabbing an entry.
                eleId = Integer.parseInt(bitBuilder.toString());
                bitBuilder.setLength(0); //Clear builder.
                continue;
            }
            if (bit == '-') {
                if (newEntry) {//newEntry = true, - defines a subversion of an entry.
                    eleVer = Integer.parseInt(bitBuilder.toString());
                } else { //newEntry = false, - defines additional zeros needed.
                    hasZeroes = true;
                    //base64 part completed.
                    elementBits = new BigInteger(1, Base64.decodeBase64(bitBuilder.toString())).toString(2);
                }
                bitBuilder.setLength(0); //Clear builder.
                continue;
            }
            if (bit == '|') {
                if (hasZeroes) { //bitBuilder contains integer of zeroes we need to add to elementBits.
                    elementBits = String.format("%" + (elementBits.length() + Integer.parseInt(bitBuilder.toString())) + "s", elementBits).replace(' ', '0');
                } else { //bitBuilder contains elementBits.
                    elementBits = new BigInteger(1, Base64.decodeBase64(bitBuilder.toString())).toString(2);
                }
                //Entry is fully defined.
                if (eleId == 0) {
                    GeneralOptions.INSTANCE.getDefaults().readBits(elementBits.toCharArray(), (name, value) -> updater.get(name).onUpdate(GeneralOptions.INSTANCE, value));
                } else {
                    //TODO: Replace eleVer functionality when multi versions of elements are added.
                    if (eleVer == 0) {
                        String finalElementBits = elementBits;
                        RenderItem.getItemById(eleId, item -> {
                            itemList.remove(item);
                            item.getDefaults().readBits(finalElementBits.toCharArray(), (name, value) -> updater.get(name).onUpdate(item, value));
                        });
                    } else {
                        //Future proofing
                        Parties.LOGGER.error("[Parties] Attempted to load an element from a future version. Reverting to default.");
                        RenderItem.getItemById(eleId, item -> {
                            itemList.remove(item);
                            RenderItem.setElementDefaults(item, updater);
                        });
                    }
                }

                bitBuilder.setLength(0); //Clear builder.
                //Reset for next element
                eleId = -1;
                eleVer = 0;
                hasZeroes = false;
                newEntry = true;
                validPreset = true;
                continue;
            }
            bitBuilder.append(bit);
        }
        if (validPreset) {
            //Make items default that aren't in preset and disable them.

            itemList.forEach(item -> {
                RenderItem.setElementDefaults(item, updater);
                item.setEnabled(false);
            });
            RenderItem.isDirty = true;
        }
        return validPreset;


    }

    public static boolean checkPresetString(char[] bits, String playerName)  {
        StringBuilder bitBuilder = new StringBuilder();
        boolean newEntry = true;
        boolean hasZeroes = false;
        boolean validPreset = false;
        int eleId = -1;
        int eleVer = 0;
        String elementBits = "";
        try {
            for (char bit : bits) {
                if (bit == ':') { //bitBuilder contains ID.
                    newEntry = false; //We are grabbing an entry.
                    eleId = Integer.parseInt(bitBuilder.toString());
                    bitBuilder.setLength(0); //Clear builder.
                    continue;
                }
                if (bit == '-') {
                    if (newEntry) {//newEntry = true, - defines a subversion of an entry.
                        eleVer = Integer.parseInt(bitBuilder.toString());
                    } else { //newEntry = false, - defines additional zeros needed.
                        hasZeroes = true;
                        //base64 part completed.
                        elementBits = new BigInteger(1, Base64.decodeBase64(bitBuilder.toString())).toString(2);
                    }
                    bitBuilder.setLength(0); //Clear builder.
                    continue;
                }
                if (bit == '|') {
                    if (hasZeroes) { //bitBuilder contains integer of zeroes we need to add to elementBits.
                        elementBits = String.format("%" + (elementBits.length() + Integer.parseInt(bitBuilder.toString())) + "s", elementBits)
                                            .replace(' ', '0');
                    } else { //bitBuilder contains elementBits.
                        elementBits = new BigInteger(1, Base64.decodeBase64(bitBuilder.toString())).toString(2);
                    }
                    //Entry is fully defined.
                    if (eleId == 0) {
                        GeneralOptions.INSTANCE.getDefaults().readBits(elementBits.toCharArray(), (name, value) -> {});
                    } else {
                        //TODO: Replace eleVer functionality when multi versions of elements are added.
                        if (eleVer == 0) {
                            String finalElementBits = elementBits;
                            RenderItem.getItemById(eleId, item -> {
                                item.getDefaults().readBits(finalElementBits.toCharArray(), (name, value) -> {});
                            });
                        }  //Can't test else yet...

                    }
                    bitBuilder.setLength(0); //Clear builder.
                    //Reset for next element
                    eleId = -1;
                    eleVer = 0;
                    hasZeroes = false;
                    newEntry = true;
                    validPreset = true;
                    continue;
                }
                bitBuilder.append(bit);
            }
            return validPreset;
        } catch (Exception e) {
            Parties.LOGGER.error("Parsing a preset threw an error!", e);
            return false;
        }
    }

    public static String getPresetString(HashMap<String, RenderItem.Getter> getter) {
        //New
        //Only save enabled items.
        //TODO: Allow people to save non-enabled items as well that are marked for saving.
        StringBuilder bits = new StringBuilder();
        //Add main entry.
        String bitForm = GeneralOptions.INSTANCE.getCurrentValues(getter).getBits();
        BigInteger intForm = bitsToInt(bitForm);
        bits.append("0:").append(intTo64(intForm)).append("-").append(bitForm.length() - intForm.toString(2).length()).append("|");

        RenderItem.forEachToSave((item) -> {
            String bitF = item.getCurrentValues(getter).getBits();
            BigInteger intF = bitsToInt(bitF);
            bits.append(item.getId()).append(":").append(intTo64(intF));
            int length = bitF.length() - intF.toString(2).length();
            if (length != 0)
                bits.append("-").append(length);
            bits.append("|");
        });
        return bits.toString();
    }

    public static BigInteger bitsToInt(String bits) {
        return new BigInteger(bits, 2);
    }

    public static String intTo64(BigInteger integer) {
        return Base64.encodeBase64String(integer.toByteArray());
    }

    public static void saveDefaultDims(List<DimConfig.DimEntryConfig> entries) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        try (FileWriter writer = new FileWriter(new File(FMLPaths.CONFIGDIR.get().resolve(Parties.MODID).resolve("dims").toFile(), "default.json"))){
            writer.write(gson.toJson(entries));
            writer.flush();
        } catch (IOException e) {
            Parties.LOGGER.error("[Parties] Error trying to save default dimension entries !", e);
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
                    if (pathName.endsWith(".json") && !pathName.equals("default.json") & !pathName.equals("missing.json")) {
                        try (Reader reader = new FileReader(master.resolve(path).toFile())) {
                            List<DimConfig.DimEntryConfig> l = gson.fromJson(reader, dimListType);
                            if (l != null) action.accept(l);
                        }
                    }
                }
            }
        } catch (IOException e) {
            Parties.LOGGER.error("[Parties] Error trying to load dimension entries!", e);
        }
    }

    public static void forEachMissing(Consumer<List<DimConfig.DimEntryConfig>> action) {
        try (Reader reader = new FileReader(FMLPaths.CONFIGDIR.get().resolve(Parties.MODID).resolve("dims").resolve("missing.json").toFile())) {
            //General values
            Type dimListType = new TypeToken<ArrayList<DimConfig.DimEntryConfig>>(){}.getType();
            List<DimConfig.DimEntryConfig> l = GSON.fromJson(reader, dimListType);
            if (l != null) action.accept(l);
        } catch (IOException e) {
            Parties.LOGGER.debug("missing.json was not found. Generating...");
        }
    }

    public static void saveDefaultPresetFromString(HashMap<String, RenderItem.Update> updater, String fileName, String description, String load) {
        JsonObject jsonObject = GSON.fromJson(load, JsonObject.class);
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
        //Force file recheck.
        OverlayRegistry.enableOverlay(ForgeIngameGui.POTION_ICONS_ELEMENT, renderPotionEffects.get());
        OverlayRegistry.enableOverlay(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, renderXPBar.get());
        OverlayRegistry.enableOverlay(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, renderPlayerHealth.get());
        OverlayRegistry.enableOverlay(ForgeIngameGui.ARMOR_LEVEL_ELEMENT, renderPlayerArmor.get());
        OverlayRegistry.enableOverlay(ForgeIngameGui.FOOD_LEVEL_ELEMENT, renderHunger.get());
        TMCompatManager.getHandler().setThirstRender(renderThirst.get());
        //CSCompatManager.getHandler().setTempRender(renderTemperature.get()); //I cri
        ANCompatManager.getHandler().setManaRender(renderMana.get());
        TANCompatManager.getHandler().setRenderers(renderThirst.get(), renderTemperature.get());
        SSCompatManager.getHandler().setManaRender(renderSSMana.get());
        FCompatManager.getHandler().setFeathersRender(renderFeathers.get());
        PHead.updateModelRenderer();
        RenderItem.updateSelfRender();
        RenderItem.updateFramePos();
    }

    public static void createDefaultPreset() {
        HashMap<String, RenderItem.Update> updater = new HashMap<>();
        RenderItem.initUpdater(updater);
        generateDefaultPreset(updater);
    }

    public static void loadDefaultPreset() {
        HashMap<String, RenderItem.Update> updater = new HashMap<>();
        RenderItem.initUpdater(updater);
        try (Reader reader = new FileReader(FMLPaths.CONFIGDIR.get().resolve(Parties.MODID).resolve("client").resolve("active-preset.json").toFile())) {
            Type presetType = new TypeToken<ArrayList<PresetEntry>>(){}.getType();
            if (PresetEntry.loadPreset(GSON.fromJson(reader, presetType))) {
                char[] bits = PresetEntry.getMainPresetString();
                if (!applyPresetString(bits, updater)) {
                    parseError();
                    generateDefaultPreset(updater);
                }
            }
        } catch (IOException e) {
            Parties.LOGGER.error("[Parties] Failed to load preset.", e);
            generateDefaultPreset(updater);
        }

    }

    private static void generateDefaultPreset(HashMap<String, RenderItem.Update> updater) {
        //General Defaults
        RenderItem.getGeneralDefaults().forEachEntry((s, v) -> updater.get(s.getName()).onUpdate(null, v));

        //Rest of item defaults.
        RenderItem.items.values().forEach(item -> RenderItem.setElementDefaults(item, updater));


        HashMap<String, RenderItem.Getter> getter = new HashMap<>();
        RenderItem.initGetter(getter);
        saveCurrentPresetAsDefault(getter);
    }

    public static void saveCurrentPresetAsDefault(HashMap<String, RenderItem.Getter> getter) {
        //TODO: Implement party preset.
        PresetEntry.updatePresetString(getPresetString(getter));
        Parties.LOGGER.debug("Updated default preset for this instance.");
    }

    public static void saveMissingDims() {
        List<DimConfig.DimEntryConfig> dims = new ArrayList<>();
        DimConfig.missingEntries.forEach((loc, dimEntry) -> {
            if (dimEntry.item.getItem().getRegistryName() != null)
                dims.add(new DimConfig.DimEntryConfig(loc, dimEntry.item.getItem().getRegistryName().toString(), dimEntry.color, dimEntry.priority));
            else
                dims.add(new DimConfig.DimEntryConfig(loc, "minecraft:bedrock", dimEntry.color, dimEntry.priority));
        });
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        try (FileWriter writer = new FileWriter(new File(FMLPaths.CONFIGDIR.get().resolve(Parties.MODID).resolve("dims").toFile(), "missing.json"))){
            writer.write(gson.toJson(dims));
            writer.flush();
        } catch (IOException e) {
            Parties.LOGGER.error("[Parties] Error trying to save missing dimension entries !", e);
        }
    }
}
