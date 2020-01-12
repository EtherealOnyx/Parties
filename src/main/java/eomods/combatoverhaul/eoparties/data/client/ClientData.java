package eomods.combatoverhaul.eoparties.data.client;

import eomods.combatoverhaul.eoparties.network.Handler;
import eomods.combatoverhaul.eoparties.network.ServerPacketData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
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
    private static HashSet<UUID> inactiveTracks = new HashSet<>();


    public static void changeOnline(UUID id, boolean isOnline) {
        if (partyMembers.containsKey(id))
            partyMembers.get(id).setOnline(isOnline);

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

    public static void addMembers(ArrayList<UUID> list) {
        for (UUID id : list) {
            if (!partyMembers.containsKey(id)) {
                partyMembers.put(id, new RenderPartyMember(id));
                AnimHandler.addToParty(id);
                inactiveTracks.add(id);
            }
        }

    }

    public static void addPetMembers(UUID id, UUID... pets) {
        if (partyMembers.containsKey(id))
            partyMembers.get(id).addPet(pets);
        else {
            partyMembers.put(id, new RenderPartyMember(pets));
            inactiveTracks.add(id);
        }
        inactiveTracks.addAll(Arrays.asList(pets));
        AnimHandler.addPetToParty(id, pets);
    }


    public static void removePetMembers(UUID id, UUID... pets) {
        if (partyMembers.containsKey(id)) {
            partyMembers.get(id).removePet(pets);
            if (isSelf(id))
                partyMembers.remove(id);
        }
        AnimHandler.removePetFromParty(id, pets);
        inactiveTracks.removeAll(Arrays.asList(pets));
        activeTracks.removeAll(Arrays.asList(pets));
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
        AnimHandler.removePartyMember(partyMember);
        inactiveTracks.remove(partyMember);
        activeTracks.remove(partyMember);
        for (UUID pet : partyMembers.get(partyMember).getPetList()) {
            inactiveTracks.remove(pet);
            activeTracks.remove(pet);
        }
    }

    public static void removePartyMember() {
        AnimHandler.dropParty();
        for (Map.Entry<UUID, RenderPartyMember> partyMember : partyMembers.entrySet()) {
            if (!isSelf(partyMember.getKey())) {
                partyMembers.remove(partyMember.getKey());
                activeTracks.remove(partyMember.getKey());
                inactiveTracks.remove(partyMember.getKey());
                for (UUID pet : partyMember.getValue().getPetList()) {
                    inactiveTracks.remove(pet);
                    activeTracks.remove(pet);
                }
            }

        }
        partyLeader = EMPTY;
    }

    public static void checkTrackerData(LivingEntity entity) {
        if (inactiveTracks.contains(entity.getUniqueID())) {
            //Remove from inactive tracks.
            inactiveTracks.remove(entity.getUniqueID());

            //Add to active tracks.
            activeTracks.add(entity.getUniqueID());
            updateClientInfo(entity.getUniqueID(), entity);
            //Send Packet #0 to server.
            Handler.network.sendToServer(new ServerPacketData(0, entity.getUniqueID()));
            AnimHandler.addClientTracker(entity.getUniqueID());
        }

    }

    private static void updateClientInfo(UUID entityToUpdate, LivingEntity entity) {
        if (partyMembers.containsKey(entityToUpdate)) {
            updatePartyMemberInfo(partyMembers.get(entityToUpdate), entity);
            return;
        }
        for (Map.Entry<UUID, RenderPartyMember> partyMember : partyMembers.entrySet()) {
            if (partyMember.getValue().getPetList().contains(entityToUpdate)) {
                updatePetMemberInfo(partyMember.getValue().getPets().get(entityToUpdate), entity);
                return;
            }
        }
    }

    private static void updatePartyMemberInfo(RenderPartyMember memberRender, LivingEntity entity) {
        System.out.println("Changing name of partyMember...");
        memberRender.setName(entity.getName().getFormattedText());
        //Set health, etc.
        memberRender.setOnline(true);
    }

    private static void updatePetMemberInfo(RenderPetMember memberRender, LivingEntity entity) {
        memberRender.setName(entity.getName().getFormattedText());
    }

    public static void checkTrackerData(ClassInheritanceMultiMap<Entity>[] pos) {
        Iterator iter;
        Entity entity;
        for (ClassInheritanceMultiMap<Entity> subMap : pos) {
            System.out.println(subMap.size());
                iter = subMap.iterator();
                while (iter.hasNext()) {
                    entity = (Entity) iter.next();
                    System.out.println("Entity with id: " + entity.getName());
                    for (UUID id : activeTracks)
                        if (((Entity) iter.next()).getUniqueID().equals(id))
                            transferToServer(id);
                }
        }
        /*for (Map.Entry<UUID, EntityLivingBase> entity : activeTracks.entrySet()) {
            if (entity.getValue().chunkCoordX == pos.x && entity.getValue().chunkCoordZ == pos.z) {
                inactiveTracks.add(entity.getKey());
                activeTracks.remove(entity.getKey());
                AnimHandler.removeClientTracker(entity.getKey());
                //Send Packet #1 to server.
                COPSHandler.INSTANCE.sendToServer(new PacketServer(1, entity.getKey()));
            }
        }*/
    }

    private static void transferToServer(UUID toTransfer) {
        inactiveTracks.add(toTransfer);
        activeTracks.remove(toTransfer);
        AnimHandler.removeClientTracker(toTransfer);
        //Send Packet #1 to server.
        Handler.network.sendToServer(new ServerPacketData(1, toTransfer));
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
            partyMembers.get(id).setName(name);
            AnimHandler.changeMemberName(id, name);
        }
    }

    public static void moveAllTrackers() {
        inactiveTracks.addAll(activeTracks);
        activeTracks.clear();
        AnimHandler.resetClientTrackers();
    }

    public static void defaultData() {
        //Delay this a bit more...
        partyMembers.put(Minecraft.getInstance().player.getUniqueID(),
                new RenderPartyMember(Minecraft.getInstance().player.getName().getFormattedText()));
    }
}
