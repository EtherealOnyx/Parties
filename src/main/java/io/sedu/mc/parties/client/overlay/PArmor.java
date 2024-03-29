package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;
import java.util.HashMap;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PArmor extends RenderIconTextItem implements TooltipItem {

    private TranslatableComponent tipName = new TranslatableComponent("ui.sedparties.tooltip.armor");
    public PArmor(String name) {
        super(name);
        width = 9;
        height = 9;
    }



    @Override
    int getColor() {
        return 0xb8b9c4;
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        setup(GUI_ICONS_LOCATION);
        blit(poseStack, b.x+8, b.y+3, 34, 9, 9, 9);
    }

    @Override
    void updateDefaultPositionForMods(HashMap<String, Update> updater) {
        if (RenderItem.barModsPresent() > 0) {
            //Move text up to make space for bar array.
            updater.get("ypos").onUpdate(this, 17);
        }
    }

    @Override
    void renderSelf(ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (!id.isSpectator)
            renderArmor(0, poseStack, gui, id.getArmor(), id.alpha);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline && !id.isSpectator)
            renderArmor(i, poseStack, gui, id.getArmor(), id.alpha);
    }

    void renderArmor(int i, PoseStack poseStack, ForgeIngameGui gui, int armor, float alpha){
        if (iconEnabled) {
            useAlpha(alpha);
            setup(Gui.GUI_ICONS_LOCATION);
            blit(poseStack, x(i), y(i), 34, 9, 9, 9);
            resetColor();
        }
        if (textEnabled)
            text(tX(i), tY(i), gui, poseStack, String.valueOf(armor), color);
    }



    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + 11;
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) + 1;
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("general");
        c.addBooleanEntry("display", isEnabled());
        c.addSliderEntry("scale", 1, () -> 3, getScale(), true);
        c.addSliderEntry("zpos", 0, () -> 10, zPos);
        c.addTitleEntry("icon");
        c.addBooleanEntry("idisplay", iconEnabled);
        c.addSliderEntry("xpos", 0, this::maxX, this.x);
        c.addSliderEntry("ypos", 0, this::maxY, this.y);
        c.addTitleEntry("text");
        c.addBooleanEntry("tdisplay", textEnabled);
        c.addBooleanEntry("tshadow", textShadow);
        c.addColorEntry("tcolor", color);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("xtpos", 0, () -> Math.max(0, frameEleW), textX));
        entries.add(c.addSliderEntry("ytpos", 0, () -> Math.max(0, frameEleH - (int)(minecraft.font.lineHeight*scale)), textY));
        toggleTextAttach(entries);
        return c;
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("scale", 2, 2);
        e.addEntry("zpos", 0, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("xpos", 46, 12);
        e.addEntry("ypos", 19, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", false, 1);
        e.addEntry("tcolor", 0xddf3ff, 24);
        e.addEntry("tattached", true, 1);
        e.addEntry("xtpos", 0, 12);
        e.addEntry("ytpos", 0, 12);
        return e;
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData.getOrderedPlayer(index, p -> {
            if (p.isOnline && !p.isSpectator) {
                renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName.getString() + p.getArmor(), 0xabfcff, 0x629b9e, 0xd1d1d1);
            }
        });
    }
}
