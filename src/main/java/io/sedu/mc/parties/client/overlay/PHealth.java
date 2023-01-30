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
        //L 1 -> .75
        if (id.health.animTime-partialTicks < 10) {
            //System.out.println(id.health.animTime-partialTicks);
            id.health.oldL += (id.health.curL - id.health.oldL) * animPos(10 - id.health.animTime, partialTicks, true, 10, 1);
            id.health.oldR += (id.health.curR - id.health.oldR) * animPos(10 - id.health.animTime, partialTicks, true, 10, 1);
        }
        if (id.health.iAnim)
            rectB(poseStack, i, 0, id.health.oldL, id.health.oldR, 0xFFC5FFFF, 0xFF6CFFFF);
        else
            rectB(poseStack, i, 0, id.health.oldL, id.health.oldR, 0xFFC52C27, 0xFF6CFFFF);
    }


}
