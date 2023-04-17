package io.sedu.mc.parties.events;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.anim.AnimHandler;
import io.sedu.mc.parties.client.overlay.gui.HoverScreen;
import io.sedu.mc.parties.client.overlay.gui.SliderButton;
import io.sedu.mc.parties.setup.ClientSetup;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;

public class ClientEvent {
    public static void onClientLeave(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        //Reset info.

        ClientPlayerData.resetOnly();
    }

    public static void onClientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        //Reset info.

        ClientPlayerData.addSelf();
    }

    public static int tick = 0;
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            AnimHandler.tick();
            if (Minecraft.getInstance().isPaused()) return;
            ClientPlayerData.playerList.values().forEach(ClientPlayerData::tick);
            if (tick++ % 20 == 8) {
                ClientPlayerData.playerList.values().forEach(ClientPlayerData::slowTick);
                ClientPlayerData.getSelf(ClientPlayerData::checkHunger);
            }
        }
    }

    public static void keyPress(InputEvent.KeyInputEvent event) {
        if (ClientSetup.showMouse.getKey().getValue() == event.getKey() && Minecraft.getInstance().screen == null) {
            Minecraft.getInstance().setScreen(new HoverScreen(ClientSetup.showMouse.getKey().getValue()));
        }
    }

    public static void mouseReleased(ScreenEvent.MouseReleasedEvent event) {
        SliderButton.clickReleased = true; //hehe
    }

}
