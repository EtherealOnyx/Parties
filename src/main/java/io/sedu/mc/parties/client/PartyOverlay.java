package io.sedu.mc.parties.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.Parties;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.IIngameOverlay;

import java.util.ArrayList;

import static io.sedu.mc.parties.client.RenderData.*;
import static io.sedu.mc.parties.Parties.LOGGER;


public class PartyOverlay {

    public static ResourceLocation partyPath = new ResourceLocation(Parties.MODID, "textures/partyicons.png");
    public static ResourceLocation worldPath = new ResourceLocation(Parties.MODID, "textures/worldicons.png");

    public static final IIngameOverlay HUD_PARTY = (gui, poseStack, partialTicks, width, height) -> {
        if (ClientPlayerData.playerOrderedList.size() > 0) {
            //TODO: Allow rearranging party list. Use a variable that stores new index in an array[oldIndex]
            renderSelf(0, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(0)), gui, poseStack, partialTicks);
            for (int i = 1; i < ClientPlayerData.playerOrderedList.size(); i++) {
                renderMember(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack, partialTicks);
            }
       }
    };

    private static void renderSelf(int partyIndex, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                                   float partialTicks) {

        //Name BG
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(2), t(2, partyIndex), r(2),b(2, partyIndex), 0x44002024, 0x3300444d);
        //Render XP
        //XP BG
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(1), t(1, partyIndex), r(1), b(1, partyIndex), 0x55002024, 0x5500444d);
        //Render Health
        int currHealthOffset = l(0)+((int)(w(0)*id.getHealth()/id.getMaxHealth()));
        //BG
        if (id.getAbsorb() > 0) {
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0), t(0, partyIndex),r(0)+1, b(0, partyIndex),0xCCfaf098, 0xCCd9cd68);
        } else {
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0), t(0, partyIndex),r(0)+1, b(0, partyIndex),0xCC111111, 0xCC555555);
        }

        String health;
        int healthC;
        if (!id.isDead) {
            //Current Health
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0)+1, t(0, partyIndex)+1,currHealthOffset, b(0, partyIndex)-1,0xFFC52C27, 0xFF6C0D15);
            //Missing Health
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, currHealthOffset, t(0, partyIndex)+1,r(0), b(0, partyIndex)-1,0xFF450202, 0xFF620909);
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
                    gui.blit(poseStack,px(6)+2, py(6, partyIndex), 16, 0, 9, 9);
                    gui.blit(poseStack,px(6)+2, py(6, partyIndex), 160, 0, 9, 9);
                }
                health = (int)Math.ceil(id.getHealth()+id.getAbsorb()) + "/" + (int)id.getMaxHealth();
                healthC = 0xFFF399;
            } else {
                health = (int)Math.ceil(id.getHealth()) + "/" + (int)id.getMaxHealth();
                healthC = 0xFFE3E3;
            }

            //Render Armor
            gui.setupOverlayRenderState(true, false);
            gui.blit(poseStack, px(2), py(2, partyIndex), 34, 9, 9, 9);
            gui.getFont().draw(poseStack, String.valueOf(id.getArmor()), px(3), py(3, partyIndex), 0xDDF3FF);

            //Render Chicken
            gui.setupOverlayRenderState(true, false);
            chicken(poseStack, partyIndex, gui, id.getHungerForced());
            gui.getFont().draw(poseStack, String.valueOf(id.getHungerForced()), px(5), py(5, partyIndex), 0xDDF3FF);
            //Head
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, px(0)-1, py(0, partyIndex)-1, px(0)+33, py(0, partyIndex)+33,0xCC111111, 0xCC555555);
            gui.setupOverlayRenderState(true, false, id.getHead());
            GuiUtils.drawTexturedModalRect(poseStack, px(0), py(0, partyIndex), 32, 32, 32, 32, 1);

            //Render World
            if (id.dimAnimActive)  {
                worldAnim(poseStack, partyIndex, gui, 0, id, partialTicks);
            } else {
                world(poseStack, partyIndex, gui, 0, id);
            }
        } else {
            //Missing Health
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0)+1, t(0, partyIndex)+1,r(0), b(0, partyIndex)-1,0xFF450202, 0xFF620909);
            health = "Dead";
            healthC = 0x530404;
            poseStack.pushPose();
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, px(0)-1, py(0, partyIndex)-1, px(0)+33, py(0, partyIndex)+33,0xCC111111, 0xCC555555);

            gui.setupOverlayRenderState(true, false, id.getHead());
            RenderSystem.setShaderColor(.75f, .5f, .5f, .5f);
            GuiUtils.drawTexturedModalRect(poseStack, px(0), py(0, partyIndex), 32, 32, 32, 32, 1);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            poseStack.popPose();
            gui.setupOverlayRenderState(true, false);
            gui.blit(poseStack,166, py(1, partyIndex), 34, 0, 9, 9);
            gui.blit(poseStack,166, py(1, partyIndex), 124, 0, 9, 9);
        }

        gui.getFont().draw(poseStack, health, l(0)+((int)((w(0)-gui.getFont().width(health))/2f)), t(0, partyIndex)+1, healthC);
        gui.getFont().drawShadow(poseStack, health, l(0)+((int)((w(0)-gui.getFont().width(health))/2f)), t(0, partyIndex)+1, healthC);

        //Render XP
        gui.setupOverlayRenderState(true, false, partyPath);
        String xp = String.valueOf(id.getLevelForced());
        int amt = (xp.length()-1)*3;
        gui.blit(poseStack, px(7)-amt, py(7, partyIndex), 9, 0, 9, 9);
        //XP Amt
        gui.getFont().draw(poseStack,xp, px(8)-amt, py(8, partyIndex), 0x80FF8B);
        gui.getFont().drawShadow(poseStack,xp, px(8)-amt, py(8, partyIndex), 0x80FF8B);
        //Render Name
        gui.getFont().draw(poseStack, id.getName(), px(1), py(1, partyIndex), 0xDDF3FF);
        gui.getFont().drawShadow(poseStack, id.getName(), px(1), py(1, partyIndex), 0xDDF3FF);

        //Render Leader
        if (id.isLeader()) {
            gui.setupOverlayRenderState(true, false, partyPath);
            gui.blit(poseStack, px(0)+26, py(0, partyIndex)+25
                    , 0, 0, 9, 9);
        }
    }

    private static void renderMember(int partyIndex, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                                     float partialTicks) {
        poseStack.pushPose();
        RenderSystem.setShaderColor(1f, 1f, 1f, id.alpha);
        //Name BG
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(2), t(2, partyIndex), r(2),b(2, partyIndex), 0x44002024, 0x3300444d);
        if (id.isOnline) {
            //Render XP
            //XP BG
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(1), t(1, partyIndex), r(1), b(1, partyIndex), 0x55002024, 0x5500444d);
            //Render Health
            int currHealthOffset = l(0)+((int)(w(0)*id.getHealth()/id.getMaxHealth()));
            //BG
            if (id.getAbsorb() > 0) {
                GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0), t(0, partyIndex),r(0)+1, b(0, partyIndex),0xCCfaf098, 0xCCd9cd68);
            } else {
                GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0), t(0, partyIndex),r(0)+1, b(0, partyIndex),0xCC111111, 0xCC555555);
            }

            String health;
            int healthC;
            if (!id.isDead) {
                //Current Health
                GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0)+1, t(0, partyIndex)+1,currHealthOffset, b(0, partyIndex)-1,0xFFC52C27, 0xFF6C0D15);
                //Missing Health
                GuiUtils.drawGradientRect(poseStack.last().pose(), 0, currHealthOffset, t(0, partyIndex)+1,r(0), b(0, partyIndex)-1,0xFF450202, 0xFF620909);
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
                        gui.blit(poseStack,px(6)+2, py(6, partyIndex), 16, 0, 9, 9);
                        gui.blit(poseStack,px(6)+2, py(6, partyIndex), 160, 0, 9, 9);
                    }
                    health = (int)Math.ceil(id.getHealth()+id.getAbsorb()) + "/" + (int)id.getMaxHealth();
                    healthC = 0xFFF399;
                } else {
                    health = (int)Math.ceil(id.getHealth()) + "/" + (int)id.getMaxHealth();
                    healthC = 0xFFE3E3;
                }

                //Render Armor
                setup(id.alpha);
                gui.blit(poseStack, px(2), py(2, partyIndex), 34, 9, 9, 9);

                //Render Chicken
                chicken(poseStack, partyIndex, gui, id.getHunger());

                //Armor Text
                gui.getFont().draw(poseStack, String.valueOf(id.getArmor()), px(3), py(3, partyIndex), 0xDDF3FF | (id.alphaI << 24));
                //Chicken text
                gui.getFont().draw(poseStack, String.valueOf(id.getHunger()), px(5), py(5, partyIndex), 0xDDF3FF | (id.alphaI << 24));
                //world(poseStack, partyIndex, gui, id.dimension);
                //Render World
                if (id.dimAnimActive)  {
                    worldAnim(poseStack, partyIndex, gui, id.dimension, id, partialTicks);
                } else {
                    world(poseStack, partyIndex, gui, id.dimension, id);
                }
            } else {
                //Missing Health
                GuiUtils.drawGradientRect(poseStack.last().pose(), 0, l(0)+1, t(0, partyIndex)+1,r(0), b(0, partyIndex)-1,0xFF450202, 0xFF620909);
                health = "Dead";
                healthC = 0x8F6D6D;
                poseStack.pushPose();
                GuiUtils.drawGradientRect(poseStack.last().pose(), 0, px(0)-1, py(0, partyIndex)-1, px(0)+33, py(0, partyIndex)+33,0xCC111111, 0xCC555555);

                gui.setupOverlayRenderState(true, false, id.getHead());
                RenderSystem.setShaderColor(.75f, .5f, .5f, .5f);
                GuiUtils.drawTexturedModalRect(poseStack, px(0), py(0, partyIndex), 32, 32, 32, 32, 1);
                RenderSystem.setShaderColor(1f, 1f, 1f, id.alpha);
                poseStack.popPose();
                setup(id.alpha);
                gui.blit(poseStack,166, py(1, partyIndex), 34, 0, 9, 9);
                gui.blit(poseStack,166, py(1, partyIndex), 124, 0, 9, 9);
            }

            gui.getFont().draw(poseStack, health, l(0)+((int)((w(0)-gui.getFont().width(health))/2f)), t(0, partyIndex)+1, healthC | (id.alphaI << 24));
            gui.getFont().drawShadow(poseStack, health, l(0)+((int)((w(0)-gui.getFont().width(health))/2f)), t(0, partyIndex)+1, healthC | (id.alphaI << 24));

            //Render XP
            setup(id.alpha, partyPath);
            String xp = String.valueOf(id.getXpLevel());
            int amt = (xp.length()-1)*3;
            gui.blit(poseStack, px(7)-amt, py(7, partyIndex), 9, 0, 9, 9);
            //XP Amt
            gui.getFont().draw(poseStack,xp, px(8)-amt, py(8, partyIndex), 0x80FF8B | (id.alphaI << 24));
            gui.getFont().drawShadow(poseStack,xp, px(8)-amt, py(8, partyIndex), 0x80FF8B | (id.alphaI << 24));
        } else {
            //Head
            poseStack.pushPose();
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, px(0)-1, py(0, partyIndex)-1, px(0)+33, py(0, partyIndex)+33,0xCC111111, 0xCC555555);

            gui.setupOverlayRenderState(true, false, id.getHead());
            RenderSystem.setShaderColor(.25f, .25f, .25f, .25f);
            GuiUtils.drawTexturedModalRect(poseStack, px(0), py(0, partyIndex), 32, 32, 32, 32, 1);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            poseStack.popPose();
            //Render Offline
            String off = "Offline...";
            gui.getFont().draw(poseStack, "§f§o" + off, l(0)+((int)((w(2)-gui.getFont().width(off))/2f))-1, t(2, partyIndex)+1+12, 0);
            gui.getFont().drawShadow(poseStack, "§f§o" + off, l(0)+((int)((w(2)-gui.getFont().width(off))/2f))-1, t(2, partyIndex)+1+12, 0);

            //Disconnected Icon
            gui.setupOverlayRenderState(true, false);
            gui.blit(poseStack, 165, py(1, partyIndex)-1, 0, 216, 10, 8);
        }
        //Render Name
        gui.getFont().draw(poseStack, id.getName(), px(1), py(1, partyIndex), 0xDDF3FF | (id.alphaI << 24));
        gui.getFont().drawShadow(poseStack, id.getName(), px(1), py(1, partyIndex), 0xDDF3FF | (id.alphaI << 24));

        //Render Leader
        if (id.isLeader()) {
            setup(id.alpha, partyPath);
            gui.blit(poseStack, px(0)+26, py(0, partyIndex)+25
                    , 0, 0, 9, 9);
        }

        poseStack.popPose();
    }

    private static void setup(float alpha) {
        setup(alpha, Gui.GUI_ICONS_LOCATION);
    }

    private static void setup(float alpha, ResourceLocation loc) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, loc);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f,1f,1f, alpha);
    }

    private static void chicken(PoseStack poseStack, int partyIndex, ForgeIngameGui gui, int hunger) {
        if (hunger > 16) {
            gui.blit(poseStack, px(4), py(4, partyIndex), 16, 27, 9, 9);
            gui.blit(poseStack, px(4), py(4, partyIndex), 52, 27, 9, 9);
        }
        else if(hunger > 4) {
            gui.blit(poseStack, px(4), py(4, partyIndex), 16, 27, 9, 9);
            gui.blit(poseStack, px(4), py(4, partyIndex), 61, 27, 9, 9);
        } else {

        }
    }

    private static void world(PoseStack poseStack, int partyIndex, ForgeIngameGui gui, int i, ClientPlayerData id) {
        //if (i == 0)
            //return;
        //Head
        GuiUtils.drawGradientRect(poseStack.last().pose(), 0, px(0)-1, py(0, partyIndex)-1, px(0)+33, py(0, partyIndex)+33,0x111111 | ((int)(id.alphaI*.75) << 24), 0x555555 | ((int)(id.alphaI*.75) << 24));
        setup(id.alpha, id.getHead()); //Why does this require setShaderColor again...
        gui.blit(poseStack, px(0), py(0, partyIndex), 32, 32, 32, 32);
        //End Head
        setWorldShader(poseStack);
        RenderSystem.setShaderColor(1f,1f,1f, .75f);
        poseStack.scale(.25f, .25f, .25f);
        gui.blit(poseStack, (px(0)-10)*4, (py(0, partyIndex)+20)*4, 64*i, 0, 64, 64);
        poseStack.popPose();
    }



    private static void worldAnim(PoseStack poseStack, int partyIndex, ForgeIngameGui gui, int i, ClientPlayerData id, float partialTicks) {
        //Parties.LOGGER.debug(gui.getGuiTicks());
        int currTick;
        float alphaPercent;
        if (id.tick(gui.getGuiTicks())) {
            if (id.tickWorldAnim())
                id.dimAnimActive = false;
        }
        //System.out.println(id.dimAnim);
        if (id.dimAnim > 90) {
            currTick = 10 - (id.dimAnim - 90); //0-10
            alphaPercent = ((id.dimAnim-80-partialTicks)/20f);
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, px(0)-1, py(0, partyIndex)-1, px(0)+33, py(0, partyIndex)+33,0x11111 | (int)((id.alphaI*.75)*alphaPercent) << 24, 0x555555 | (int)((id.alphaI*.75)*alphaPercent) << 24);
            setup(id.alpha*alphaPercent, id.getHead()); //Why does this require setShaderColor again...
            gui.blit(poseStack, px(0), py(0, partyIndex), 32, 32, 32, 32);
            setWorldShader(poseStack);
            RenderSystem.setShaderColor(1f,1f,1f, .75f +  currTick/40f);
            poseStack.scale(.25f+(currTick+partialTicks)/40f, .25f+(currTick+partialTicks)/40f, .25f+(currTick+partialTicks)/40f);
            gui.blit(poseStack, (int) ((px(0)-10+currTick+partialTicks)*(4-((currTick+partialTicks)/5f))),
                     + (int)((py(0, partyIndex)+(20-((currTick+partialTicks)*2f)))*(4-((currTick+partialTicks)/5f))), 64*id.oldDimension, 0, 64, 64);
            //Dimension text

        } else if (id.dimAnim > 10) {
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, px(0)-1, py(0, partyIndex)-1, px(0)+33, py(0, partyIndex)+33,0x111111 | (int)((id.alphaI*.75)*.5f) << 24, 0x555555 | (int)((id.alphaI*.75)*.5f) << 24);
            setup(id.alpha*.5f, id.getHead()); //Why does this require setShaderColor again...
            gui.blit(poseStack, px(0), py(0, partyIndex), 32, 32, 32, 32);
            setWorldShader(poseStack);
            currTick = id.dimAnim - 10; //80 - 0
            RenderSystem.setShaderColor(1f,1f,1f, ((currTick+(1*partialTicks))/80f));
            poseStack.scale(.5f, .5f, .5f);
            gui.blit(poseStack, (px(0))*2, (py(0, partyIndex))*2, 64*id.oldDimension, 0, 64, 64);
            RenderSystem.setShaderColor(1f,1f,1f, 1f - ((currTick+(1*partialTicks))/80f));
            gui.blit(poseStack, (px(0))*2, (py(0, partyIndex))*2, 64*id.dimension, 0, 64, 64);
            int x, y;
            float transX;
            poseStack.scale(2f, 2f, 2f);
            if (currTick > 70) {
                currTick = (currTick-70); // 10 - 0
                alphaPercent = (10 - currTick+partialTicks)/10f;
                for (int j = 0; j < id.dimName.size(); j++) {
                    if (j % 2 == 1)
                        transX =  -(15+(2*(currTick-partialTicks)));
                    else
                        transX = (15+(2*(currTick-partialTicks)));
                    x = (int) (px(0)+((32-gui.getFont().width(id.dimName.get(j)))/2f) + transX);
                    y = (py(0, partyIndex)+(j*gui.getFont().lineHeight)+1)+((int)((32-gui.getFont().lineHeight*id.dimName.size())/2f));
                    gui.getFont().draw(poseStack, id.dimName.get(j), x, y, 0xfff390 | (int)(255*alphaPercent) << 24);
                    gui.getFont().drawShadow(poseStack, id.dimName.get(j), x, y, 0xfff390 | (int)(255*alphaPercent) << 24);
                }
            } else if (currTick > 10) {
                currTick = currTick - 10; // 60 - 0
                for (int j = 0; j < id.dimName.size(); j++) {
                    if (j % 2 == 1)
                        transX = (30 - currTick-partialTicks)/2f;
                    else
                        transX = (currTick-partialTicks - 30)/2f;
                    x = (int) (px(0)+((32-gui.getFont().width(id.dimName.get(j)))/2f) + transX);
                    //poseStack.translate()
                    y = (py(0, partyIndex) + (j * gui.getFont().lineHeight) + 1) + ((int) ((32 - gui.getFont().lineHeight * id.dimName.size()) / 2f));
                    gui.getFont().draw(poseStack, id.dimName.get(j), x, y, 0xfff390 | (255 << 24));
                    gui.getFont().drawShadow(poseStack, id.dimName.get(j), x, y, 0xfff390 | (255 << 24));
                }
            } else {
                currTick = 10 - currTick; // 0 - 10
                for (int j = 0; j < id.dimName.size(); j++) {
                    if (j % 2 == 1)
                        transX = (int) (15+(2*(currTick+partialTicks)));
                    else
                        transX = (int) -(15+(2*(currTick+partialTicks)));
                    x = (int) (px(0)+((32-gui.getFont().width(id.dimName.get(j)))/2f) + transX);
                    y = (py(0, partyIndex)+(j*gui.getFont().lineHeight)+1)+((int)((32-gui.getFont().lineHeight*id.dimName.size())/2f));
                    gui.getFont().draw(poseStack, id.dimName.get(j), x, y, 0xfff390 | (255 - (255*currTick/10) << 24));
                    gui.getFont().drawShadow(poseStack, id.dimName.get(j), x, y, 0xfff390 | (255 - (255*currTick/10) << 24));
                }
            }

        } else {
            currTick = 10 - id.dimAnim;
            alphaPercent = (currTick+partialTicks+10)/20f;
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, px(0)-1, py(0, partyIndex)-1, px(0)+33, py(0, partyIndex)+33,0x11111 | (int)((id.alphaI*.75)*alphaPercent) << 24, 0x555555 | (int)((id.alphaI*.75)*alphaPercent) << 24);
            setup(id.alpha*alphaPercent, id.getHead()); //Why does this require setShaderColor again...
            gui.blit(poseStack, px(0), py(0, partyIndex), 32, 32, 32, 32);
            setWorldShader(poseStack);
            RenderSystem.setShaderColor(1f,1f,1f, .75f + id.dimAnim/40f);
            poseStack.scale(.25f+(id.dimAnim-partialTicks)/40f, .25f+(id.dimAnim-partialTicks)/40f, .25f+(id.dimAnim-partialTicks)/40f);

            gui.blit(poseStack, (int) ((px(0)-10+id.dimAnim-partialTicks)*(4-((id.dimAnim-partialTicks)/5f))),
                     + (int)((py(0, partyIndex)+(20-((id.dimAnim-partialTicks)*2f)))*(4-((id.dimAnim-partialTicks)/5f))), 64*id.dimension, 0, 64, 64);
        }
        poseStack.popPose();
    }

    private static void setWorldShader(PoseStack poseStack) {
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, worldPath);
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    //public void setupOverlayRenderState()
}
