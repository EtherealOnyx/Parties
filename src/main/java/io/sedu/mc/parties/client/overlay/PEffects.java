package io.sedu.mc.parties.client.overlay;

import io.sedu.mc.parties.util.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import io.sedu.mc.parties.client.overlay.effects.ClientEffect;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.sedu.mc.parties.util.RenderUtils.sizeRectNoA;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;

public abstract class PEffects extends RenderSelfItem {

    //Potion Effects
    protected int maxSize;
    protected int maxPerRow;
    int borderSize = 1;

    static int beneColor = 0xA9E5FF;
    static int badColor = 0xFFA9A9;
    static int flashColor = 0xFFFFFF;

    boolean renderText;
    boolean renderBg;


    public PEffects(String name, int x, int y, int width, int height, int max, int row) {
        super(name, x, y, width, height);
        maxSize = max;
        maxPerRow = row;
        renderText = true;
        renderBg = true;
    }

    @Override
    int getColor() {
        return beneColor;
    }

    @Override
    public String getType() {
        return "Bar";
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline) {
            renderSelf(i, id, gui, poseStack, partialTicks);
        }
    }

    boolean renderOverflow(ForgeIngameGui gui, PoseStack poseStack, int i, int iX, int iY, float partialTicks) {

        drawOverflowText(gui, poseStack,
                         (int) ((rX(i, iX)+2)/scale), (int) ((rY(i, iY)+4)/scale),
                         (int) Mth.clamp((255*(float) (.5f + Math.sin((gui.getGuiTicks() + partialTicks) / 4f) / 2f)), 10, 245));
        return (notEditing() && withinBounds(rX(i, iX), rY(i, iY),13, 13, 1, scale));
    }

    void drawOverflowText(ForgeIngameGui gui, PoseStack p, int x, int y, int alpha) {

        gui.getFont().draw(p, "▪▪▪", x, y, beneColor);
        gui.getFont().draw(p, "▫▫▫", x, y, badColor | alpha << 24);
    }

    boolean checkRow(int x) {
        return x+1 >maxPerRow;
    }

    void renderEffect(ClientEffect effect, ForgeIngameGui gui, PoseStack poseStack, int i, int iX, int iY, float partialTicks) {
        //BG Border
        if (effect.isInstant() && (gui.getGuiTicks() >> 3 & 1) == 0) {
            //TODO: Remove width hardcoding for all values here. Maybe.
            rectInscribedFlash(poseStack.last().pose(), borderSize, sX(i, iX), sY(i, iY), 26, 26, 0xFFFFFF, effect.getEffect()
                                                                                                           .getColor());
        } else {
            rectInscribed(poseStack.last().pose(), borderSize, sX(i, iX), sY(i, iY), 26, 26, effect.getEffect().getColor(), effect.bene());
        }

        //Texture
        TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get(effect.getEffect());
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        RenderSystem.enableBlend();
        if (effect.isDying())
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks) / 2f) / 4f));
        else
            resetColor();
        Gui.blit(poseStack, sX(i, iX) + 4, sY(i, iY) + 4,0, 18, 18, sprite);

        //Text
        int x, y;
        if (!effect.getRomanTrimmed().equals("")) {
            x = sX(i, iX) + 24 - gui.getFont().width(effect.getRomanTrimmed());
            y = sY(i, iY) + 16;
            gui.getFont().drawShadow(poseStack, effect.getRomanTrimmed(), x, y, 0xFFD700);
        }

        String secs = "§oInstant";
        int scol = 0x88888888;
        if (renderText && !effect.isInstant()) {
            x = sX(i, iX) + effect.getOffset() + 7;
            y = sY(i, iY) + 29;
            gui.getFont().drawShadow(poseStack, effect.getDisplay(), x, y, 0xFFFFFF);
            secs = effect.getDur() + "s";
            scol = 0xFFFFFF;
        }

        if (notEditing() && withinBounds(rX(i, iX), rY(i, iY), 13, 13, 1, scale)) {
            poseStack.pushPose();
            poseStack.scale(2f,2f,1f);
            List<ColorComponent> list = new ArrayList<>();
            list.add(new ColorComponent(new TranslatableComponent(effect.getEffect().getDescriptionId()).append(" ").append(effect.getRoman()), effect.getEffect().isBeneficial() ? beneColor : badColor));
            //TODO: Support descriptions via datapacks :)
            list.add(new ColorComponent(new TextComponent(secs), scol));
            renderSingleEffectTooltip(poseStack, gui, 10, 0, list,
                                      effect.getEffect().getColor());
            poseStack.popPose();
        }
    }


    void start(PoseStack poseStack, int i, int size) {
        poseStack.pushPose();
        if (renderBg)
            RenderUtils.rect(poseStack.last().pose(), -zPos - 1, x(i) - 2, (y(i) - 2),
                             x(i) + (width * Math.min(size, maxPerRow) >> 1),
                             -1 + y(i) + (height * (int) Math.ceil((double) Math.min(maxSize, size) / maxPerRow) >> 1),
                             0x44002024, 0x44002024);
        poseStack.scale(.5f, .5f, 1f);
        RenderSystem.enableDepthTest();
    }

    void end(PoseStack poseStack) {
        resetColor();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }


    private void rectInscribed(Matrix4f pose, int radius, int x, int y, int width, int height, int outColor, boolean ben) {
        if (ben)
            sizeRectNoA(pose, x, y, 0, width, height, beneColor, beneColor);
        else
            sizeRectNoA(pose, x, y, 0, width, height, badColor, badColor);
        sizeRectNoA(pose, x+radius, y+radius, 0, width-(radius*2), height-(radius*2), 0x212121, (outColor & 0xfefefe) >> 1);
    }

    private void rectInscribedFlash(Matrix4f pose, int radius, int x, int y, int width, int height, int flashColor, int outColor) {
        sizeRectNoA(pose, x, y, 0, width, height, flashColor, flashColor);
        sizeRectNoA(pose, x+radius, y+radius, 0, width-(radius*2), height-(radius*2), 0x212121, (outColor & 0xfefefe) >> 1);
    }

    private int sX(int pI, int bI) {
        return (int) (((frameW<<1)*pI+((x+frameX)<<1))/scale +width*bI);
    }

    private int rX(int pI, int bI) {
        return (int) (frameW*pI+x+frameX + (width/2*scale)*bI);
    }

    private int sY(int pI, int bI) {
        return (int) (((frameH<<1)*pI+((y+frameY)<<1))/scale +height*bI);
    }

    private int rY(int pI, int bI) {
        return (int) (frameH*pI+(y+frameY)+(height/2*scale)*bI);
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("display");
        c.addBooleanEntry("display", isEnabled());
        c.addBooleanEntry("tdisplay", renderText);
        c.addSliderEntry("bsize", 1, () -> 4, borderSize);
        getColorEntry(c);
        c.addTitleEntry("position");
        c.addSliderEntry("xpos", 0, () -> Math.max(clickArea.r(0), frameX + frameW) - frameW + 32, this.x);
        c.addSliderEntry("ypos", 0, () -> Math.max(clickArea.b(0), frameY + frameH) - frameY + 32, this.y);
        c.addSliderEntry("zpos", 0, () -> 10, zPos);
        c.addSliderEntry("scale", 1, () -> 3, getScale());

        c.addTitleEntry("icon");
        c.addBooleanEntry("bgdisplay", renderBg);
        c.addSliderEntry("spacex", 1, () -> 64, width);
        c.addSliderEntry("spacey", 1, () -> 64, height);
        getLimitEntries(c);

        return c;
    }

    protected void updateAffectedSliders(HashMap<String, ConfigOptionsList.SliderEntry> sliders) {
        sliders.computeIfPresent("totalmax", ((s, sliderEntry) -> sliderEntry.forceUpdate(Math.max(maxPerRow, maxSize))));
        sliders.computeIfPresent("rowmax", ((s, sliderEntry) -> sliderEntry.forceUpdate(Math.min(maxPerRow, maxSize))));
    }

    protected void getLimitEntries(ConfigOptionsList c) {
        final HashMap<String, ConfigOptionsList.SliderEntry> entries = new HashMap<>();
        entries.put("rowmax", c.addSliderWithUpdater("rowmax", 1, () -> maxSize, maxPerRow, () -> updateAffectedSliders(entries), false));
        entries.put("totalmax", c.addSliderWithUpdater("totalmax", maxPerRow, Registry.MOB_EFFECT::size, maxSize, () -> updateAffectedSliders(entries), false));
    }

    protected void getColorEntry(ConfigOptionsList c) {
    }

    @Override
    public void setColor(int type, int data) {
        switch(type) {
            case 0 -> beneColor = data;
            case 1 -> badColor = data;
            case 2 -> flashColor = data;
        }
    }

    public void setBorderSize(int data) {
        this.borderSize = data;
    }

    public void setMaxSize(int data) {
        this.maxSize = data;
    }

    public void setMaxPerRow(int data) {
        this.maxPerRow = data;
    }

    @Override
    public void toggleText(boolean data) {
        renderText = data;
    }

    @Override
    public void toggleIcon(boolean data) {
        renderBg = data;
    }
}
