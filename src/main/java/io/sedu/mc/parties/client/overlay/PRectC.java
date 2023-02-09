package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.gui.HoverScreen;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PRectC extends RenderItem {


    public PRectC(String n, int x, int y, int w, int h) {
        super(n, x, y, w, h);
        HoverScreen.addClickable(this);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        //Clickable
        //rect(i, poseStack,-2, 0, 0x88FFFFFF, 0x88FFFFFF);
    }

    @Override
    public void initItem() {
        item = (gui, poseStack, partialTicks, width, height) -> {
            if (!HoverScreen.isActive()) { //TODO: Switch to isConfigActive
                for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++) {
                    renderMember(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack, partialTicks);
                }
            } else {

                //Config stuffies
                if (HoverScreen.arranging()) {
                    int alpha = (int) (255*(.25f+Math.sin((gui.getGuiTicks() + partialTicks) / 6f) / 8f));
                    for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++)
                        rect(i, poseStack,-2, -4, 0x666666 | alpha << 24, alpha << 24);
                    return;
                }
                if (HoverScreen.moving()) {
                    int alpha = (int) (255*(.25f+Math.sin((gui.getGuiTicks() + partialTicks) / 6f) / 8f));
                    int index = ClientPlayerData.playerOrderedList.size()-1;
                    //System.out.println(ClientPlayerData.playerOrderedList.size());

                    drawRect(poseStack.last().pose(), -2, frameX, frameY,
                             frameW == 0 ? r(index) : l(0)+(frameW*ClientPlayerData.playerOrderedList.size()),
                             frameH == 0 ? b(index) : t(0)+(frameH*ClientPlayerData.playerOrderedList.size()),
                             alpha << 24, alpha << 24);
                }
            }
        };
    }
}
