package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

public class POrigin extends RenderIconTextItem {

    public static ItemStack icon = null;
    public final ItemStack iconTemp;


    public POrigin(String name) {
        super(name);
        iconTemp = Items.NETHER_STAR.getDefaultInstance();
        iconTemp.addTagElement("Enchantments", StringTag.valueOf(""));
        width = 14;
        height = 14;
    }

    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + 18;
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) + 1;
    }

    @Override
    int getColor() {
        return 0xFFD700;
    }

    @Override
    public String getType() {
        return "";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        poseStack.pushPose();
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, b.x+4, b.y-1, 0);
        poseStack.popPose();
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("scale", 2, 2);
        e.addEntry("zpos", 1, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("xpos", 157, 12);
        e.addEntry("ypos", 9, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", true, 1);
        e.addEntry("tcolor", 0xFFD700, 24);
        e.addEntry("tattached", false, 1);
        e.addEntry("xtpos", 25, 12);
        e.addEntry("ytpos", 20, 12);
        return e;
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
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline && !id.isSpectator)
            if (iconEnabled) {
                RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, -1, width, height, 0x222222, 0xFFD700);
                RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 0, width, height, 0xAAAAAA, 0x555555);
                RenderUtils.renderGuiItem(iconTemp, xNormal(i), yNormal(i), scale, 6*scale, zPos, partyScale);
            }
        if (textEnabled)
            textCentered(tX(i), tY(i), gui, poseStack, "Origin", color);

    }

    @Override
    void renderSelf(ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isSpectator) return;
        if (iconEnabled) {
            RenderUtils.offRectNoA(poseStack.last().pose(), x(0), y(0), zPos, -1, width, height, 0x222222, 0xFFD700);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(0), y(0), zPos, 0, width, height, 0xAAAAAA, 0x555555);
            RenderUtils.renderGuiItem(iconTemp, xNormal(0), yNormal(0), scale, 6*scale, zPos, playerScale);
        }
        if (textEnabled)
            textCentered(tX(0), tY(0), gui, poseStack, "Origin", color);


    }
}
