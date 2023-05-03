package io.sedu.mc.parties.api.openpac;

import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public interface IPACHandler {
    void initParties(MinecraftServer server);

    void memberAdded(UUID owner, UUID newMember, UUID partyId);

    void memberLeft(UUID memberLeft);

    void memberKicked(UUID owner, UUID memberLeft, UUID partyId);

    void changeLeader(UUID owner, UUID newLeader);

    void disbandParty(UUID partyId);

    void initPartiesSync(MinecraftServer server);
}
