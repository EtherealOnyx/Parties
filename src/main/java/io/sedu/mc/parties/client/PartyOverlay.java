package io.sedu.mc.parties.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.IIngameOverlay;


public class PartyOverlay {
    static int startX = 16;
    static int startY = 8;
    public static final IIngameOverlay HUD_PARTY = (gui, poseStack, partialTicks, width, height) -> {
        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++) {
            renderMember(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack);
        }
    };

    static int healthLeft = 36;
    static int healthRight = 144;
    static int healthTop = 38;
    static int healthBottom = 46;
    /*static int listPadding = 8;
    static int startX = 16;
    static int startY = 16;
    static int height = 64;
    static int width = 160;

     */
    public static void renderMember(int partyIndex, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack) {
        int yOffset = partyIndex*(64);
        //Render Health
        int currHealthOffset = startX+46+((int)(111*id.getHealth()/id.getMaxHealth()));
        //BG
        if (id.getAbsorb() > 0) {
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, startX+46, yOffset+startY+37,startX+46+112, startY+yOffset+47,0xCCfaf098, 0xCCd9cd68);
        } else {
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, startX+46, yOffset+startY+37,startX+46+112, startY+yOffset+47,0xCC111111, 0xCC555555);
        }
        //Current Health
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, startX+46+1, yOffset+startY+38,currHealthOffset, startY+yOffset+47-1,0xFFC52C27, 0xFF6C0D15);
        //Missing Health
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, currHealthOffset, yOffset+startY+38,startX+46+111, startY+yOffset+47-1,0xFF450202, 0xFF620909);
        //Absorption
        int effectiveHealth = (int) (id.getHealth() + id.getAbsorb());
        if (id.getHealth() != id.getMaxHealth()) {
            int fillAbsorptionOffset = startX+46+((int)(111*Math.min(effectiveHealth, id.getMaxHealth())/id.getMaxHealth()));
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, currHealthOffset-1, -1+yOffset+startY+38, fillAbsorptionOffset+1, startY+yOffset+47, 0x77faf098, 0x77d9cd68);
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, currHealthOffset, yOffset+startY+38,fillAbsorptionOffset, +startY+yOffset+47-1, 0xCCFFCD42, 0xCCB08610);

        }

        String health = (int)(id.getHealth()+id.getAbsorb()) + "/" + (int)id.getMaxHealth();
        gui.getFont().draw(poseStack, "§f" + health, startX+46+((int)((112-gui.getFont().width(health))/2f)), yOffset+startY+38, 0);
        gui.getFont().drawShadow(poseStack, "§f" + health, startX+46+((int)((112-gui.getFont().width(health))/2f)), yOffset+startY+38, 0);

        //GuiUtils.drawGradientRect(poseStack.last().pose(), 0, (16), (16)+yOffset, (16)+160, (16)+yOffset+(64),0xFF555555, 0xFF888888);
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, (16)+(7), yOffset+16+7, (16)+7+34, yOffset+16+7+34,0xCC111111, 0xCC555555);
        gui.setupOverlayRenderState(true, false, id.getHead());
        GuiUtils.drawTexturedModalRect(poseStack, (16)+(8), yOffset+16+8, 32, 32, 32, 32, 1);

        //Render Name
        gui.getFont().draw(poseStack, "§f" + id.getName(), (startX+47), yOffset+25, 0);
        gui.getFont().drawShadow(poseStack, "§f" + id.getName(), (startX+47), yOffset+25, 0);
        //Render Armor
        gui.setupOverlayRenderState(true, false);
        //Track armorAmt and change armorXOffset depending on how much armor they have.
        //0-9 -> 0 (15,26) | 10-99 -> -3 (12,23) | 100-999 -> -6 | 1000-9999 -> -9
        //OR BASICALLY: (int).toString().length-1*-3
        gui.blit(poseStack, startX + 47, yOffset+35, 34, 9, 9, 9);
        gui.getFont().draw(poseStack, "§f" + 9, (startX+58), yOffset+36, 0);
        //gui.getFont().drawShadow(poseStack, "§f" + 9, (startX+58), yOffset+36, 0);
        //gui.getFont().drawShadow(poseStack, "§7" + 9, (startX+57), yOffset+36, 0);

        //Render Chicken
        gui.setupOverlayRenderState(true, false);
        gui.blit(poseStack, startX + 133, yOffset+35, 16, 27, 9, 9);
        gui.blit(poseStack, startX + 133, yOffset+35, 52, 27, 9, 9);

        gui.getFont().draw(poseStack, "§f" + 17, (startX+145), yOffset+36, 0);
        //Render Absorption Heart
        if (effectiveHealth > id.getMaxHealth()) {
            gui.setupOverlayRenderState(true, false);
            gui.blit(poseStack,startX + 50 +111, yOffset+46, 16, 0, 9, 9);
            gui.blit(poseStack,startX + 50 +111, yOffset+46, 160, 0, 9, 9);
        }
        //gui.getFont().drawShadow(poseStack, "§f" + 17, (startX+145), yOffset+36, 0);


    }
}
