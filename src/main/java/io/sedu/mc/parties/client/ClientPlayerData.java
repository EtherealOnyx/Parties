package io.sedu.mc.parties.client;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.sedu.mc.parties.Parties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientPlayerData {
    public static HashMap<UUID, ClientPlayerData> playerList = new HashMap<>();
    public static ArrayList<UUID> playerOrderedList = new ArrayList<>();

    //Client Information
    //private static int globalIndex;
    //private int partyIndex;
    private Player clientPlayer;
    //Client-side functionality.
    private boolean isOnline;
    private String playerName;
    private boolean trackedOnClient;
    public static UUID leader;
    //Skin ResourceLocation
    private ResourceLocation skinLoc = null;
    private static final ResourceLocation defaultLoc = new ResourceLocation(Parties.MODID, "default.png");

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

    public void setId(UUID uuid) {
        trackedOnClient = false;
        playerList.put(uuid, this);
        playerOrderedList.add(uuid);
    }

    private void setSkin(String name) {
        System.out.println("Setting skin...");
        //Magic, here we go.
        SkullBlockEntity.updateGameprofile(new GameProfile(null, name), (filledProfile) -> {
            System.out.println("Found skin! :)");
            Minecraft minecraft = Minecraft.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(filledProfile);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                System.out.println("Has texture info...");
                this.skinLoc = minecraft.getSkinManager().registerTexture(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            } else {
                System.out.println("Still no texture!?");
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
    }

    public void setClientPlayer(Player entity) {
        clientPlayer = entity;
        trackedOnClient = true;
        playerName = entity.getName().getContents();
        //Try to add client skin if it exists.
        if (skinLoc == null) {
            setSkin(playerName);
        }
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
        return skinLoc == null ? defaultLoc : skinLoc;
    }
}
