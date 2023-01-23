package io.sedu.mc.parties.events;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.ClientPlayerData;
import io.sedu.mc.parties.commands.PartyCommands;
import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.network.ClientPacketHelper;
import io.sedu.mc.parties.network.InfoPacketHelper;
import io.sedu.mc.parties.network.ServerPacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

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
                    Minecraft.getInstance().execute(() -> ClientPacketHelper.sendTrackerToClient((Player) event.getEntity()));
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





    //Client never receives any hunger updates OR xp level updates from the server for any entity aside from self.

    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            if (event.getEntity() instanceof Player p && (trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                trackers.forEach((id, serverTracked) -> {
                    if (serverTracked) {
                        if (event.getAmount() != 0f) {
                            InfoPacketHelper.sendHealth(id, p.getUUID(), Math.max(p.getHealth() - event.getAmount(), 0f));
                        } //else, send empty update to trigger refresh?
                        InfoPacketHelper.sendAbsorb(id, p.getUUID(), p.getAbsorptionAmount());
                        //TODO: Max Health
                    }
                });
            }
        }
    }
    @SubscribeEvent
    public static void onEntityHealed(LivingHealEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            if (event.getEntity() instanceof Player p && (trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                trackers.forEach((id, serverTracked) -> {
                    if (serverTracked) {
                        if (event.getAmount() != 0f) {
                            InfoPacketHelper.sendHealth(id, p.getUUID(), Math.min(p.getHealth() + event.getAmount(), p.getMaxHealth()));
                        }//else, send empty update to trigger refresh?
                        //InfoPacketHelper.sendAbsorb(id, p.getUUID(), event.getAmount());
                        //TODO: Max Health
                    }
                });
            }
        }
    }
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        //May not be required...
    }
    @SubscribeEvent
    public static void onEntityArmorChange(LivingEquipmentChangeEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            if (event.getEntity() instanceof Player p && (trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                trackers.forEach((id, serverTracked) -> {
                    if (serverTracked) {
                        int val = p.getArmorValue();
                        if (event.getFrom().getItem() instanceof ArmorItem from)
                            val -= from.getDefense();
                        if (event.getTo().getItem() instanceof ArmorItem to)
                            val += to.getDefense();
                        InfoPacketHelper.sendArmor(id, p.getUUID(), val);
                    }
                });
            }
        }
    }
    @SubscribeEvent
    public static void onFoodConsumption(LivingEntityUseItemEvent.Finish event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            if (event.getItem().isEdible() && event.getEntity() instanceof Player p && (trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                trackers.keySet().forEach(id -> InfoPacketHelper.sendFood(id, p.getUUID(), p.getFoodData().getFoodLevel()));
            }
        }
    }

    @SubscribeEvent
    public static void onLevelChange(PlayerXpEvent.LevelChange event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            if (event.getEntity() instanceof Player p && (trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                trackers.keySet().forEach(id -> InfoPacketHelper.sendXp(id, p.getUUID(), event.getLevels()));
            }
        }
    }


    @SubscribeEvent
    public static void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        //TODO : Send client data of potion added.
        //TODO: Send client data if the entity is being tracked.
        if (event.getEntityLiving().level.isClientSide()) {
            System.out.print("Client Side: ");
        } else {
            System.out.print("Server Side: ");
        }
        System.out.println(event.getClass());
    }
    @SubscribeEvent
    public static void onPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        //TODO : Send client data of potion removed.
        //TODO: Send client data if the entity is being tracked.
        if (event.getEntityLiving().level.isClientSide()) {
            System.out.print("Client Side: ");
        } else {
            System.out.print("Server Side: ");
        }
        System.out.println(event.getClass());
    }

    @SubscribeEvent
    public static void track(PlayerEvent.StartTracking event) {
    }

    @SubscribeEvent
    public static void untrack(PlayerEvent.StopTracking event) {
    }

    @SubscribeEvent
    public static void RegisterCommands(RegisterCommandsEvent event) {
        PartyCommands.register(event.getDispatcher());
    }
}
