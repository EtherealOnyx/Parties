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

    public RenderPacketData(int type, UUID player, Object... data) {
        
        this.type = type;
        this.player = player;
        if (data.length == 1)
            this.data = data[0]; //Remove Unnecessary array.
        else
            this.data = data;
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
            case 0, 9, 34 -> { //Name
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
            case 1, 2, 3, 14, 16, 20, 21, 22, 23, 25, 27, 28, 29, 32, 33 -> data = buf.readFloat();
            case 4, 5, 6, 13, 19, 24, 26, 30, 31, 35, 36 -> data = buf.readInt();
            case 12 -> data = new Object[]{buf.readInt(), buf.readInt(), buf.readInt()};
            case 15, 17 -> data = new Object[]{buf.readBoolean(), buf.readInt()};
            case 18 -> data = buf.readBoolean();
            case 37 -> data = new Object[]{buf.readInt(), buf.readInt()};
            case 39 -> data = new Object[]{buf.readUtf(), buf.readUtf()};
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
            case 0, 9, 34 -> { //Name, Dimension, Origin
                for (int letter : ((String) data).toCharArray()) {
                    buf.writeChar(letter);
                }
            }
            case 1, 2, 3, 14, 16, 20, 21, 22, 23, 25, 27, 28, 29, 32, 33 -> //Health, Max Health, Absorb
                    buf.writeFloat((Float) data);

            case 4, 5, 6, 13, 19, 24, 26, 30, 31, 35, 36 -> //Armor, Hunger, XP Level
                    buf.writeInt((Integer) data);
            case 12 -> {
                buf.writeInt((Integer) ((Object[]) data)[0]); //Type
                buf.writeInt((Integer) ((Object[]) data)[1]); //Duration
                buf.writeInt((Integer) ((Object[]) data)[2]); //Amp
            }

            case 15, 17 -> {
                buf.writeBoolean((Boolean) ((Object[]) data)[0]); //Bleeding/Downed
                buf.writeInt((Integer) ((Object[]) data)[1]); //Duration
            }
            case 18 -> buf.writeBoolean((Boolean) data);

            case 37 -> {
                buf.writeInt((Integer) ((Object[]) data)[0]); //spellId
                buf.writeInt((Integer) ((Object[]) data)[1]); //castTime
            }

            case 39 -> {
                buf.writeUtf((String) ((Object[]) data)[0]); //Preset
                buf.writeUtf((String) ((Object[]) data)[1]); //Name
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
                case 15 -> RenderPacketHelper.setBleeding(player, (Boolean) ((Object[]) data)[0], (Integer) ((Object[]) data)[1]);
                case 16 -> RenderPacketHelper.setReviveProgress(player, (Float) data);
                case 17 -> RenderPacketHelper.setDowned(player, (Boolean) ((Object[]) data)[0], (Integer) ((Object[]) data)[1]);
                case 18 -> RenderPacketHelper.setSpectating(player, (Boolean) data);
                case 19 -> RenderPacketHelper.setThirst(player, (Integer) data);
                case 20 -> RenderPacketHelper.setWorldTemp(player, (Float) data);
                case 21 -> RenderPacketHelper.setBodyTemp(player, (Float) data);
                case 22 -> RenderPacketHelper.setWorldTempTAN(player, (Float) data);
                case 23 -> RenderPacketHelper.setMana(player, (Float) data);
                case 24 -> RenderPacketHelper.setMaxMana(player, (Integer) data);
                case 25 -> RenderPacketHelper.setCurrentStamina(player, (Float) data);
                case 26 -> RenderPacketHelper.setMaxStamina(player, (Integer) data);
                case 27 -> RenderPacketHelper.setManaSS(player, (Float) data);
                case 28 -> RenderPacketHelper.setMaxManaSS(player, (Float) data);
                case 29 -> RenderPacketHelper.setExtraMana(player, (Float) data);
                case 30 -> RenderPacketHelper.setExtraStam(player, (Integer) data);
                case 31 -> RenderPacketHelper.setQuench(player, (Integer) data);
                case 32 -> RenderPacketHelper.setMaxHunger(player, (Float) data);
                case 33 -> RenderPacketHelper.setSaturation(player, (Float) data);
                case 34 -> RenderPacketHelper.setOrigin(player, (String) data);
                case 35 -> RenderPacketHelper.setManaI(player, (Integer) data);
                case 36 -> RenderPacketHelper.setMaxManaI(player, (Integer) data);
                case 37 -> RenderPacketHelper.startCast(player, (Integer) ((Object[]) data)[0], (Integer) ((Object[]) data)[1]);
                case 38 -> RenderPacketHelper.endCast(player);
                case 39 -> RenderPacketHelper.sendMessage(player, (String) ((Object[]) data)[0], (String) ((Object[]) data)[1]);
                default -> {

                }
            }
        });
        return true;
    }
}
