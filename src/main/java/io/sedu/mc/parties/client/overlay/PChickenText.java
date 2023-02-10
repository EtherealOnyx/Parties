package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PChickenText extends RenderSelfItem {
    int color;

    public PChickenText(String name, int x, int y, int color) {
        super(name, x, y);
        this.color = color;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline) {
            render(i, gui, poseStack, String.valueOf(id.getHunger()));
        }
    }

    @Override
    int getColor() {
        return 0xb88458;
    }

    @Override
    public String getType() {
        return "Text";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        poseStack.pushPose();
        poseStack.scale(2f,2f,0);
        poseStack.translate(-.5f, 1, 0);
        setup(GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        assert Minecraft.getInstance().player != null;
        int hunger = Minecraft.getInstance().player.getFoodData().getFoodLevel();
        if (hunger > 16) {
            blit(poseStack, (b.x>>1)+4, b.y>>1, 16, 27, 9, 9);
            blit(poseStack, (b.x>>1)+4, b.y>>1, 52, 27, 9, 9);
        }
        else if (hunger > 12) {
            blit(poseStack, (b.x>>1)+4, b.y>>1, 16, 27, 9, 9);
            blit(poseStack, (b.x>>1)+4, b.y>>1, 61 - (gui.getGuiTicks() >> 4 & 1)*9, 27, 9, 9);
        } else if (hunger > 4) {
            blit(poseStack, (b.x>>1)+4, b.y>>1, 16, 27, 9, 9);
            if ((gui.getGuiTicks() >> 4 & 1) == 0)
                blit(poseStack, (b.x>>1)+4,b.y>>1, 61, 27, 9, 9);
        } else
            blit(poseStack, (b.x>>1)+4, b.y>>1, 16 + (gui.getGuiTicks() >> 3 & 1)*9, 27, 9, 9);
        poseStack.popPose();
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        render(i, gui, poseStack, String.valueOf(id.getHungerForced()));

    }

    void render(int i, ForgeIngameGui gui, PoseStack poseStack, String text) {
        text(i, gui, poseStack, text, color);
    }
}
