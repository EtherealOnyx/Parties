package io.sedu.mc.parties.events;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.anim.AnimHandler;
import io.sedu.mc.parties.client.overlay.gui.HoverScreen;
import io.sedu.mc.parties.setup.ClientSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
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
            ClientPlayerData.playerList.values().forEach(ClientPlayerData::tick);
            if (tick++ % 20 == 8) {
                ClientPlayerData.playerList.values().forEach(ClientPlayerData::slowTick);
            }
        }
    }

    public static void keyPress(InputEvent.KeyInputEvent event) {
        if (ClientSetup.showMouse.getKey().getValue() == event.getKey() && Minecraft.getInstance().screen == null) {
            Minecraft.getInstance().setScreen(new HoverScreen(ClientSetup.showMouse.getKey().getValue()));
        }
    }

    public static void guiOpen(ScreenOpenEvent event) {
        if (event.getScreen() instanceof HoverScreen || event.getScreen() instanceof ChatScreen || event.getScreen() instanceof DeathScreen) {
            HoverScreen.activate();
            return;
        }

        if (event.getScreen() == null)
            HoverScreen.disable();
    }

    public static void guiRender(ScreenEvent.DrawScreenEvent event) {
        if (HoverScreen.isActive()) {
            HoverScreen.updateValues(event.getMouseX(), event.getMouseY());
        }
    }

    public static void mouseClick(ScreenEvent.MouseClickedEvent.Post event) {
        if (HoverScreen.moving() && Minecraft.getInstance().screen instanceof HoverScreen hS) {
            //hS.drag(event.getDragX(), event.getDragY());
        }
    }

    public static void mouseUnclick(ScreenEvent.MouseReleasedEvent.Post event) {
        if (HoverScreen.moving()) {
            System.out.println("Release");
        }
    }

}
