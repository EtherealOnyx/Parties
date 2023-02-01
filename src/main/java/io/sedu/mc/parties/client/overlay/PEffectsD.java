package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;

import java.util.concurrent.atomic.AtomicInteger;

public class PEffectsD extends PEffects{

    public PEffectsD(String name, int x, int y, int width, int height, int maxEffects, int maxPerRow) {
        super(name, x, y, width, height, maxEffects, maxPerRow);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        y = frameY + 21;
        x = frameX + 162;
        //OverlayRegistry.enableOverlay(ForgeIngameGui.POTION_ICONS_ELEMENT, false);
        if (id.effects.sizeBad() > 0) {
            start(poseStack, i, id.effects.sizeBad());
            AtomicInteger iX = new AtomicInteger();
            AtomicInteger iY = new AtomicInteger();
            id.effects.forEachBad((effect) -> {
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
