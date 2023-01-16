package io.sedu.mc.parties.events;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.ClientPlayerData;
import io.sedu.mc.parties.commands.PartyCommands;
import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.PartyHelper;
import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.network.ClientPacketHelper;
import io.sedu.mc.parties.network.ServerPacketHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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

    /*@SubscribeEvent
    public static void onClientLeave(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        PlayerData.playerList.clear();
    }*/

    @SubscribeEvent
    public static void onEntitySpawned(EntityJoinWorldEvent event) {
        System.out.println("Entity Spawned: " + event.getEntity().getName().getContents());
        if (event.getWorld().isClientSide && event.getEntity() instanceof Player) {
            System.out.println("PLAYER Spawned: " + event.getEntity().getName().getContents());
            if (ClientPlayerData.playerList.containsKey(event.getEntity().getUUID()))
                    if (ClientPlayerData.playerList.get(event.getEntity().getUUID()).isTrackedOnServer()) {
                        ClientPacketHelper.sendTrackerToClient((Player) event.getEntity());
                    }
            else {
                ClientPlayerData.potentialTracks.put(event.getEntity().getUUID(), (Player) event.getEntity());
            }
        }
    }


    /*private static int timer = 0;
    @SubscribeEvent
    public void clientTickEvent(TickEvent.ClientTickEvent event) {
        //Check if our trackers are valid every sec (20 ticks per sec?)...
        if (event.phase == TickEvent.Phase.START && ++timer > 20) {
            Player p;
            Iterator it;
            timer = 0;
            it = PlayerData.playerList.values().iterator();
            while (it.hasNext()) {
                p = ((PlayerData) it.next()).getClientPlayer();
                if (!p.isAlive())
                    ClientPacketHelper.sendTrackerToServer(p);
            }
            it = PlayerData.potentialTracks.values().iterator();
            while (it.hasNext()) {
                p = (Player) it.next();
                if (!p.isAlive())
                    PlayerData.potentialTracks.remove(p.getUUID());
            }
        }
    }*/



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
