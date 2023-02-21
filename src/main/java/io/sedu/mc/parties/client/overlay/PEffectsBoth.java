package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.effects.EffectHolder;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PEffectsBoth extends PEffects {



    public PEffectsBoth(String name, int x, int y, int width, int height, int max, int row) {
        super(name, x, y, width, height, max, row);
        updateMax();
    }

    private void updateMax() {
        EffectHolder.maxAll = maxSize;
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        RenderSystem.enableDepthTest();
        TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get(MobEffects.BAD_OMEN);
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        Gui.blit(poseStack, b.x + 11, b.y+2, 0, 18, 18, sprite);
        sprite = Minecraft.getInstance().getMobEffectTextures().get(MobEffects.DAMAGE_RESISTANCE);
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        Gui.blit(poseStack, b.x + 3, b.y +3,0, 18, 18, sprite);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
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
                if (renderOverflow(gui, poseStack, i, iX.get(), iY.get(), partialTicks)) {
                    List<ColorComponent> lC = new ArrayList<>();
                    //poseStack.translate((mouseX()+10), (mouseY()), 0);
                    id.effects.forAllRemainder(maxSize, (effect) -> {

                        lC.add(new ColorComponent(new TranslatableComponent(effect.getEffect().getDescriptionId()).append(" ").append(effect.getRoman()), effect.getEffect().isBeneficial() ? beneColor : badColor));
                        //renderTooltip(poseStack, gui, 10, 0, new TranslatableComponent(effect.getEffect().getDescriptionId()).append(" ").append(effect.getRoman()), effect.getEffect().getColor(), (effect.getEffect().getColor() & 0xfefefe) >> 1, effect.colorType());
                    });
                    renderGroupEffectTooltip(poseStack, gui, 10, 0, lC, 0x3101b8, 0x24015b, 0x150615, 0x150615);
                }
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
        sliders.put("rowmax", c.addSliderWithUpdater("config.sedparties.name.rowmax", 1, () -> maxSize, maxPerRow, () -> updateAffectedSliders(sliders), false));
        sliders.put("totalmax", c.addSliderWithUpdater("config.sedparties.name.totalmax", maxPerRow, Registry.MOB_EFFECT::size, maxSize, () -> updateAffectedSliders(sliders), false));

        c.addBooleanEntry("config.sedparties.name.bsep", EffectHolder.prioDur, () -> toggleLimSliders(entries));
        c.addSpaceEntry();
        c.addTitleEntry("config.sedparties.title.sepe");
        entries.add(c.addBooleanEntry("config.sedparties.name.dfirst", EffectHolder.debuffFirst));

        ConfigOptionsList.SliderEntry sE = c.addSliderWithUpdater("config.sedparties.name.blim", 0, () -> Math.max(0, maxSize - 1), EffectHolder.bLim, () -> updateLimSliders(sliders), false);
        entries.add(sE);
        sliders.put("blim", sE);
        sE = c.addSliderWithUpdater("config.sedparties.name.dlim", 0, () -> Math.max(0, maxSize - 1), EffectHolder.dLim, () -> updateLimSliders(sliders), false);
        entries.add(sE);
        sliders.put("dlim", sE);
    }

    private void toggleLimSliders(ArrayList<ConfigOptionsList.Entry> entries) {
        entries.forEach(entry -> entry.setVisible(!EffectHolder.prioDur));
    }

    private void updateLimSliders(HashMap<String, ConfigOptionsList.SliderEntry> sliders) {
        EffectHolder.bLim = Math.max(0, EffectHolder.bLim);
        EffectHolder.dLim = Math.max(0, maxSize - 1 - EffectHolder.bLim);
        sliders.computeIfPresent("dlim", ((s, sliderEntry) -> sliderEntry.forceUpdate(EffectHolder.dLim)));
        sliders.computeIfPresent("blim", ((s, sliderEntry) -> sliderEntry.forceUpdate(EffectHolder.bLim)));
    }

    @Override
    protected void updateAffectedSliders(HashMap<String, ConfigOptionsList.SliderEntry> sliders) {
        super.updateAffectedSliders(sliders);
        updateLimSliders(sliders);
    }

    @Override
    protected void getColorEntry(ConfigOptionsList c) {
        c.addColorEntry("config.sedparties.name.buffg", beneColor);
        c.addColorEntry("config.sedparties.name.buffb", badColor);
        c.addColorEntry("config.sedparties.name.flash", flashColor);
    }

    @Override
    public void setMaxSize(int data) {
        this.maxSize = data;
        updateMax();
    }
}