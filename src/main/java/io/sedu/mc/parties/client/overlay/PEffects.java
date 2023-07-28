package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import io.sedu.mc.parties.client.overlay.effects.ClientEffect;
import io.sedu.mc.parties.client.overlay.effects.EffectHolder;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.sedu.mc.parties.util.RenderUtils.sizeRectNoA;

public abstract class PEffects extends RenderSelfItem {

    //Potion Effects
    protected int maxSize;
    protected int maxPerRow;
    int borderSize = 1;

    static int beneColor = 0xA9E5FF;
    static int badColor = 0xFFA9A9;
    static int flashColor = 0xFFFFFF;


    private int xOff;
    private int yOff;
    private int eleWidth;
    private int eleHeight;

    private static final List<PEffects> effectItems = new ArrayList<>();
    private TranslatableComponent instantText = new TranslatableComponent("ui.sedparties.tooltip.effectinstant");

    @Override
    public SmallBound changeVisibility(boolean data) {
        this.elementEnabled = data;
        //Prevent tooltip rendering.
        int index = effectItems.indexOf(this);
        if (data) {
            if (index == -1) effectItems.add(this);
        } else {
            if (index == -1) return null;
            effectItems.remove(index);
        }
        return null;
    }

    public PEffects(String name) {
        super(name);
    }

