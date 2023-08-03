package io.sedu.mc.parties.network;

import io.sedu.mc.parties.client.config.Config;
import io.sedu.mc.parties.client.config.DimConfig;
import io.sedu.mc.parties.client.overlay.RenderItem;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.setup.ClientSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

import static io.sedu.mc.parties.network.ClientPacketHelper.msg;

public class ClientPacketData {

    private final ArrayList<UUID> list;
    private final int type;

    ClientPacketData(FriendlyByteBuf buf) {
        this.type = buf.readInt();
        list = new ArrayList<>();
        while (true) {
            try {
                list.add(new UUID(buf.readLong(), buf.readLong()));
            } catch(IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public ClientPacketData(int i, ArrayList<UUID> party) {
        
        this.type = i;
        list = party;
    }

    public ClientPacketData(int i, UUID futureMember) {
        
        list = new ArrayList<>();
        list.add(futureMember);
        this.type = i;
    }

    public ClientPacketData(int i) {
        
        this.type = i;
        list = new ArrayList<>();
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        for (UUID id : list) {
            buf.writeLong(id.getMostSignificantBits());
            buf.writeLong(id.getLeastSignificantBits());
        }

    }

    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        switch (type) {
            //#0 - Sends a UUID to the client stating that the player is online.
            case 0 -> ClientPacketHelper.markOnline(list);

            //#1 - Sends a UUID to the client stating that the player is offline.
            case 1 -> ClientPacketHelper.markOffline(list);

            //#2 - Sends a list of UUIDs to the client, where all of them are player UUID's that need to be added to
            // the client's party list.
            case 2 -> ClientPacketHelper.addMembers(list);

            //#3 - Sends a UUID to the client, to tell it that the specific UUID is now the party leader of the party.
            case 3 -> {
                if (list.size() == 0)
                    ClientPacketHelper.setLeader();
                else
                    ClientPacketHelper.changeLeader(list);
            }
            //#4 - Sends a UUID to the client, to tell it that the specified UUID is no longer in the party.
            case 4 -> {
                if (list.size() == 0)
                    ClientPacketHelper.dropParty();
                else
                    ClientPacketHelper.removePartyMemberDropped(list.get(0));
            }
            //#5 Sends a packet to the client, indicating that the player has been kicked from the party.
            case 5 -> {
                if (list.size() == 0)
                    ClientPacketHelper.dropPartyKicked();
                else
                    ClientPacketHelper.removePartyMemberKicked(list.get(0));
            }
            //#6 Sends a packet to the client, indicating that the party has been disbanded.
            case 6 -> ClientPacketHelper.disbandParty();

            //#7 Reload config
            case 7 -> {
                DimConfig.reload();
                ClientSetup.saveDefaultPresets();
                Config.reloadClientConfigs();
                Config.loadDefaultPreset();
            }
            case 8 -> {
                ClientPacketData.closeScreens(false);
            }

            case 9 -> {
                Minecraft mc = Minecraft.getInstance();
                HashMap<String, RenderItem.Update> updater = new HashMap<>();
                RenderItem.initUpdater(updater);
                closeScreens(true);
                if (Config.pastePreset(mc, updater)) {
                    msg(new TranslatableComponent("messages.sedparties.preset.loadsuccessclipboard").withStyle(ChatFormatting.GREEN));
                } else {
                    msg(new TranslatableComponent("messages.sedparties.preset.loadfailclipboard").withStyle(ChatFormatting.RED));
                }
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void closeScreens(boolean closeAll) {
        if (closeAll) {
            Minecraft.getInstance().setScreen(null);
        } else {
            if (Minecraft.getInstance().screen instanceof SettingsScreen) {
                Minecraft.getInstance().setScreen(null);
            }
        }

    }


}
