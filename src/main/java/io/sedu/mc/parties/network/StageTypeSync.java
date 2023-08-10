package io.sedu.mc.parties.network;

import io.sedu.mc.parties.api.mod.gamestages.GSEventHandler;
import io.sedu.mc.parties.api.mod.gamestages.SyncType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StageTypeSync {

    private final SyncType type;

    public StageTypeSync(FriendlyByteBuf buf) {
        this.type = buf.readEnum(SyncType.class);
    }

    public StageTypeSync(SyncType type) {
        this.type = type;
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeEnum(type);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer p;
            if ((p = supplier.get().getSender()) != null) {
                GSEventHandler.changePlayerOption(p.getUUID(), type, true);
            }

        });
        return true;
    }


}
