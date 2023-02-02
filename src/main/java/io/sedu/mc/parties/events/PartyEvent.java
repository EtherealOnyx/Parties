package io.sedu.mc.parties.events;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.anim.AnimHandler;
import io.sedu.mc.parties.commands.PartyCommands;
import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.network.ClientPacketHelper;
import io.sedu.mc.parties.network.InfoPacketHelper;
import io.sedu.mc.parties.network.ServerPacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

import static io.sedu.mc.parties.data.Util.getPlayer;

@Mod.EventBusSubscriber(modid = Parties.MODID)
public class PartyEvent {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide) {
            UUID id = event.getPlayer().getUUID();
            if (getPlayer(id) == null) {
                new PlayerData(id);
            }
            getPlayer(id).setServerPlayer((ServerPlayer) event.getPlayer());//.setOnline();
            ServerPacketHelper.sendOnline((ServerPlayer) event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getPlayer().level.isClientSide) {
            getPlayer(event.getPlayer().getUUID()).removeServerPlayer().removeInviters();
            ServerPacketHelper.sendOffline(event.getPlayer().getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getPlayer().level.isClientSide) {
            getPlayer(event.getPlayer().getUUID()).setServerPlayer((ServerPlayer) event.getPlayer());//.setOffline();
            
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

    private static final short playerUpdateInterval = 10;
    public static final short playerAcceptTimer = 15;

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval == 3) {
                HashMap<UUID, Boolean> trackers;
                if ((trackers = PlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                    UUID player;
                    int hunger;
                    boolean updateHunger = PlayerData.playerList.get(player = e.player.getUUID()).setHunger(hunger = (e.player.getFoodData().getFoodLevel()));
                    if (updateHunger)
                        trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendFood(id, player, hunger));
                }
            }
            if (e.player.tickCount % 20 == 7) {
                PlayerData.playerList.get(e.player.getUUID()).tickInviters();
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

    @SubscribeEvent
    public static void onWorldChange(WorldEvent.Load event) {
        if (event.getWorld().isClientSide()) {
            ClientPlayerData.updateSelfDim(String.valueOf(((ClientLevel) event.getWorld()).dimension().location()));
        }
    }

    @SubscribeEvent
    public static void onDimChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        ResourceKey<Level> l = event.getTo();
        

        HashMap<UUID, Boolean> trackers;
        Player p;
        if ((trackers = PlayerData.playerTrackers.get((p = event.getPlayer()).getUUID())) != null) {
            trackers.keySet().forEach(id -> InfoPacketHelper.sendDim(id, p.getUUID(), event.getTo().location()));
        }
    }

    public static void onClientLeave(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        //Reset info.
        
        ClientPlayerData.resetOnly();
    }

    public static void onClientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        //Reset info.
        
        ClientPlayerData.addSelf();
    }

    public static int tick = 0;
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            AnimHandler.tick();
            ClientPlayerData.playerList.values().forEach(ClientPlayerData::tick);
            if (tick++ % 20 == 8)
                ClientPlayerData.playerList.values().forEach(ClientPlayerData::slowTick);
        }
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
            if (event.getEntity() instanceof Player p && !p.isDeadOrDying() && (trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
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
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            if (event.getEntity() instanceof Player p) {
                InfoPacketHelper.sendDeath((ServerPlayer)p);
                if ((trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                    trackers.keySet().forEach(id -> InfoPacketHelper.sendDeath(id, p.getUUID()));
                }
            }

        }
    }

    @SubscribeEvent
    public static void onEntityRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            UUID p;
            InfoPacketHelper.sendLife((ServerPlayer) event.getPlayer());
            if ((trackers = PlayerData.playerTrackers.get(p = event.getPlayer().getUUID())) != null) {
                trackers.forEach((id, serverTrack) -> {
                    InfoPacketHelper.sendAlive(id, p);
                    if (serverTrack)
                        InfoPacketHelper.forceUpdate(id, p, false);
                    InfoPacketHelper.sendDim(id, p, event.getEntityLiving().level.dimension().location());

                });
            }
        }
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
                        if (val != p.getArmorValue())
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
                trackers.keySet().forEach(id -> InfoPacketHelper.sendXp(id, p.getUUID(), p.experienceLevel + event.getLevels()));
            }
        }
    }


    @SubscribeEvent
    public static void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        System.out.println("Potion Effect Added: " + event.getPotionEffect().getAmplifier() + " | " + event.getPotionEffect().getDuration());
        //TODO : Send client data of potion added.
        //TODO: Send client data if the entity is being tracked.
        if (!event.getEntityLiving().level.isClientSide()) {
            if (event.getEntity() instanceof Player p) {
                InfoPacketHelper.sendEffect(p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect()), event.getPotionEffect().getDuration(),
                                            event.getPotionEffect().getAmplifier());
                HashMap<UUID, Boolean> trackers;
                if ((trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                    trackers.forEach((id, serverTracked) -> {
                        InfoPacketHelper.sendEffect(id, p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect()),
                                                    event.getPotionEffect().getDuration(),
                                                    event.getPotionEffect().getAmplifier());
                    });
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        //TODO : Send client data of potion removed.
        //TODO: Send client data if the entity is being tracked.
        if (!event.getEntityLiving().level.isClientSide()) {
            if (event.getEntity() instanceof Player p) {
                InfoPacketHelper.sendEffectExpired(p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect()));
                HashMap<UUID, Boolean> trackers;
                if ((trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                   trackers.forEach((id, serverTracked) -> {
                        InfoPacketHelper.sendEffectExpired(id, p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect()));
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPotionExpired(PotionEvent.PotionExpiryEvent event) {
        //TODO : Send client data of potion removed.
        //TODO: Send client data if the entity is being tracked.
        if (!event.getEntityLiving().level.isClientSide()) {
            if (event.getEntity() instanceof Player p) {
                InfoPacketHelper.sendEffectExpired(p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect()));
                HashMap<UUID, Boolean> trackers;
                if ((trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                    trackers.forEach((id, serverTracked) -> {
                        InfoPacketHelper.sendEffectExpired(id, p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect()));
                    });
                }
            }
        }
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
