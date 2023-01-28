package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.GuiUtils;

public class PDimIcon extends RenderSelfItem {


    public PDimIcon(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack, partialTicks);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.dimAnimActive)  {
            worldAnim(poseStack, i, gui, id, partialTicks);
        } else {
            world(poseStack, i, gui, id);
        }
    }

    private static void setWorldShader(PoseStack poseStack) {
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, worldPath);
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    private void world(PoseStack poseStack, int pI, ForgeIngameGui gui, ClientPlayerData id) {

        setWorldShader(poseStack);
        RenderSystem.setShaderColor(1f, 1f, 1f, .75f*id.alpha);
        poseStack.scale(.25f, .25f, .25f);
        gui.blit(poseStack, (x(pI)-10)*4, (y(pI)+20)*4, 64*id.dimension, 0, 64, 64);
        poseStack.popPose();
    }



    private void worldAnim(PoseStack poseStack, int partyIndex, ForgeIngameGui gui, ClientPlayerData id, float partialTicks) {
        //Parties.LOGGER.debug(gui.getGuiTicks());
        int currTick;
        float alphaPercent;
        if (id.tick(gui.getGuiTicks())) {
            if (id.tickWorldAnim())
                id.dimAnimActive = false;
        }
        if (id.dimAnim > 90) {
            currTick = 10 - (id.dimAnim - 90); //0-10
            alphaPercent = ((id.dimAnim-80-partialTicks)/20f);
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, x(partyIndex)-1, y(partyIndex)-1, x(partyIndex)+33, y(partyIndex)+33, 0x11111 | (int)((id.alphaI*.75)*alphaPercent) << 24, 0x555555 | (int)((id.alphaI*.75)*alphaPercent) << 24);
            setup(id.alpha*alphaPercent, id.getHead()); //Why does this require setShaderColor again...
            gui.blit(poseStack, x(partyIndex), y(partyIndex), 32, 32, 32, 32);
            setWorldShader(poseStack);
            RenderSystem.setShaderColor(1f,1f,1f, .75f +  currTick/40f);
            poseStack.scale(.25f+(currTick+partialTicks)/40f, .25f+(currTick+partialTicks)/40f, .25f+(currTick+partialTicks)/40f);
            gui.blit(poseStack, (int) ((x(partyIndex)-10+currTick+partialTicks)*(4-((currTick+partialTicks)/5f))),
                     + (int)((y(partyIndex)+(20-((currTick+partialTicks)*2f)))*(4-((currTick+partialTicks)/5f))), 64*id.oldDimension, 0, 64, 64);
            //Dimension text

        } else if (id.dimAnim > 10) {
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, x(partyIndex)-1, y(partyIndex)-1, x(partyIndex)+33, y(partyIndex)+33,0x111111 | (int)((id.alphaI*.75)*.5f) << 24, 0x555555 | (int)((id.alphaI*.75)*.5f) << 24);
            setup(id.alpha*.5f, id.getHead()); //Why does this require setShaderColor again...
            gui.blit(poseStack, x(partyIndex), y(partyIndex), 32, 32, 32, 32);
            setWorldShader(poseStack);
            currTick = id.dimAnim - 10; //80 - 0
            RenderSystem.setShaderColor(1f,1f,1f, ((currTick+(1*partialTicks))/80f));
            poseStack.scale(.5f, .5f, .5f);
            gui.blit(poseStack, (x(partyIndex))*2, (y(partyIndex))*2, 64*id.oldDimension, 0, 64, 64);
            RenderSystem.setShaderColor(1f,1f,1f, 1f - ((currTick+(1*partialTicks))/80f));
            gui.blit(poseStack, (x(partyIndex))*2, (y(partyIndex))*2, 64*id.dimension, 0, 64, 64);
            int x, y;
            float transX;
            transX = 0;
            alphaPercent = 0f;
            //poseStack.pushPose();
            poseStack.scale(2f, 2f, 2f);
            if (currTick > 75) {

            } else if (currTick > 70) {
                currTick = (currTick-70); // 5 - 0
                alphaPercent = 1f - (currTick - partialTicks)/5f;
                transX = 10+(currTick-partialTicks)*4f;
            } else if (currTick > 10) {
                currTick = currTick - 10; // 60 - 0
                alphaPercent = 1f;
                transX = 20*((currTick-partialTicks)/60f) - 10;
            } else if (currTick > 5) {
                // 5 - 0
                alphaPercent = (currTick - partialTicks)/5f;
                transX = -10-(10-(currTick-partialTicks))*4f;
            } else {
                transX = -10-(10-(currTick-partialTicks))*4f;
            }

            for (int j = 0; j < id.dimName.size(); j++) {
                poseStack.pushPose();
                if (j % 2 == 1)
                    poseStack.translate(-transX, 0, 0);
                else
                    poseStack.translate(transX, 0, 0);
                x = (int) (x(partyIndex)+((32-gui.getFont().width(id.dimName.get(j)))/2f));
                y = (y(partyIndex)+(j*gui.getFont().lineHeight)+1)+((int)((32-gui.getFont().lineHeight*id.dimName.size())/2f));
                if (alphaPercent > 0f) {
                    gui.getFont().draw(poseStack, id.dimName.get(j), x, y, id.dimColor | ((int)(255*alphaPercent) << 24));
                    gui.getFont().drawShadow(poseStack, id.dimName.get(j), x, y, id.dimColor | ((int)(255*alphaPercent) << 24));
                }

                poseStack.popPose();
            }
            //poseStack.popPose();

        } else {
            currTick = 10 - id.dimAnim;
            alphaPercent = (currTick+partialTicks+10)/20f;
            GuiUtils.drawGradientRect(poseStack.last().pose(), 0, x(partyIndex)-1, y(partyIndex)-1, x(partyIndex)+33, y(partyIndex)+33,0x11111 | (int)((id.alphaI*.75)*alphaPercent) << 24, 0x555555 | (int)((id.alphaI*.75)*alphaPercent) << 24);
            setup(id.alpha*alphaPercent, id.getHead()); //Why does this require setShaderColor again...
            gui.blit(poseStack, x(partyIndex), y(partyIndex), 32, 32, 32, 32);
            setWorldShader(poseStack);
            RenderSystem.setShaderColor(1f,1f,1f, .75f + id.dimAnim/40f);
            poseStack.scale(.25f+(id.dimAnim-partialTicks)/40f, .25f+(id.dimAnim-partialTicks)/40f, .25f+(id.dimAnim-partialTicks)/40f);

            gui.blit(poseStack, (int) ((x(partyIndex)-10+id.dimAnim-partialTicks)*(4-((id.dimAnim-partialTicks)/5f))),
                     + (int)((y(partyIndex)+(20-((id.dimAnim-partialTicks)*2f)))*(4-((id.dimAnim-partialTicks)/5f))), 64*id.dimension, 0, 64, 64);
        }
        poseStack.popPose();
    }
}
