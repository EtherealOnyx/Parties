package eomods.combatoverhaul.eoparties.events;


import eomods.combatoverhaul.eoparties.data.client.ClientData;
import eomods.combatoverhaul.eoparties.data.server.Events;
import eomods.combatoverhaul.eoparties.data.server.ServerData;
import eomods.combatoverhaul.eoparties.data.server.Util;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.UUID;

import static eomods.combatoverhaul.eoparties.data.server.Util.getName;


public class PartyEvent {

    @SubscribeEvent
    public void interactEntity(Event event) {
        if (!event.getWorld().isRemote)
            if (event.getTarget() instanceof EntityPlayerMP) {
                if (Events.addPlayerToParty(event.getEntityPlayer().getUniqueID(),
                        event.getTarget().getUniqueID()))
                    System.out.println("Party creation success.");
                else
                    System.out.println("Party creation failed!");

            }
    }
    @SubscribeEvent
    public void interactEntity(PlayerInteractEvent event) {
        if (!event.getWorld().isRemote)
            if (event.getEntityPlayer().getHeldItemMainhand().getItem() == Items.STICK) {
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



    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntitySpawned(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote && event.getEntity() instanceof EntityLivingBase) {
            ClientData.checkTrackerData((EntityLivingBase) event.getEntity());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getWorld().isRemote) {
            ClientData.checkTrackerData(event.getChunk().getEntityLists());
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
        Events.onPlayerJoin((EntityPlayerMP) event.player);
    }

    @SubscribeEvent()
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Events.onPlayerLeave(event.player.getUniqueID());
    }

    @SubscribeEvent()
    public void onClientLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        ClientData.resetData();
    }

    @SubscribeEvent
    public void onEntityDamage(LivingHurtEvent event) {
        System.out.println("Hi!?!");
        if (event.getEntityLiving().getEntityWorld().isRemote && event.getEntityLiving() instanceof EntityPlayer) {
            System.out.println("Player named : " + event.getEntityLiving().getName() + " was hurt!");
        }
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
    public boolean leaveParty(EntityPlayerMP partyMember, EntityPlayerMP toLeave) {
        return false;
    }
    //TODO: Implement a semi-event of when a player adds a pet to their party.
    public boolean joinSubParty(EntityPlayerMP owner, EntityLivingBase pet) {
        return false;
    }
    //TODO: Implement a semi-event of when a player removes a pet from their party.
    public boolean leaveSubParty(EntityPlayerMP owner, EntityLivingBase pet) {
        return false;
    }
}
