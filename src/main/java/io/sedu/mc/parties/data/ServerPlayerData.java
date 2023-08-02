package io.sedu.mc.parties.data;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.helper.PartyAPI;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.api.mod.dietarystats.DSCompatManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

import static io.sedu.mc.parties.data.DataType.*;
import static io.sedu.mc.parties.data.ServerConfigData.playerAcceptTimer;

public class ServerPlayerData {

    public static HashMap<UUID, ServerPlayerData> playerList = new HashMap<>();

    public static List<MessageCdHolder> messageCd = new ArrayList<>();

    //Boolean true = server side tracking, false = client side tracking.
    //Inner UUID belongs to ID that is tracking the outer player.
    public static HashMap<UUID,HashMap<UUID, Boolean>> playerTrackers = new HashMap<>();

    private List<Player> nearMembers = new ArrayList<>();
    private List<Player> globalMembers = new ArrayList<>();
    private boolean listDirty = true;

    //Player Entity
    protected WeakReference<ServerPlayer> serverPlayer;

    private final HashMap<DataType, Object> dataItems = new HashMap<>();


    //Invite tracker
    private LinkedHashMap<UUID, Integer> inviters = new LinkedHashMap<>();

    //The UUID of the party that this player belongs to.
    private UUID party;

    public ServerPlayerData(UUID id) {
        playerList.put(id, this);
    }

    public ServerPlayerData(UUID playerId, UUID partyId, String name) {
        this.party = partyId;
        setName(name);
        playerList.put(playerId, this);
    }

    public static boolean isOnMessageCd(UUID uuid) {
        for (MessageCdHolder h : messageCd) {
            if (h.id == uuid)
                return true;
        }
        return false;
    }

    public boolean hasParty() {
        return party != null;
    }

    public boolean isInviter(UUID inviter) {
        return inviters.containsKey(inviter);
    }

    public void removeInviter(UUID inviter) {
        inviters.remove(inviter);
    }

    public void addInviter(UUID inviter) {
        inviters.put(inviter, playerAcceptTimer.get());
    }

    public void addParty(UUID id) {
        if (party != null)
            return;
        party = id;
    }

    public ServerPlayer getPlayer() {
        if (serverPlayer != null)
            return serverPlayer.get();
        return null;
    }

    public UUID getPartyId() {
        return party;
    }

    public void removeParty() {
        party = null;
    }

    public ServerPlayerData setServerPlayer(ServerPlayer player) {
        serverPlayer = new WeakReference<>(player);
        setName(player.getName().getContents());
        return this;
    }

    public String getName() {

        return serverPlayer != null && serverPlayer.get() != null ? serverPlayer.get().getName().getContents() : (String) dataItems.get(NAME);
    }

    public void setName(String name) {
        dataItems.put(DataType.NAME, name);
    }

    public ServerPlayerData removeServerPlayer() {
        serverPlayer = null;
        return this;
    }

    public static void addTracker(UUID trackerHost, UUID toTrack) {
        if(!playerTrackers.containsKey(toTrack)) {
            playerTrackers.put(toTrack, new HashMap<>());
        }
        playerTrackers.get(toTrack).put(trackerHost, true);
        PlayerAPI.getPlayer(trackerHost, ServerPlayerData::markDirty);
    }

    private void markDirty() {
        listDirty = true;
    }

    public static void removeTracker(UUID trackerHost, UUID toTrack) {
        if(!playerTrackers.containsKey(toTrack)) {
            return;
        }
        playerTrackers.get(toTrack).remove(trackerHost);
        if (playerTrackers.get(toTrack).size() == 0)
            playerTrackers.remove(toTrack);
        PlayerAPI.getPlayer(trackerHost, ServerPlayerData::markDirty);
    }

    public static void changeTracker(UUID trackerHost, UUID toTrack, boolean serverTracked) {
        playerTrackers.get(toTrack).put(trackerHost, serverTracked);
        PlayerAPI.getPlayer(trackerHost, ServerPlayerData::markDirty);
    }

    public void setHunger(int hunger, Consumer<Integer> action) {
        if ((int) dataItems.getOrDefault(HUNGER, 0) != hunger) {
            dataItems.put(HUNGER, hunger);
            action.accept(hunger);
        }
    }