    public static void checkEffectTooltip(int posX, int posY, BiConsumer<PEffects, Integer> action) {
        effectItems.forEach(item -> {
            int newX = (posX - item.x)/(item.width/2);
            if (newX < item.maxPerRow) {
                int eleIndex = (posY - item.y);
                if (eleIndex < 0) return;
                eleIndex = (eleIndex/(item.height/2))*item.maxPerRow + newX;
                if (eleIndex < item.maxSize && eleIndex > -1) {
                    action.accept(item, eleIndex);
                }
            }
        });


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
            renderSelf(id, gui, poseStack, partialTicks);
        }
    }

    void renderOverflow(ForgeIngameGui gui, PoseStack poseStack, int i, int iX, int iY, float partialTicks) {

        drawOverflowText(gui, poseStack,
                         (int) ((rX(i, iX))/scale)+3, (int) ((rY(i, iY))/scale)+3,
                         (int) Mth.clamp((255*(float) (.5f + Math.sin((gui.getGuiTicks() + partialTicks) / 4f) / 2f)), 10, 245));
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

        if (textEnabled && !effect.isInstant()) {
            x = sX(i, iX) + effect.getOffset() + 7;
            y = sY(i, iY) + 29;
            gui.getFont().drawShadow(poseStack, effect.getDisplay(), x, y, 0xFFFFFF);
        }
    }

    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, EffectHolder effects, Integer buffIndex, int pMouseX, int pMouseY) {
        if (getClientEffect(effects, buffIndex, (effect) -> {
            List<ColorComponent> list = new ArrayList<>();
            list.add(new ColorComponent(new TranslatableComponent(effect.getEffect().getDescriptionId()).append(" ").append(effect.getRoman()), effect.getEffect().isBeneficial() ? beneColor : badColor));
            //TODO: Support descriptions via datapacks :)
            if (effect.isInstant())
                list.add(new ColorComponent(instantText, 0x88888888));
            else
                list.add(new ColorComponent(new TextComponent( effect.getDur() + "s"), 0xFFFFFF));
            renderSingleEffectTooltip(poseStack, gui, pMouseX, pMouseY, 10, 0, list,
                                      effect.getEffect().getColor());
        })) {
            renderOverflow(effects, poseStack, gui, pMouseX, pMouseY);
        }

    }

    protected boolean getClientEffect(EffectHolder effects, Integer buffIndex, Consumer<ClientEffect> action) {
        return effects.getEffect(maxSize, effects.sortedEffectAll, buffIndex, action);
    }

    protected void renderOverflow(EffectHolder effects, PoseStack poseStack, ForgeIngameGui gui, int mouseX, int mouseY) {
        List<ColorComponent> lC = new ArrayList<>();
        effects.forAllRemainder(maxSize, (effect) -> {

            lC.add(new ColorComponent(new TranslatableComponent(effect.getEffect().getDescriptionId()).append(" ").append(effect.getRoman()), effect.getEffect().isBeneficial() ? beneColor : badColor));

        });
        renderGroupEffectTooltip(poseStack, gui, mouseX, mouseY, 10, 0, lC, 0x3101b8, 0x24015b, 0x150615, 0x150615);
    }



    void start(PoseStack poseStack, int i, int size) {
        poseStack.pushPose();
        if (iconEnabled)
            RenderUtils.rect(poseStack.last().pose(), -zPos - 1, x(i) - 2, y(i),
                             x(i) + (width * Math.min(size, maxPerRow) >> 1) + 2,
                             y(i) + (height * (int) Math.ceil((double) Math.min(maxSize, size) / maxPerRow) >> 1),
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
        return (int) (((framePosW<<1)*pI+((x+(pI == 0 ? selfFrameX : partyFrameX))<<1))/scale +width*bI) + xOff/2;
    }

    private float rX(int pI, int bI) {
        return (framePosW*pI+x+(pI == 0 ? selfFrameX : partyFrameX) + (width/2f*scale)*bI);
    }

    private int sY(int pI, int bI) {
        return (int) (((framePosH<<1)*pI+((y+(pI == 0 ? selfFrameY : partyFrameY))<<1))/scale +height*bI) + yOff/2;
    }

    private float rY(int pI, int bI) {
        return framePosH*pI+(y+(pI == 0 ? selfFrameY : partyFrameY))+(height/2f*scale)*bI;
    }


    protected SmallBound setWidth(Integer d) {
        this.width = d;
        this.xOff = (width-26)/2;
        this.eleWidth = ((width * maxPerRow) / 2);
        return new SmallBound(2, (int) (width / 2 * maxPerRow * scale));
    }


    protected SmallBound setHeight(Integer d) {
        this.height = d;
        this.yOff = (height-26)/2;
        this.eleHeight = (int) (height * Math.ceil(maxSize / (float)maxPerRow) / 2); //why is this off by 1...
        return new SmallBound(3, (int) ((height / 2) * Math.ceil(1f * maxSize / maxPerRow) * scale));
    }

    protected int maxX() {
        return Math.max(0, frameEleW - (int)(eleWidth*scale));
    }

    protected int maxY() {
        return Math.max(0, frameEleH - (int)(eleHeight*scale));
    }


    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("display");
        c.addBooleanEntry("display", isEnabled());
        c.addBooleanEntry("tdisplay", textEnabled);
        c.addSliderEntry("bsize", 1, () -> 4, borderSize);
        getColorEntry(c);
        c.addTitleEntry("position");
        c.addSliderEntry("xpos", 0, this::maxX, this.x);
        c.addSliderEntry("ypos", 0, this::maxY, this.y);
        c.addSliderEntry("zpos", 0, () -> 10, zPos);
        c.addSliderEntry("scale", 1, () -> 3, getScale(), true);

        c.addTitleEntry("icon");
        c.addBooleanEntry("bgdisplay", iconEnabled);
        c.addSliderEntry("spacex", 1, () -> 64, width, true);
        c.addSliderEntry("spacey", 1, () -> 64, height, true);
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
        entries.put("totalmax", c.addSliderWithUpdater("totalmax", maxPerRow, () -> 64, maxSize, () -> updateAffectedSliders(entries), false));
    }

    protected void getColorEntry(ConfigOptionsList c) {
    }

    @Override
    public SmallBound setColor(int type, int data) {
        switch(type) {
            case 0 -> beneColor = data;
            case 1 -> badColor = data;
            case 2 -> flashColor = data;
        }
        return null;
    }

    @Override
    public int getColor(int type) {
        switch(type) {
            case 0 -> {return beneColor;}
            case 1 -> {return badColor;}
            case 2 -> {return flashColor;}
        }
        return 0;
    }

    public SmallBound setBorderSize(int data) {
        this.borderSize = data;
        return null;
    }

    public SmallBound setMaxSize(int data) {
        this.maxSize = data;
        return new SmallBound(2, (int) (width/2*maxPerRow*scale)){
            @Override
            public void update(BiConsumer<Integer, Integer> action) {
                action.accept(type, value);
                action.accept(3, (int) ((height/2)*Math.ceil(1f*maxSize/maxPerRow)*scale));
            }
        };
    }

    public SmallBound setMaxPerRow(int data) {
        this.maxPerRow = data;
        return new SmallBound(2, (int) (width/2*maxPerRow*scale)){
            @Override
            public void update(BiConsumer<Integer, Integer> action) {
                action.accept(type, value);
                action.accept(3, (int) ((height/2)*Math.ceil(1f*maxSize/maxPerRow)*scale));
            }
        };
    }

    @Override
    public ItemBound getRenderItemBound() {
        return new ItemBound(selfFrameX + x, selfFrameY + y, (int) (width / 2 * maxPerRow * scale),
                             (int) ((height / 2) * Math.ceil(1f * maxSize / maxPerRow) * scale));
    }

    @Override
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
        return new SmallBound(2, (int) (width/2*maxPerRow*scale)){
            @Override
            public void update(BiConsumer<Integer, Integer> action) {
                action.accept(type, value);
                action.accept(3, (int) ((height/2)*Math.ceil(1f*maxSize/maxPerRow)*scale));
            }
        };
    }


}
