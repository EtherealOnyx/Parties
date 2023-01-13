package io.sedu.mc.parties.events;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.data.PartyHelper;
import io.sedu.mc.parties.data.PlayerData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static io.sedu.mc.parties.data.Util.getPlayer;

@Mod.EventBusSubscriber(modid = Parties.MODID)
public class PartyEvent {

    @SubscribeEvent
    public static void onItemInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!event.getWorld().isClientSide) {
            if (event.getTarget() instanceof Player) {
                if (event.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.DIAMOND) {
                    if (PartyHelper.invitePlayer(event.getPlayer().getUUID(),
                            event.getTarget().getUUID()))
                        System.out.println("Party creation success.");
                    else
                        System.out.println("Party creation failed!");
                } /*else if (event.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.DIAMOND_SWORD) {
                    PartyHelper.kickPlayer(event.getPlayer().getUniqueID(), event.getTarget().getUniqueID());
                }*/
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        UUID id = event.getPlayer().getUUID();
        if (getPlayer(id) == null) {
            PlayerData.playerList.put(id, new PlayerData(id));
        }
    }
}
