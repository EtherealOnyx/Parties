package io.sedu.mc.parties.client.overlay;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.mod.arsnoveau.ANCompatManager;
import io.sedu.mc.parties.api.mod.coldsweat.CSCompatManager;
import io.sedu.mc.parties.api.mod.epicfight.EFCompatManager;
import io.sedu.mc.parties.api.mod.feathers.FCompatManager;
import io.sedu.mc.parties.api.mod.playerrevive.PRCompatManager;
import io.sedu.mc.parties.api.mod.spellsandshields.SSCompatManager;
import io.sedu.mc.parties.api.mod.thirstmod.TMCompatManager;
import io.sedu.mc.parties.api.mod.toughasnails.TANCompatManager;
import io.sedu.mc.parties.client.config.Config;
import io.sedu.mc.parties.client.overlay.anim.*;
import io.sedu.mc.parties.client.overlay.effects.ClientEffect;
import io.sedu.mc.parties.client.overlay.effects.EffectHolder;
import io.sedu.mc.parties.data.ClientConfigData;
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
import java.util.function.Consumer;

import static io.sedu.mc.parties.client.overlay.DataType.*;

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

    private final HashMap<DataType, Object> data = new HashMap<>();
    boolean isDead = false;


    //Default to server tracker.
    float alpha = .6f;
    public int alphaI = 216;

    public boolean shouldRenderModel = false;

    //Potion Effects
    public EffectHolder effects = new EffectHolder();

    //Client constructor.
    public ClientPlayerData() {
        trackedOnClient = false;
        playerName = "???";
        initData();
    }



    public static void forSelf(Consumer<ClientPlayerData> action) {
        if (ClientConfigData.renderSelfFrame.get()) {
            action.accept(ClientPlayerData.playerList.get(playerOrderedList.get(0)));
        }
    }

    public static void forOthersOrdered(BiConsumer<Integer, ClientPlayerData> action) {
        for (int i = 1; i < ClientPlayerData.playerOrderedList.size(); i++) {
            action.accept(i, ClientPlayerData.playerList.get(playerOrderedList.get(i)));
        }
    }

    private void initData() {
        data.put(DIM, new DimAnim(100, this));
        data.put(HEALTH, new HealthAnim(20, true));
        data.put(HUNGER, new HungerAnim(20, true));
        if (ANCompatManager.getHandler().exists())
            data.put(MANA, new ManaAnim(20, true));
        if (EFCompatManager.active() || FCompatManager.active())
            data.put(EF_STAM, new StaminAnim(20, true));
        if (SSCompatManager.active())
            data.put(SSMANA, new ManaSSAnim(20, true));
        if (TMCompatManager.active() || TANCompatManager.active())
            data.put(THIRST, new ThirstAnim(20, true));

    }
    public static void getOrderedPlayer(int index, Consumer<ClientPlayerData> action) {
        UUID id;
        ClientPlayerData data;
        if (playerOrderedList.size() > 0 && (id = playerOrderedList.get(index)) != null && (data = playerList.get(id)) != null)
            action.accept(data);
    }

    public static void getSelf(Consumer<ClientPlayerData> action) {
        getOrderedPlayer(0, action);
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
            if (RenderItem.items.get("dim").isEnabled()) playerList.get(p.getUUID()).setClientPlayer(p).getDim().activate(String.valueOf(p.level.dimension().location()), true);
            playerList.get(p.getUUID()).isOnline = true;

        }
    }

    public DimAnim getDim() {
        return (DimAnim) data.get(DIM);
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
        Player p = Minecraft.getInstance().player;
        if (p != null)
            ClientPlayerData.addClientMember(p.getUUID());
    }

    public static void resetOnly() {
        playerList.clear();
        playerOrderedList.clear();
        leader = null;
    }

    public static void updateSelfDim(String data) {
        getSelf(playerData -> {
            if (RenderItem.items.get("dim").isEnabled())
                playerData.getDim().activate(data, false);
        });
    }

    public static void swap(int f, int s) {
        UUID temp = ClientPlayerData.playerOrderedList.get(f);
        ClientPlayerData.playerOrderedList.set(f, ClientPlayerData.playerOrderedList.get(s));
        ClientPlayerData.playerOrderedList.set(s, temp);
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
        getHealth().activate(entity.getHealth(), entity.getMaxHealth(), entity.getAbsorptionAmount());
        data.put(ARMOR, entity.getArmorValue());
        if (getHealth().cur > 0f)
            markAlive();
        else
            markDead();
        alpha = 1f;
        alphaI = 255;
        return this;
    }

    public HealthAnim getHealth() {
        return (HealthAnim) data.get(HEALTH);
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
        return clientPlayer!= null ? clientPlayer.getArmorValue() : (int) data.getOrDefault(ARMOR, 0);
    }

    public void getHunger(Consumer<HungerAnim> action) {
        data.computeIfPresent(HUNGER, (data, hunger) -> {
            action.accept((HungerAnim) hunger);
            return hunger;
        });
    }

    public int getLevelForced() {
        return clientPlayer != null ? clientPlayer.experienceLevel : 0;
    }

    public float getXpBarForced() { return clientPlayer != null ? clientPlayer.experienceProgress : 0;}

    public float getXpBar() {return (float) data.getOrDefault(XPPROGRESS, 0f);}


    public int getXpLevel() {
        return (int) data.getOrDefault(XPLEVEL, 0);
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setHealth(float data) {
        getHealth().checkHealth(data);
    }

    public float getReviveProgress() {
        if ((boolean) data.getOrDefault(BLEEDING, false))
            return clientPlayer != null ? PRCompatManager.getHandler().getReviveProgress(clientPlayer) : (float) data.getOrDefault(REVIVEPROG, 0f);
        return (float) data.getOrDefault(REVIVEPROG, 0f);
    }

    public void setAbsorb(float data) {
        getHealth().checkAbsorb(data);
    }

    public void setArmor(int data) {
        this.data.put(ARMOR, data);
    }

    public void setFood(int data) {
        getHunger(hunger -> hunger.checkHealth(data));
    }

    public void setXp(int data) {
        this.data.put(XPLEVEL, data);
    }

    public void setMaxHealth(float max) {
        getHealth().checkMax(max);
    }

    public void markDead() {
        isDead = true;
        data.put(BLEEDING, false);
        data.put(DOWNED, false);
        effects.markForRemoval();
    }

    public void markAlive() {
        isDead = false;
    }

    public void addEffect(int type, int duration, int amp) {
        effects.add(type, (int) Math.ceil(duration/20f), amp);
        if (MobEffect.byId(type) == MobEffects.ABSORPTION) {
            float absorb = (amp+1)*4f;
            if (getHealth().getAbsorb() < absorb)
                getHealth().checkAbsorb(absorb);
        }
    }

    public void removeEffect(int type) {
        effects.markForRemoval(type);
        if (MobEffect.byId(type) == MobEffects.ABSORPTION) {
            getHealth().checkAbsorb(0f);
        }
    }

    public void tick() {
        if (trackedOnClient)
            getHealth().checkAnim(clientPlayer.getHealth(), clientPlayer.getMaxHealth(), clientPlayer.getAbsorptionAmount());
    }

    public void slowTick() {
        if (!isDead) {
            effects.removeIf(ClientEffect::update);
        }
        int bleedTimer = (int) data.getOrDefault(BLEEDTIMER, 0);
        if (bleedTimer > 0)
            data.put(BLEEDTIMER, --bleedTimer);
    }

    public void setXpBar(float data) {
        this.data.put(XPPROGRESS, data);
    }

    public void changeBleeding(boolean isBleeding, Integer datum) {
        data.put(BLEEDING, isBleeding);
        if (isBleeding) {
            data.put(DOWNED, false);
        }
        data.put(BLEEDTIMER, datum);
    }

    public void changeDownedState(boolean isDowned, Integer datum) {
        data.put(DOWNED, isDowned);
        if (isDowned) {
            data.put(BLEEDING, false);
        } else {
            data.put(REVIVEPROG, 0f);
        }
        data.put(BLEEDTIMER, datum - 2);
    }

    public void setReviveProgress(Float data) {
        this.data.put(REVIVEPROG, data);
    }

    public void getThirst(Consumer<ThirstAnim> action) {
        data.computeIfPresent(THIRST, (data, stam) -> {
            action.accept((ThirstAnim) stam);
            return stam;
        });
    }



    public void setThirst(Integer data) {
        getThirst(thirst -> thirst.checkHealth(data));
    }

    public void setWorldTemp(Float data) {
        try {
            CSCompatManager.getHandler().convertTemp(data, (temp, sev) -> {
                this.data.put(WORLDTEMP, (int) temp);
                this.data.put(SEVERITY, (int) sev);
            });
        } catch (Throwable t) {
            CSCompatManager.changeHandler();
            Config.loadDefaultPreset(); //Forces refresh.
            if (Minecraft.getInstance().player != null)
                Minecraft.getInstance().player.sendMessage(
                        new TranslatableComponent("messages.sedparties.api.partiesprefix").withStyle(ChatFormatting.DARK_AQUA).append(
                                new TranslatableComponent("messages.sedparties.api.coldsweatunload").withStyle(ChatFormatting.RED))
                        , Minecraft.getInstance().player.getUUID());
            Parties.LOGGER.error("Failed to support Cold Sweat!", t);
        }
    }

    public void setBodyTemp(float data) {
        this.data.put(BODYTEMP, (int) data);
    }

    public void updateTemperatures() {

        if (clientPlayer != null) {
            try {
                CSCompatManager.getHandler().getClientWorldTemp(clientPlayer, (temp, sev, body) -> {
                    data.put(WORLDTEMP, temp);
                    data.put(SEVERITY, sev);
                    data.put(BODYTEMP, body);
                });
            } catch (Throwable t) {
                CSCompatManager.changeHandler();
                Config.loadDefaultPreset(); //Forces refresh.
                if (Minecraft.getInstance().player != null)
                    Minecraft.getInstance().player.sendMessage(
                            new TranslatableComponent("messages.sedparties.api.partiesprefix").withStyle(ChatFormatting.DARK_AQUA).append(
                            new TranslatableComponent("messages.sedparties.api.coldsweatunload").withStyle(ChatFormatting.RED))
                            , Minecraft.getInstance().player.getUUID());
                Parties.LOGGER.error("Failed to support Cold Sweat!", t);
            }

        }
    }

    public void updateTemperaturesTAN() {
        if (clientPlayer != null) {
            TANCompatManager.getHandler().getPlayerTemp(clientPlayer, (temp, text) -> {
                data.put(WORLDTEMP, temp);
                this.data.put(TEMPTYPE, text);
                if (temp == 0 || temp == 4) {
                    data.put(SEVERITY, 1);
                } else {
                    data.put(SEVERITY, 0);
                }
            });
        }
    }

    public void updateThirst() {
        if (clientPlayer != null) {
            getThirst(thirst -> thirst.checkAnim(TMCompatManager.getHandler().getThirst(clientPlayer), 20, TMCompatManager.getHandler()
                                                                                                                          .getQuench(clientPlayer)));
        }
    }

    public void updateThirstTAN() {
        if (clientPlayer != null) {
            getThirst(thirst -> thirst.checkHealth(TANCompatManager.getHandler().getPlayerThirst(clientPlayer)));
        }
    }

    public void setWorldTempTAN(int data) {
        this.data.put(WORLDTEMP, data);
        this.data.put(TEMPTYPE, getTempType(data));
        if (data == 0 || data == 4) {
            this.data.put(SEVERITY, 1);
        } else {
            this.data.put(SEVERITY, 0);
        }
    }

    private String getTempType(int data) {
        Parties.LOGGER.debug("Setting temperature for: " + data);
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
            ANCompatManager.getHandler().getManaValues(clientPlayer, (f1, f2) -> getMana(mana -> mana.checkValues(f1, f2)));
        }
    }

    public void getMana(Consumer<ManaAnim> action) {
        data.computeIfPresent(MANA, (data, mana) -> {
            action.accept((ManaAnim) mana);
            return mana;
        });
    }



    public void setMana(float data) {
        getMana(mana -> mana.checkHealth(data));
    }

    public void setMaxMana(int data) {
        getMana(mana -> mana.checkMax(data));
    }

    public boolean bleedOrDowned() {
        return (boolean) data.getOrDefault(BLEEDING, false) || (boolean) data.getOrDefault(DOWNED, false);
    }

    public boolean getBleeding() {
        return (boolean) data.getOrDefault(BLEEDING, false);
    }

    public boolean getDowned() {
        return (boolean) data.getOrDefault(DOWNED, false);
    }

    public int getTimer() {
        return (int) data.getOrDefault(BLEEDTIMER, 0);
    }

    public int getWorldTemp() {
        return (int) data.getOrDefault(WORLDTEMP, 0);
    }

    public int getBodyTemp() {
        return (int) data.getOrDefault(BODYTEMP, 0);
    }

    public String getTempType() {
        return (String) data.getOrDefault(TEMPTYPE, "");
    }

    public int getSeverity() {
        return (int) data.getOrDefault(SEVERITY, 0);
    }

    public void getStaminaEF(Consumer<StaminAnim> action) {
        data.computeIfPresent(EF_STAM, (data, stam) -> {
            action.accept((StaminAnim) stam);
            return stam;
        });
    }

    public void updateStamEF() {
        if (clientPlayer != null) {
            EFCompatManager.getHandler().getClientValues(clientPlayer, (f1, f2) -> getStaminaEF(stam -> stam.checkAnim(f1, f2, 0f)));
        }
    }

    public void setCurrentStamina(float stamina) {
        getStaminaEF(stam -> stam.checkHealth(stamina));
    }

    public void setMaxStamina(int stamina) {
        getStaminaEF(stam -> stam.checkMax(stamina));
    }

    public void checkHunger() {
        if (clientPlayer != null) {
            getHunger(hunger -> hunger.checkHealth(clientPlayer.getFoodData().getFoodLevel()));
        }
    }

    public void getManaSS(Consumer<ManaSSAnim> action) {
        data.computeIfPresent(SSMANA, (data, mana) -> {
            action.accept((ManaSSAnim) mana);
            return mana;
        });
    }

    public void updateManaSS() {
        if (clientPlayer != null) {
            getManaSS(mana -> SSCompatManager.getHandler().getAllMana(clientPlayer, mana::checkAnim));
        }

    }

    public void checkMaxManaSS() {
        if (clientPlayer != null) {
            getManaSS(mana -> mana.checkMax(SSCompatManager.getHandler().getMax(clientPlayer)));
        }
    }

    public void setManaSS(Float data) {
        getManaSS(mana -> mana.checkHealth(data));
    }

    public void setMaxManaSS(Float data) {
        getManaSS(mana -> mana.checkMax(data));
    }

    public void setExtraManaSS(Float data) {
        getManaSS(mana -> mana.checkAbsorb(data));
    }

    public void checkStamF() {
        FCompatManager.getHandler().getClientFeathers((cur, max, abs) -> getStaminaEF(staminAnim -> staminAnim.checkAnim(Math.min(max, cur), max, abs)));
    }

    public void setExtraStam(Integer data) {
        getStaminaEF(stam -> stam.checkAbsorb(data));
    }

    public void setQuench(int data) {
        getThirst(thirst -> thirst.checkAbsorb(data));
    }

    public void setMaxHunger(float data) {
        getHunger(hunger -> hunger.checkMax(data));
    }

    public void setSaturation(float data) {
        getHunger(hunger -> hunger.checkAbsorb(data));
    }
}

//TODO: Check max health updates for server tracked player.