    private boolean saturationDirty = false;
    public void setSaturation(float sat, Consumer<Float> action) {
        if ((float) dataItems.getOrDefault(SATURATION, 0f) != sat) {
            dataItems.put(SATURATION, sat);
            action.accept(sat);
            saturationDirty = true;
        }
    }

    public void updateSaturation(Consumer<Float> action) {
        if (saturationDirty) {
            action.accept(getSaturation());
            saturationDirty = false;
        }
    }

    public float getSaturation() {
        return (float) dataItems.getOrDefault(SATURATION, 0f);
    }

    public void setReviveProg(float data, Consumer<Float> action) {
        if ((float) dataItems.getOrDefault(REVIVE, 0f) != data) {
            dataItems.put(REVIVE, data);
            action.accept(data);
        }
    }

    public void tickInviters() {
        LinkedHashMap<UUID, Integer> invNew = new LinkedHashMap<>();
        inviters.forEach((uuid, anInt) -> {
            if (anInt-- <= 0) {
                PartyHelper.dismissInvite(this, uuid);
            } else {
                invNew.put(uuid, anInt);
            }
        });
        inviters = invNew;
    }

    public void removeInviters() {
        inviters.forEach((uuid, integer) -> {
            PartyHelper.dismissInvite(uuid);
        });
        inviters.clear();
    }

    public void setXpBar(float xpprog, Consumer<Float> action) {
        if ((float) dataItems.getOrDefault(XPPROG, 0f) != xpprog) {
            dataItems.put(XPPROG, xpprog);
            action.accept(xpprog);
        }
    }

    public void ifInviterExists(Consumer<UUID> action) {
        Iterator<UUID> iter = inviters.keySet().iterator();
        UUID id = null;
        while (iter.hasNext()) {
            id = iter.next();
        }
        if (id != null) action.accept(id);
    }

    public void setBleeding(boolean b) {
        dataItems.put(BLEED, b);
        if (!b) {
            dataItems.put(REVIVE, 0f);
        }
    }

    public boolean isBleeding() {
        return (boolean) dataItems.getOrDefault(BLEED, false);
    }

    public void setDowned(boolean b) {
        dataItems.put(DOWNED, b);
        if (!b)
            dataItems.put(REVIVE, 0f);
    }

    public void setThirst(int v, Consumer<Integer> action) {
        if ((int) dataItems.getOrDefault(THIRST, 0) != v) {
            dataItems.put(THIRST, v);
            action.accept(v);
        }
    }

    public void setQuench(int v, Consumer<Integer> action) {
        if ((int) dataItems.getOrDefault(QUENCH, 0) != v) {
            dataItems.put(QUENCH, v);
            action.accept(v);
        }
    }

    public void setWorldTemp(float v, Consumer<Float> action) {
        if ((float) dataItems.getOrDefault(WORLDTEMP, 0f) != v) {
            dataItems.put(WORLDTEMP, v);
            action.accept(v);
        }
    }

    public void setBodyTemp(float v, Consumer<Float> action) {
        if ((float) dataItems.getOrDefault(BODYTEMP, 0f) != v) {
            dataItems.put(BODYTEMP, v);
            action.accept(v);
        }
    }

    public void setMana(float v, Consumer<Float> action) {
        if ((float) dataItems.getOrDefault(MANA, 0f) != v) {
            dataItems.put(MANA, v);
            action.accept(v);
        }
    }

    public void setMaxMana(int v, Consumer<Integer> action) {
        if ((int) dataItems.getOrDefault(MAXMANA, 0) != v) {
            dataItems.put(MAXMANA, v);
            action.accept(v);
        }
    }

    public int getThirst() {
        return (int) dataItems.getOrDefault(THIRST, 0);
    }

    public int getQuench() {
        return (int) dataItems.getOrDefault(QUENCH, 0);
    }

    public float getWorldTemp() {
        return (float) dataItems.getOrDefault(WORLDTEMP, 0f);
    }

    public float getReviveProg() {
        return (float) dataItems.getOrDefault(REVIVE, 0f);
    }

    public float getBodyTemp() {
        return (float) dataItems.getOrDefault(BODYTEMP, 0f);
    }

    public float getCurrentMana() {
        return  (float) dataItems.getOrDefault(MANA, 0f);
    }

    public int getMaxMana() {
        return (int) dataItems.getOrDefault(MAXMANA, 0);
    }

