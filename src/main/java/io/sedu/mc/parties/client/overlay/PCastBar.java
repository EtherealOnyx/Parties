package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.anim.CastAnim;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

public class PCastBar extends RenderIconTextItem {

    protected boolean textHalfSize = true;

    public PCastBar(String name) {
        super(name);
    }



    @Override
    int getColor() {
        return 0xdd943c;
    }

    @Override
    public String getType() {
        return "";
    }

    protected int maxW() {
        return (int) Math.ceil(frameEleW/scale);
    }

    protected int maxH() {
        return (int) Math.ceil(frameEleH/scale);
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+3, b.y+5, 0, 18, 7, 0xffcc5b, 0xDDA528);
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+4, b.y+6, 0, 12, 5, 0xFFDB8A, 0xFFDB8A);
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+16, b.y+6, 0, 4, 5, 0x3F2B00, 0x3F2B00);
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("scale", 2, 2);
        e.addEntry("zpos", 0, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("xpos", 46, 12);
        e.addEntry("ypos", 29, 12);
        e.addEntry("width", 120, 12);
        e.addEntry("height", 10, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", true, 1);
        e.addEntry("tattached", true, 1);
        e.addEntry("xtpos", 0, 12);
        e.addEntry("ytpos", 0, 12);
        e.addEntry("thalfsize", true, 1);
        return e;
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h,
                                                 boolean parse) {
        ConfigOptionsList c = new ConfigOptionsList(this::getColor, s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("general");
        c.addBooleanEntry("display", elementEnabled);
        c.addSliderEntry("scale", 1, () -> 3, getScale(), true);
        c.addSliderEntry("zpos", 0, () -> 10, zPos);
        c.addTitleEntry("icon");
        c.addBooleanEntry("idisplay", iconEnabled);
        c.addSliderEntry("xpos", 0, this::maxX, this.x, true);
        c.addSliderEntry("ypos", 0, this::maxY, this.y, true);
        c.addSliderEntry("width", 1, this::maxW, width, true);
        c.addSliderEntry("height", 1, this::maxH, height, true);
        c.addTitleEntry("text");
        c.addBooleanEntry("tdisplay", textEnabled);
        c.addBooleanEntry("tshadow", textShadow);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("xtpos", 0, () -> Math.max(0, frameEleW), textX));
        entries.add(c.addSliderEntry("ytpos", 0, () -> Math.max(0, frameEleH - (int)(minecraft.font.lineHeight*scale)), textY));
        toggleTextAttach(entries);
        c.addBooleanEntry("thalfsize", textHalfSize);
        return c;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        id.getCastInfo(castAnim -> renderSpell(i, castAnim, poseStack, gui, partialTicks));
    }

    @Override
    void renderSelf(ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        id.getCastInfo(castAnim -> renderSpell(0, castAnim, poseStack, gui, partialTicks));
    }

    void renderSpell(int index, CastAnim castAnim, PoseStack poseStack, ForgeIngameGui gui, float partialTicks) {
        if (castAnim.active) {
            castAnim.getSpell(partialTicks, (spell, percent) -> {
                RenderUtils.offRectNoA(poseStack.last().pose(), x(index),y(index), zPos, 0, width,height, spell.school.bgTop, spell.school.bgBottom);
                RenderUtils.offRectNoA(poseStack.last().pose(), x(index),y(index), zPos, 1, width,height, spell.school.bgInner, spell.school.bgInner);
                RenderUtils.offRectNoA(poseStack.last().pose(), x(index),y(index), zPos, 1, width*percent,height, spell.school.bgTextColor, spell.school.bgTextColor);

                int xOffset = 0;
                if (iconEnabled) {
                    xOffset = 9;
                    resetColor();
                    setupSpell(spell.location);
                    GuiComponent.blit(poseStack, tX(index), tY(index)+2, 0, 0, 8, 8, 8, 8);
                    resetColor();
                }

                if (textEnabled) {
                    if (textHalfSize) {
                        poseStack.pushPose();
                        poseStack.scale(.5f,.5f,1f);
                        text(((tX(index)+xOffset)<<1), (tY(index)<<1) + 9, gui, poseStack, spell.name, spell.school.bgTextColor);
                        poseStack.popPose();
                    } else {
                        text(tX(index) + xOffset, tY(index)+2, gui, poseStack, spell.name, spell.school.bgTextColor);
                    }
                }

            });
        }
    }

    static void setupSpell(ResourceLocation location) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, location);
    }

    //TODO: remove color param

    void text(int x, int y, ForgeIngameGui gui, PoseStack p, MutableComponent s, int color) {
        p.translate(0,0,zPos);
        if (textShadow) {
            gui.getFont().draw(p, s, x+1, y+1, 0);
            gui.getFont().draw(p, s, x+1, y+1, color | 75 << 24);
            gui.getFont().draw(p, s, x, y, color);
            p.translate(0,0,-zPos);
            return;
        }
        gui.getFont().draw(p, s, x, y, color);
        p.translate(0,0,-zPos);
    }

    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset);
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset)-10;
    }


    protected void rectAnim(PoseStack p, int i, float rightPosition, int startColor, int endColor ) {
        //Left Pos: x + offset + (width - offset*2)*leftPos | Right Pos: x + offset + (
        RenderUtils.rectNoA(p.last().pose(), zPos, Math.max(x(i), x(i) + (width-2))+1, y(i)+1, Math.min(x(i)+width-1, x(i)+1 + (width-2)*rightPosition), y(i)+height-1, startColor, endColor);
        //Render.rectNoA(p.last().pose(), zLevel, l(i)+width*leftPosition-1, t(i)+1, l(i)-1+width*rightPosition, b(i)-1, startColor, endColor);
    }
}
