package io.sedu.mc.parties.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

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

    /*public RenderPacketData(int i, UUID propOf, float health, float maxHealth, float absorptionAmount, int armorValue, int foodLevel, int experienceLevel) {

        this.type = i;
        this.player = propOf;
        data = new Object[]{health, maxHealth, absorptionAmount, armorValue, foodLevel, experienceLevel};
    }*/

    public RenderPacketData(int type, UUID player, Object... data) {
        
        this.type = type;
        this.player = player;
        if (data.length == 1)
            this.data = data[0]; //Remove Unnecessary array.
        else
            this.data = data;
    }

    /*public RenderPacketData(int i, UUID propOf) {
        this.type = i;
        this.player = propOf;
        this.data = null;
    }*/


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
            case 1, 2, 3, 14 -> data = buf.readFloat();
            case 4, 5, 6, 13 -> data = buf.readInt();
            case 12 -> data = new Object[]{buf.readInt(), buf.readInt(), buf.readInt()};
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
            case 1, 2, 3, 14 -> //Health, Max Health, Absorb
            {
                buf.writeFloat((Float) data);
                
            }

            case 4, 5, 6, 13 -> //Armor, Hunger, XP Level
            {
                buf.writeInt((Integer) data);
                
            }
            case 12 -> {
                buf.writeInt((Integer) ((Object[]) data)[0]); //Type
                buf.writeInt((Integer) ((Object[]) data)[1]); //Duration
                buf.writeInt((Integer) ((Object[]) data)[2]); //Amp
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
                case 10 -> RenderPacketHelper.markDeath(); //Self
                case 11 -> RenderPacketHelper.markLife(); //Self
                case 12 -> RenderPacketHelper.addPotionEffect(player, (Integer) ((Object[]) data)[0], (Integer) ((Object[]) data)[1], (Integer) ((Object[]) data)[2]);
                case 13 -> RenderPacketHelper.removePotionEffect(player, (Integer) data);
                case 14 -> RenderPacketHelper.setXpBar(player, (Float) data);
                default -> {

                }
            }
        });
        return true;
    }
}
