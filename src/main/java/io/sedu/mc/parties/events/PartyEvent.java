package io.sedu.mc.parties.events;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.ClientPlayerData;
import io.sedu.mc.parties.commands.PartyCommands;
import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.network.ClientPacketHelper;
import io.sedu.mc.parties.network.ServerPacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static io.sedu.mc.parties.data.Util.getPlayer;

@Mod.EventBusSubscriber(modid = Parties.MODID)
public class PartyEvent {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide) {
            UUID id = event.getPlayer().getUUID();
            if (getPlayer(id) == null) {
                PlayerData d = new PlayerData(id);
            }
            getPlayer(id).setServerPlayer((ServerPlayer) event.getPlayer());//.setOnline();
            ServerPacketHelper.sendOnline((ServerPlayer) event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getPlayer().level.isClientSide) {
            getPlayer(event.getPlayer().getUUID()).removeServerPlayer();//.setOffline();
            ServerPacketHelper.sendOffline(event.getPlayer().getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getPlayer().level.isClientSide) {
            getPlayer(event.getPlayer().getUUID()).setServerPlayer((ServerPlayer) event.getPlayer());//.setOffline();
            System.out.println("Clone Event for :" + event.getPlayer().getName().getContents());
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide && event.getEntity() instanceof Player) {
            if (ClientPlayerData.playerList.containsKey(event.getEntity().getUUID())) {
                if (ClientPlayerData.playerList.get(event.getEntity().getUUID()).isTrackedOnServer()) {
                    ClientPacketHelper.sendTrackerToClient((Player) event.getEntity());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveWorldEvent event) {
        if (event.getWorld().isClientSide && event.getEntity() instanceof Player) {
            if (ClientPlayerData.playerList.containsKey(event.getEntity().getUUID())) {
                if (!ClientPlayerData.playerList.get(event.getEntity().getUUID()).isTrackedOnServer()) {
                    ClientPacketHelper.sendTrackerToServer(event.getEntity().getUUID());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWorldChange(WorldEvent.Unload event) {
        if (event.getWorld().isClientSide()) {
            ClientPacketHelper.refreshClientOnDimChange();
        }
    }

    public static void onClientLeave(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        //Reset info.
        System.out.println("Resetting info...");
        ClientPlayerData.reset();
    }

    public static void onClientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
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
    public void onPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        //TODO : Send client data of potion removed.
    }

    @SubscribeEvent
    public static void RegisterCommands(RegisterCommandsEvent event) {
        PartyCommands.register(event.getDispatcher());
    }
}
