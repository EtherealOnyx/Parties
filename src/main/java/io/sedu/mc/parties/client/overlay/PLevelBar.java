package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.*;

public class PLevelBar extends RenderIconTextItem {


    public PLevelBar(String name, int x, int y, int width, int height, int textColor) {
        super(name, x, y, width, height, textColor, true);
    }



    @Override
    int getColor() {
        return 0x7efc20;
    }

    @Override
    public String getType() {
        return "Bar";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        assert Minecraft.getInstance().player != null;
        float bar = Minecraft.getInstance().player.experienceProgress;
        setup(Gui.GUI_ICONS_LOCATION);
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        blit(poseStack, b.x+2, b.y+10, 0, 64, 14, 5);
        blit(poseStack, b.x+16, b.y+10, 168, 64, 14, 5);
        int w = (int) (28*bar);
        if (w > 14) {
            blit(poseStack, b.x+2, b.y+10, 0, 69, 14, 5);
            blit(poseStack, b.x+16, b.y+10, 168, 69, w-14, 5);
        } else {
            blit(poseStack, b.x+2,b.y+10, 0, 69, w, 5);
        }
        if (w > 14) {
            blit(poseStack, b.x+2, b.y+10, 0, 69, 14, 5);
            blit(poseStack, b.x+16, b.y+10, 168, 69, w-14, 5);
        } else {
            blit(poseStack, b.x+2,b.y+10, 0, 69, w, 5);
        }
        String level = String.valueOf(Minecraft.getInstance().player.experienceLevel);
        int x = b.x + 16 - (gui.getFont().width(level)>>1);
        int y = b.y + 9;
        poseStack.translate(0,0,zPos);
        gui.getFont().draw(poseStack, level, (float)(x + 1), y, 0);
        gui.getFont().draw(poseStack, level, (float)(x - 1), (float)y, 0);
        gui.getFont().draw(poseStack, level, (float)x, (float)(y + 1), 0);
        gui.getFont().draw(poseStack, level, (float)x, (float)(y - 1), 0);
        gui.getFont().draw(poseStack, level, (float)x, (float)y, 8453920);
        poseStack.translate(0,0,-zPos);

    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline) {
            renderBar(i, poseStack, id.getXpBar(), id.getXpLevel(), gui);
        }
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        //useAlpha(id.alpha);
        renderBar(i, poseStack, id.getXpBarForced(), id.getLevelForced(), gui);
    }

    void renderBar(int i, PoseStack poseStack, float bar, int level, ForgeIngameGui gui) {
        if (iconEnabled) {
            setup(Gui.GUI_ICONS_LOCATION);
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            RenderSystem.enableDepthTest();
            //this.blit(poseStack, pXPos, l, 0, 64, 182, 5);
            blit(poseStack, x(i), y(i), 0, 64, width>>1, height);
            blit(poseStack, x(i)+(width>>1), y(i), 182-(width>>1), 64, width>>1, height);
            int w = (int) (width*bar);

            if (w > width>>1) {
                blit(poseStack, x(i), y(i), 0, 69, width>>1, height);
                blit(poseStack, x(i)+(width>>1), y(i), 182-(width>>1), 69, w-(width>>1), height);
            } else {
                blit(poseStack, x(i), y(i), 0, 69, w, height);
            }
        }
        if (textEnabled)
            renderText(gui, poseStack, String.valueOf(level), tX(i) - (gui.getFont().width(String.valueOf(level))>>1), tY(i), bar);
    }

    private void renderText(ForgeIngameGui g, PoseStack poseStack, String s, int x, int y, float level) {
        poseStack.translate(0,0,zPos);
        if (textShadow) {
            g.getFont().draw(poseStack, s, (float)(x + 1), (float)y, 0);
            g.getFont().draw(poseStack, s, (float)(x - 1), (float)y, 0);
            g.getFont().draw(poseStack, s, (float)x, (float)(y + 1), 0);
            g.getFont().draw(poseStack, s, (float)x, (float)(y - 1), 0);
        }
        g.getFont().draw(poseStack, s, (float)x, (float)y, 8453920);
        poseStack.translate(0,0,-zPos);
        if (notEditing() && withinBounds(x, y, g.getFont().width(s), g.getFont().lineHeight, 2, scale)) {
            renderXpTooltip(poseStack, g, 10, 0, level);
        }
    }

    protected void renderXpTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, float level) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 100);
        rectCO(poseStack, 0, -3, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+offsetX+182, currentY+mouseY()+5+offsetY, 0x8ec265, 0x385e1a);
        rectCO(poseStack, 0, -2, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+offsetX+182, currentY+mouseY()+5+offsetY, 0x140514, 0x140514);
        setup(Gui.GUI_ICONS_LOCATION);
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        blit(poseStack, mouseX()+offsetX, currentY+mouseY()+offsetY, 0, 64, 182, 5);
        blit(poseStack, mouseX()+offsetX, currentY+mouseY()+offsetY, 0, 69, (int) (182*level), 5);
        poseStack.popPose();
        currentY += gui.getFont().lineHeight+offsetY+8;
    }

    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + (width>>1);
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) - 1;
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h);
        c.addTitleEntry("config.sedparties.title.general");
        c.addBooleanEntry("config.sedparties.name.display", isEnabled());
        c.addSliderEntry("config.sedparties.name.scale", 1, () -> 3, getScale(), true);
        c.addSliderEntry("config.sedparties.name.zpos", 0, () -> 10, zPos);

        c.addTitleEntry("config.sedparties.title.icon");
        c.addBooleanEntry("config.sedparties.name.idisplay", iconEnabled);
        c.addSliderEntry("config.sedparties.name.xpos", 0, () -> Math.max(0, Math.max(clickArea.r(0), frameX + frameW) - frameX - (int)(width*scale)), this.x, true);
        c.addSliderEntry("config.sedparties.name.ypos", 0, () -> Math.max(0, Math.max(clickArea.b(0), frameY + frameH) - frameY - (int)(height*scale)), this.y, true);
        c.addSliderEntry("config.sedparties.name.width", 1, () -> (int) Math.ceil((Math.max(clickArea.x + clickArea.w(), frameW) - x)/scale), width, true);

        c.addTitleEntry("config.sedparties.title.text");
        c.addBooleanEntry("config.sedparties.name.tdisplay", textEnabled);
        c.addBooleanEntry("config.sedparties.name.tshadow", textShadow);
        c.addColorEntry("config.sedparties.name.tcolor", color);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("config.sedparties.name.tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("config.sedparties.name.xtpos", 0, () -> Math.max(0, Math.max(clickArea.r(0), frameX + frameW) - frameX), textX));
        entries.add(c.addSliderEntry("config.sedparties.name.ytpos", 0, () -> Math.max(0, Math.max(clickArea.b(0), frameY + frameH) - frameY - (int)(minecraft.font.lineHeight*scale)), textY));
        toggleTextAttach(entries);
        return c;
    }

    //TODO: Modify getConfigOptions to send a variable too.
    //TODO: When true, add only refresh sliders to config options.
    //TODO: Grab sliders when in general config tab.
    //TODO: Call the update on sliders when width/height changes.


}
