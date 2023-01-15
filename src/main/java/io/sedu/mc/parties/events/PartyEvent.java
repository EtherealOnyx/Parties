package io.sedu.mc.parties.events;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.PartyHelper;
import io.sedu.mc.parties.data.PlayerData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
                } else if (event.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.DIAMOND_SWORD) {
                    if (PartyHelper.kickPlayer(event.getPlayer().getUUID(), event.getTarget().getUUID()))
                            System.out.println("Player kicked successfully.");
                        else
                            System.out.println("Player kick failed!");
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        UUID id = event.getPlayer().getUUID();
        if (getPlayer(id) == null) {
            PlayerData.playerList.put(id, new PlayerData(id));
        } else {
            //TODO: Send client info if they belong to party.
        }
    }

    @SubscribeEvent
    public void interactEntity(PlayerInteractEvent.RightClickItem event) {
        if (event.getWorld().isClientSide) {
            /*if (event.getItemStack().getItem().equals(Items.STICK)) {
                if (ClientData.party != null) {
                    System.out.println("**** Party ****");
                    ClientData.party.getMembers().forEach(entry -> System.out.println("Member: " + entry.toString()));
                    System.out.println("**** Leader ****");
                    System.out.println(ClientData.party.getLeader());
                }
            }*/
        } else {
            System.out.println("**** Party ****");
            AtomicInteger i = new AtomicInteger();
            PartyData.partyList.forEach((id, party) -> {
                System.out.println("Party #" + i.incrementAndGet() + " - " + id.toString());
                AtomicInteger i2 = new AtomicInteger();
                party.getMembers().forEach(id2 -> {
                    System.out.println("Member #" + i2.incrementAndGet() + ": " + id2);
                });
            });
        }
    }
}
