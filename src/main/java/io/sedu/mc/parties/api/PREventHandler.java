package io.sedu.mc.parties.api;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import team.creative.playerrevive.api.event.PlayerBleedOutEvent;
import team.creative.playerrevive.api.event.PlayerRevivedEvent;

public class PREventHandler {

    @SubscribeEvent
    public static void onPlayerRevived(PlayerRevivedEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            System.out.println("Server message");
        } else {
            System.out.println("Client massage");
        }
    }

    @SubscribeEvent
    public static void onPlayerRevived(PlayerBleedOutEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            System.out.println("Server message");
        } else {
            System.out.println("Client massage");
        }
    }
}
