package io.sedu.mc.parties.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.Parties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.IIngameOverlay;
import org.apache.logging.log4j.Level;

import static io.sedu.mc.parties.client.RenderData.*;


public class PartyOverlay {

    public static ResourceLocation partyPath = new ResourceLocation(Parties.MODID, "textures/partyicons.png");

    public static final IIngameOverlay HUD_PARTY = (gui, poseStack, partialTicks, width, height) -> {
        if (ClientPlayerData.showSelf) {
            //TODO: Allow rearranging party list. Use a variable that stores new index in an array[oldIndex]
            renderSelf(0, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(0)), gui, poseStack);
        }
        for (int i = 1; i < ClientPlayerData.playerOrderedList.size(); i++) {
            renderMember(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack);
        }
    };

    private static void renderSelf(int partyIndex, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack) {
        //Render XP
        //XP BG
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(1), t(1, partyIndex), r(1), b(1, partyIndex), 0x55002024, 0x5500444d);

        //Name BG
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(2), t(2, partyIndex), r(2),b(2, partyIndex), 0x44002024, 0x3300444d);

        //Render Health
        int currHealthOffset = l(0)+((int)(w(0)*id.getHealth()/id.getMaxHealth()));
        //BG
        if (id.getAbsorb() > 0) {
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0), t(0, partyIndex),r(0)+1, b(0, partyIndex),0xCCfaf098, 0xCCd9cd68);
        } else {
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0), t(0, partyIndex),r(0)+1, b(0, partyIndex),0xCC111111, 0xCC555555);
        }
        //Current Health
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0)+1, t(0, partyIndex)+1,currHealthOffset, b(0, partyIndex)-1,0xFFC52C27, 0xFF6C0D15);
        //Missing Health
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, currHealthOffset, t(0, partyIndex)+1,r(0), b(0, partyIndex)-1,0xFF450202, 0xFF620909);
        String health;
        //Absorption
        if (id.getAbsorb() > 0) {
            int effectiveHealth = (int) (id.getHealth() + id.getAbsorb());
            if (id.getHealth() != id.getMaxHealth()) {
                int fillAbsorptionOffset = l(0)+((int)(w(0)*Math.min(effectiveHealth, id.getMaxHealth())/id.getMaxHealth()));
                GuiUtils.drawGradientRect(poseStack.last().pose(), 0, currHealthOffset-1, t(0, partyIndex), fillAbsorptionOffset+1, b(0, partyIndex), 0x77faf098, 0x77d9cd68);
                GuiUtils.drawGradientRect(poseStack.last().pose(), 0, currHealthOffset, t(0, partyIndex)+1,fillAbsorptionOffset, b(0, partyIndex)-1, 0xCCFFCD42, 0xCCB08610);
            }
            //Render Absorption Heart
            if (effectiveHealth > id.getMaxHealth()) {
                gui.setupOverlayRenderState(true, false);
                gui.blit(poseStack,px(6)+3, py(6, partyIndex), 16, 0, 9, 9);
                gui.blit(poseStack,px(6)+3, py(6, partyIndex), 160, 0, 9, 9);
            }
            health = (int)(id.getHealth()+id.getAbsorb()) + "/" + (int)id.getMaxHealth();
        } else {
            health = (int)(id.getHealth()) + "/" + (int)id.getMaxHealth();
        }


        gui.getFont().draw(poseStack, "§f" + health, l(0)+((int)((w(0)-gui.getFont().width(health))/2f)), t(0, partyIndex)+1, 0);
        gui.getFont().drawShadow(poseStack, "§f" + health, l(0)+((int)((w(0)-gui.getFont().width(health))/2f)), t(0, partyIndex)+1, 0);

        //Head
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, px(0)-1, py(0, partyIndex)-1, px(0)+33, py(0, partyIndex)+33,0xCC111111, 0xCC555555);
        gui.setupOverlayRenderState(true, false, id.getHead());
        GuiUtils.drawTexturedModalRect(poseStack, px(0), py(0, partyIndex), 32, 32, 32, 32, 1);

        //Render Name
        gui.getFont().draw(poseStack, "§f" + id.getName(), px(1), py(1, partyIndex), 0);
        gui.getFont().drawShadow(poseStack, "§f" + id.getName(), px(1), py(1, partyIndex), 0);

        //Render Armor
        gui.setupOverlayRenderState(true, false);

        gui.blit(poseStack, px(2), py(2, partyIndex), 34, 9, 9, 9);
        gui.getFont().draw(poseStack, "§f" + id.getArmor(), px(3), py(3, partyIndex), 0);

        //Render Chicken
        gui.setupOverlayRenderState(true, false);
        gui.blit(poseStack, px(4), py(4, partyIndex), 16, 27, 9, 9);
        gui.blit(poseStack, px(4), py(4, partyIndex), 52, 27, 9, 9);
        gui.getFont().draw(poseStack, "§f" + id.getHungerForced(), px(5), py(5, partyIndex), 0);

        gui.setupOverlayRenderState(true, false, partyPath);
        String xp = String.valueOf(id.getLevelForced());
        int amt = (xp.length()-1)*3;
        gui.blit(poseStack, px(7)-amt, py(7, partyIndex), 9, 0, 9, 9);
        //XP Amt
        gui.getFont().draw(poseStack,xp, px(8)-amt, py(8, partyIndex), 0x33DD33);
        //Render Leader
        if (id.isLeader()) {
            gui.setupOverlayRenderState(true, false, partyPath);
            gui.blit(poseStack, px(0)+25, py(0, partyIndex)+25
                    , 0, 0, 9, 9);
        }
    }

    private static void renderMember(int partyIndex, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack) {
        //Render XP
        //XP BG
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(1), t(1, partyIndex), r(1), b(1, partyIndex), 0x55002024, 0x5500444d);

        //Name BG
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(2), t(2, partyIndex), r(2),b(2, partyIndex), 0x44002024, 0x3300444d);
        //Render Health
        int currHealthOffset = l(0)+((int)(w(0)*id.getHealth()/id.getMaxHealth()));
        //BG
        if (id.getAbsorb() > 0) {
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0), t(0, partyIndex),r(0)+1, b(0, partyIndex),0xCCfaf098, 0xCCd9cd68);
        } else {
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0), t(0, partyIndex),r(0)+1, b(0, partyIndex),0xCC111111, 0xCC555555);
        }
        //Current Health
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0)+1, t(0, partyIndex)+1,currHealthOffset, b(0, partyIndex)-1,0xFFC52C27, 0xFF6C0D15);
        //Missing Health
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, currHealthOffset, t(0, partyIndex)+1,r(0), b(0, partyIndex)-1,0xFF450202, 0xFF620909);
        String health;
        //Absorption
        if (id.getAbsorb() > 0) {
            int effectiveHealth = (int) (id.getHealth() + id.getAbsorb());
            if (id.getHealth() != id.getMaxHealth()) {
                int fillAbsorptionOffset = l(0)+((int)(w(0)*Math.min(effectiveHealth, id.getMaxHealth())/id.getMaxHealth()));
                GuiUtils.drawGradientRect(poseStack.last().pose(), 0, currHealthOffset-1, t(0, partyIndex), fillAbsorptionOffset+1, b(0, partyIndex), 0x77faf098, 0x77d9cd68);
                GuiUtils.drawGradientRect(poseStack.last().pose(), 0, currHealthOffset, t(0, partyIndex)+1,fillAbsorptionOffset, b(0, partyIndex)-1, 0xCCFFCD42, 0xCCB08610);
                //Render Absorption Heart
                if (effectiveHealth > id.getMaxHealth()) {
                    gui.setupOverlayRenderState(true, false);
                    gui.blit(poseStack,px(6), py(6, partyIndex), 16, 0, 9, 9);
                    gui.blit(poseStack,px(6), py(6, partyIndex), 160, 0, 9, 9);
                }
            }
            health = (int)(id.getHealth()+id.getAbsorb()) + "/" + (int)id.getMaxHealth();
        } else {
            health = (int)(id.getHealth()) + "/" + (int)id.getMaxHealth();
        }


        gui.getFont().draw(poseStack, "§f" + health, l(0)+((int)((w(0)-gui.getFont().width(health))/2f)), t(0, partyIndex)+1, 0);
        gui.getFont().drawShadow(poseStack, "§f" + health, l(0)+((int)((w(0)-gui.getFont().width(health))/2f)), t(0, partyIndex)+1, 0);

        //Head
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, px(0)-1, py(0, partyIndex)-1, px(0)+33, py(0, partyIndex)+33,0xCC111111, 0xCC555555);
        gui.setupOverlayRenderState(true, false, id.getHead());
        GuiUtils.drawTexturedModalRect(poseStack, px(0), py(0, partyIndex), 32, 32, 32, 32, 1);

        //Render Name
        gui.getFont().draw(poseStack, "§f" + id.getName(), px(1), py(1, partyIndex), 0);
        gui.getFont().drawShadow(poseStack, "§f" + id.getName(), px(1), py(1, partyIndex), 0);

        //Render Armor
        gui.setupOverlayRenderState(true, false);
        gui.blit(poseStack, px(2), py(2, partyIndex), 34, 9, 9, 9);
        gui.getFont().draw(poseStack, "§f" + id.getArmor(), px(3), py(3, partyIndex), 0);
        //gui.getFont().drawShadow(poseStack, "§f" + 9, (startX+58), yOffset+36, 0);
        //gui.getFont().drawShadow(poseStack, "§7" + 9, (startX+57), yOffset+36, 0);

        //Render Chicken
        gui.setupOverlayRenderState(true, false);
        gui.blit(poseStack, px(4), py(4, partyIndex), 16, 27, 9, 9);
        gui.blit(poseStack, px(4), py(4, partyIndex), 52, 27, 9, 9);
        gui.getFont().draw(poseStack, "§f" + id.getHunger(), px(5), py(5, partyIndex), 0);

        //Render XP
        gui.setupOverlayRenderState(true, false, partyPath);
        String xp = String.valueOf(id.getLevel());
        int amt = (xp.length()-1)*3;
        gui.blit(poseStack, px(7)-amt, py(7, partyIndex), 9, 0, 9, 9);
        //XP Amt
        gui.getFont().draw(poseStack,xp, px(8)-amt, py(8, partyIndex), 0x33DD33);
        gui.getFont().drawShadow(poseStack,xp, px(8)-amt, py(8, partyIndex), 0x33DD33);
        //Render Leader
        if (id.isLeader()) {
            gui.setupOverlayRenderState(true, false, partyPath);
            gui.blit(poseStack, px(0)+26, py(0, partyIndex)+25
                    , 0, 0, 9, 9);
        }
    }
}
