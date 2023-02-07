package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.Config;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PEffectsB extends PEffects{

    public PEffectsB(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.effects.sizeBene() > 0) {
            start(poseStack, i, id.effects.sizeBene(), Config.rG(), Config.mG());
            AtomicInteger iX = new AtomicInteger();
            AtomicInteger iY = new AtomicInteger();
            if (id.effects.largerBene()) {
                id.effects.forEachBeneLim((effect) -> {
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
                    id.effects.forBeneRemainder((effect) -> lC.add(new ColorComponent(new TranslatableComponent(effect.getEffect().getDescriptionId()).append(" ").append(effect.getRoman()), effect.colorType())));
                    renderGroupEffectTooltip(poseStack, gui, 10, 0, lC, 0x3101b8, 0x24015b, 0x150615, 0x150615);

                }
                poseStack.popPose();
            } else {
                id.effects.forEachBene((effect) -> {
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
    boolean checkRow(int x) {
        return x+1 > Config.rG();
    }
}
