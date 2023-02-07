package io.sedu.mc.parties.client.overlay.anim;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DimAnim extends AnimHandler {


    //Specific data
    public String oldDimension = "";
    public String dimension = "";
    public List<String> dimName = new ArrayList<>();
    public String dimNorm = "";


    public DimAnim(int length, boolean enabled) {
        super(length, enabled);
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

    void setOld() {
        oldDimension = dimension;
    }

    private void setupDim(String data) {
        dimension = data;
        data = data.substring(data.indexOf(':')+1).toLowerCase();
        String[] split = data.split("[-_]");
        List<String> dim = Arrays.asList(split);
        List<String> fString = new ArrayList<>();
        if (!dim.contains("the")) {
            fString.add("§oThe");
        }
        dim.forEach(word -> fString.add("§o" + word.substring(0, 1).toUpperCase() + word.substring(1)));
        this.dimName = fString;
        this.dimNorm = String.join(" ", dimName).replace("§o", "");
    }
}
