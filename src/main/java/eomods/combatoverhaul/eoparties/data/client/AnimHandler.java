package eomods.combatoverhaul.eoparties.data.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.UUID;

import static eomods.combatoverhaul.eoparties.data.client.ClientData.*;

@OnlyIn(Dist.CLIENT)
class AnimHandler {
    static void addPetToParty(UUID partyMember, List<UUID> pets) {
        //Tell the UI to add pet below partyMember.
        send(getName(partyMember) + " contained " + pets.size() + " pets! You are now " +
                "tracking them!");
    }

    static void registerOnline(UUID partyMember) {
        //Tell the UI to put said party member marked as offline.
        send(getName(partyMember) + " is now online!");
    }

    static void registerOffline(UUID partyMember) {
        //Tell the UI to put said party member marked as online.
        send(getName(partyMember) + " is now offline.");
    }

    static void addToParty(UUID partyMember) {
        //Tell the UI to add this party member to the party.
        //If self, add to party UI.
        send(getName(partyMember) + " is now in your party!");
    }

    static void removePetFromParty(UUID id, List<UUID> pets) {
        //Tell the UI to remove these pets from below party member.
        send(getName(id) + " has removed " + pets.size() + " pet(s) from the party!");
    }

    static void changePartyLead(UUID oldLeader, UUID newLeader) {
        if (!oldLeader.equals(ClientData.EMPTY))
            send(getName(oldLeader) + " is no longer leader!");
        send(getName(newLeader) + " is now the party leader!");
    }

    static void changePartyLead(UUID partyLeader) {
        //Tell the UI that the new party leader is the client themselves.
        if (!partyLeader.equals(ClientData.EMPTY))
            send(getName(partyLeader) + " is no longer leader!");
        send("You are now party leader!");
    }

    static void dropParty() {
        //Tell the UI that the client dropped the party.
        send("You have left the party!");
    }

    static void addClientTracker(UUID trackerMember) {
        //Tell the UI that the client has client access to trackerMember.
        send("You are now able to track " + getName(trackerMember) + " on your client!");
    }

    static void removeClientTracker(UUID trackerMember) {
        //Tell the UI that the client now relies on the server for access to trackerMember.
        send("You are no longer able to track " + getName(trackerMember) + " on the client...");
    }

    static void send(String message) {
        Minecraft.getInstance().player.sendMessage(new StringTextComponent(message));
    }

    static String getName(UUID id) {
        //Check if ID is a player member.
        if (ClientData.partyMembers.get(id) != null) {
            System.out.println(ClientData.partyMembers.get(id).getName());
            if (ClientData.partyMembers.get(id).getName() != null)
                return "[" + ClientData.partyMembers.get(id).getName() + "]";
            else
                return "[????]";
        }
        //Check if ID is a pet member.
        for (RenderPartyMember member : partyMembers.values()) {
            if (member.getPetMember(id) != null)
                if (member.getPetMember(id).getName() != null)
                    return "[" + member.getPetMember(id).getName() + "]";
                else
                    return "[????]";
        }
        return "[????]";
    }

    static void changeMemberName(UUID id, String name) {
        send(getName(id) +  " --> [" + name + "]");
    }

    public static void changePetName(UUID owner, UUID pet, String name) {
        send(getName(owner) + ", " + getName(pet) + " --> [" + name + "]");
    }

    static void resetClientTrackers() {
        send("All tracker data has been reset!");
    }
    public static void printTrackers() {
        for (UUID track : inactiveTracks) {
            System.out.println ("Tracking " + getName(track) + " on the server");
        }
        for (UUID track : activeTracks) {
            System.out.println ("Tracking " + getName(track) + " on the client");
        }

    }

    public static void disbandParty() {
        send("The party has been disbanded...");
    }

    public static void kickedFromParty() {
        send("You have been kicked from the party...");
    }

    public static void removePartyMemberKicked(UUID playerToRemove) {
        //Tell the UI that the party member dropped the party.
        send(getName(playerToRemove) + " has been kicked from the party.");
    }

    public static void removePartyMemberDropped(UUID playerToRemove) {
        //Tell the UI that the party member dropped the party.
        send(getName(playerToRemove) + " has left the party.");
    }
}
