package io.sedu.mc.parties.client.overlay;

import Util.Render;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.overlay.anim.DimAnim;
import io.sedu.mc.parties.client.overlay.anim.HealthAnim;
import io.sedu.mc.parties.client.overlay.effects.EffectHolder;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.client.overlay.gui.TabButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.mouseX;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.mouseY;
import static io.sedu.mc.parties.client.overlay.gui.SettingsScreen.INNER_LOC;
import static net.minecraftforge.client.gui.ForgeIngameGui.HOTBAR_ELEMENT;

public abstract class RenderItem {

    //TODO: Go through each poseStack.scale() method and ensure the last element is 1f, not zero or zPos.

    public static RenderItem clickArea;

    public static final LinkedHashMap<String, RenderItem> items = new LinkedHashMap<>();
    public static ArrayList<String> parser = new ArrayList<>();
    static final ResourceLocation partyPath = new ResourceLocation(Parties.MODID, "textures/partyicons.png");
    static final ResourceLocation TAB_LOC = new ResourceLocation("textures/block/glass.png");

    public static int frameX = 16;
    public static int frameY = 16;
    public static int frameH = 56;
    public static int frameW = 0;

    public static int currentY = 0;

    int x, y, width, height;
    float scale = 1f;
    float scalePos = 0f;
    int zPos = 0;
    //TODO: Allow alpha changes in config per item?
    float alpha;
    boolean textShadow = true;


    public static void resetPos() {
        currentY = 0;
    }

    void setDefaults(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void setDefaults(int x, int y, int width, int height) {
        setDefaults(x, y);
        this.width = width;
        this.height = height;
    }

    int hOffset(int pOffset) {
        return pOffset*frameH;
    }

    int wOffset(int pOffset) {
        return pOffset*frameW;
    }

    public int x(int pOffset) {
        return (int) ((frameX + x + wOffset(pOffset))/scale);
    }

    public int y(int pOffset) {
        return (int) ((frameY + y + hOffset(pOffset))/scale);
    }

    public int xNormal(int pOffset) {
        return frameX + x + wOffset(pOffset);
    }

    public int yNormal(int pOffset) {
        return frameY + y + hOffset(pOffset);
    }

    public int l(int pOffset) {
        return x+frameX + wOffset(pOffset);
    }

    public int r(int pOffset) {
        return x+frameX  + width + wOffset(pOffset);
    }

    public int t(int pOffset) {
        return y+frameY + hOffset(pOffset);
    }

    public int b(int pOffset) {
        return y+frameY + height + hOffset(pOffset);
    }

    abstract void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks);

    String name;
    IIngameOverlay item;

