package io.sedu.mc.parties.client;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientPlayerData {
    public static HashMap<UUID, ClientPlayerData> playerList = new HashMap<>();
    public static ArrayList<UUID> playerOrderedList = new ArrayList<>();
    public static boolean showSelf = false;
    private static UUID leader;

    //Client Information
    //private static int globalIndex;
    //private int partyIndex;
    public Player clientPlayer;
    //Client-side functionality.
    boolean isOnline;
    private String playerName;
    private boolean trackedOnClient;
    private boolean isLeader;
    //Skin ResourceLocation
    private ResourceLocation skinLoc = null;

    //PlayerData
    private float health = 20f;
    private float maxHealth = 20f;
    private float absorb = 0f;
    private int armor = 0;
    private int xpLevel = 0;
    boolean isDead = false;



    private int hunger = 20;
    private int mana = 1000;



    //Client constructor.
    public ClientPlayerData() {
        trackedOnClient = false;
        playerName = "???";
    }

    public static void addClientMember(UUID uuid) {
        ClientPlayerData p = new ClientPlayerData();
        p.setId(uuid);
        //Try to add client skin if it exists.
    }

    public static int partySize() {
        return playerOrderedList.size();
    }

    public static void reset() {
        playerList.clear();
        playerOrderedList.clear();
    }

    public static void changeLeader(UUID uuid) {
        //Create party of 1 (self) if necessary.
        checkParty();
        if (leader != null) {
            ClientPlayerData.playerList.get(leader).isLeader = false;
        }
        leader = uuid;
        ClientPlayerData.playerList.get(uuid).isLeader = true;
    }

    private static void checkParty() {
        if (partySize() == 0)
            addSelf();
    }

    public static void addSelf() {
        ClientPlayerData.addClientMember(Minecraft.getInstance().player.getUUID());
        ClientPlayerData.showSelf = true;
    }

    public void setId(UUID uuid) {
        trackedOnClient = false;
        playerList.put(uuid, this);
        playerOrderedList.add(uuid);
    }

    private void setSkin(String name) {
        SkullBlockEntity.updateGameprofile(new GameProfile(null, name), (filledProfile) -> {
            Minecraft minecraft = Minecraft.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(filledProfile);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                this.skinLoc = minecraft.getSkinManager().registerTexture(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            } else {
                this.skinLoc = DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(filledProfile));
            }
        });
    }

    public void setOnline() {
        isOnline = true;
    }

    public void setOffline() {
        isOnline = false;
    }

    public String getName() {
        return trackedOnClient ? clientPlayer.getName().getContents() : playerName;
        //return playerName;
    }

    public void setClientPlayer(Player entity) {
        clientPlayer = entity;
        trackedOnClient = true;
        playerName = entity.getName().getContents();
        //Try to add client skin if it exists.
        if (skinLoc == null) {
            setSkin(playerName);
        }
        health = entity.getHealth();
        maxHealth = entity.getMaxHealth();
        hunger = entity.getFoodData().getFoodLevel();
        armor = entity.getArmorValue();
        xpLevel = entity.experienceLevel;
        absorb = entity.getAbsorptionAmount();
    }

    public void removeClientPlayer() {
        clientPlayer = null;
        trackedOnClient = false;
    }

    public boolean isTrackedOnServer() {
        return !trackedOnClient;
    }

    public static String getName(UUID uuid) {
        return playerList.get(uuid).getName();
    }

    public void setName(String data) {
        playerName = data;
        setSkin(data);
    }

    public ResourceLocation getHead() {
        return skinLoc == null ? DefaultPlayerSkin.getDefaultSkin() : skinLoc;
    }

    public float getHealth() {
        return clientPlayer!= null ? clientPlayer.getHealth() : health;
    }

    public float getMaxHealth() {
        return clientPlayer!= null ? clientPlayer.getMaxHealth() : maxHealth;
    }

    public int getArmor() {
        return clientPlayer!= null ? clientPlayer.getArmorValue() : armor;
    }

    public float getAbsorb() {
        return clientPlayer!= null ? clientPlayer.getAbsorptionAmount() : absorb;
    }

    public int getHunger() {
        return hunger;
    }

    public int getHungerForced() {
        return clientPlayer != null ? clientPlayer.getFoodData().getFoodLevel() : 20;
    }

    public int getLevelForced() {
        return clientPlayer != null ? clientPlayer.experienceLevel : 0;
    }

    public int getMana() {
        return mana;
    }

    public int getXpLevel() {
        return xpLevel;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setHealth(float data) {
        health = data;
    }

    public void setAbsorb(float data) {
        absorb = data;
    }

    public void setArmor(int data) {
        armor = data;
    }

    public void setFood(int data) {
        hunger = data;
    }

    public void setXp(int data) {
        xpLevel = data;
    }

    public void setMaxHealth(float max) {
        maxHealth = max;
    }

    public void markDead() {
        isDead = true;
    }

    public void markAlive() {
        isDead = false;
    }
}
