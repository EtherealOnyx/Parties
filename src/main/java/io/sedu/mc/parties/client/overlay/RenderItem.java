package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.anim.DimAnim;
import io.sedu.mc.parties.client.overlay.effects.EffectHolder;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.client.overlay.gui.TabButton;
import io.sedu.mc.parties.data.ClientConfigData;
import io.sedu.mc.parties.util.RenderUtils;
import io.sedu.mc.parties.util.TriConsumer;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.minecraftforge.client.gui.ForgeIngameGui.HOTBAR_ELEMENT;

public abstract class RenderItem {


    public static RenderItem clickArea;

    public static final LinkedHashMap<String, RenderItem> items = new LinkedHashMap<>();
    private static final List<RenderSelfItem> selfItems = new ArrayList<>();
    private static final List<RenderItem> memberItems = new ArrayList<>();
    private static final List<RenderItem> tooltipItems = new ArrayList<>();
    public static ArrayList<String> parser = new ArrayList<>();
    static final ResourceLocation partyPath = new ResourceLocation(Parties.MODID, "textures/partyicons.png");

    public static int selfFrameX = 16;
    public static int selfFrameY = 16;
    public static int otherFrameX = 16;
    public static int otherFrameY = 128;
    public static int framePosW = 0;
    public static int framePosH = 0;
    public static int frameEleH = 56;
    public static int frameEleW = 0;
    public static int currentY = 0;
    public static boolean renderSelfFrame = true;

    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean elementEnabled = false;
    public static boolean isDirty = false;

    float scale = 1f;
    float scalePos = 0f;
    int zPos = 0;
    boolean textShadow = true;
    boolean textEnabled;
    boolean iconEnabled;
    private static ItemRender itemRender;

    public static void checkTooltip(int posX, int posY, Consumer<TooltipItem> action) {
        tooltipItems.forEach(t -> {
            if (t.isInBound(posX, posY) && t.isEnabled())
                action.accept((TooltipItem) t);
        });
    }

    public static void syncItems() {
        selfItems.clear();
        memberItems.clear();
        tooltipItems.clear();
        items.forEach((name, item) -> {
            if (item.isEnabled()) {
                if (item instanceof RenderSelfItem selfItem) {
                    selfItems.add(selfItem);
                } else {
                    memberItems.add(item);
                }
                if (item instanceof TooltipItem) {
                    tooltipItems.add(item);
                }
            }
        });
    }

    public static void updateSelfRender() {
        if (ClientConfigData.renderSelfFrame.get()) {
            Parties.LOGGER.debug("Enabling self rendering in settings...");
            renderSelfFrame = true;
            itemRender = (gui, poseStack, partialTicks) -> {
                ClientPlayerData.forSelf((id) -> {
                    poseStack.pushPose();
                    poseStack.scale(playerScale, playerScale, 1f);
                    for (RenderSelfItem item : selfItems) {
                        item.itemStart(poseStack);
                        item.renderSelf(id, gui, poseStack, partialTicks);
                        item.itemEnd(poseStack);
                    }
                    for (RenderItem item : memberItems) {
                        item.itemStart(poseStack);
                        item.renderMember(0, id, gui, poseStack, partialTicks);
                        item.itemEnd(poseStack);
                    }
                    poseStack.popPose();
                });
                //TODO: Check if this is fine to have outside.
                poseStack.pushPose();
                poseStack.scale(partyScale, partyScale, 1f);
                ClientPlayerData.forOthersOrdered((i, id) -> {
                    //Render other players.
                    for (RenderSelfItem item : selfItems) {
                        item.itemStart(poseStack);
                        item.renderMember(i, id, gui, poseStack, partialTicks);
                        item.itemEnd(poseStack);
                    }
                    for (RenderItem item : memberItems) {
                        item.itemStart(poseStack);
                        item.renderMember(i, id, gui, poseStack, partialTicks);
                        item.itemEnd(poseStack);
                    }
                });
                poseStack.popPose();
            };
        }
    }

    public static void updateFramePos() {
        Window w = Minecraft.getInstance().getWindow();
        //TODO: include other frame.
        selfFrameX = Math.min(ClientConfigData.xPos.get(), w.getScreenWidth() - frameEleW);
        selfFrameY = Math.min(ClientConfigData.yPos.get(), w.getScreenHeight() - frameEleH);
    }

