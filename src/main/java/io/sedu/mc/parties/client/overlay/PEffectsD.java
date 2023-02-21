package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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

public class PEffectsD extends PEffects{

    public PEffectsD(String name, int x, int y, int width, int height, int max, int row) {
        super(name, x, y, width, height, max, row);
    }

    @Override
    int getColor() {
        return badColor;
    }



    @Override
    protected void getColorEntry(ConfigOptionsList c) {
        c.addColorEntry("config.sedparties.name.buffb", badColor);
        c.addColorEntry("config.sedparties.name.flash", flashColor);
    }


    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        RenderSystem.enableDepthTest();
        TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get(MobEffects.WITHER);
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        Gui.blit(poseStack, b.x + 7, b.y+2, 0, 18, 18, sprite);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.effects.sizeBad() > 0) {
            start(poseStack, i, id.effects.sizeBene());
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
                poseStack.scale(2f,2f,2f);
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



}
