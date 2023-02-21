package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class HexBox extends InputBox {
    protected static boolean rgbMode = false;
    int hex;
    HexBox.OnTextInput onNumInput;
    public HexBox(int color, Font pFont, int pWidth, int pHeight, Component pMessage, OnTextInput onInput) {
        super(6, color, pFont, pWidth, pHeight, pMessage, input -> {}, true);
        super.onInput = this::updateInput;
        this.onNumInput = onInput;
    }

    @Override
    protected boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.digit(s.charAt(i), 16) == -1)
                return false;
        }
        return true;
    }

    private void updateInput(String input) {
        hex = Integer.parseInt(input, 16);
        onNumInput.onInput(hex);
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.isVisible()) {
            font.draw(pPoseStack, "0x", this.x-15, this.y, this.color | 100 << 24);

        }
        super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }


    public interface OnTextInput {
        void onInput(int input);
    }

}
