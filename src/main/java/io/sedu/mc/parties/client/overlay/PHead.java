package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PHead extends RenderItem {

    public static ItemStack playerHead = null;


    public PHead(String name, int x, int y) {
        super(name, x, y, 32, 32);
    }

    @Override
    int getColor() {
        return 0xAACCAA;
    }

    @Override
    public String getType() {
        return "Icon";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(playerHead, b.x+8, b.y+3, 0);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isDead)
            setColor(1f, .5f, .5f, .5f);
        else
            setColor(1f, 1f, 1f, id.alpha);
        rect(i, poseStack,0,  -1,  0x111111, 0x555555, (int)(id.alphaI*.75));
        setup(id.getHead());
        blit(poseStack, x(i), y(i), 32, 32, 32, 32);
        resetColor();
    }
}
