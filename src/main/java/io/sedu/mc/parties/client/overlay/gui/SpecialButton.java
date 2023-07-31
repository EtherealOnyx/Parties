package io.sedu.mc.parties.client.overlay.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public class SpecialButton extends SmallButton {

    protected final Button.OnPress onPressSpecial;
    public SpecialButton(int pX, int pY, String m, OnPress pOnPress, OnPress pOnPressSpecial, OnTooltip pOnTooltip, float offX, float offY, float r, float g, float b) {
        super(pX, pY, m, pOnPress, pOnTooltip, offX, offY, r, g, b);
        this.onPressSpecial = pOnPressSpecial;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(pButton)) {
                if (this.clicked(pMouseX, pMouseY)) {
                    onClickSpecial(pButton);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isValidClickButton(int pButton) {
        return pButton == 0 || pButton == 1;
    }

    private void onClickSpecial(int pButton) {
        if (pButton == 0) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.onPress();
        } else { //Can only be 0 or 1 because of isValidClickButton.
            if (onPressSpecial == null) return;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.75F));
            this.onPressSpecial.onPress(this);
        }
    }
}
