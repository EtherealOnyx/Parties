package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.api.mod.origins.OCompatManager;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.HashMap;

public class PLeader extends RenderItem implements TooltipItem{

    private TranslatableComponent tipName = new TranslatableComponent("ui.sedparties.tooltip.leader");

    public PLeader(String name) {
        super(name);
        width = 9;
        height = 9;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isLeader()) {
            useAlpha(id.alpha);
            setup(partyPath);
            blit(poseStack, x(i), y(i), 0, 0, 9, 9);
            resetColor();
        }
    }

    @Override
    int getColor() {
        return 0xFFE554;
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        setup(partyPath);
        RenderSystem.enableDepthTest();
        blit(poseStack, (b.x)+8, b.y+2, 0, 0, 9, 9);
    }

    @Override
    void updateDefaultPositionForMods(HashMap<String, Update> updater) {
        if (OCompatManager.active()) {
            //Move default to top-left instead of bottom-right.
            updater.get("xpos").onUpdate(this, 4);
            updater.get("ypos").onUpdate(this, 3);
        }
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        addDisplaySettings(c);
        addPositionalSettings(c, true, true, true);
        return c;
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("xpos", 34, 12);
        e.addEntry("ypos", 33, 12);
        e.addEntry("zpos", 2, 4);
        e.addEntry("scale", 2, 2);
        return e;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData.getOrderedPlayer(index, p -> {
            if (p.isLeader()) {
                renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName, 0xFFF2A9, 0x978B47, 0xFFE554);
            }
        });
    }
}
