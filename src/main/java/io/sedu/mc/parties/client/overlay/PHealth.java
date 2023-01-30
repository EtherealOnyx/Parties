package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.anim.HealthAnim;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PHealth extends RenderSelfItem {

    public PHealth(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack, partialTicks);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        //System.out.println(id.getName());
        useAlpha(id.alpha);
        if (id.isDead) {
            rect(i, poseStack, 0, 0, 0xCC080101, 0xCCA11616);
            rect(i, poseStack, 0, 1, 0xFF450202, 0xFF620909);
            return;
        }
        renderHealth(i, poseStack, id);
        if (id.health.active)
            renderHealthAnim(i, poseStack, id, partialTicks);

    }

    private void renderHealth(int i, PoseStack poseStack, ClientPlayerData id) {

        float hB, aB;
        hB = HealthAnim.getPercent(id.health.cur, id.health.max, id.health.absorb);
        if (id.health.absorb > 0) {
            rect(i, poseStack, 0, 0, 0xCCfaf098, 0xCCd9cd68);
            rect(i, poseStack, 0, 1, 0xFF450202, 0xFF620909); //Missing
            aB = hB + HealthAnim.getPercentA(id.health.cur, id.health.max, id.health.absorb);
            rectR(poseStack, i, 0, hB, 0xFFC52C27, 0xFF6C0D15); //Health
            rectB(poseStack, i, 0, hB, aB, 0xCCFFCD42, 0xCCB08610); //Absorb
        } else {
            rect(i, poseStack, 0, 0, 0xCC111111, 0xCC555555);
            rect(i, poseStack, 0, 1, 0xFF450202, 0xFF620909); //Missing
            rectR(poseStack, i, 0, hB, 0xFFC52C27, 0xFF6C0D15); //Health
        }
    }

    private void rectR(PoseStack p, int i, int zLevel, float rightPosition, int startColor, int endColor ) {
        drawRect(p.last().pose(), zLevel, 1+l(i), t(i)+1, l(i)-1+width*rightPosition, b(i)-1, startColor, endColor);
    }

    private void rectB(PoseStack p, int i, int zLevel, float leftPosition, float rightPosition, int startColor, int endColor ) {
        drawRect(p.last().pose(), zLevel, l(i)+width*leftPosition-1, t(i)+1, l(i)-1+width*rightPosition, b(i)-1, startColor, endColor);
    }

    private void renderHealthAnim(int i, PoseStack poseStack, ClientPlayerData id, float partialTicks) {
        if (id.health.animTime - partialTicks < 10) {
            id.health.oldH += (id.health.curH - id.health.oldH) * animPos(10 - id.health.animTime, partialTicks, true, 10, 1);
            id.health.oldA += (id.health.curA - id.health.oldA) * animPos(10 - id.health.animTime, partialTicks, true, 10, 1);
        }

        if (id.health.hInc) {
            if (id.health.effHOld())
                rectB(poseStack, i, 0, id.health.oldH, id.health.curH, 0xFFFFCD72, 0xFFB08672);
            else
                rectB(poseStack, i, 0, id.health.oldH, id.health.curH, 0xFFC5FFC5, 0xFF6CFF6C);

        } else {
            if (id.health.effH())
                rectB(poseStack, i, 0, id.health.curH, id.health.oldH, 0xFFFFCD72, 0xFFB08672);
            else
                rectB(poseStack, i, 0, id.health.curH, id.health.oldH, 0xFFFFC5C5, 0xFFFF6C6C);

        }

        if (id.health.aInc)
            rectB(poseStack, i, 0, id.health.oldA, id.health.curA, 0xFFFFCD72, 0xFFB08672);
        else
            rectB(poseStack, i, 0, id.health.curA, id.health.oldA, 0xFFFFCD72, 0xFFB08672);

    }


}
