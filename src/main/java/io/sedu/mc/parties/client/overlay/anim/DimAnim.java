package io.sedu.mc.parties.client.overlay.anim;

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
}