    public boolean isEnabled() {
        return elementEnabled;
    }

    boolean isInBound(int mouseX, int mouseY) {
        return mouseX > x - 2 && mouseY > y - 2
                && mouseX < x + 2 + width*scale && mouseY < y + 2 + height*scale;
    }

    public static class ItemBound {

        int x;
        int y;
        int width;
        int height;

        public ItemBound(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

    }

    public static class SmallBound {
        int type;
        int value;
        public SmallBound(int type, int value) {
            this.type = type;
            this.value = value;
        }

        public void update(BiConsumer<Integer, Integer> action) {
            action.accept(type, value);
        }
    }




    public SmallBound toggleIcon(boolean data) {
        iconEnabled = data;
        return null;
    }
    public SmallBound toggleText(boolean data) {
        textEnabled = data;
        return null;
    }

    //TODO: For all frameX and frameY references, check if index == 0 ? frameX : otherFrameX, where otherFrameX is party member frame X position.

    public static void resetPos() {
        currentY = 0;
    }

    //abstract void resetElement();

    int hOffset(int pOffset) {
        return pOffset == 0 ? 0 : (pOffset-1)*framePosH;
    }

    int wOffset(int pOffset) {
        return pOffset == 0 ? 0 : (pOffset-1)*framePosW;
    }

    public int x(int pOffset) {
        return (int) ((pOffset == 0 ? selfFrameX + x : otherFrameX + x + framePosW*(pOffset-1))/scale);
    }

    public int y(int pOffset) {
        return (int) ((pOffset == 0 ? selfFrameY + y: otherFrameY + y + framePosH*(pOffset-1))/scale);
    }


    public int xNormal(int pOffset) {
        return pOffset == 0 ? selfFrameX + x: otherFrameX + x + framePosW*(pOffset-1);
    }

    public int yNormal(int pOffset) {
        return pOffset == 0 ? selfFrameY + y: otherFrameY + y + framePosH*(pOffset-1);
    }

    public int l(int pOffset) {
        return pOffset == 0 ? x + selfFrameX: x + otherFrameX + framePosW*(pOffset - 1);
    }

    public int r(int pOffset) {
        return pOffset == 0 ? x + selfFrameX + width : x + otherFrameX + framePosW*(pOffset - 1) + width;
    }

    public int t(int pOffset) {
        return pOffset == 0 ? y + selfFrameY : y + otherFrameY + framePosH*(pOffset - 1);
    }

    public int b(int pOffset) {
        return pOffset == 0 ? y + selfFrameY + height : y + otherFrameY + framePosH*(pOffset - 1) + height;
    }

    abstract void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks);

    String name;

