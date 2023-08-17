package io.sedu.mc.parties.events;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.api.helper.PartyAPI;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.api.mod.openpac.PACCompatManager;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.commands.PartyCommands;
import io.sedu.mc.parties.data.PartySaveData;
import io.sedu.mc.parties.data.ServerConfigData;
import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.network.ClientPacketHelper;
import io.sedu.mc.parties.network.InfoPacketHelper;
import io.sedu.mc.parties.network.ServerPacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

import static io.sedu.mc.parties.api.helper.PlayerAPI.getPlayer;
import static io.sedu.mc.parties.data.ServerConfigData.playerUpdateInterval;

@Mod.EventBusSubscriber(modid = Parties.MODID)
public class PartyEvent {

    public static boolean ignoreXpShare = false;

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide) {
            UUID id = event.getPlayer().getUUID();
            ServerPlayerData pD;
            if ((pD = PlayerAPI.getNormalPlayer(id)) == null) {
                pD = new ServerPlayerData(id);
            }
            pD.setServerPlayer((ServerPlayer) event.getPlayer());//.setOnline();
            ServerPacketHelper.sendOnline((ServerPlayer) event.getPlayer());
            //Send spectating mode
            Player p = event.getPlayer();
            boolean spectating = p.isSpectator();
            InfoPacketHelper.sendSpectating(p.getUUID(), spectating);
            HashMap<UUID, Boolean> trackers;
            if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                trackers.forEach((trackId, serverTracked) -> InfoPacketHelper.sendSpectating(trackId, p.getUUID(), spectating));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getPlayer().level.isClientSide) {
            getPlayer(event.getPlayer().getUUID(), (playerData) -> playerData.removeServerPlayer().removeInviters());
            ServerPacketHelper.sendOffline(event.getPlayer().getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getPlayer().level.isClientSide) {
            getPlayer(event.getPlayer().getUUID(), (playerData) -> playerData.setServerPlayer((ServerPlayer) event.getPlayer()));
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
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval.get() == 3) {
                HashMap<UUID, Boolean> trackers;
                UUID player;
                ServerPlayerData pD;
                FoodData d;
                (pD = ServerPlayerData.playerList.get(player = e.player.getUUID())).setSaturation((d = e.player.getFoodData()).getSaturationLevel(), saturation -> {
                    InfoPacketHelper.sendSaturationUpdate((ServerPlayer) e.player, saturation);
                });

                if ((trackers = ServerPlayerData.playerTrackers.get(e.player.getUUID())) != null) {

                    pD.setHunger(d.getFoodLevel(), hunger -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendFood(id, player, hunger)));
                    pD.updateSaturation(sat -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendSaturationUpdate(id, player, sat)));
                    pD.setXpBar(e.player.experienceProgress, (xp) -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendXpBar(id, player, xp)));
                }
            }
            if (e.player.tickCount % 20 == 7) {
                ServerPlayerData.playerList.get(e.player.getUUID()).tickInviters();
            }
        }
    }

    private static int tickCounter = 8;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && tickCounter++ >= 20) {
            tickCounter = 1;
            ServerPlayerData.messageCd.removeIf(ServerPlayerData.MessageCdHolder::tick);
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
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> PlayerAPI.getPlayer(propOf, p -> InfoPacketHelper.sendSaturationUpdate(sendTo, propOf, p.getSaturation())));
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
        HashMap<UUID, Boolean> trackers;
        ServerPlayer p;
        if ((trackers = ServerPlayerData.playerTrackers.get((p = (ServerPlayer) event.getPlayer()).getUUID())) != null) {
            trackers.keySet().forEach(id -> InfoPacketHelper.sendDim(id, p.getUUID(), event.getTo().location()));
        }

        //XP Bug Fix
        p.setExperienceLevels(p.experienceLevel);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityAttacked(LivingAttackEvent event) {
        if (!event.getEntity().level.isClientSide()
                && event.getEntity() instanceof LivingEntity member1
                && event.getSource() != null
                && event.getSource().getEntity() instanceof LivingEntity member2
                && !ServerConfigData.friendlyFire.get()
                && PartyAPI.inSameParty(member1, member2)) {
            event.setCanceled(true);
        }

    }


    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            if (event.getEntity() instanceof Player p) {
                InfoPacketHelper.sendClose((ServerPlayer) p);
                if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                    trackers.forEach((id, serverTracked) -> {
                        if (serverTracked) {
                            if (event.getAmount() != 0f) {
                                InfoPacketHelper.sendHealth(id, p.getUUID(), Math.max(p.getHealth() - event.getAmount(), 0f));
                            }
                            InfoPacketHelper.sendAbsorb(id, p.getUUID(), p.getAbsorptionAmount());
                            //TODO: Max Health
                        }
                    });
                }
            }
        }
    }
    @SubscribeEvent
    public static void onEntityHealed(LivingHealEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            if (event.getEntity() instanceof Player p && !p.isDeadOrDying() && (trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                trackers.forEach((id, serverTracked) -> {
                    if (serverTracked) {
                        if (event.getAmount() != 0f) {
                            InfoPacketHelper.sendHealth(id, p.getUUID(), Math.min(p.getHealth() + event.getAmount(), p.getMaxHealth()));
                        }
                        //TODO: Max Health
                    }
                });
            }
        }
    }
    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.isCanceled()) return;
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            if (event.getEntity() instanceof Player p) {
                InfoPacketHelper.sendDeath((ServerPlayer)p);
                if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
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
            if ((trackers = ServerPlayerData.playerTrackers.get(p = event.getPlayer().getUUID())) != null) {
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
            if (event.getEntity() instanceof Player p && (trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
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
            if (event.getItem().isEdible() && event.getEntity() instanceof Player p && (trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                trackers.keySet().forEach(id -> InfoPacketHelper.sendFood(id, p.getUUID(), p.getFoodData().getFoodLevel()));
            }
        }
    }

    @SubscribeEvent
    public static void onLevelChange(PlayerXpEvent.LevelChange event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            if (event.getEntity() instanceof Player p && (trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                trackers.keySet().forEach(id -> InfoPacketHelper.sendXp(id, p.getUUID(), p.experienceLevel + event.getLevels()));
            }
        }
    }


    @SubscribeEvent
    public static void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            if (event.getEntity() instanceof Player p) {
                InfoPacketHelper.sendEffect(p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect()), event.getPotionEffect().getDuration(),
                                            event.getPotionEffect().getAmplifier());
                HashMap<UUID, Boolean> trackers;
                if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                    trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendEffect(id, p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect()),
                                                                                    event.getPotionEffect().getDuration(),
                                                                                    event.getPotionEffect().getAmplifier()));
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            if (event.getEntity() instanceof Player p) {
                if (event.getPotionEffect() == null) return;
                InfoPacketHelper.sendEffectExpired(p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect()));
                HashMap<UUID, Boolean> trackers;
                if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                   trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendEffectExpired(id, p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect())));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onGamemodeChange(PlayerEvent.PlayerChangeGameModeEvent event) {
        Player p = event.getPlayer();
        if (event.getCurrentGameMode() == event.getNewGameMode()) return;
        boolean spectating = event.getNewGameMode() == GameType.SPECTATOR;
        InfoPacketHelper.sendSpectating(p.getUUID(), spectating);
        HashMap<UUID, Boolean> trackers;
        if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
            trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendSpectating(id, p.getUUID(), spectating));
        }
    }

    @SubscribeEvent
    public static void onPotionExpired(PotionEvent.PotionExpiryEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            if (event.getEntity() instanceof Player p) {
                if (event.getPotionEffect() == null) return;
                InfoPacketHelper.sendEffectExpired(p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect()));
                HashMap<UUID, Boolean> trackers;
                if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                    trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendEffectExpired(id, p.getUUID(), MobEffect.getId(event.getPotionEffect().getEffect())));
                }
            }
        }
    }

    @SubscribeEvent
    public static void RegisterCommands(RegisterCommandsEvent event) {
        PartyCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        //This should always be server side...
        PartySaveData.server = event.getServer();
        Parties.LOGGER.debug("Server saved successfully...");
        ServerLevel l = event.getServer().getLevel(Level.OVERWORLD);
        if (l != null) {
            PartySaveData.globalLevel = l;
            Parties.LOGGER.debug("Level saved successfully...");
            if (ServerConfigData.isPartySyncEnabled()) {
                PACCompatManager.getHandler().initParties(event.getServer());
                return;
            }
            PartySaveData.get(); //Load it into cache.
        }
    }
}