    public void setStamina(float v, Runnable action) {
        if (getStamina() != v) {
            dataItems.put(STAM, v);
            action.run();
        }
    }

    public void setMaxStamina(int v, Runnable action) {
        if (getMaxStamina() != v) {
            dataItems.put(MAXSTAM, v);
            action.run();
        }
    }

    public float getStamina() {
        return (float) dataItems.getOrDefault(STAM, 0f);
    }

    public float getMaxHunger(boolean forceUpdate) {
        if (forceUpdate && !dataItems.containsKey(MAXHUNGER)) {
            if (serverPlayer.get() != null) { // Try to get info before sending it.
                DSCompatManager.getHandler().getMaxHunger(serverPlayer.get(), food -> dataItems.put(MAXHUNGER, food));
            }
        }
        return (float) dataItems.getOrDefault(MAXHUNGER, 0f);
    }

    public void setMaxHunger(float max, Runnable action) {
        if (getMaxHunger(false) != max) {
            dataItems.put(MAXHUNGER, max);
            action.run();
        }
    }

    public int getMaxStamina() {
        return (int) dataItems.getOrDefault(MAXSTAM, 0);
    }

    public int getExtraStamina() {
        return (int) dataItems.getOrDefault(EXTRASTAM, 0);
    }

    public void setManaSS(float v, Runnable action) {
        if (getManaSS() != v) {
            dataItems.put(SSMANA, v);
            action.run();
        }
    }

    public void setManaI(Integer v, Runnable r) {
        if (getManaI() != v) {
            dataItems.put(IMANA, v);
            r.run();
        }
    }

    public void setMaxManaSS(float v, Runnable action) {
        if (getMaxManaSS() != v) {
            dataItems.put(SSMANAMAX, v);
            action.run();
        }
    }

    public void setMaxManaI(int v, Runnable action) {
        if (getMaxManaI() != v) {
            dataItems.put(IMANAMAX, v);
            action.run();
        }
    }

    public void setExtraManaSS(float v, Runnable action) {
        if (getExtraManaSS() != v) {
            dataItems.put(SSMANAABS, v);
            action.run();
        }
    }

    public int getManaI() { return (int) dataItems.getOrDefault(IMANA, 0);}
    public int getMaxManaI() { return (int) dataItems.getOrDefault(IMANAMAX, 0);}

    public float getManaSS() { return (float) dataItems.getOrDefault(SSMANA, 0f);}
    public float getMaxManaSS() { return (float) dataItems.getOrDefault(SSMANAMAX, 0f);}
    public float getExtraManaSS() { return (float) dataItems.getOrDefault(SSMANAABS, 0f);}

    public void setExtraStamina(int v, Runnable action) {
        if (getExtraStamina() != v) {
            dataItems.put(EXTRASTAM, v);
            action.run();
        }
    }

    public String getOrigin() {
        return (String) dataItems.getOrDefault(ORIGIN, "origins:empty");
    }

    public void setOrigin(String v, Runnable action) {
        if (!Objects.equals(getOrigin(), v)) {
            dataItems.put(ORIGIN, v);
            action.run();
        }
    }




    public static class MessageCdHolder {
        UUID id;
        int cooldown;
        public MessageCdHolder(UUID id, int cooldown) {
            this.id = id;
            this.cooldown = cooldown;
        }

        public boolean tick() {
            return cooldown-- <= 0;
        }
    }

    public List<Player> getNearbyMembers() {
        ServerPlayer p = getPlayer();
        if (listDirty) {
            if (p == null) {
                Parties.LOGGER.error("Attempted to redo experience calculations when a player was not fully connected yet!");
            } else {
                redoList(p.getUUID());
            }

        }
        return nearMembers;
    }

    public List<Player> getOnlineMembers() {
        ServerPlayer p = getPlayer();
        if (listDirty) {
            if (p == null) {
                Parties.LOGGER.error("Attempted to redo experience calculations when a player was not fully connected yet!");
            } else {
                redoList(p.getUUID());
            }

        }
        return globalMembers;
    }

    private void redoList(UUID id) {
        Parties.LOGGER.debug("Refreshing members for XP share!");
        nearMembers = PartyAPI.getNearMembersWithoutSelf(id);
        globalMembers = PartyAPI.getOnlineMembersWithoutSelf(id);
        listDirty = false;
    }
}
