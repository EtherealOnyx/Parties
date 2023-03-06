package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        poseStack.translate(b.x+23, b.y+6, 0);
        RenderSystem.enableDepthTest();
        TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get(MobEffects.WITHER);
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        Gui.blit(poseStack, b.x, b.y, 0, 18, 18, sprite);
        poseStack.popPose();
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.effects.sizeBad() > 0) {
            start(poseStack, i, id.effects.sizeBad());
            AtomicInteger iX = new AtomicInteger();
            AtomicInteger iY = new AtomicInteger();
            if (id.effects.largerBad(maxSize)) {
                id.effects.forEachBadLim(maxSize, (effect) -> {
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
                    id.effects.forBadRemainder(maxSize, (effect) -> lC.add(new ColorComponent(new TranslatableComponent(effect.getEffect().getDescriptionId()).append(" ").append(effect.getRoman()), badColor)));
                    renderGroupEffectTooltip(poseStack, gui, 10, 0, lC, 0x3101b8, 0x24015b, 0x150615, 0x150615);

                }
                poseStack.popPose();
            } else {
                id.effects.forEachBad((effect) -> {
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
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", false, 1);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("bsize", 1, 4);
        e.addEntry("buffb", 0xffa9a9, 24);
        e.addEntry("flash", 0xffffff, 24);
        e.addEntry("xpos", 170, 12);
        e.addEntry("ypos", 19, 12);
        e.addEntry("zpos", 0, 4);
        e.addEntry("scale", 2, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("spacex", 30, 8);
        e.addEntry("spacey", 44, 8);
        e.addEntry("rowmax", 4, 8);
        e.addEntry("totalmax", 8, 8);
        return e;
    }



}