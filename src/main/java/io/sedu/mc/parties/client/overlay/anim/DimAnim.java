package io.sedu.mc.parties.client.overlay.anim;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.PDimIcon;
import io.sedu.mc.parties.client.overlay.gui.BoundsEntry;
import io.sedu.mc.parties.setup.ClientSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DimAnim extends AnimHandler {


    //Specific data
    public int oldDimension = 0;
    public int oldColor = 0xDDF3FF;
    public int dimension = 0;
    public int color = 0xDDF3FF;
    public List<String> dimName = new ArrayList<>();


    public DimAnim(int length, boolean enabled) {
        super(length, enabled);
        dimName.add("?");
        dimName.add("?");
    }

    @Override
    void activateValues(Object... data) {
        setOld();
        setupDim((String) data[0]);
        if ((Boolean) data[1]) {
            setOld();
        }
    }

    void setOld() {
        oldDimension = dimension;
        oldColor = color;
    }

    @Override
    boolean tickAnim() {
        if (super.tickAnim()) {
            return true;
        }
        return false;
    }

    private void setupDim(String data) {
        dimension = getWorld(data);
        data = data.substring(data.indexOf(':')+1).toLowerCase();
        String[] split = data.split("[-_]");
        List<String> dim = Arrays.asList(split);
        List<String> fString = new ArrayList<>();
        if (!dim.contains("the")) {
            fString.add("§oThe");
        }
        dim.forEach(word -> fString.add("§o" + word.substring(0, 1).toUpperCase() + word.substring(1)));
        this.dimName = fString;
    }

    private int getWorld(String world) {
        if (world.equals("minecraft:overworld")) {
            color = 0x7CDF9D;
            return 1;
        }

        if (world.equals("minecraft:the_nether")) {
            color = 0xFFDA7A;
            return 2;
        }

        if (world.equals("minecraft:the_end")) {
            color = 0xCF7CDF;
            return 3;
        }
        color = 0xDDF3FF;
        return 0;
    }

    public static void updateBounds(int index) {
        if (index == -1)
            return;
        DimAnim d = ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(index)).dim;
        PDimIcon i = (PDimIcon) ClientSetup.items.get(7);
        BoundsEntry b = new BoundsEntry(i.x(index), i.x(index)+8, i.y(index), i.y(index)+8);
        b.expand(5);
        b.setTooltip((TextComponent) new TextComponent(String.join(" ", d.dimName).replace("§o", "")).withStyle(ChatFormatting.RESET));
        b.setColor(d.color);
        BoundsEntry.add(0, index, b);
    }
}
