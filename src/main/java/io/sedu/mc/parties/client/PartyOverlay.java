package io.sedu.mc.parties.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.IIngameOverlay;

public class PartyOverlay {
    public static ResourceLocation playerHead = null;


    /*public static final IIngameOverlay HUD_PLAYER = (gui, poseStack, partialTicks, width, height) -> {

        if (playerHead == null && Minecraft.getInstance().player.connection.getOnlinePlayers().size() > 0) {
            for (PlayerInfo pi : Minecraft.getInstance().player.connection.getOnlinePlayers()) {
                if (pi.getSkinLocation() != DefaultPlayerSkin.getDefaultSkin(pi.getProfile().getId()))
                    playerHead = pi.getSkinLocation();
            }
        }

        String toDisplay = "Testing";
        int x = 0;
        int y = 0;
        if (x >= 0 && y >= 0) {
            x = 0;
            y = 0;
        }
        if (playerHead != null) {
            gui.setupOverlayRenderState(true, false, playerHead);
            GuiUtils.drawTexturedModalRect(poseStack, 16, 16, 32, 32, 32, 32, 1);
            gui.getFont().draw(poseStack, Minecraft.getInstance().player.getDisplayName().getString(), 56, 32, 0);
        }


    };*/
}