    protected void itemStart(PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1);
        poseStack.translate(0,0,zPos);
    }

    protected void itemEnd(PoseStack poseStack) {
        poseStack.popPose();
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

    //TODO: Make a client config for this option.
    public static float playerScale = 1f;
    public static float partyScale = .5f;
    public static void register() {
        IIngameOverlay overlay = (gui, poseStack, partialTicks, width, height) -> {
            if (ClientPlayerData.playerOrderedList.size() == 0) return;
            playerScale = 1f;
            itemRender.render(gui, poseStack, partialTicks);
            if (isDirty) {
                syncItems();
                isDirty = false;
            }
        };
        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "parties_hud", overlay);
    }




    public RenderItem setEnabled(boolean enabled) {
        this.elementEnabled = enabled;
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

    static void setupSprite(ResourceLocation loc) {
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

    void blit(PoseStack p, int x, int y, int u, int v, int w, int h, int truew, int trueh) {
        RenderSystem.enableDepthTest();
        final float uScale = 1f / 0x100;
        final float vScale = 1f / 0x100;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder wr = tessellator.getBuilder();
        wr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = p.last().pose();
        wr.vertex(matrix, x        , y + h, zPos).uv( u          * uScale, ((v + trueh) * vScale)).endVertex();
        wr.vertex(matrix, x + w, y + h, zPos).uv((u + truew) * uScale, ((v + trueh) * vScale)).endVertex();
        wr.vertex(matrix, x + w, y         , zPos).uv((u + truew) * uScale, ( v           * vScale)).endVertex();
        wr.vertex(matrix, x        , y         , zPos).uv( u          * uScale, ( v           * vScale)).endVertex();
        tessellator.end();
    }


    static void setColor(float r, float g, float b, float a) {
        RenderSystem.setShaderColor(r, g, b, a);
    }

    void rect(int i, PoseStack pose, int z, int offset, int startColor, int endColor, int alpha) {
        RenderUtils.rect(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor | (alpha << 24), endColor | (alpha << 24));
    }

    void rect(int i, PoseStack pose, int z, int offset, int startColor, int endColor) {
        RenderUtils.rect(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor, endColor);
    }
    void rectNoA(int i, PoseStack pose, int z, int offset, int startColor, int endColor) {
        RenderUtils.rectNoA(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor, endColor);
    }

    public void rect(int i, PoseStack pose, int z, int offset, int startColor) {
        RenderUtils.rect(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor);
    }

    public static void rectCO(PoseStack pose, int z, int offset, int l, int t, int r, int b, int startColor, int endColor) {
        RenderUtils.rectNoA(pose.last().pose(), z, l+offset, t+offset, r-offset, b-offset, startColor, endColor);
    }

    void renderTypeText(PoseStack p, ForgeIngameGui gui, Component type, int x, int y) {
        gui.getFont().drawShadow(p, type, x+16-(gui.getFont().width(type)>>1), y+22, getColor());
    }

    void renderTab(PoseStack p, TabButton b) {
        RenderSystem.enableDepthTest();
        RenderUtils.sizeRect(p.last().pose(), b.x, b.y, 0, b.getWidth(), b.getHeight(), getColor() | 100 << 24, (getColor() & 0xfefefe) >> 1 | 200 << 24);
        RenderUtils.borderRect(p.last().pose(), -1, 1, b.x, b.y, b.getWidth(), b.getHeight(), getColor() | 100 << 24, getColor() | 100 << 24);

    }


    void renderTabHover(PoseStack p, TabButton b) {
        RenderSystem.enableDepthTest();
        RenderUtils.sizeRect(p.last().pose(), b.x, b.y, 0, b.getWidth(), b.getHeight(), (getColor() & 0xfefefe) >> 1 | 200 << 24, getColor() | 100 << 24);
        RenderUtils.borderRect(p.last().pose(), -1, 1, b.x, b.y, b.getWidth(), b.getHeight(), getColor() | 200 << 24, getColor());
    }

    void renderTabClicked(PoseStack p, TabButton b) {
        RenderSystem.enableDepthTest();
        RenderUtils.sizeRectNoA(p.last().pose(), b.x, b.y, 0, b.getWidth(), b.getHeight(), (getColor() & 0xfefefe) >> 1, getColor());
        p.translate(0,0,5);
        RenderUtils.borderRectNoA(p.last().pose(), -1, 2, b.x, b.y, b.getWidth(), b.getHeight(), 0xFFFFFF);
        p.translate(0,0,-5);
    }

    abstract int getColor();

    protected int getColor(int type) {
        return 0;
    }

    void text(int i, ForgeIngameGui gui, PoseStack p, String text, int color) {
        p.translate(0,0,.5);
        if (textShadow) {
            textS(gui,p,text,x(i), y(i),color);
            p.translate(0,0,-.5);
            return;
        }
        text(x(i), y(i), gui, p, text, color);
        p.translate(0,0,-.5);
    }

    void text(int x, int y, ForgeIngameGui gui, PoseStack p, String s, int color) {
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

    void textCentered(int x, int y, ForgeIngameGui gui, PoseStack p, String text, int color) {
        text((int) (x - (gui.getFont().width(text)/2f)), (int) (y - (gui.getFont().lineHeight/2f)), gui, p, text, color);
    }

    static void useAlpha(float alpha) {
        setColor(1f,1f,1f,alpha);
    }

    static void resetColor() {
        setColor(1f,1f,1f,1f);
    }

    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int mouseX, int mouseY, int offsetX, int offsetY, MutableComponent text, int outStart, int outEnd, int inStart, int inEnd, int textColor) {
        rectCO(poseStack, 0, -3, mouseX+offsetX, currentY+mouseY+offsetY, mouseX+gui.getFont().width(text)+offsetX, currentY+mouseY+(gui.getFont().lineHeight)+offsetY, outStart, outEnd);
        rectCO(poseStack, 0, -2, mouseX+offsetX, currentY+mouseY+offsetY, mouseX+gui.getFont().width(text)+offsetX, currentY+mouseY+(gui.getFont().lineHeight)+offsetY, inStart, inEnd);
        gui.getFont().drawShadow(poseStack, text, mouseX+offsetX, currentY+mouseY+1, textColor);
        currentY += gui.getFont().lineHeight+offsetY+8;

    }

    public abstract String getType();



    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int mouseX, int mouseY, int offsetX, int offsetY, String text, int outStart, int outEnd, int inStart, int inEnd, int textColor) {
        renderTooltip(poseStack, gui, mouseX, mouseY, offsetX, offsetY, new TextComponent(text), outStart, outEnd, inStart, inEnd, textColor);
    }

    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int mouseX, int mouseY, int offsetX, int offsetY, String text, int outStart, int outEnd, int textColor) {
        renderTooltip(poseStack, gui, mouseX, mouseY, offsetX, offsetY, new TextComponent(text), outStart, outEnd, 0x140514, 0x140514, textColor);
    }

    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int mouseX, int mouseY, int offsetX, int offsetY, MutableComponent text, int outStart, int outEnd, int textColor) {
        renderTooltip(poseStack, gui, mouseX, mouseY, offsetX, offsetY, text, outStart, outEnd, 0x140514, 0x140514, textColor);
    }

    protected void renderGroupEffectTooltip(PoseStack poseStack, ForgeIngameGui gui, int mouseX, int mouseY, int offsetX, int offsetY, List<ColorComponent> text, int outStart, int outEnd, int inStart, int inEnd) {
        poseStack.pushPose();
        int max = 0;
        int y = offsetY;
        poseStack.translate(0,0,100);
        for (ColorComponent c : text) {
            gui.getFont().drawShadow(poseStack, c.c, mouseX+offsetX, currentY+mouseY+1+y, c.color);
            max = Math.max(max, gui.getFont().width(c.c));
            y += gui.getFont().lineHeight+1;
        }
        rectCO(poseStack, -1, -3, mouseX+offsetX, currentY+mouseY+offsetY, mouseX+max+offsetX, currentY+mouseY+y+offsetY, outStart, outEnd);
        rectCO(poseStack, -1, -2, mouseX+offsetX, currentY+mouseY+offsetY, mouseX+max+offsetX, currentY+mouseY+y+offsetY, inStart, inEnd);
        poseStack.popPose();
        currentY += y+8;
    }

    protected void renderSingleEffectTooltip(PoseStack poseStack, ForgeIngameGui gui, int mouseX, int mouseY, int offsetX, int offsetY, List<ColorComponent> text, int color) {
        poseStack.pushPose();
        poseStack.translate(0, 0, 100);
        int max = 0;
        int y = 0;

        ColorComponent c = text.get(0);
        gui.getFont().drawShadow(poseStack, c.c, mouseX+offsetX, currentY+mouseY+1, c.color);
        max = Math.max(max, gui.getFont().width(c.c));
        y += (gui.getFont().lineHeight)+offsetY+4;

        c = text.get(1);
        max = Math.max(max, gui.getFont().width(c.c));
        gui.getFont().drawShadow(poseStack, c.c, (mouseX+offsetX)+((max-gui.getFont().width(c.c))>>1), (currentY+mouseY+1+y), c.color);

        y += gui.getFont().lineHeight;
        rectCO(poseStack, -1, -3, mouseX+offsetX, currentY+mouseY+offsetY, mouseX+max+offsetX, currentY+mouseY+y+offsetY, (color & 0xfefefe) >> 1, color);
        rectCO(poseStack, -1, -2, mouseX+offsetX, currentY+mouseY+offsetY, mouseX+max+offsetX, currentY+mouseY+y+offsetY, 0x140514, (color & 0xfefefe) >> 1);
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
                //renderTypeText(p, gui, b.type, b.x, b.y);
                renderElement(p, gui, b);
            }

            @Override
            public void onHover(PoseStack p, TabButton b) {
                renderTabHover(p, b);
                //renderTypeText(p, gui, b.type, b.x, b.y);
                renderElement(p, gui, b);
            }

            @Override
            public void onSelect(PoseStack p, TabButton b) {
                renderTabClicked(p, b);
                //renderTypeText(p, gui, b.type, b.x, b.y);
                renderElement(p, gui, b);
            }

            @Override
            public ConfigOptionsList getOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h) {
                return getConfigOptions(s, minecraft, x, y, w, h, false);
            }

            @Override
            public ResourceLocation getInnerBackground() {
                return getItemBackground();
            }

            @Override
            public ItemBound getItemBound() {
                return getRenderItemBound();
            }
        };
    }

    public ItemBound getRenderItemBound() {
        //Render item bound will always target the self frame.
        return new ItemBound(selfFrameX + x, selfFrameY + y, (int) (width * scale), (int) (height * scale));
    }

    protected ResourceLocation getItemBackground() {
        return new ResourceLocation("textures/block/deepslate_bricks.png");
    }

    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        return new ConfigOptionsList(this::getColor, s, minecraft, x, y, w, h, parse);
    }


    abstract void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b);



    public SmallBound changeVisibility(boolean data) {
        elementEnabled = data;
        //Prevent tooltip rendering.
        isDirty = true;
        return null;
    }

    public SmallBound setXPos(int data) {
        this.x = data;
        return new SmallBound(0, selfFrameX + x);
    }

    public SmallBound setYPos(int data) {
        this.y = data;
        return new SmallBound(1, selfFrameY + y);
    }

    public SmallBound setZPos(int data) {
        this.zPos = data;
        return null;
    }


    public SmallBound setScale(int data) {
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
        return new SmallBound(2, (int) (width*scale)){
            @Override
            public void update(BiConsumer<Integer, Integer> action) {
                action.accept(type, value);
                action.accept(3, (int) (height*scale));
            }
        };
    }

    public int getScale() {
        if (scale == 0.5f) return 1;
        if (scale == 1f) return 2;
        if (scale == 2f) return 3;
        return -1;
    }

    public SmallBound setColor(int type, int data) {
        return null;
    }

    public SmallBound setTextShadow(boolean data) {
        this.textShadow = data;
        return null;
    }


    protected SmallBound setWidth(Integer d) {
        this.width = d;
        return new SmallBound(2, (int) (width * scale));
    }

    protected SmallBound setHeight(Integer d) {
        this.height = d;
        return new SmallBound(3, (int) (height * scale));
    }

    protected void updateValues() {
        x = Mth.clamp(x, 0, maxX());
        y = Mth.clamp(y, 0, maxY());
    }

    protected int maxX() {
        return Math.max(0, frameEleW - (int)(width*scale));
    }

    protected int maxY() {
        return Math.max(0, frameEleH - (int)(height*scale));
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

    public interface Update {
        SmallBound onUpdate(RenderItem item, Object data);
    }

    public static void initUpdater(HashMap<String, Update> updater) {
        //Make this be per item instead.
        updater.put("display", (n, d) -> n.changeVisibility((Boolean) d));
        updater.put("tshadow", (n, d) -> n.setTextShadow((Boolean) d));
        updater.put("idisplay", (n, d) -> n.toggleIcon((Boolean) d));
        updater.put("tdisplay", (n, d) -> n.toggleText((Boolean) d));
        updater.put("bgdisplay", (n, d) -> n.toggleIcon((Boolean) d));
        updater.put("tattached", (n, d) -> ((RenderIconTextItem)n).toggleTextAttach((Boolean) d));
        updater.put("xpos", (n, d) -> n.setXPos((int) d));
        updater.put("ypos", (n, d) -> n.setYPos((int) d));
        updater.put("scale", (n, d) -> n.setScale((int) d));
        updater.put("zpos", (n, d) -> n.setZPos((int) d));
        updater.put("xtpos", (n, d) -> ((RenderIconTextItem)n).setXTextPos((int) d));
        updater.put("ytpos", (n, d) -> ((RenderIconTextItem)n).setYTextPos((int) d));
        updater.put("tmax", (n, d) -> ((PName)n).setMaxTextSize((int) d));
        updater.put("width", (n, d) -> n.setWidth((int)d));
        updater.put("height", (n, d) -> n.setHeight((int)d));
        updater.put("ttype", (n,d) ->((BarBase)n).setTextType((int) d));
        updater.put("tcolor", (n, d) -> n.setColor(0, (int)d));
        updater.put("barmode", (n, d) ->  ((BarBase)n).toggleBarMode((boolean) d));
        updater.put("bhue", (n,d) -> ((BarBase)n).setMainHue((int) d));
        updater.put("ohue", (n,d) -> ((OverflowBarBase)n).setOverflowHue((int) d));


        updater.put("bcit", (n, d) -> n.setColor(0, (int)d));
        updater.put("bcib", (n, d) -> n.setColor(1, (int)d));

        updater.put("bcdt", (n, d) -> n.setColor(2, (int)d));
        updater.put("bcdb", (n, d) -> n.setColor(3, (int)d));

        updater.put("buffg", (n, d) -> n.setColor(0, (int)d));
        updater.put("buffb", (n, d) -> n.setColor(1, (int)d));
        updater.put("flash", (n, d) -> n.setColor(2, (int)d));

        updater.put("blim", (n,d) -> EffectHolder.updatebLim((int) d));
        updater.put("dlim", (n,d) -> EffectHolder.updatedLim((int) d));
        updater.put("dfirst", (n,d) -> {PEffectsBoth.debuffFirst = (boolean) d; ClientPlayerData.markEffectsDirty(); return null;});
        updater.put("bsep", (n,d) -> {PEffectsBoth.prioDur = (boolean) d; ClientPlayerData.markEffectsDirty(); return null;});


        updater.put("spacex", (n, d) -> n.setWidth((int)d));
        updater.put("spacey", (n, d) -> n.setHeight((int)d));
        updater.put("bsize", (n,d) -> ((PEffects) n).setBorderSize((int) d));
        updater.put("rowmax", (n,d) -> ((PEffects) n).setMaxPerRow((int) d));
        updater.put("totalmax", (n,d) -> ((PEffects) n).setMaxSize((int) d));

        updater.put("danim", (n,d) -> {DimAnim.animActive = (boolean)d; return null;});
        updater.put("gen_w", (n,d) -> {frameEleW = (int) d; return null;});
        updater.put("gen_h", (n,d) -> {frameEleH = (int) d; return null;});
        updater.put("gen_pw", (n,d) -> {framePosW = (int) d; return null;});
        updater.put("gen_ph", (n,d) -> {framePosH = (int) d; return null;});
        updater.put("genc_w", (n, d) -> n.setWidth((int)d));
        updater.put("genc_h", (n, d) -> n.setHeight((int)d));
        updater.put("genc_x", (n, d) -> n.setXPos((int)d));
        updater.put("genc_y", (n, d) -> n.setYPos((int)d));

        updater.put("htype", (n, d) -> {PHead.renderType = (int)d; return null;});
        updater.put("bleed", (n, d) -> {PHead.renderBleed = (boolean)d; return null;});
    }

    public interface Getter {
        Object getValue(RenderItem item);
    }

    public ConfigEntry getCurrentValues(HashMap<String, Getter> getter) {
        ConfigEntry defaults = getDefaults();
        ConfigEntry currents = new ConfigEntry();
        defaults.forEachEntry((entry, value) -> currents.addEntry(entry, getter.get(entry.getName()).getValue(this)));

        return currents;
    }

    public static void initGetter(HashMap<String, Getter> getter) {
        //Make this be per item instead.
        getter.put("display", (n) -> n.elementEnabled);
        getter.put("tshadow", (n) -> n.textShadow);
        getter.put("idisplay", (n) -> n.iconEnabled);
        getter.put("tdisplay", (n) -> n.textEnabled);
        getter.put("bgdisplay", (n) -> n.iconEnabled);
        getter.put("tattached", (n) -> ((RenderIconTextItem)n).textAttached);
        getter.put("xpos", (n) -> n.x);
        getter.put("ypos", (n) -> n.y);
        getter.put("scale", RenderItem::getScale);
        getter.put("zpos", (n) -> n.zPos);
        getter.put("xtpos", (n) -> ((RenderIconTextItem)n).textX);
        getter.put("ytpos", (n) -> ((RenderIconTextItem)n).textY);
        getter.put("tmax", (n) -> ((PName)n).length);
        getter.put("width", (n) -> n.width);
        getter.put("height", (n) -> n.height);
        getter.put("ttype", (n) -> ((BarBase)n).getTextType());
        getter.put("tcolor", (n) -> n.getColor(0));
        getter.put("barmode", (n) -> ((BarBase)n).isBarMode());
        getter.put("bhue", (n) -> ((BarBase)n).hue);
        getter.put("ohue", (n) -> ((OverflowBarBase)n).oHue);

        getter.put("bcit", (n) -> n.getColor(0));
        getter.put("bcib", (n) -> n.getColor(1));

        getter.put("bcdt", (n) -> n.getColor(2));
        getter.put("bcdb", (n) -> n.getColor(3));

        getter.put("buffg", (n) -> n.getColor(0));
        getter.put("buffb", (n) -> n.getColor(1));
        getter.put("flash", (n) -> n.getColor(2));

        getter.put("blim", (n) -> PEffectsBoth.bLim);
        getter.put("dlim", (n) -> PEffectsBoth.dLim);
        getter.put("dfirst", (n) -> PEffectsBoth.debuffFirst);
        getter.put("bsep", (n) -> PEffectsBoth.prioDur);


        getter.put("spacex", (n) -> n.width);
        getter.put("spacey", (n) -> n.height);
        getter.put("bsize", (n) -> ((PEffects) n).borderSize);
        getter.put("rowmax", (n) -> ((PEffects) n).maxPerRow);
        getter.put("totalmax", (n) -> ((PEffects) n).maxSize);

        getter.put("danim", (n) -> DimAnim.animActive);
        getter.put("gen_w", (n) -> frameEleW);
        getter.put("gen_h", (n) -> frameEleH);
        getter.put("gen_pw", (n) -> framePosW);
        getter.put("gen_ph", (n) -> framePosH);
        getter.put("genc_w", (n) -> clickArea.width);
        getter.put("genc_h", (n) -> clickArea.height);
        getter.put("genc_x", (n) -> clickArea.x);
        getter.put("genc_y", (n) -> clickArea.y);
        getter.put("htype", (n) -> PHead.renderType);
        getter.put("bleed", (n) -> PHead.renderBleed);
    }

    public static void setDefaultValues() {
        frameEleW = 168;
        frameEleH = 64;
        framePosW = 0;
        framePosH = 63;
        HashMap<String, Update> updater = new HashMap<>();
        RenderItem.initUpdater(updater);
        items.values().forEach(item -> item.getDefaults().forEachEntry((s, v) -> {
            updater.get(s.getName()).onUpdate(item, v);
        }));
        syncItems();
    }

    public static void setElementDefaults(RenderItem item, HashMap<String, Update> updater) {
        item.getDefaults().forEachEntry((s, v) -> updater.get(s.getName()).onUpdate(item, v));
    }

    public abstract ConfigEntry getDefaults();


    public static ConfigEntry getGeneralValues() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("gen_w", frameEleW, 12);
        e.addEntry("gen_h", frameEleH, 12);
        e.addEntry("gen_pw", framePosW, 12);
        e.addEntry("gen_ph", framePosH, 12);
        return e;
    }

    public static ConfigEntry getGeneralDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("gen_w", 168, 12);
        e.addEntry("gen_h", 64, 12);
        e.addEntry("gen_pw", 0, 12);
        e.addEntry("gen_ph", 63, 12);
        return e;
    }

    public static void getCurrentMouseFrame(int mouseX, int mouseY, TriConsumer<Integer, Integer, Integer> action) {
        int partyMouseX = mouseX;
        int partyMouseY = mouseY;
        mouseX /= playerScale;
        mouseY /= playerScale;
        //if (mouseX < selfFrameX || mouseY < selfFrameY) return;

        mouseX = mouseX - selfFrameX;
        mouseY = mouseY - selfFrameY;
        if (mouseX < frameEleW && mouseY < frameEleH) {
            if (renderSelfFrame)
                action.accept(0, mouseX, mouseY);
        }

        partyMouseX /= partyScale;
        partyMouseY /= partyScale;
        partyMouseX -= otherFrameX; //TODO: Switch to otherFrameX/Y.
        partyMouseY -= otherFrameY;


        for (int i = 1; i < ClientPlayerData.playerOrderedList.size(); i++) {
            if (partyMouseX < 0 || partyMouseY < 0) return;
            if (partyMouseX < frameEleW && partyMouseY < frameEleH) {
                action.accept(i, partyMouseX, partyMouseY);
            }
            partyMouseX -= framePosW;
            partyMouseY -= framePosH;
        }
    }

    private interface ItemRender {
        void render(ForgeIngameGui gui, PoseStack poseStack, float partialTicks);
    }


}
