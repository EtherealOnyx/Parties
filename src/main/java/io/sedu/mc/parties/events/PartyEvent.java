package io.sedu.mc.parties.events;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.commands.PartyCommands;
import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.PartyHelper;
import io.sedu.mc.parties.data.PlayerData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
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
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        UUID id = event.getPlayer().getUUID();
        if (getPlayer(id) == null) {
            new PlayerData(id);
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

    @SubscribeEvent
    public static void RegisterCommands(RegisterCommandsEvent event) {
        PartyCommands.register(event.getDispatcher());
    }
}
