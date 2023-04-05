package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.anim.ManaAnim;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.ColorUtils;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;

import static io.sedu.mc.parties.util.AnimUtils.animPos;

public class PMana extends RenderIconTextItem implements TooltipItem {

    private final TranslatableComponent tipName = new TranslatableComponent("ui.sedparties.tooltip.mana");

    int hue = 0;

    int deadColor;

    int colorTop;
    int colorBot;
    int colorTopMissing;
    int colorBotMissing;

    int colorIncTop;
    int colorIncBot;
    int colorDecTop;
    int colorDecBot;

    int bColorTop;
    int bColorBot;
    private static Renderer renderLastDimmer;

    public PMana(String name) {
        super(name);
        renderLastDimmer = (i, id, poseStack) -> {

        };
    }

    @Override
    int getColor() {
        return colorTop;
    }

    @Override
    public String getType() {
        return "Bar";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+7, b.y+5, 0, 14, 7, bColorTop, bColorBot);
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+8, b.y+6, 0, 12, 5, colorTop, colorBot);
        setup(partyPath);
        RenderSystem.enableDepthTest();
        blit(poseStack,b.x+3, b.y+4, 9, 0, 9, 9);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack, partialTicks);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isSpectator) return;
        if (id.isDead) {
            if (iconEnabled) {
                RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorBot, bColorBot);
                RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing);
            }

            //textCentered(tX(i), tY(i), gui, poseStack, "Dead", deadColor);
            return;
        }
        if (iconEnabled) {
            renderMana(i, poseStack, id);
            if (id.mana.active)
                renderManaAnim(i, poseStack, id, partialTicks);



            //Dimmer
            RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 255 - id.alphaI << 24);
        }
        if (textEnabled)
                textCentered(tX(i), tY(i), gui, poseStack, id.mana.manaText, color);

        renderLastDimmer.render(i, id, poseStack);
    }

    public void updateRendererForMods() {
        renderLastDimmer = ((i, id, poseStack) -> {
            if (id.isBleeding || id.isDowned) {
                RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 150 << 24);
            }
        });
    }

    private void renderMana(int i, PoseStack poseStack, ClientPlayerData id) {

        float hB;
        hB = id.mana.getPercent();
        RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorTop, bColorBot);
        RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
        rectRNoA(poseStack, i, zPos, hB, colorTop, colorBot); //Mana
    }

    private void rectRNoA(PoseStack p, int i, int zLevel, float rightPosition, int startColor, int endColor ) {
        RenderUtils.sizeRectNoA(p.last().pose(), x(i)+1, y(i)+1, zLevel, Math.min(width - 1, ((width-2)*rightPosition)), height-2, startColor, endColor);
    }

    private void rectAnim(PoseStack p, int i, int zLevel, float leftPosition, float rightPosition, int startColor, int endColor ) {
        //Left Pos: x + offset + (width - offset*2)*leftPos | Right Pos: x + offset + (
        RenderUtils.rectNoA(p.last().pose(), zPos, Math.max(x(i), x(i) + (width-2)*leftPosition)+1, y(i)+1, Math.min(x(i)+width-1, x(i)+1 + (width-2)*rightPosition), y(i)+height-1, startColor, endColor);
        //Render.rectNoA(p.last().pose(), zLevel, l(i)+width*leftPosition-1, t(i)+1, l(i)-1+width*rightPosition, b(i)-1, startColor, endColor);
    }

    private void renderManaAnim(int i, PoseStack poseStack, ClientPlayerData id, float partialTicks) {
        if (id.mana.animTime - partialTicks < 10) {
            id.mana.oldH += (id.mana.curH - id.mana.oldH) * animPos(10 - id.mana.animTime, partialTicks, true, 10, 1);
        }

        if (id.mana.hInc) {
            rectAnim(poseStack, i, zPos, id.mana.oldH, id.mana.curH, colorIncTop, colorIncBot);
        } else {
            rectAnim(poseStack, i, zPos, id.mana.curH, id.mana.oldH, colorDecTop, colorDecBot);
        }
    }


    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + (width>>1);
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) + (height>>1) + 1;
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
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
        c.addSliderEntry("mtype", 0, () -> 2, ManaAnim.type);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("xtpos", 0, () -> Math.max(0, frameEleW), textX));
        entries.add(c.addSliderEntry("ytpos", 0, () -> Math.max(0, frameEleH - (int)(minecraft.font.lineHeight*scale)), textY));
        toggleTextAttach(entries);
        c.addSpaceEntry();

        c.addTitleEntry("bhue");
        c.addSliderEntry("mhue", 0, () -> 100, hue, false);
        c.addTitleEntry("bai");
        c.addColorEntry("bcit", colorIncTop);
        c.addColorEntry("bcib", colorIncBot);
        c.addTitleEntry("bad");
        c.addColorEntry("bcdt", colorDecTop);
        c.addColorEntry("bcdb", colorDecBot);

        return c;
    }


    @Override
    protected void updateValues() {
        x = Mth.clamp(x, 0, maxX());
        y = Mth.clamp(y, 0, maxY());
        width = Mth.clamp(width, 0, maxW());
        height = Mth.clamp(height, 0, maxH());
    }

    protected int maxW() {
        return (int) Math.ceil(frameEleW/scale);
    }

    protected int maxH() {
        return (int) Math.ceil(frameEleH/scale);
    }

    protected void setMainColors() {
        float hue = this.hue/100f;
        bColorTop = ColorUtils.HSBtoRGB(hue, .5f, .25f);
        bColorBot = ColorUtils.HSBtoRGB(hue, .25f, .5f);
        color = ColorUtils.HSBtoRGB(hue, .2f, 1f);
        deadColor = ColorUtils.HSBtoRGB(hue, .25f, .75f);
        colorTop = ColorUtils.HSBtoRGB(hue, .8f, .77f);
        colorBot = ColorUtils.HSBtoRGB(hue, .88f, .42f);
        colorTopMissing = ColorUtils.HSBtoRGB(hue, .97f, .27f);
        colorBotMissing = ColorUtils.HSBtoRGB(hue, .91f, .38f);
    }

    @Override
    public SmallBound setColor(int type, int data) {
        switch(type) {
            case 0 -> colorIncTop = data;
            case 1 -> colorIncBot = data;
            case 2 -> colorDecTop = data;
            case 3 -> colorDecBot = data;
        }
        return null;
    }

    @Override
    public int getColor(int type) {
        switch(type) {
            case 0 -> {return colorIncTop;}
            case 1 -> {return colorIncBot;}
            case 2 -> {return colorDecTop;}
            case 3 -> {return colorDecBot;}
        }
        return 0;
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", false, 1);
        e.addEntry("scale", 2, 2);
        e.addEntry("zpos", 0, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("xpos", 46, 12);
        e.addEntry("ypos", 29, 12);
        e.addEntry("width", 120, 12);
        e.addEntry("height", 10, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", true, 1);
        e.addEntry("mtype", 0, 4);
        e.addEntry("tattached", true, 1);
        e.addEntry("xtpos", 0, 12);
        e.addEntry("ytpos", 0, 12);
        e.addEntry("mhue", 60, 7);
        e.addEntry("bcit", 0x9ccaff, 24);
        e.addEntry("bcib", 0x6cb0ff, 24);
        e.addEntry("bcdt", 0x6790d6, 24);
        e.addEntry("bcdb", 0x1d3b6c, 24);
        return e;
    }

    protected SmallBound setMainHue(int d) {
        this.hue = d;
        setMainColors();
        return null;
    }

    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData p;
        if ((p = ClientPlayerData.getOrderedPlayer(index)).isOnline) {
            ManaAnim h = p.mana;
            renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName.getString() + (h.cur) + "/" + h.max, 0x9D7CFC, 0x310F4D, 0xFFE187);

        }
    }

    private interface Renderer {
        void render(int i, ClientPlayerData id, PoseStack poseStack);
    }

    @Override
    public boolean isEnabled() {
        return elementEnabled && ModList.get().isLoaded("ars_nouveau");
    }
}
