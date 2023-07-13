package io.sedu.mc.parties.data;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartySaveData extends SavedData
{
    private static final String ID = "partiesdata";

    public static ServerLevel globalLevel;
    public static MinecraftServer server;

    public static @NotNull PartySaveData get() {
        return globalLevel.getDataStorage().computeIfAbsent(PartySaveData::new, PartySaveData::new, ID);
    }

    public PartySaveData() {
        Parties.LOGGER.info("Creating party save data...");
    }

    public PartySaveData(CompoundTag tag) {
        Parties.LOGGER.info("Loading party save data...");
        if (ServerConfigData.isPersistDisabled() || ServerConfigData.isPartySyncEnabled()) {
            Parties.LOGGER.info("Loading cancelled, party persistence disabled or syncing from other parties mod...");
            return;
        }

        ListTag parties = tag.getList("parties", Tag.TAG_COMPOUND);
        for (Tag t : parties) {

            CompoundTag tC = (CompoundTag) t;
            //Add new party to save;
            UUID partyId;
            PartyData party = new PartyData(partyId = tC.getUUID("id"), tC.getUUID("leader"), false);
            ListTag members = tC.getList("members", Tag.TAG_COMPOUND);
            for (Tag m : members) {
                CompoundTag mC = (CompoundTag) m;
                //Add Player first.
                UUID pId = mC.getUUID("id");
                new ServerPlayerData(pId, partyId, mC.getString("name"));
                //Then add player to party.
                party.addMemberSilently(pId);
            }
            //Then add party.
            PartyData.partyList.put(partyId, party);
        }

        Parties.LOGGER.debug("Created " + PartyData.partyList.size() + " parties.");
        for (PartyData p : PartyData.partyList.values()) {
            Parties.LOGGER.debug("Party with " + p.getMembers().size() + " people");
            Parties.LOGGER.debug("Party leader is " + PlayerAPI.getName(p.getLeader()));
            for (UUID member : p.getMembers()) {
                Parties.LOGGER.debug("Party member: " + PlayerAPI.getName(member));

            }
        }
    }


    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        Parties.LOGGER.info("Saving parties to disk...");
        if (ServerConfigData.isPersistDisabled()) {
            Parties.LOGGER.info("Saving cancelled, party persistence disabled...");
            return tag;
        }
        ListTag list = new ListTag();
        PartyData.partyList.forEach((uuid, partyData) -> {
            CompoundTag partyTag = new CompoundTag();
            partyTag.putUUID("id", uuid);
            partyTag.putUUID("leader", partyData.getLeader());
            ListTag partyMems = new ListTag();
            partyData.getMembers().forEach((id) -> {
                CompoundTag playerTag = new CompoundTag();
                playerTag.putString("name", PlayerAPI.getName(id));
                playerTag.putUUID("id", id);
                partyMems.add(playerTag);
            });
            partyTag.put("members", partyMems);
            list.add(partyTag);
        });
        tag.put("parties", list);
        return tag;
    }
}
