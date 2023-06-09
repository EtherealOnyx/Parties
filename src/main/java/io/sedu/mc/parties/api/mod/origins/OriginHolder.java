package io.sedu.mc.parties.api.mod.origins;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.helper.ColorAPI;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class OriginHolder {

    public static HashMap<String, OriginHolder> oTable = new HashMap<>();

    String name;
    String desc;
    ItemStack item;
    int color;

    public OriginHolder(String resourceName, String name, String desc, ItemStack item) {
        this.name = name;
        this.desc = desc;
        this.item = item;
        this.color = ColorAPI.getDomColor(item);
        Parties.LOGGER.debug("Dom color for " + name + " is " + color);
        oTable.put(resourceName, this);
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

    //TODO: get main origin from player on the server.
    //Then, store it on client. Figure out what code is used when Orb of Origin is used to change origins, trigger event.
    //
}
