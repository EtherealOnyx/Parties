package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.Config;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.api.helper.ColorAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.HashMap;

public class PresetOptions extends RenderItem {

    public static ItemStack icon = null;

    public PresetOptions(String name) {
        super(name);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {

    }

    @Override
    int getColor() {
        return ColorAPI.getRainbowColor();
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, b.x+8, b.y+4, 0);
        gui.getFont().drawShadow(poseStack, "Load", b.x+4, b.y+22, 0xFFFFFF);
    }

    @Override
    void updateDefaultPositionForMods(HashMap<String, Update> updater) {

    }

    @Override
    public ConfigEntry getDefaults() {
        return new ConfigEntry();
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        Config.getDefaultPresets((file, desc) -> c.addPresetEntry(file, desc, true));
        Config.getCustomPresets((file, desc) -> c.addPresetEntry(file, desc, false));
        return c;
    }

}
