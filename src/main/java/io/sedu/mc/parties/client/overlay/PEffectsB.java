package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.concurrent.atomic.AtomicInteger;

public class PEffectsB extends PEffects{

    public PEffectsB(String name, int x, int y, int width, int height, int maxEffects, int maxPerRow) {
        super(name, x, y, width, height, maxEffects, maxPerRow);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.effects.sizeBene() > 0) {
            start(poseStack, i, id.effects.sizeBene());
            AtomicInteger iX = new AtomicInteger();
            AtomicInteger iY = new AtomicInteger();
            id.effects.forEachBene((effect) -> {
                //If we reached the limit
                if (check(iX.get(), iY.get())) {
                    return;
                }

                //If we reached max per row
                if (checkRow(iX.get())) {
                    iX.set(0);
                    iY.getAndIncrement();
                }

                renderEffect(effect, gui, poseStack, i, iX.get(), iY.get(), partialTicks);

                iX.getAndIncrement();
                resetColor();
            });
            end(poseStack);
        }
    }
}
