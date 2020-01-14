package eomods.combatoverhaul.eoparties.data.client;

import eomods.combatoverhaul.eoparties.data.server.Util;
import eomods.combatoverhaul.eoparties.network.Handler;
import eomods.combatoverhaul.eoparties.network.ServerPacketData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class ClientData {

    //This stores all the party members in a HashMap.
    public static HashMap<UUID, RenderPartyMember> partyMembers = new HashMap<>();
    static final UUID EMPTY = new UUID(0,0);
    static UUID partyLeader = EMPTY;


    //This stores all of the UUIDs that the client is able to actively track because it's in their rendering range.
    static HashSet<UUID> activeTracks = new HashSet<>();

    //This stores a list of all the UUIDs in which the client isn't able to track.
    static HashSet<UUID> inactiveTracks = new HashSet<>();


    public static void changeOnline(UUID id, boolean isOnline) {
        if (partyMembers.containsKey(id)) {
            if (partyMembers.get(id).isOnline() == isOnline)
                return;
            partyMembers.get(id).setOnline(isOnline);
        }

        if (isOnline) {
            inactiveTracks.add(id);
            AnimHandler.registerOnline(id);
        }
        else {
            AnimHandler.registerOffline(id);
            inactiveTracks.remove(id);
            activeTracks.remove(id);
        }
    }

    public static void changeOnlineForced(UUID id, boolean isOnline) {
        if (partyMembers.containsKey(id)) {
            if (partyMembers.get(id).isOnline() == isOnline)
                return;
            partyMembers.get(id).setOnline(isOnline);
        }
        if (isOnline) {
            AnimHandler.registerOnline(id);
        }
        else {
            AnimHandler.registerOffline(id);
        }
    }

    public static void addMembers(ArrayList<UUID> list) {
        for (UUID id : list) {
            if (!partyMembers.containsKey(id)) {
                partyMembers.put(id, new RenderPartyMember(id));
                AnimHandler.addToParty(id);
            }
        }

    }

    public static void addPetMembers(UUID id, List<UUID> pets) {
        if (partyMembers.containsKey(id))
            partyMembers.get(id).addPet(pets);
        else {
            partyMembers.put(id, new RenderPartyMember(pets));
            if (!isSelf(id))
                inactiveTracks.add(id);
        }
        inactiveTracks.addAll(pets);
        AnimHandler.addPetToParty(id, pets);
    }


    public static void removePetMembers(UUID id, List<UUID> pets) {
        if (partyMembers.containsKey(id)) {
            partyMembers.get(id).removePet(pets);
            if (isSelf(id))
                partyMembers.remove(id);
        }
        AnimHandler.removePetFromParty(id, pets);
        inactiveTracks.removeAll(pets);
        activeTracks.removeAll(pets);
    }

    private static boolean isSelf(UUID id) {
        return Minecraft.getInstance().player.getUniqueID().equals(id);
    }

    public static void changeLeader(UUID newLeader) {
        AnimHandler.changePartyLead(partyLeader, newLeader);
        partyLeader = newLeader;
    }

    public static void changeLeader() {
        AnimHandler.changePartyLead(partyLeader);
        partyLeader = Minecraft.getInstance().player.getUniqueID();
    }

    public static void removePartyMember(UUID partyMember) {
        inactiveTracks.remove(partyMember);
        activeTracks.remove(partyMember);
        for (UUID pet : partyMembers.get(partyMember).getPetList()) {
            inactiveTracks.remove(pet);
            activeTracks.remove(pet);
        }
        partyMembers.remove(partyMember);
    }

    public static void dropParty() {
        AnimHandler.dropParty();
        removeParty();
    }

    private static void removeParty() {
        Iterator iter = partyMembers.entrySet().iterator();
        Map.Entry<UUID, RenderPartyMember> partyMember;
        while (iter.hasNext()) {
            partyMember = (Map.Entry<UUID, RenderPartyMember>) iter.next();
            if (!isSelf(partyMember.getKey())) {
                for (UUID pet : partyMember.getValue().getPetList()) {
                    inactiveTracks.remove(pet);
                    activeTracks.remove(pet);
                }
                activeTracks.remove(partyMember.getKey());
                inactiveTracks.remove(partyMember.getKey());
                iter.remove();
            }
        }
        partyLeader = EMPTY;
    }

    public static void checkTrackerData(LivingEntity entity) {
        if (inactiveTracks.contains(entity.getUniqueID())) {
            //Remove from inactive tracks.
            inactiveTracks.remove(entity.getUniqueID());

            //Add to active tracks.
            moveToClient(entity.getUniqueID(), entity);
            return;
        }
        //Check if entity is player that was marked offline.
        if (partyMembers.containsKey(entity.getUniqueID()) && Util.notMe(entity.getUniqueID())) {
            moveToClient(entity.getUniqueID(), entity);
        }
    }

    public static void checkTrackerData(ChunkPos pos) {
        for (UUID activeTracking : activeTracks) {
            if (partyMembers.get(activeTracking).getChunk().equals(pos)) {
                movePlayerToServer(activeTracking);
                for (Map.Entry<UUID, RenderMember> pets : partyMembers.get(activeTracking).getPets().entrySet()) {
                    if (pets.getValue().getChunk().equals(pos))
                        movePetToServer(activeTracking, pets.getKey());
                }
            }
        }
    }

    private static void movePlayerToServer(UUID activeTracking) {
        partyMembers.get(activeTracking).entity = null;
        moveToServer(activeTracking);
    }
    private static void moveToServer(UUID activeTracking) {
        activeTracks.remove(activeTracking);
        inactiveTracks.add(activeTracking);
        AnimHandler.removeClientTracker(activeTracking);
        //Send Packet #1 to server.
        Handler.network.sendToServer(new ServerPacketData(1, activeTracking));
    }

    private static void movePetToServer(UUID owner, UUID activeTracking) {
        partyMembers.get(owner).getPetMember(activeTracking).entity = null;
        moveToServer(activeTracking);
    }

    private static void moveToClient(UUID entityToUpdate, LivingEntity entity) {

        if (partyMembers.containsKey(entityToUpdate)) {
            updatePartyMemberInfo(partyMembers.get(entityToUpdate), entity);
            return;
        }
        for (Map.Entry<UUID, RenderPartyMember> partyMember : partyMembers.entrySet()) {
            if (partyMember.getValue().getPetList().contains(entityToUpdate)) {
                updateMemberInfo(partyMember.getValue().getPets().get(entityToUpdate), entity);
                return;
            }
        }
    }

    private static void updatePartyMemberInfo(RenderPartyMember memberRender, LivingEntity entity) {
        changeOnlineForced(entity.getUniqueID(), true);
        updateMemberInfo(memberRender, entity);

    }

    private static void updateMemberInfo(RenderMember memberRender, LivingEntity entity) {
        memberRender.setName(entity.getName().getFormattedText());
        memberRender.entity = entity;
        activeTracks.add(entity.getUniqueID());
        inactiveTracks.remove(entity.getUniqueID());
        Handler.network.sendToServer(new ServerPacketData(0, entity.getUniqueID()));
        AnimHandler.addClientTracker(entity.getUniqueID());
    }

    public static void removeTracker(UUID trackerToRemove) {
        AnimHandler.removeClientTracker(trackerToRemove);
        activeTracks.remove(trackerToRemove);
        inactiveTracks.add(trackerToRemove);
    }

    public static void resetData() {
        //This is called when the client disconnects from the server. The client needs to get rid of all their data
        // related to the server.
        partyMembers.clear();
        System.out.println("Clearing out client data....");
        activeTracks.clear();
        inactiveTracks.clear();
        partyLeader = EMPTY;
    }

    public static void changeName(UUID id, String name) {
        if(partyMembers.containsKey(id)) {
            AnimHandler.changeMemberName(id, name);
            partyMembers.get(id).setName(name);
            return;
        }

        for (Map.Entry<UUID, RenderPartyMember> partyMember : partyMembers.entrySet()) {
            if (partyMember.getValue().getPetMember(id) != null) {
                AnimHandler.changePetName(partyMember.getKey(), id, name);
                partyMember.getValue().getPetMember(id).setName(name);
            }
        }
    }

    public static void moveAllTrackers() {
        inactiveTracks.addAll(activeTracks);
        activeTracks.clear();
        AnimHandler.resetClientTrackers();
    }

    public static void defaultData() {
        //Delay this a bit more...maybe
        partyMembers.put(Minecraft.getInstance().player.getUniqueID(),
                new RenderPartyMember(Minecraft.getInstance().player.getName().getFormattedText()));
    }

    public static void printTrackers() {
        System.out.println("Attempting to print trackers...");
        AnimHandler.printTrackers();
    }

    public static void disbandParty() {
        AnimHandler.disbandParty();
        removeParty();
    }

    public static void removePartyMemberKicked(UUID playerToRemove) {
        AnimHandler.removePartyMemberKicked(playerToRemove);
        removePartyMember(playerToRemove);
    }

    public static void removePartyMemberDropped(UUID playerToRemove) {
        AnimHandler.removePartyMemberDropped(playerToRemove);
        removePartyMember(playerToRemove);
    }

    public static void dropPartyKicked() {
        AnimHandler.kickedFromParty();
        removeParty();
    }

    public static void triggerUpdate(UUID playerToUpdate, int upgradeType) {
        triggerUpdate(playerToUpdate, null, upgradeType);
    }

    public static void triggerUpdate(UUID playerOwner, UUID petToUpdate, int upgradeType) {
        RenderMember member = partyMembers.get(playerOwner);
        if (petToUpdate != null)
            member = ((RenderPartyMember) member).getPetMember(petToUpdate);
        switch(upgradeType) {
            case 0:
                //Health
                triggerUpdate(playerOwner, petToUpdate, upgradeType, member.getAmount(upgradeType));
        }
    }

    public static void triggerUpdate(UUID playerOwner, UUID petToUpdate, int upgradeType, float amount) {
        StatUpdateRenderHelper helper = new StatUpdateRenderHelper(playerOwner, petToUpdate, upgradeType, amount);
        //Do something with this...
        //addQueue(helper);
    }

    public static void attemptToFindTrackers() {
        for (Entity e : Minecraft.getInstance().world.getAllEntities()) {
            if (e instanceof TameableEntity || e instanceof PlayerEntity)
                checkTrackerData((LivingEntity) e);
        }


    }
}
