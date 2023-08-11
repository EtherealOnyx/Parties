package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.effects.ClientEffect;
import io.sedu.mc.parties.client.overlay.effects.EffectHolder;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class PEffectsBad extends PEffects{

    public PEffectsBad(String name) {
        super(name);
    }

    @Override
    int getColor() {
        return badColor;
    }



    @Override
    protected void getColorEntry(ConfigOptionsList c) {
        c.addColorEntry("buffb", badColor);
        c.addColorEntry("flash", flashColor);
    }


    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        poseStack.pushPose();
        poseStack.scale(.5f,.5f,1f);
        poseStack.translate(b.x+15, b.y+6, 0);
        RenderSystem.enableDepthTest();
        TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get(MobEffects.WITHER);
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        Gui.blit(poseStack, b.x, b.y, 0, 18, 18, sprite);
        poseStack.popPose();
    }

    @Override
    void updateDefaultPositionForMods(HashMap<String, Update> updater) {
        //No changes
    }

    @Override
    void renderSelf(ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.effects.sizeBad() > 0) {
            start(poseStack, 0, id.effects.sizeBad());
            AtomicInteger iX = new AtomicInteger();
            AtomicInteger iY = new AtomicInteger();
            if (id.effects.largerBad(maxSize)) {
                id.effects.forEachBadLim(maxSize, (effect) -> {
                    //If we reached max per row
                    if (checkRow(iX.get())) {
                        iX.set(0);
                        iY.getAndIncrement();
                    }
                    renderEffect(effect, gui, poseStack, 0, iX.get(), iY.get(), partialTicks);
                    iX.getAndIncrement();
                    resetColor();
                });
                poseStack.pushPose();
                poseStack.scale(2f,2f,1f);
                if (checkRow(iX.get())) {
                    iX.set(0);
                    iY.getAndIncrement();
                }
                renderOverflow(gui, poseStack, 0, iX.get(), iY.get(), partialTicks);
                poseStack.popPose();
            } else {
                id.effects.forEachBad((effect) -> {
                    //If we reached max per row
                    if (checkRow(iX.get())) {
                        iX.set(0);
                        iY.getAndIncrement();
                    }
                    renderEffect(effect, gui, poseStack, 0, iX.get(), iY.get(), partialTicks);
                    iX.getAndIncrement();
                    resetColor();
                });
            }
            end(poseStack);
        }
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", false, 1);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("bsize", 1, 4);
        e.addEntry("buffb", 0xffa9a9, 24);
        e.addEntry("flash", 0xffffff, 24);
        e.addEntry("xpos", 46, 12);
        e.addEntry("ypos", 41, 12);
        e.addEntry("zpos", 0, 4);
        e.addEntry("scale", 2, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("spacex", 30, 8);
        e.addEntry("spacey", 44, 8);
        e.addEntry("rowmax", 8, 8);
        e.addEntry("totalmax", 8, 8);
        return e;
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    protected boolean getClientEffect(EffectHolder effects, Integer buffIndex, Consumer<ClientEffect> action) {
        return effects.getEffect(maxSize, effects.sortedEffectBad, buffIndex, action);
    }



}
