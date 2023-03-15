package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PDead extends RenderItem {

    public PDead(String name) {
        super(name);
        width = 9;
        height = 9;
    }

    @Override
    int getColor() {
        return 0xAAAAAA;
    }

    @Override
    public String getType() {
        return "Icon";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        setup(GUI_ICONS_LOCATION);
        blit(poseStack, b.x+8, b.y+4, 16 + (gui.getGuiTicks() >> 4 & 1)*9, 0, 9, 9);
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("xpos", 157, 12);
        e.addEntry("ypos", 9, 12);
        e.addEntry("zpos", 0, 4);
        e.addEntry("scale", 2, 2);
        return e;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isDead) {
            setup(Gui.GUI_ICONS_LOCATION);
            blit(poseStack,x(i), y(i), 16 + (gui.getGuiTicks() >> 4 & 1)*9, 0, 9, 9);
        }
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        addDisplaySettings(c);
        addPositionalSettings(c, true, true, true);
        return c;
    }
}
