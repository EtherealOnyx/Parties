package io.sedu.mc.parties.client.overlay;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.sedu.mc.parties.api.arsnoveau.ANCompatManager;
import io.sedu.mc.parties.api.coldsweat.CSCompatManager;
import io.sedu.mc.parties.api.playerrevive.PRCompatManager;
import io.sedu.mc.parties.api.thirstmod.TMCompatManager;
import io.sedu.mc.parties.api.toughasnails.TANCompatManager;
import io.sedu.mc.parties.client.config.Config;
import io.sedu.mc.parties.client.overlay.anim.DimAnim;
import io.sedu.mc.parties.client.overlay.anim.HealthAnim;
import io.sedu.mc.parties.client.overlay.anim.ManaAnim;
import io.sedu.mc.parties.client.overlay.effects.ClientEffect;
import io.sedu.mc.parties.client.overlay.effects.EffectHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class ClientPlayerData {
    public static HashMap<UUID, ClientPlayerData> playerList = new HashMap<>();
    public static ArrayList<UUID> playerOrderedList = new ArrayList<>();
    public static boolean showSelf = true;
    private static UUID leader;

    //Client Information
    public Player clientPlayer;

    //Client-side functionality.
    boolean isOnline;
    boolean isSpectator;
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

    //PlayerRevive Support, HardcoreRevival Support
    boolean isBleeding = false;
    boolean isDowned = false;
    int bleedTimer = 0;
    float reviveProgress = 0;

    //Thirst Was Taken Support
    int thirst = 0;

    //Cold Sweat Support
    int worldTemp = 0;
    int bodyTemp = 0;
    int severity = 0;

    //TAN Support
    String tempType = "";


    //Default to server tracker.
    float alpha = .6f;
    public int alphaI = 216;

    //Dimension Animation
    public DimAnim dim = new DimAnim(100, this);
    public boolean shouldRenderModel = false;

    //Health Animation
    public HealthAnim health = new HealthAnim(20, true);

    //Mana Animation
    public ManaAnim mana = new ManaAnim(20, true);

    //Potion Effects
    public EffectHolder effects = new EffectHolder();

    private int hunger = 20;



    //Client constructor.
    public ClientPlayerData() {
        trackedOnClient = false;
        playerName = "???";
    }

    public static ClientPlayerData getOrderedPlayer(int index) {
        return playerList.get(playerOrderedList.get(index));
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
            if (RenderItem.items.get("dim").isEnabled()) playerList.get(p.getUUID()).setClientPlayer(p).dim.activate(String.valueOf(p.level.dimension().location()), true);
            playerList.get(p.getUUID()).isOnline = true;

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
        RenderSelfItem.selfIndex = 0;
        leader = null;
    }

    public static void updateSelfDim(String data) {
        if(ClientPlayerData.playerOrderedList.size() > 0) {
            if (RenderItem.items.get("dim").isEnabled())
                playerList.get(Minecraft.getInstance().player.getUUID()).dim.activate(data, false);
        }
    }

    public static void swap(int f, int s) {
        UUID temp = ClientPlayerData.playerOrderedList.get(f);
        ClientPlayerData.playerOrderedList.set(f, ClientPlayerData.playerOrderedList.get(s));
        ClientPlayerData.playerOrderedList.set(s, temp);
        RenderSelfItem.updateSelfIndex();
    }

    public static void markEffectsDirty() {
        ClientPlayerData.playerList.values().forEach(c -> c.effects.refresh());
    }

    public static void forEachOrdered(BiConsumer<Integer, ClientPlayerData> action) {
        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++) {
            action.accept(i,
                          ClientPlayerData.playerList.get(playerOrderedList.get(i)));
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

    public void setSpectator(boolean spec) {
        isSpectator = spec;
    }

    public void setOffline() {
        isOnline = false;
        isDead = false;
        alpha = .25f;
        alphaI = 64;
    }

    public String getName() {
        return trackedOnClient ? clientPlayer.getName().getContents() : playerName;
    }

    public ClientPlayerData setClientPlayer(Player entity) {
        clientPlayer = entity;
        trackedOnClient = true;
        shouldRenderModel = true;
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
        shouldRenderModel = false;
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


    public int getXpLevel() {
        return xpLevel;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setHealth(float data) {
        health.checkHealth(data);
    }

    public float getReviveProgress() {
        if (isBleeding)
            return clientPlayer != null ? PRCompatManager.getHandler().getReviveProgress(clientPlayer) : reviveProgress;
        return reviveProgress;
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
        isBleeding = false;
        isDowned = false;
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
    }

    public void slowTick() {
        if (!isDead) {
            effects.removeIf(ClientEffect::update);
        }

        if (bleedTimer > 0)
            bleedTimer--;
    }

    public void setXpBar(Float data) {
        this.xpBar = data;
    }

    public void changeBleeding(boolean isBleeding, Integer datum) {
        this.isBleeding = isBleeding;
        if (this.isBleeding) {
            this.isDowned = false;
        }
        this.bleedTimer = datum;
    }

    public void changeDownedState(boolean isDowned, Integer datum) {
        this.isDowned = isDowned;
        if (this.isDowned) {
            this.isBleeding = false;
        } else {
            this.reviveProgress = 0;
        }
        this.bleedTimer = datum - 2;
    }

    public void setReviveProgress(Float data) {
        this.reviveProgress = data;
    }

    public int getThirst() {
        return thirst;
    }

    public void setThirst(Integer data) {
        this.thirst = data;
    }

    public void setWorldTemp(Float data) {
        try {
            CSCompatManager.getHandler().convertTemp(data, (temp, sev) -> {
                this.worldTemp = temp;
                this.severity = sev;
            });
        } catch (Throwable t) {
            CSCompatManager.changeHandler();
            Config.loadDefaultPreset(); //Forces refresh.
            if (Minecraft.getInstance().player != null)
                Minecraft.getInstance().player.sendMessage(
                        new TranslatableComponent("messages.sedparties.api.partiesprefix").withStyle(ChatFormatting.DARK_AQUA).append(
                                new TranslatableComponent("messages.sedparties.api.coldsweatunload").withStyle(ChatFormatting.RED))
                        , Minecraft.getInstance().player.getUUID());
        }
    }

    public void setBodyTemp(float data) {
        this.bodyTemp = (int) data;
    }

    public void updateTemperatures() {
        if (clientPlayer != null) {
            try {
                CSCompatManager.getHandler().getClientWorldTemp(clientPlayer, (temp, sev, body) -> {
                    this.worldTemp = temp;
                    this.severity = sev;
                    this.bodyTemp = body;
                });
            } catch (Throwable t) {
                CSCompatManager.changeHandler();
                Config.loadDefaultPreset(); //Forces refresh.
                if (Minecraft.getInstance().player != null)
                    Minecraft.getInstance().player.sendMessage(
                            new TranslatableComponent("messages.sedparties.api.partiesprefix").withStyle(ChatFormatting.DARK_AQUA).append(
                            new TranslatableComponent("messages.sedparties.api.coldsweatunload").withStyle(ChatFormatting.RED))
                            , Minecraft.getInstance().player.getUUID());
            }

        }
    }

    public void updateTemperaturesTAN() {
        if (clientPlayer != null) {
            TANCompatManager.getHandler().getPlayerTemp(clientPlayer, (temp, text) -> {
                this.worldTemp = temp;
                this.tempType = text;
                if (temp == 0 || temp == 4) {
                    severity = 1;
                } else {
                    severity = 0;
                }
            });
        }
    }

    public void updateThirst() {
        if (clientPlayer != null) {
            this.thirst = TMCompatManager.getHandler().getThirst(clientPlayer);
        }
    }

    public void updateThirstTAN() {
        if (clientPlayer != null) {
            this.thirst = TANCompatManager.getHandler().getPlayerThirst(clientPlayer);
        }
    }

    public void setWorldTempTAN(int data) {
        this.worldTemp = data;
        this.tempType = getTempType(data);
        if (data == 0 || data == 4) {
            severity = 1;
        } else {
            severity = 0;
        }
    }

    private String getTempType(int data) {
        return switch(data) {
            case 0 -> "Icy";
            case 1 -> "Cold";
            case 2 -> "Cool";
            case 3 -> "Warm";
            default -> "Hot";
        };
    }

    public void updateMana() {
        if (clientPlayer != null) {
            ANCompatManager.getHandler().getManaValues(clientPlayer, mana::checkValues);
        }
    }

    public void setMana(float data) {
        mana.checkHealth(data);
    }

    public void setMaxMana(int data) {
        mana.checkMax(data);
    }
}

//TODO: Check max health updates for server tracked player.
