package eomods.combatoverhaul.eoparties.events;


import eomods.combatoverhaul.eoparties.data.client.ClientData;
import eomods.combatoverhaul.eoparties.data.server.Events;
import eomods.combatoverhaul.eoparties.data.server.ServerData;
import eomods.combatoverhaul.eoparties.data.server.Util;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static eomods.combatoverhaul.eoparties.data.server.Util.getName;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE)
public class PartyEvent {

    public static PartyEvent instance = new PartyEvent();

    @SubscribeEvent
    public void interactEntity(PlayerInteractEvent.EntityInteractSpecific event) {
        System.out.println("Pleaseeeee");
        if (!event.getWorld().isRemote)
            if (event.getTarget() instanceof PlayerEntity) {
                if (event.getPlayer().getHeldItemMainhand().getItem() == Items.DIAMOND) {
                    if (Events.addPlayerToParty(event.getPlayer().getUniqueID(),
                            event.getTarget().getUniqueID()))
                        System.out.println("Party creation success.");
                    else
                        System.out.println("Party creation failed!");
                } else if (event.getPlayer().getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
                    //Events.kickPartyMember(event.getTarget().getUniqueID());
                }
            }
        if (event.getPlayer().getHeldItemMainhand().getItem() == Items.EMERALD) {
            Events.dropParty(event.getPlayer().getUniqueID());
        }
    }
    @SubscribeEvent
    public void interactEntity(PlayerInteractEvent.RightClickItem event) {
        if (!event.getWorld().isRemote) {
            if (event.getPlayer().getHeldItemMainhand().getItem() == Items.STICK) {
                for (UUID toTrack : ServerData.trackers.keySet())
                    System.out.println("Server tracker exists for: " + getName(toTrack));
                for (UUID toTrack : ServerData.clientTrackers.keySet())
                    System.out.println("Client tracker exists for: " + getName(toTrack));
                /*for (Map.Entry<UUID, LivingMember> members : ServerData.livingMembers.entrySet()) {
                    //if (members.getValue().getPlayer() != null)
                        //System.out.println("Found Player: " + members.getValue().getPlayer());
                }
                for (UUID partyLeader : ServerData.partyLeaders) {
                        //System.out.println("Found Leader: " + Util.getName(partyLeader));
                }*/
            }
        }
        else {
                ClientData.printTrackers();
        }
    }



    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntitySpawned(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote && event.getEntity() instanceof TameableEntity) {
            ClientData.checkTrackerData((LivingEntity) event.getEntity());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getWorld().isRemote()) {
            //ClientData.checkTrackerData(event.getChunk());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDimensionChangeEvent(EntityTravelToDimensionEvent event) {
        if (Util.hasParty(event.getEntity().getUniqueID())) {
            Events.moveAllToServer(event.getEntity().getUniqueID());
        }

    }


    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Events.onPlayerJoin((ServerPlayerEntity) event.getPlayer());
    }

    @SubscribeEvent()
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Events.onPlayerLeave(event.getPlayer().getUniqueID());
    }

    public void onPlayerDrop(ServerPlayerEntity player) {

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent()
    public void onClientLeave(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientData.resetData();
    }

    @SubscribeEvent
    public void onEntityDamage(LivingHurtEvent event) {

        //TODO: Send client data if the entity is being tracked.
    }

    @SubscribeEvent
    public void onEntityHealed(LivingHealEvent event) {
        //TODO: Send the client data if the entity is being tracked.
    }
    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        //TODO: Send the client data all the time.
    }
    //TODO: Implement a semi-event of when a player leaves a party.
    //TODO: Implement a semi-event of when a player adds a pet to their party.
    //TODO: Implement a semi-event of when a player removes a pet from their party.

}
