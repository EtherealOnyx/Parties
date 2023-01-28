package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.GuiUtils;

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
            rect(i, poseStack, 0, 1, l(i), r(i), 0xFF450202, 0xFF620909);
            return;
        }
        int currHealthOffset = l(i) + ((int)(width*id.getHealth()/id.getMaxHealth()));
        int fillAbsorptionOffset = currHealthOffset;

        if (id.getAbsorb() > 0) {
            //int effectiveHealth = (int) (id.getHealth() + id.getAbsorb());
            if (id.getHealth() != id.getMaxHealth()) {
                fillAbsorptionOffset = (int) Math.ceil(Math.min(width*id.getAbsorb() + fillAbsorptionOffset, r(i)));
                //rect(i, poseStack, 3, 1,currHealthOffset-2, fillAbsorptionOffset+2,  0x77faf098, 0x77d9cd68);
            }
            rect(i, poseStack, 0, 0, 0xCCfaf098, 0xCCd9cd68);
        } else {
            rect(i, poseStack, 0, 0, 0xCC111111, 0xCC555555);
        }
        rect(i, poseStack, 0, 1,l(i), currHealthOffset, 0xFFC52C27, 0xFF6C0D15);
        rect(i, poseStack, 0, 1, currHealthOffset-2, r(i), 0xFF450202, 0xFF620909);
        rect(i, poseStack, 0, 1,currHealthOffset-2, fillAbsorptionOffset,  0xCCFFCD42, 0xCCB08610);

    }


}
