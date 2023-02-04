package io.sedu.mc.parties.client.overlay;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.sedu.mc.parties.client.overlay.anim.DimAnim;
import io.sedu.mc.parties.client.overlay.anim.HealthAnim;
import io.sedu.mc.parties.client.overlay.effects.ClientEffect;
import io.sedu.mc.parties.client.overlay.effects.EffectHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.*;

public class ClientPlayerData {
    public static HashMap<UUID, ClientPlayerData> playerList = new HashMap<>();
    public static ArrayList<UUID> playerOrderedList = new ArrayList<>();
    public static boolean showSelf = true;
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
    private int armor = 0;
    private int xpLevel = 0;
    private float xpBar = 0f;
    boolean isDead = false;


    //Default to server tracker.
    float alpha = .6f;
    public int alphaI = 216;

    //Dimension Animation
    public DimAnim dim = new DimAnim(100, true);

    //Health Animation
    public HealthAnim health = new HealthAnim(20, true);

    //Potion Effects
    public EffectHolder effects = new EffectHolder();

    private int hunger = 20;
    private final int mana = 1000;



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
        resetOnly();
        addSelf();
    }

    public static void addSelf() {
        Player p;
        if (showSelf & (p = Minecraft.getInstance().player) != null)
        {
            ClientPlayerData.addClientMember(p.getUUID());
            playerList.get(p.getUUID()).setClientPlayer(p).dim.activate(String.valueOf(p.level.dimension().location()), true);
        }
    }

    public static void changeLeader(UUID uuid) {
        //Create party of 1 (self) if necessary.
        checkParty();
        if (ClientPlayerData.playerList.get(leader) != null) {
            ClientPlayerData.playerList.get(leader).isLeader = false;
        }
        leader = uuid;
        ClientPlayerData.playerList.get(uuid).isLeader = true;
    }

    private static void checkParty() {
        if (partySize() == 0)
            addSelfParty();
    }

    public static void addSelfParty() {
        ClientPlayerData.addClientMember(Minecraft.getInstance().player.getUUID());
    }

    public static void resetOnly() {
        playerList.clear();
        playerOrderedList.clear();
        leader = null;
    }

    public static void updateSelfDim(String data) {
        if(ClientPlayerData.playerOrderedList.size() > 0) {
            playerList.get(Minecraft.getInstance().player.getUUID()).dim.activate(data, false);
        }
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
        if (isTrackedOnServer()) {
            //Default to server tracker.
            alpha = .6f;
            alphaI = 216;
            return;
        }
        alpha = 1f;
        alphaI = 255;
    }

    public void setOffline() {
        isOnline = false;
        alpha = .25f;
        alphaI = 64;
    }

    public String getName() {
        return trackedOnClient ? clientPlayer.getName().getContents() : playerName;
    }

    public ClientPlayerData setClientPlayer(Player entity) {
        clientPlayer = entity;
        trackedOnClient = true;
        playerName = entity.getName().getContents();
        //Try to add client skin if it exists.
        if (skinLoc == null) {
            setSkin(playerName);
        }
        health.activate(entity.getHealth(), entity.getMaxHealth(), entity.getAbsorptionAmount());
        armor = entity.getArmorValue();
        if (health.cur > 0f)
            markAlive();
        else
            markDead();
        alpha = 1f;
        alphaI = 255;
        return this;
    }

    public void removeClientPlayer() {
        clientPlayer = null;
        trackedOnClient = false;
        alpha = .6f;
        alphaI = 192;
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


    public int getArmor() {
        return clientPlayer!= null ? clientPlayer.getArmorValue() : armor;
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

    public float getXpBarForced() { return clientPlayer != null ? clientPlayer.experienceProgress : 0;}

    public float getXpBar() {return xpBar;}

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
        health.checkHealth(data);
    }

    public void setAbsorb(float data) {
        health.checkAbsorb(data);
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
        health.checkMax(max);
    }

    public void markDead() {
        isDead = true;
        effects.markForRemoval();
    }

    public void markAlive() {
        isDead = false;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isAlive() {
        return !isDead;
    }

    public void addEffect(int type, int duration, int amp) {
        effects.add(type, (int) Math.ceil(duration/20f), amp);
        if (MobEffect.byId(type) == MobEffects.ABSORPTION) {
            float absorb = (amp+1)*4f;
            if (health.getAbsorb() < absorb)
                health.checkAbsorb(absorb);
        }

    }

    public void removeEffect(int type) {
        effects.markForRemoval(type);
    }

    public void tick() {
        if (trackedOnClient)
            health.checkAnim(clientPlayer.getHealth(), clientPlayer.getMaxHealth(), clientPlayer.getAbsorptionAmount());
        //Potion
        //TODO: Decide if we automatically remove effects or wait for the server to tell us to remove them.
        //Remove automatically for now.
    }

    public void slowTick() {
        if (!isDead)
            effects.removeIf(ClientEffect::update);
    }

    public void setXpBar(Float data) {
        this.xpBar = data;
        System.out.println("XpBar: " + data);
    }
}
