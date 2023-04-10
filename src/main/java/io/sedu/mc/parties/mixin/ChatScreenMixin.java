package io.sedu.mc.parties.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.PEffects;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    protected ChatScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(at = @At("RETURN"), method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V")
    private void render(PoseStack poseStack, int mX, int mY, float partialTick, CallbackInfo callback) {
        assert minecraft != null;
        RenderItem.getCurrentMouseFrame(mX, mY, (index, posX, posY) -> {
            RenderItem.checkTooltip(posX, posY, (tooltipItem) -> tooltipItem.renderTooltip(poseStack, (ForgeIngameGui) minecraft.gui, index, mX, mY));
            ClientPlayerData.getOrderedPlayer(index, player -> PEffects.checkEffectTooltip(posX, posY, (effectItem, buffIndex) -> effectItem.renderTooltip(poseStack, (ForgeIngameGui) minecraft.gui, player.effects, buffIndex, mX, mY)));
        });
    }

}
