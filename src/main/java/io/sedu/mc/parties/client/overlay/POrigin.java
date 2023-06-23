package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.api.mod.origins.OCompatManager;
import io.sedu.mc.parties.api.mod.origins.OriginHolder;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.HoverScreen;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;
import java.util.List;

public class POrigin extends RenderIconTextItem implements TooltipItem {

    public static ItemStack icon = null;
    public final ItemStack iconTemp;


    public POrigin(String name) {
        super(name);
        iconTemp = Items.NETHER_STAR.getDefaultInstance();
        iconTemp.addTagElement("Enchantments", StringTag.valueOf(""));
        width = 16;
        height = 16;
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
        if (id.isOnline && !id.isSpectator) {
            id.getOrigin(origin -> OriginHolder.getOriginInfo(origin, (name, item, color) -> {
                if (iconEnabled) {
                    RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, -1, width, height, 0x222222, color);
                    RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 0, width, height, color, color);
                    RenderUtils.renderGuiItem(item, xNormal(i), yNormal(i), scale, 6*scale, zPos, partyScale);
                }

                if (textEnabled)
                    textCentered(tX(i), tY(i), gui, poseStack, name, color);
            }));
        }



    }

    @Override
    void renderSelf(ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isSpectator) return;
        id.getOrigin(origin -> OriginHolder.getOriginInfo(origin, (name, item, color) -> {
            if (iconEnabled) {
                RenderUtils.offRectNoA(poseStack.last().pose(), x(0), y(0), zPos, -1, width, height, (color & 0xfefefe) >> 1, color);
                RenderUtils.offRectNoA(poseStack.last().pose(), x(0), y(0), zPos, 0, width, height, 0x222222, (color & 0xfefefe) >> 1);
                RenderUtils.renderGuiItem(item, xNormal(0), yNormal(0), scale, 8*scale, zPos+2, playerScale);
            }

            if (textEnabled)
                textCentered(tX(0), tY(0), gui, poseStack, name, color);
        }));
    }

    @Override
    public boolean isEnabled() {
        return elementEnabled && OCompatManager.active();
    }

    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData.getOrderedPlayer(index, p -> p.getOrigin(origin -> OriginHolder.getOriginTooltip(origin, (name, list, impact) -> {
            if (HoverScreen.showInfo) {
                int maxWidth = Math.max(15, gui.getFont().width(name.c));
                for (Component c : list) {
                    maxWidth = Math.max(maxWidth, gui.getFont().width(c) / 2);
                }
                int finalMaxWidth = maxWidth;
                OriginHolder.getOriginStack(origin, itemStack -> renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, name, list, impact, finalMaxWidth, itemStack));
            } else {
                Component c = new TranslatableComponent("gui.sedparties.tooltip.ctrlinfo").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.BOLD);
                int maxWidth = Math.max(gui.getFont().width(c)/2, gui.getFont().width(name.c));

                int y = 0;
                gui.getFont().drawShadow(poseStack, name.c, mouseX+10, currentY+mouseY+1, name.color);
                y += gui.getFont().lineHeight+1;

                poseStack.pushPose();
                poseStack.scale(.5f,.5f,1f);
                poseStack.translate(mouseX+10, currentY+mouseY+1+y, 0);
                gui.getFont().drawShadow(poseStack, c, mouseX+10, currentY+mouseY+1+y, name.color);
                poseStack.popPose();
                y += (gui.getFont().lineHeight+1)/2;
                rectCO(poseStack, -1, -3, mouseX+10, currentY+mouseY, mouseX+maxWidth+10, currentY+mouseY+y,  (name.color & 0xfefefe) >> 1, name.color);
                rectCO(poseStack, -1, -2, mouseX+10, currentY+mouseY, mouseX+maxWidth+10, currentY+mouseY+y, 0xFF111111, (name.color & 0xfefefe) >> 1);

                currentY += y+12;
            }
        })));
    }

    private void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int mouseX, int mouseY, int offsetX, int offsetY, ColorComponent name, List<Component> list, Component impact, int max, ItemStack itemStack) {
        poseStack.pushPose();
        int y = offsetY;
        gui.getFont().drawShadow(poseStack, name.c, mouseX+offsetX + ((max - gui.getFont().width(name.c))/2f), currentY+mouseY+1+y, name.color);
        int ySnap = y += gui.getFont().lineHeight+4;
        int colorCut = (name.color & 0xfefefe) >> 1;
        colorCut = (colorCut & 0xfefefe) >> 1;
        colorCut = (colorCut & 0xfefefe) >> 1;
        RenderUtils.horizRect(poseStack.last().pose(), 0, mouseX+offsetX + 2, currentY+mouseY+offsetY+ySnap-2, mouseX+offsetX+max/2f, currentY+mouseY+offsetY+ySnap-1, colorCut | 255 << 24, name.color | 255 << 24);
        RenderUtils.horizRect(poseStack.last().pose(), 0, mouseX+offsetX+max/2f, currentY+mouseY+offsetY+ySnap-2, mouseX+offsetX+max-2, currentY+mouseY+offsetY+ySnap-1, name.color | 255 << 24, colorCut | 255 << 24);

        int color = name.color | 200 << 24;
        poseStack.pushPose();
        poseStack.scale(.5f, .5f, 1f);
        poseStack.translate(mouseX+offsetX, currentY+mouseY+1+y, 0);
        int yFont = 0;
        for (Component c : list) {
            gui.getFont().drawShadow(poseStack, c, (mouseX+offsetX), currentY+mouseY+1+y+yFont, color);
            yFont += gui.getFont().lineHeight+1;
        }
        poseStack.popPose();
        y += yFont/2f;
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(itemStack, mouseX+offsetX+max-15, currentY+mouseY+y+1, 0);
        y+= 16;
        gui.getFont().draw(poseStack, impact, mouseX+offsetX, currentY+mouseY+y-gui.getFont().lineHeight+3, 0);

        rectCO(poseStack, -1, -4, mouseX+offsetX, currentY+mouseY+offsetY, mouseX+max+offsetX, currentY+mouseY+y+offsetY,  (name.color & 0xfefefe) >> 1, name.color);
        rectCO(poseStack, -1, -3, mouseX+offsetX, currentY+mouseY+offsetY, mouseX+max+offsetX, currentY+mouseY+ySnap+offsetY-6, colorCut, colorCut);
        rectCO(poseStack, -1, -3, mouseX+offsetX, currentY+mouseY+offsetY+ySnap, mouseX+max+offsetX, currentY+mouseY+y+offsetY, colorCut, (name.color & 0xfefefe) >> 1);
        poseStack.popPose();
        currentY += y+12;

    }
}
