package io.sedu.mc.parties.api.mod.origins;

import io.github.apace100.origins.origin.Impact;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.helper.ColorAPI;
import io.sedu.mc.parties.client.overlay.RenderItem;
import io.sedu.mc.parties.util.RenderUtils;
import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class OriginHolder {

    public static HashMap<String, OriginHolder> oTable = new HashMap<>();

    String name;
    String desc;
    ItemStack item;
    int color;
    Imp i;

    public static void getOriginStack(String origin, Consumer<ItemStack> action) {
        oTable.computeIfPresent(origin, (o, data) -> {
            action.accept(data.item);
            return data;
        });
    }

    enum Imp {
        NONE("", ChatFormatting.GRAY),
        LOW("■□□", ChatFormatting.GREEN),
        MEDIUM("■■□", ChatFormatting.YELLOW),
        HIGH("■■■", ChatFormatting.RED);

        private String text;
        private ChatFormatting style;

        Imp(String text, ChatFormatting style) {
            this.text = text;
            this.style = style;
        }

        public Component getComponent() {
            return new TextComponent(text).withStyle(style);
        }
    }


    public OriginHolder(String resourceName, String name, String desc, ItemStack item, Impact impact) {
        this.name = name;
        this.desc = desc;
        this.item = item;
        this.color = ColorAPI.getDomColor(item);
        Parties.LOGGER.debug("Dom color for " + name + " is " + color);
        oTable.put(resourceName, this);
        switch (impact.getImpactValue()) {
            case 1 -> i = Imp.LOW;
            case 2 -> i = Imp.MEDIUM;
            case 3 -> i = Imp.HIGH;
            default -> i = Imp.NONE;
        }
    }

    public static void printOriginInfo() {
        oTable.forEach(((s, originHolder) -> {
            Parties.LOGGER.info("Resource Name: " + s);
            Parties.LOGGER.info("Name: " + originHolder.name);
            Parties.LOGGER.info("Desc: " + originHolder.desc);
            Parties.LOGGER.info("Item used: " + originHolder.item.getDisplayName().getString());
            Parties.LOGGER.info("Dominant Color: 0x" + Integer.toHexString(originHolder.color));
            Parties.LOGGER.info("--------------------------------------------------");
        }));
    }

    public static void getOriginInfo(String origin, TriConsumer<String, ItemStack, Integer> action) {
        oTable.computeIfPresent(origin, (o, data) -> {
            action.accept(data.name, data.item, data.color);
            return data;
        });
    }

    public static void getOriginTooltip(String origin, TriConsumer<RenderItem.ColorComponent, List<Component>, Component> action) {
        oTable.computeIfPresent(origin, (o, data) -> {
            ArrayList<Component> list = new ArrayList<>(RenderUtils.splitTooltip(data.desc, 40, false, true));
            action.accept(new RenderItem.ColorComponent(new TextComponent(data.name), data.color), list, data.i.getComponent());
            return data;
        });
    }


    //TODO: get main origin from player on the server.
    //Then, store it on client. Figure out what code is used when Orb of Origin is used to change origins, trigger event.
    //
}
