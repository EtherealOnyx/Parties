package com.github.etherealonyx.parties.data.server;

import com.github.etherealonyx.parties.data.PartyData;
import com.github.etherealonyx.parties.network.ClientPacketData;
import com.github.etherealonyx.parties.network.NetworkHandler;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.UUID;

import static com.github.etherealonyx.parties.data.server.Util.getNet;

public class PacketHelper {

    public static void updateLeader(UUID newLeader) {
        Util.getParty(newLeader).getMembers().forEach(member -> updateLeader(newLeader, member));
    }

    //This only sends the new leader to the targetMember, instead of sending it to the whole party.
    public static void updateLeader(UUID newLeader, UUID targetMember) {
        if (targetMember == newLeader)
        NetworkHandler.network.sendTo(new ClientPacketData(5), getNet(targetMember),
                NetworkDirection.PLAY_TO_CLIENT);
        else
            NetworkHandler.network.sendTo(new ClientPacketData(5, newLeader), getNet(targetMember),
                    NetworkDirection.PLAY_TO_CLIENT);

    }


    public static void setOnline(UUID uniqueID) {
    }

    public static void setOffline(UUID uniqueID) {
    }

    public static void addMember(UUID futureMember) {
        for (UUID id : Util.getParty(futureMember).getMembers()) {
            if (id == futureMember) {
                NetworkHandler.network.sendTo(new ClientPacketData(2, Util.getParty(futureMember).getMembers(),
                                futureMember),
                        getNet(futureMember),
                        NetworkDirection.PLAY_TO_CLIENT);
            } else {
                NetworkHandler.network.sendTo(new ClientPacketData(2, futureMember), getNet(id),
                        NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }

    public static void kickPlayer(UUID removedMember) {
        for (UUID id : Util.getParty(removedMember).getMembers()) {
            if (id == removedMember) {
                NetworkHandler.network.sendTo(new ClientPacketData(9),
                        getNet(removedMember),
                        NetworkDirection.PLAY_TO_CLIENT);
            } else {
                NetworkHandler.network.sendTo(new ClientPacketData(9, removedMember), getNet(id),
                        NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }

    public static void disbandParty(PartyData party) {
        party.getMembers().forEach(id -> NetworkHandler.network.sendTo(new ClientPacketData(10),
                getNet(id), NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void runDefault(UUID id) {
        NetworkHandler.network.sendTo(new ClientPacketData(8), getNet(id), NetworkDirection.PLAY_TO_CLIENT);

    }
}