    public void initItem() {
        item = (gui, poseStack, partialTicks, width, height) -> {
            for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++) {
                itemStart(poseStack);
                renderMember(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack, partialTicks);
                itemEnd(poseStack);
            }
        };
    }

    protected void itemStart(PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1);
        poseStack.translate(0,0,zPos);
    }

    protected void itemEnd(PoseStack poseStack) {
        poseStack.popPose();
    }

    protected void tooltipStart(PoseStack poseStack) {
        poseStack.scale(1/scale, 1/scale, 1);
    }

    protected void tooltipEnd(PoseStack poseStack) {
        poseStack.scale(scale, scale, 1);
    }

    public boolean isTabRendered() {
        return true;
    }

    public String translateName() {
        return "gui.sedparties.name." + name;
    }


    public RenderItem(String name) {
        this.name = name;
    }

    public RenderItem(String name, int x, int y) {
        this.name = name;
        setDefaults(x, y);
    }

    public RenderItem(String name, int x, int y, int width, int height) {
        this.name = name;
        setDefaults(x, y, width, height);
    }

    public void register() {
        initItem();
        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, name, item);
    }


    public RenderItem setEnabled(boolean enabled) {
        OverlayRegistry.enableOverlay(item, enabled);
        return this;
    }


    private static void setup(float alpha) {
        setup(alpha, Gui.GUI_ICONS_LOCATION);
    }

    static void setup(float alpha, ResourceLocation loc) {
        setup(loc);
        setColor(1f, 1f, 1f, alpha);
    }
    static void setup(ResourceLocation loc) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, loc);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    void blit(PoseStack p, int x, int y, int u, int v, int w, int h) {
        RenderSystem.enableDepthTest();
        GuiUtils.drawTexturedModalRect(p, x, y, u, v, w, h, zPos);
    }


    static void setColor(float r, float g, float b, float a) {
        RenderSystem.setShaderColor(r, g, b, a);
    }

    void rect(int i, PoseStack pose, int z, int offset, int startColor, int endColor, int alpha) {
        Render.rect(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor | (alpha << 24), endColor | (alpha << 24));
    }

    void rect(int i, PoseStack pose, int z, int offset, int startColor, int endColor) {
        Render.rect(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor, endColor);
    }
    void rectNoA(int i, PoseStack pose, int z, int offset, int startColor, int endColor) {
        Render.rectNoA(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor, endColor);
    }

    void rect(int i, PoseStack pose, int z, int offset, int startColor) {
        Render.rect(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor);
    }

    public static void rectCO(PoseStack pose, int z, int offset, int l, int t, int r, int b, int startColor, int endColor) {
        Render.rectNoA(pose.last().pose(), z, l+offset, t+offset, r-offset, b-offset, startColor, endColor);
    }

    void renderTypeText(PoseStack p, ForgeIngameGui gui, Component type, int x, int y) {
        gui.getFont().drawShadow(p, type, x+16-(gui.getFont().width(type)>>1), y+22, getColor());
    }

    void renderTab(PoseStack p, TabButton b) {
        RenderSystem.enableDepthTest();
        Render.setColor(getColor());
        Render.renderBg(b.x, b.y, b.x+32, b.y+32, 32, 32, 150, TAB_LOC);
        Render.sizeRect(p.last().pose(), b.x, b.y, 0, b.getWidth(), b.getHeight(), 0x44FFFFFF, 0x88000000);
        resetColor();
        Render.borderRect(p.last().pose(), -1, 1, b.x, b.y, b.getWidth(), b.getHeight(), getColor() | 100 << 24,getColor() | 100 << 24);

    }


    void renderTabHover(PoseStack p, TabButton b) {
        RenderSystem.enableDepthTest();
        Render.setColor(getColor());
        Render.renderBg(b.x, b.y, b.x+32, b.y+32, 32, 32, 255, TAB_LOC);
        Render.sizeRect(p.last().pose(), b.x, b.y, 0, b.getWidth(), b.getHeight(), 0x66FFFFFF, 0x22FFFFFF);
        resetColor();
        Render.borderRect(p.last().pose(), -1, 1, b.x, b.y, b.getWidth(), b.getHeight(), getColor() | 200 << 24, getColor());
    }

    void renderTabClicked(PoseStack p, TabButton b) {
        RenderSystem.enableDepthTest();
        Render.renderBg(b.x, b.y, b.x+32, b.y+32, 32, 32, 110, INNER_LOC);
        Render.setColor(getColor());
        Render.sizeRect(p.last().pose(), b.x, b.y, 0, b.getWidth(), b.getHeight(), 0x77FFFFFF, 0x00FFFFFF);
        resetColor();
        p.translate(0,0,5);
        Render.borderRectNoBottom(p.last().pose(), -1, 2, b.x, b.y, b.getWidth(), b.getHeight(), getColor() | 255 << 24, getColor() | 150 << 24);
        p.translate(0,0,-5);
    }

    abstract int getColor();

    void text(int i, ForgeIngameGui gui, PoseStack p, String text, int color) {
        p.translate(0,0,.5);
        if (textShadow) {
            textS(gui,p,text,x(i), y(i),color);
            p.translate(0,0,-.5);
            return;
        }
        text(gui,p,text,x(i), y(i),color);
        p.translate(0,0,-.5);
    }

    void text(ForgeIngameGui gui, PoseStack p, String s, int x, int y, int color) {
        p.translate(0,0,zPos);
        if (textShadow) {
            textS(gui,p,s,x,y,color);
            p.translate(0,0,-zPos);
            return;
        }
        gui.getFont().draw(p, s, x, y, color);
        p.translate(0,0,-zPos);
    }

    void textS(ForgeIngameGui gui, PoseStack p, String text, int x, int y, int color) {
        gui.getFont().draw(p, text, x+1, y+1, 0);
        gui.getFont().draw(p, text, x+1, y+1, color | 75 << 24);
        gui.getFont().draw(p, text, x, y, color);
    }

    void textCentered(int i, int x, int y, ForgeIngameGui gui, PoseStack p, String text, int color) {
        text(gui, p, text, (int) (x - (gui.getFont().width(text)/2f)), (int) (y - (gui.getFont().lineHeight/2f)), color);
    }

    static void useAlpha(float alpha) {
        setColor(1f,1f,1f,alpha);
    }

    static void resetColor() {
        setColor(1f,1f,1f,1f);
    }

    static float animPos(int currTick, float partialTicks, boolean countingUp, int animLength, float scaleFactor) {
        return (float) (countingUp ? Math.pow((currTick+partialTicks)/animLength, scaleFactor) : Math.pow((currTick-partialTicks)/animLength, scaleFactor));
    }

    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, MutableComponent text, int outStart, int outEnd, int inStart, int inEnd, int textColor) {
        tooltipStart(poseStack);
        //gui.setBlitOffset(400);
        //poseStack.pushPose();
        poseStack.translate(0, 0, 100);
        rectCO(poseStack, 0, -3, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+gui.getFont().width(text)+offsetX, currentY+mouseY()+(gui.getFont().lineHeight)+offsetY, outStart, outEnd);
        rectCO(poseStack, 0, -2, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+gui.getFont().width(text)+offsetX, currentY+mouseY()+(gui.getFont().lineHeight)+offsetY, inStart, inEnd);
        gui.getFont().drawShadow(poseStack, text, mouseX()+offsetX, currentY+mouseY()+1, textColor);
        poseStack.translate(0,0,-100);
        tooltipEnd(poseStack);
        //poseStack.popPose();

        currentY += gui.getFont().lineHeight+offsetY+8;

    }

    public abstract String getType();



    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, String text, int outStart, int outEnd, int inStart, int inEnd, int textColor) {
        renderTooltip(poseStack, gui, offsetX, offsetY, new TextComponent(text), outStart, outEnd, inStart, inEnd, textColor);
    }

    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, String text, int outStart, int outEnd, int textColor) {
        renderTooltip(poseStack, gui, offsetX, offsetY, new TextComponent(text), outStart, outEnd, 0x140514, 0x140514, textColor);
    }

    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, MutableComponent text, int outStart, int outEnd, int textColor) {
        renderTooltip(poseStack, gui, offsetX, offsetY, text, outStart, outEnd, 0x140514, 0x140514, textColor);
    }

    protected void renderGroupEffectTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, List<ColorComponent> text, int outStart, int outEnd, int inStart, int inEnd) {
        poseStack.pushPose();
        tooltipStart(poseStack);
        int max = 0;
        int y = offsetY;
        poseStack.translate(0,0,100);
        for (ColorComponent c : text) {
            gui.getFont().drawShadow(poseStack, c.c, mouseX()+offsetX, currentY+mouseY()+1+y, c.color);
            max = Math.max(max, gui.getFont().width(c.c));
            y += gui.getFont().lineHeight+1;
        }
        rectCO(poseStack, -1, -3, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+max+offsetX, currentY+mouseY()+y+offsetY, outStart, outEnd);
        rectCO(poseStack, -1, -2, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+max+offsetX, currentY+mouseY()+y+offsetY, inStart, inEnd);
        poseStack.popPose();
        currentY += y+8;
    }

    protected void renderSingleEffectTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, List<ColorComponent> text, int color) {
        poseStack.pushPose();
        tooltipStart(poseStack);
        poseStack.translate(0, 0, 100);
        int max = 0;
        int y = 0;

        ColorComponent c = text.get(0);
        gui.getFont().drawShadow(poseStack, c.c, mouseX()+offsetX, currentY+mouseY()+1, c.color);
        max = Math.max(max, gui.getFont().width(c.c));
        y += (gui.getFont().lineHeight)+offsetY+4;

        c = text.get(1);
        max = Math.max(max, gui.getFont().width(c.c));
        gui.getFont().drawShadow(poseStack, c.c, (mouseX()+offsetX)+((max-gui.getFont().width(c.c))>>1), (currentY+mouseY()+1+y), c.color);

        y += gui.getFont().lineHeight;
        rectCO(poseStack, -1, -3, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+max+offsetX, currentY+mouseY()+y+offsetY, (color & 0xfefefe) >> 1, color);
        rectCO(poseStack, -1, -2, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+max+offsetX, currentY+mouseY()+y+offsetY, 0x140514, (color & 0xfefefe) >> 1);
        poseStack.popPose();
        currentY += y+7;
    }

    public int w() {
        return width;
    }

    public int h() {
        return height;
    }

    public TabButton.Action render(ForgeIngameGui gui) {
        return new TabButton.Action() {

            @Override
            public void onRender(PoseStack p, TabButton b) {
                renderTab(p, b);
                renderTypeText(p, gui, b.type, b.x, b.y);
                renderElement(p, gui, b);
            }

            @Override
            public void onHover(PoseStack p, TabButton b) {
                renderTabHover(p, b);
                renderTypeText(p, gui, b.type, b.x, b.y);
                renderElement(p, gui, b);
            }

            @Override
            public void onSelect(PoseStack p, TabButton b) {
                renderTabClicked(p, b);
                renderTypeText(p, gui, b.type, b.x, b.y);
                renderElement(p, gui, b);
            }

            @Override
            public ConfigOptionsList getOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h) {
                return getConfigOptions(s, minecraft, x, y, w, h, false);
            }
        };
    }

    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        return new ConfigOptionsList(this::getColor, s, minecraft, x, y, w, h, parse);
    }


    abstract void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b);

    protected boolean isEnabled() {
        return OverlayRegistry.getEntry(this.item).isEnabled();
    }

    public void changeVisibility(boolean data) {
        OverlayRegistry.enableOverlay(this.item, data);
    }

    public void setXPos(int data) {
        this.x = data;
    }

    public void setYPos(int data) {
        this.y = data;
    }

    public void setZPos(int data) {
        this.zPos = data;
    }


    public void setScale(int data) {
        switch (data) {
            case 1 -> {
                scale = 0.5f;
                scalePos = 1;
            }
            case 2 -> {
                scale = 1f;
                scalePos = 0;
            }
            case 3 -> {
                scale = 2f;
                scalePos = -0.5f;
            }
        }
    }

    public int getScale() {
        if (scale == 0.5f) return 1;
        if (scale == 1f) return 2;
        if (scale == 2f) return 3;
        return -1;
    }

    public void setColor(int type, int data) {
    }

    public void setTextShadow(boolean data) {
        this.textShadow = data;
    }

    public void setMaxTextSize(int data) {
    }

    public void toggleIcon(boolean data) {
    }

    public void toggleText(boolean data) {
    }

    public void toggleTextAttach(boolean data) {

    }
    public void setXTextPos(Integer data) {
    }

    public void setYTextPos(Integer data) {
    }

    private void setWidth(Integer d) {
        this.width = d;

    }

    private void setHeight(Integer d) {
        this.height = d;
    }

    protected void updateValues() {
        x = Mth.clamp(x, 0, maxX());
        y = Mth.clamp(y, 0, maxY());
        //width = Mth.clamp(width, 0, maxW());
        //height = Mth.clamp(width, 0, maxH());
    }

    protected int maxX() {
        return Math.max(0, Math.max(clickArea.r(0), frameX + frameW) - frameX - (int)(width*scale));
    }

    protected int maxY() {
        return Math.max(0, Math.max(clickArea.b(0), frameY + frameH) - frameY - (int)(height*scale));
    }


    static class ColorComponent {
        static final ColorComponent EMPTY = new ColorComponent(new TextComponent(""), 0);
        MutableComponent c;
        int color;

        ColorComponent(MutableComponent c, int color) {
            this.color = color;
            this.c = c;
        }
    }

    void addDisplaySettings(ConfigOptionsList c) {
        c.addTitleEntry("display");
        c.addBooleanEntry("display", isEnabled());
    }

    void addPositionalSettings(ConfigOptionsList c, boolean bStandardPos, boolean bZPos, boolean bScale) {
        c.addTitleEntry("position");
        if (bStandardPos) {
            c.addSliderEntry("xpos", 0, this::maxX, this.x);
            c.addSliderEntry("ypos", 0, this::maxY, this.y);
        }
        if (bZPos)
            c.addSliderEntry("zpos", 0, () -> 10, zPos);

        if (bScale)
            c.addSliderEntry("scale", 1, () -> 3, getScale(), true);
    }


    public static void initUpdater(HashMap<String, Update> updater) {
        //Make this be per item instead.
        updater.put("display", (n, d) -> items.get(n).changeVisibility((Boolean) d));
        updater.put("tshadow", (n, d) -> items.get(n).setTextShadow((Boolean) d));
        updater.put("idisplay", (n, d) -> items.get(n).toggleIcon((Boolean) d));
        updater.put("tdisplay", (n, d) -> items.get(n).toggleText((Boolean) d));
        updater.put("bgdisplay", (n, d) -> items.get(n).toggleIcon((Boolean) d));
        updater.put("tattached", (n, d) -> items.get(n).toggleTextAttach((Boolean) d));
        updater.put("xpos", (n, d) -> items.get(n).setXPos((int) d));
        updater.put("ypos", (n, d) -> items.get(n).setYPos((int) d));
        updater.put("scale", (n, d) -> items.get(n).setScale((int) d));
        updater.put("zpos", (n, d) -> items.get(n).setZPos((int) d));
        updater.put("xtpos", (n, d) -> items.get(n).setXTextPos((int) d));
        updater.put("ytpos", (n, d) -> items.get(n).setYTextPos((int) d));
        updater.put("tmax", (n, d) -> items.get(n).setMaxTextSize((int) d));
        updater.put("width", (n, d) -> items.get(n).setWidth((int)d));
        updater.put("height", (n, d) -> items.get(n).setHeight((int)d));
        updater.put("ttype", (n,d) -> HealthAnim.setTextType((int)d));
        updater.put("tcolor", (n, d) -> items.get(n).setColor(0, (int)d));
        updater.put("tcabsorb", (n, d) -> items.get(n).setColor(1, (int)d));
        updater.put("tcdead", (n, d) -> items.get(n).setColor(2, (int)d));

        updater.put("bbct", (n, d) -> items.get(n).setColor(3, (int)d));
        updater.put("bbcb", (n, d) -> items.get(n).setColor(4, (int)d));

        updater.put("bbact", (n, d) -> items.get(n).setColor(5, (int)d));
        updater.put("bbacb", (n, d) -> items.get(n).setColor(6, (int)d));


        updater.put("bct", (n, d) -> items.get(n).setColor(7, (int)d));
        updater.put("bcb", (n, d) -> items.get(n).setColor(8, (int)d));

        updater.put("bctm", (n, d) -> items.get(n).setColor(9, (int)d));
        updater.put("bcbm", (n, d) -> items.get(n).setColor(10, (int)d));

        updater.put("bcta", (n, d) -> items.get(n).setColor(11, (int)d));
        updater.put("bcba", (n, d) -> items.get(n).setColor(12, (int)d));

        updater.put("bcat", (n, d) -> items.get(n).setColor(13, (int)d));
        updater.put("bcab", (n, d) -> items.get(n).setColor(14, (int)d));

        updater.put("bcit", (n, d) -> items.get(n).setColor(15, (int)d));
        updater.put("bcib", (n, d) -> items.get(n).setColor(16, (int)d));

        updater.put("bcdt", (n, d) -> items.get(n).setColor(17, (int)d));
        updater.put("bcdb", (n, d) -> items.get(n).setColor(18, (int)d));

        updater.put("buffg", (n, d) -> items.get(n).setColor(0, (int)d));
        updater.put("buffb", (n, d) -> items.get(n).setColor(1, (int)d));
        updater.put("flash", (n, d) -> items.get(n).setColor(2, (int)d));

        updater.put("blim", (n,d) -> EffectHolder.updatebLim((int) d));
        updater.put("dlim", (n,d) -> EffectHolder.updatedLim((int) d));
        updater.put("dfirst", (n,d) -> {EffectHolder.debuffFirst = (boolean) d; ClientPlayerData.markEffectsDirty();});
        updater.put("bsep", (n,d) -> {EffectHolder.prioDur = (boolean) d; ClientPlayerData.markEffectsDirty();});


        updater.put("spacex", (n, d) -> items.get(n).setWidth((int)d));
        updater.put("spacey", (n, d) -> items.get(n).setHeight((int)d));
        updater.put("bsize", (n,d) -> ((PEffects) items.get(n)).setBorderSize((int) d));
        updater.put("rowmax", (n,d) -> ((PEffects) items.get(n)).setMaxPerRow((int) d));
        updater.put("totalmax", (n,d) -> {((PEffects) items.get(n)).setMaxSize((int) d);  ClientPlayerData.markEffectsDirty();});

        updater.put("danim", (n,d) -> DimAnim.animActive = (boolean)d);
        updater.put("gen_x", (n,d) -> frameX = (int) d);
        updater.put("gen_y", (n,d) -> frameY = (int) d);
        updater.put("gen_w", (n,d) -> frameW = (int) d);
        updater.put("gen_h", (n,d) -> frameH = (int) d);
        updater.put("genc_w", (n,d) -> clickArea.width = (int) d);
        updater.put("genc_h", (n,d) -> clickArea.height = (int) d);
        updater.put("genc_x", (n,d) -> clickArea.x = (int) d);
        updater.put("genc_y", (n,d) -> clickArea.y = (int) d);

        //TODO: implement maxX, maxY, maxWidth, maxHeight function base renderItem. reference that when in general width/height tab instead of iterating over getconfigoptions.
    }


    public interface Update {
        void onUpdate(String name, Object data);
    }


}
