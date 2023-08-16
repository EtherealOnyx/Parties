package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class PEffectsBoth extends PEffects {


    public static int maxAll;
    public static boolean prioDur = false;
    public static int dLim;
    public static int bLim;
    public static boolean debuffFirst = true;

    public PEffectsBoth(String name) {
        super(name);
        updateMax();
    }

    private void updateMax() {
        maxAll = maxSize;
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        poseStack.pushPose();
        poseStack.scale(.5f,.5f,1f);
        poseStack.translate(b.x, b.y, 0);
        RenderSystem.enableDepthTest();
        TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get(MobEffects.BAD_OMEN);
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        Gui.blit(poseStack, b.x + 20, b.y+5, 0, 18, 18, sprite);
        sprite = Minecraft.getInstance().getMobEffectTextures().get(MobEffects.DAMAGE_RESISTANCE);
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        Gui.blit(poseStack, b.x + 10, b.y +7,0, 18, 18, sprite);

        poseStack.popPose();
    }

    @Override
    void updateDefaultPositionForMods(HashMap<String, Update> updater) {
        //No changes
    }

    @Override
    void renderEffect(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.effects.sizeAll() > 0) {
            start(poseStack, i, id.effects.sizeAll());
            AtomicInteger iX = new AtomicInteger();
            AtomicInteger iY = new AtomicInteger();
            if (id.effects.largerAll(maxSize)) {
                id.effects.forEachAllLim(maxSize, (effect) -> {
                    //If we reached max per row
                    if (checkRow(iX.get())) {
                        iX.set(0);
                        iY.getAndIncrement();
                    }
                    renderEffect(effect, gui, poseStack, i, iX.get(), iY.get(), partialTicks);
                    iX.getAndIncrement();
                    resetColor();
                });
                poseStack.pushPose();
                poseStack.scale(2f,2f,1f);
                if (checkRow(iX.get())) {
                    iX.set(0);
                    iY.getAndIncrement();
                }
                renderOverflow(gui, poseStack, i, iX.get(), iY.get(), partialTicks);
                poseStack.popPose();
            } else {
                id.effects.forEachAll((effect) -> {
                    //If we reached max per row
                    if (checkRow(iX.get())) {
                        iX.set(0);
                        iY.getAndIncrement();
                    }
                    renderEffect(effect, gui, poseStack, i, iX.get(), iY.get(), partialTicks);
                    iX.getAndIncrement();
                    resetColor();
                });
            }

            end(poseStack);
        }
    }


    @Override
    protected void getLimitEntries(ConfigOptionsList c) {
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        final HashMap<String, ConfigOptionsList.SliderEntry> sliders = new HashMap<>();
        sliders.put("rowmax", c.addSliderWithUpdater("rowmax", 1, () -> maxSize, maxPerRow, () -> updateAffectedSliders(sliders), false));
        sliders.put("totalmax", c.addSliderWithUpdater("totalmax", maxPerRow, () -> 32, maxSize, () -> updateAffectedSliders(sliders), false));

        c.addBooleanEntry("bsep", prioDur, () -> toggleLimSliders(entries));
        c.addSpaceEntry();
        c.addTitleEntry("sepe");
        entries.add(c.addBooleanEntry("dfirst", debuffFirst));

        ConfigOptionsList.SliderEntry sE = c.addSliderWithUpdater("blim", 0, () -> Math.max(0, maxSize - 1), bLim, () -> updateLimSliders(sliders), false);
        entries.add(sE);
        sliders.put("blim", sE);
        sE = c.addSliderWithUpdater("dlim", 0, () -> Math.max(0, maxSize - 1), dLim, () -> updateLimSliders(sliders), false);
        entries.add(sE);
        sliders.put("dlim", sE);
    }

    private void toggleLimSliders(ArrayList<ConfigOptionsList.Entry> entries) {
        entries.forEach(entry -> entry.setVisible(!prioDur));
    }

    private void updateLimSliders(HashMap<String, ConfigOptionsList.SliderEntry> sliders) {
        bLim = Math.max(0, bLim);
        dLim = Math.max(0, maxSize - 1 - bLim);
        sliders.computeIfPresent("dlim", ((s, sliderEntry) -> sliderEntry.forceUpdate(dLim)));
        sliders.computeIfPresent("blim", ((s, sliderEntry) -> sliderEntry.forceUpdate(bLim)));
    }

    @Override
    protected void updateAffectedSliders(HashMap<String, ConfigOptionsList.SliderEntry> sliders) {
        super.updateAffectedSliders(sliders);
        updateLimSliders(sliders);
    }

    @Override
    protected void getColorEntry(ConfigOptionsList c) {
        c.addColorEntry("buffg", beneColor);
        c.addColorEntry("buffb", badColor);
        c.addColorEntry("flash", flashColor);
    }

    @Override
    public SmallBound setMaxSize(int data) {
        this.maxSize = data;
        updateMax();
        return new SmallBound(2, (int) (width/2*maxPerRow*scale)){
            @Override
            public void update(BiConsumer<Integer, Integer> action) {
                action.accept(type, value);
                action.accept(3, (int) ((height/2)*Math.ceil(1f*maxSize/maxPerRow)*scale));
            }
        };
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("bsize", 1, 4);
        e.addEntry("buffg", 0xa9e5ff,24);
        e.addEntry("buffb", 0xffa9a9, 24);
        e.addEntry("flash", 0xffffff, 24);
        e.addEntry("xpos", 46, 12);
        e.addEntry("ypos", 41, 12);
        e.addEntry("zpos", 0, 4);
        e.addEntry("scale", 2, 2);
        e.addEntry("idisplay", true, 1);
        e.addEntry("spacex", 30, 8);
        e.addEntry("spacey", 44, 8);
        e.addEntry("rowmax", 8, 8);
        e.addEntry("totalmax", 8, 8);
        e.addEntry("bsep", false, 1);
        e.addEntry("dfirst", true, 1);
        e.addEntry("dlim", 4, 8);
        e.addEntry("blim", 3, 8);
        return e;
    }

    @Override
    public int getId() {
        return 10;
    }

}
