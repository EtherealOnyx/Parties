package io.sedu.mc.parties.data;

import io.sedu.mc.parties.Parties;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

import static io.sedu.mc.parties.data.ServerConfigData.playerAcceptTimer;

public class PlayerData {

    public static HashMap<UUID, PlayerData> playerList = new HashMap<>();

    public static List<MessageCdHolder> messageCd = new ArrayList<>();

    //Boolean true = server side tracking, false = client side tracking.
    //Inner UUID belongs to ID that is tracking the outer player.
    public static HashMap<UUID,HashMap<UUID, Boolean>> playerTrackers = new HashMap<>();

    private List<Player> nearMembers = new ArrayList<>();
    private List<Player> globalMembers = new ArrayList<>();
    private boolean listDirty = true;

    //Player Entity
    protected WeakReference<ServerPlayer> serverPlayer;

    //Player Entity name
    private String name;

    //Invite tracker
    private LinkedHashMap<UUID, Integer> inviters = new LinkedHashMap<>();

    //Player old hunger;
    private int oldHunger;

    //Player old bar;
    private float xpBar;

    //is Player Downed (Hardcore Revival)
    private boolean isDowned = false;

    //is Player Bleeding (Player Revive)
    private boolean isBleeding = false;
    //Player revive progress;
    private float reviveProgress;

    //Player Thirst (Thirst was Taken)
    private int thirst;

    //World Temp and Body Temp (Cold Sweat)
    private float worldTemp;
    private float bodyTemp;

    //Mana (Ars Noveau)
    private float mana;
    private int maxMana;

    //The UUID of the party that this player belongs to.
    private UUID party;

    public PlayerData(UUID id) {
        playerList.put(id, this);
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

    public PlayerData setServerPlayer(ServerPlayer player) {
        serverPlayer = new WeakReference<>(player);
        name = player.getName().getContents();
        return this;
    }

    public String getName() {
        return serverPlayer != null && serverPlayer.get() != null ? serverPlayer.get().getName().getContents() : name;
    }

    public PlayerData removeServerPlayer() {
        serverPlayer = null;
        return this;
    }

    public static void addTracker(UUID trackerHost, UUID toTrack) {
        if(!playerTrackers.containsKey(toTrack)) {
            playerTrackers.put(toTrack, new HashMap<>());
        }
        playerTrackers.get(toTrack).put(trackerHost, true);
        Util.getPlayer(trackerHost).markDirty();
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
        Util.getPlayer(trackerHost).markDirty();
    }

    public static void changeTracker(UUID trackerHost, UUID toTrack, boolean serverTracked) {
        playerTrackers.get(toTrack).put(trackerHost, serverTracked);
        Util.getPlayer(trackerHost).markDirty();
    }

    public boolean setHunger(int hunger) {
        if (oldHunger != hunger) {
            oldHunger = hunger;
            return true;
        }
        return false;
    }

    public boolean setReviveProg(float reviveProg) {
        if (reviveProgress != reviveProg) {
            reviveProgress = reviveProg;
            return true;
        }
        return false;
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

    public boolean setXpBar(float v) {
        if (xpBar != v) {
            xpBar = v;
            return true;
        }
        return false;
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
        this.isBleeding = b;
        if (!b) {
            this.reviveProgress = 0;
        }
    }

    public boolean isBleeding() {
        return this.isBleeding;
    }

    public void setDowned(boolean b) {
        this.isDowned = b;
        if (!b)
            this.reviveProgress = 0;
    }

    public boolean setThirst(int thirst) {
        if (this.thirst != thirst) {
            this.thirst = thirst;
            return true;
        }
        return false;
    }

    public boolean setWorldTemp(float v) {
        if (this.worldTemp != v) {
            this.worldTemp = v;
            return true;
        }
        return false;
    }

    public boolean setBodyTemp(float v) {
        if (this.bodyTemp != v) {
            this.bodyTemp = v;
            return true;
        }
        return false;
    }

    public boolean setMana(float v) {
        if (this.mana != v) {
            this.mana = v;
            return true;
        }
        return false;
    }

    public boolean setMax(int v) {
        if (this.maxMana != v) {
            this.maxMana = v;
            return true;
        }
        return false;
    }

    public int getThirst() {
        return thirst;
    }

    public float getWorldTemp() {
        return worldTemp;
    }

    public float getReviveProg() {
        return reviveProgress;
    }

    public float getBodyTemp() {
        return bodyTemp;
    }

    public float getCurrentMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
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
        if (listDirty) redoList(getPlayer().getUUID());
        return nearMembers;
    }

    public List<Player> getOnlineMembers() {
        if (listDirty) redoList(getPlayer().getUUID());
        return globalMembers;
    }

    private void redoList(UUID id) {
        Parties.LOGGER.debug("Refreshing members for XP share!");
        nearMembers = Util.getNearMembersWithoutSelf(id);
        globalMembers = Util.getOnlineMembersWithoutSelf(id);
        listDirty = false;
    }
}
