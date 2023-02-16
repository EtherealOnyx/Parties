package io.sedu.mc.parties.client.overlay;

import Util.Render;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.anim.HealthAnim;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;
import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PHealth extends RenderIconTextItem {

    int absorbColor;
    int deadColor;

    public PHealth(String name, int x, int y, int width, int height, int color, int absorbColor, int deadColor) {
        super(name, x, y, width, height, color, true);
        this.absorbColor = absorbColor;
        this.deadColor = deadColor;
    }

    @Override
    int getColor() {
        return 0xe3403d;
    }

    @Override
    public String getType() {
        return "Bar";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        Render.sizeRectNoA(poseStack.last().pose(), b.x+7, b.y+9, 22, 7, 0x111111, 0x555555);
        Render.sizeRectNoA(poseStack.last().pose(), b.x+8, b.y+10, 20, 5, 0xC52C27, 0x6C0D15);
        setup(GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        blit(poseStack,b.x+3, b.y+8, 16, 0, 9, 9);
        blit(poseStack,b.x+3, b.y+8, 52, 0, 9, 9);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack, partialTicks);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isDead) {
            rect(i, poseStack, 0, 0, 0xCC080101, 0xCCA11616);
            rect(i, poseStack, 0, 1, 0xFF450202, 0xFF620909);
            textCentered(i, tX(i), tY(i), gui, poseStack, "Dead", deadColor);
            return;
        }
        renderHealth(i, poseStack, id);
        if (id.health.active)
            renderHealthAnim(i, poseStack, id, partialTicks);

        if (id.health.absorb > 0) {
            textCentered(i, tX(i), tY(i), gui, poseStack, (int)Math.ceil(id.health.cur+id.health.absorb) + "/" + (int)id.health.max, absorbColor);
        } else {
            textCentered(i, tX(i), tY(i), gui, poseStack, (int)Math.ceil(id.health.cur) + "/" + (int)id.health.max, color);
        }

        //Dimmer
        rect(i, poseStack, 0, 0, 255 - id.alphaI << 24, 255 - id.alphaI << 24);

        if (notEditing() && withinBounds(l(i), t(i), r(i), b(i), 2)) {
            renderTooltip(poseStack, gui, 10, 0, "Health: " + (id.health.cur + id.health.absorb) + "/" + id.health.max, 0xfc807c, 0x4d110f, 0xffbfbd);
        }



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


    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + (width>>1);
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) + 1;
    }
}
