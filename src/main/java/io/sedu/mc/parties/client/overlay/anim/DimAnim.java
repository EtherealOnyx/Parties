package io.sedu.mc.parties.client.overlay.anim;


import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.config.DimConfig;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DimAnim extends AnimHandlerBase {


    //Specific data
    public String oldDimension = "";
    public String dimension = "";
    public List<String> dimName = new ArrayList<>();
    public String dimNorm = "";
    public static boolean animActive = true;
    protected ClientPlayerData player;
    private boolean oldFlag = false;


    public DimAnim(int length, ClientPlayerData player) {
        super(length);
        this.player = player;
        dimName.add("?");
        dimName.add("?");
    }

    @Override
    void activateValues(Object... data) {
        setOld();
        setupDim((String) data[0]);
        if ((Boolean) data[1] || oldDimension.equals("")) {
            setOld();
        }
    }

    @Override
    public void activate(Object... data) {
        if (animActive) {
            super.activate(data);
            oldFlag = player.shouldRenderModel;
            player.shouldRenderModel = false;
        }
        else setupDim((String) data[0]);
    }

    void setOld() {
        oldDimension = dimension;
    }

    private void setupDim(String data) {
        DimConfig.checkDim(data);

        //Custom RFTools Dimensions support (since it has unlimited entries)
        if (data.substring(0, data.indexOf(':')).equals("rftoolsdim")) {
            dimension = "rftoolsdim:dim";
            data = data.substring(data.indexOf(':')+1);
            dimName = new ArrayList<>();
            dimName.add("§oRFTools");
            dimName.add("§oDimension:");
            dimName.add("§o'" + data + "'");
            dimNorm = "RFTools Dim: " + data;
            return;
        }
        dimension = data;
        data = data.substring(data.indexOf(':')+1).toLowerCase();
        String[] split = data.split("[-_ ]");
        List<String> dim = Arrays.asList(split);
        List<String> fString = new ArrayList<>();
        if (!dim.contains("the")) {
            fString.add("§oThe");
        }
        dim.forEach(word -> fString.add("§o" + word.substring(0, 1).toUpperCase() + word.substring(1)));
        this.dimName = fString;
        this.dimNorm = String.join(" ", dimName).replace("§o", "");
        Parties.LOGGER.debug("DimAnim: {} has been converted to {}.", dimName, dimNorm);
    }

    @Override
    boolean tickAnim() {
        animTime -= 1;
        if (animTime <= 0) {
            animTime = 0;
            active = false;
            player.shouldRenderModel = oldFlag;
            return true;
        }
        return false;
    }
}
