package io.sedu.mc.parties.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class RenderPacketData {
    private UUID player;
    private final int type;
    private Object data;

    RenderPacketData(FriendlyByteBuf buf) {
        this.type = buf.readInt();
        if (type == 10 || type == 11)
            return;
        player = new UUID(buf.readLong(), buf.readLong());
        readData(buf);
    }

    public RenderPacketData(int i, UUID propOf, float health, float maxHealth, float absorptionAmount, int armorValue, int foodLevel, int experienceLevel) {
        System.out.println("Sending packet with TYPE : MASTER");
        this.type = i;
        this.player = propOf;
        data = new Object[]{health, maxHealth, absorptionAmount, armorValue, foodLevel, experienceLevel};
    }

    public RenderPacketData(int type, UUID player, Object data) {
        System.out.println("Sending packet with TYPE : " + type);
        this.type = type;
        this.player = player;
        this.data = data;
    }

    public RenderPacketData(int i, UUID propOf) {
        this.type = i;
        this.player = propOf;
        this.data = null;
    }


    public RenderPacketData(UUID propOf, ResourceLocation world) {
        this.type = 9;
        this.player = propOf;
        this.data = world.toString();
    }

    public RenderPacketData(int i) {
        this.type = i;
    }

    private void readData(FriendlyByteBuf buf) {
        switch (type) {
            case -1 ->
                    data = new Object[]{buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt(),
                            buf.readInt(), buf.readInt()};
            case 0, 9 -> { //Name
                StringBuilder builder = new StringBuilder();
                while (true) {
                    try {
                        builder.append(buf.readChar());
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                }
                data = builder.toString();
            }
            case 1, 2, 3 -> {data = buf.readFloat();
                System.out.println("Data: " + data);
            }
            case 4, 5, 6 -> {
                data = buf.readInt();
                System.out.println("Data: " + data);
            }
        }
    }

    private void writeData(FriendlyByteBuf buf) {
        switch (type) {
            case -1 -> {
                buf.writeFloat((Float) ((Object[]) data)[0]);
                buf.writeFloat((Float) ((Object[]) data)[1]);
                buf.writeFloat((Float) ((Object[]) data)[2]);
                buf.writeInt((Integer) ((Object[]) data)[3]);
                buf.writeInt((Integer) ((Object[]) data)[4]);
                buf.writeInt((Integer) ((Object[]) data)[5]);
            }
            case 0, 9 -> { //Name
                for (int letter : ((String) data).toCharArray()) {
                    buf.writeChar(letter);
                }
            }
            case 1, 2, 3 -> //Health, Max Health, Absorb
            {
                buf.writeFloat((Float) data);
                System.out.println("Wrote value of: " + data);
            }

            case 4, 5, 6 -> //Armor, Hunger, XP Level
            {
                buf.writeInt((Integer) data);
                System.out.println("Wrote value of: " + data);
            }

        }
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        if (type == 10 || type == 11) {
            return;
        }
        buf.writeLong(player.getMostSignificantBits());
        buf.writeLong(player.getLeastSignificantBits());
        writeData(buf);
    }

    boolean handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            switch (type) {
                case -1 -> {
                    RenderPacketHelper.setHealth(player, (Float) ((Object[]) data)[0]);
                    RenderPacketHelper.setMaxHealth(player, (Float) ((Object[]) data)[1]);
                    RenderPacketHelper.setAbsorb(player, (Float) ((Object[]) data)[2]);
                    RenderPacketHelper.setArmor(player, (Integer) ((Object[]) data)[3]);
                    RenderPacketHelper.setFood(player, (Integer) ((Object[]) data)[4]);
                    RenderPacketHelper.setXp(player, (Integer) ((Object[]) data)[5]);
                }
                case 0 -> RenderPacketHelper.setName(player, (String) data);
                case 1 -> RenderPacketHelper.setHealth(player, (Float) data);
                case 3 -> RenderPacketHelper.setAbsorb(player, (Float) data);
                case 4 -> RenderPacketHelper.setArmor(player, (Integer) data);
                case 5 -> RenderPacketHelper.setFood(player, (Integer) data);
                case 6 -> RenderPacketHelper.setXp(player, (Integer) data);
                case 7 -> RenderPacketHelper.markDeath(player);
                case 8 -> RenderPacketHelper.markLife(player);
                case 9 -> RenderPacketHelper.setDim(player, (String) data);
                case 10 -> RenderPacketHelper.markDeath();
                case 11 -> RenderPacketHelper.markLife();
                default -> {

                }
            }
        });
        return true;
    }
}
