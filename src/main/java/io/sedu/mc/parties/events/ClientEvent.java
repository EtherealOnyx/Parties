package io.sedu.mc.parties.events;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.anim.AnimBarHandler;
import io.sedu.mc.parties.client.overlay.gui.GUIRenderer;
import io.sedu.mc.parties.client.overlay.gui.HoverScreen;
import io.sedu.mc.parties.client.overlay.gui.SliderButton;
import io.sedu.mc.parties.setup.ClientSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvent {

    @SubscribeEvent
    public static void onClientLeave(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        //Reset info.

        ClientPlayerData.resetOnly();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        //Reset info.

        ClientPlayerData.addSelf();
    }

    public static int tick = 0;
    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            AnimBarHandler.tick();
            if (Minecraft.getInstance().isPaused()) return;
            ClientPlayerData.playerList.values().forEach(ClientPlayerData::tick);
            if (tick++ % 20 == 8) {
                ClientPlayerData.playerList.values().forEach(ClientPlayerData::slowTick);
                ClientPlayerData.getSelf(ClientPlayerData::checkHunger);
            }
        }
    }

    @SubscribeEvent
    public static void keyPress(InputEvent.KeyInputEvent event) {
        if (ClientSetup.showMouse.getKey().getValue() == event.getKey() && Minecraft.getInstance().screen == null) {
            Minecraft.getInstance().setScreen(new HoverScreen(ClientSetup.showMouse.getKey().getValue()));
        }
    }

    @SubscribeEvent
    public static void mouseReleased(ScreenEvent.MouseReleasedEvent event) {
        SliderButton.clickReleased = true; //hehe
    }

    @SubscribeEvent
    public static void nameTagRender(RenderNameplateEvent event) {
        if (event.getEntity() instanceof Player p && ((GUIRenderer) p).isRenderingUI()) {
            event.setResult(Event.Result.DENY);
        }
    }
}
