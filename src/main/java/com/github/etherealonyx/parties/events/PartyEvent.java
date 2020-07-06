package com.github.etherealonyx.parties.events;

import com.github.etherealonyx.parties.data.PartyHelper;
import com.github.etherealonyx.parties.data.PlayerHelper;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class PartyEvent {

    public static PartyEvent instance = new PartyEvent();

    @SubscribeEvent
    public void interactEntity(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!event.getWorld().isRemote) {
            if (event.getTarget() instanceof PlayerEntity) {
                if (event.getPlayer().getHeldItemMainhand().getItem() == Items.DIAMOND) {
                    if (PartyHelper.invitePlayer(event.getPlayer().getUniqueID(),
                            event.getTarget().getUniqueID()))
                        System.out.println("Party creation success.");
                    else
                        System.out.println("Party creation failed!");
                } else if (event.getPlayer().getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
                    PartyHelper.kickPlayer(event.getPlayer().getUniqueID(), event.getTarget().getUniqueID());
                }
            }
            /*if (event.getTarget() instanceof TameableEntity) {
                if (event.getPlayer().getHeldItemMainhand().getItem() == Items.DIAMOND) {
                    if (Events.addPetToParty(event.getPlayer().getUniqueID(),
                            (LivingEntity) event.getTarget()))
                        System.out.println("Adding pet to a subparty.");
                    else
                        System.out.println("Subparty creation failed!");
                } else if (event.getPlayer().getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
                    Events.kickPetMember(event.getPlayer().getUniqueID(), event.getTarget().getUniqueID());
                }
            }
            if (event.getPlayer().getHeldItemMainhand().getItem() == Items.EMERALD) {
                Events.dropParty(event.getPlayer().getUniqueID());
            }*/
        }
    }
    /*@SubscribeEvent
    public void interactEntity(PlayerInteractEvent.RightClickItem event) {
        if (!event.getWorld().isRemote) {
            if (event.getPlayer().getHeldItemMainhand().getItem() == Items.STICK) {
                for (UUID toTrack : ServerData.trackers.keySet())
                    System.out.println("Server tracker exists for: " + getName(toTrack));
                for (UUID toTrack : ServerData.clientTrackers.keySet())
                    System.out.println("Client tracker exists for: " + getName(toTrack));
            }
        }
        else {
            ClientData.printTrackers();
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntitySpawned(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) {
            if ((event.getEntity() instanceof PlayerEntity || event.getEntity() instanceof TameableEntity)) {
                System.out.println("Found entity spawned : " + event.getEntity().getName());
                ClientData.checkTrackerData((LivingEntity) event.getEntity());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getWorld().isRemote()) {
            ClientData.checkTrackerData(event.getChunk().getPos());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDimensionChangeEvent(EntityTravelToDimensionEvent event) {
        if ((event.getEntity() instanceof PlayerEntity || event.getEntity() instanceof TameableEntity))
            Events.moveAllToServer(event.getEntity().getUniqueID());
    }

*/
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerHelper.onPlayerJoin((ServerPlayerEntity) event.getPlayer());
    }


    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerHelper.onPlayerLeave(event.getPlayer().getUniqueID());
    }
    /*

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

    @SubscribeEvent
    public void onEntityArmorChange(LivingEquipmentChangeEvent event) {
        //TODO: Send the client data of armor.
    }

    @SubscribeEvent
    public void onFoodConsumption(LivingEntityUseItemEvent.Finish event) {
        //TODO: Send the client data of food consumption.
    }

    @SubscribeEvent
    public void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        //TODO : Send client data of potion added.
    }

    @SubscribeEvent
    public void onPotionRemoved(PotionEvent.PotionAddedEvent event) {
        //TODO : Send client data of potion removed.
    }

    int update = 0;
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && update++ >= Config.SERVER_UPDATE_FREQUENCY) {
            Events.update();
        }
    }*/
}
