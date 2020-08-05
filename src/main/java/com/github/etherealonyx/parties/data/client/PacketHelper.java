package com.github.etherealonyx.parties.data.client;

import com.github.etherealonyx.parties.data.PartyData;
import com.github.etherealonyx.parties.data.PlayerData;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.github.etherealonyx.parties.data.client.Util.exists;

public class PacketHelper {

    //This marks the player online.
    public static void markOnline(UUID uuid) {
        if (exists(uuid)) {
            ClientData.players.get(uuid).markOnline();
            System.out.println(uuid + " is now online.");
        }
        //TODO: Fix trackers.
    }

    //This marks the player offline.
    public static void markOffline(UUID uuid) {
        if (exists(uuid)) {
            ClientData.players.get(uuid).markOffline();
            System.out.println(uuid + " is now offline.");
        }
        //TODO: Fix trackers.
    }

    //This adds member to the party.
    public static void addMembers(ArrayList<UUID> list) {
        partyExists();

        list.forEach(id -> {
            //Adds members to players list on client side.
            ClientData.players.put(id, new PlayerData());
            //Adds members to party list on client side.
            ClientData.party.addMember(id);
            System.out.println("Adding" + id + " to member list.");
        });
        //TODO: Fix trackers.
    }

    private static void partyExists() {
        if (ClientData.party == null) {
            ClientData.party = new PartyData();
            ClientData.players.values().forEach(data -> data.assignParty(ClientData.party));
            System.out.println("Party didn't exist! Creating a new one.");
        }
    }

    //This adds pet members to the player.
    public static void addPetMembers(UUID uuid, List<UUID> subList) {
        if (!exists(uuid)) {
            System.out.println("Issue adding pets! Player doesn't exist!");
            return;
        }
        ClientData.players.get(uuid).addPets((UUID[]) subList.toArray());
        System.out.println("Adding pets to " + uuid + "'s pet list");
        //TODO: Fix trackers.
    }

    //This removes the pet members from the party.
    public static void removePetMembers(UUID uuid, List<UUID> subList) {
        if (!exists(uuid)) {
            System.out.println("Issue adding pets! Player doesn't exist!");
            return;
        }
        ClientData.players.get(uuid).addPets((UUID[]) subList.toArray());
        System.out.println("Removing pets from " + uuid + "'s pet list");
        //TODO: Fix trackers.
    }


    //This changes the leader (to the client).
    public static void changeLeader() {
        partyExists();
        ClientData.party.changeLeader(ClientData.client);
        ClientData.players.get(ClientData.client).makeLeader();
        System.out.println("Changing leader to client.");
    }

    //This changes the leader to the given player.
    public static void changeLeader(UUID uuid) {
        partyExists(); // This shouldn't be needed...
        if (!exists(uuid)) {
            System.out.println("Issue changing leader! Player doesn't exist!");
            return;
        }
        if (ClientData.party.getLeader() != null) {
            ClientData.players.get(ClientData.party.getLeader()).removeLead();
        }
        ClientData.party.changeLeader(uuid);
        ClientData.players.get(uuid).makeLeader();
        System.out.println("Changing leader to " + uuid);
    }

    //This makes the client drop party.
    public static void dropParty() {
        ClientData.party = null;
        ClientData.players.entrySet().removeIf(entry -> !entry.getKey().equals(ClientData.client));
        System.out.println("Dropping the party");
        //TODO: Fix trackers.
    }


    //This makes the given player drop the party.
    public static void removePartyMemberDropped(UUID uuid) {
        ClientData.party.removeMember(uuid);
        ClientData.players.remove(uuid);
        System.out.println("Party member " + uuid + "left the party.");
        //TODO: Fix trackers.
    }

    //This tells the client to move all the trackers to the server-side for tracking there.
    public static void moveAllTrackers() {
        ClientData.trackers.entrySet().forEach(entry -> entry.getValue().changeToServ());
        System.out.println("Moving all trackers to the server.");
        //TODO: Fix trackers.
    }

    public static void moveTracker(UUID uuid) {
        ClientData.trackers.get(uuid).changeToServ();
        System.out.println("Moving a specific tracker to the server");
        //TODO: Fix trackers.
    }

    public static void defaultData() {
        ClientData.party = null;
        ClientData.players = new HashMap<>();
        ClientData.client = Minecraft.getInstance().player.getUniqueID();
        ClientData.players.put(ClientData.client, new PlayerData());
        //TODO: Fix trackers.
        System.out.println("Grabbing default data...");
    }

    public static void dropPartyKicked() {
        ClientData.party = null;
        ClientData.players.entrySet().removeIf(entry -> !entry.getKey().equals(ClientData.client));
        //TODO: Fix trackers.
        System.out.println("You have been kicked from the party...");
    }

    public static void removePartyMemberKicked(UUID uuid) {
        ClientData.party.removeMember(uuid);
        ClientData.players.remove(uuid);
        //TODO: Fix trackers.
        System.out.println("Party member " + uuid + " was kicked from the party.");
    }

    public static void disbandParty() {
        ClientData.party = null;
        ClientData.players.entrySet().removeIf(entry -> !entry.getKey().equals(ClientData.client));
        //TODO: Fix trackers.
    }

    public static void attemptToFindTrackers() {
        //TODO: Tee-hee.
    }
}
