package io.sedu.mc.parties.api.openpac;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.data.PartySaveData;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public class PACHandlerFake implements IPACHandler {

    @Override
    public void initParties(MinecraftServer server) {
        //initialize default...
        Parties.LOGGER.error("Error initializing parties with Open-PAC Support!");
        PartySaveData.get();
    }

    @Override
    public void memberAdded(UUID owner, UUID newMember, UUID partyId) {
    }

    @Override
    public void memberLeft(UUID memberLeft) {

    }

    @Override
    public void memberKicked(UUID owner, UUID memberLeft, UUID partyId) {

    }

    @Override
    public void changeLeader(UUID owner, UUID newLeader) {

    }

    @Override
    public void disbandParty(UUID partyId) {

    }
}
