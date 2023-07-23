package io.sedu.mc.parties.api.homeostatic;


import homeostatic.common.capabilities.Temperature;
import homeostatic.common.capabilities.Water;
import homeostatic.common.temperature.BodyTemperature;
import homeostatic.common.temperature.Environment;
import homeostatic.config.ConfigHandler;
import homeostatic.util.TempHelper;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.function.BiConsumer;

public class HHandler implements IHHandler {

    private static final Capability<Water> WATER = CapabilityManager.get(new CapabilityToken<>() {});
    private static final Capability<Temperature> TEMP = CapabilityManager.get(new CapabilityToken<>() {});

    public boolean exists() {
        return WATER != null && TEMP != null;
    }



    static {
        Parties.LOGGER.info("Initializing Compatibility with Homeostatic.");
    }

    @Override
    public int getWaterLevel(Player player) {
        Water water = player.getCapability(WATER).orElse(null);
        //noinspection ConstantConditions
        if (water != null) {
            return water.getWaterLevel();
        }
        return 20;
    }



    @Override
    public void getClientTemperature(Player clientPlayer, TriConsumer<Integer, Integer, Integer> action) {
        Temperature temp = clientPlayer.getCapability(TEMP).orElse(null);
        //noinspection ConstantConditions
        if (temp != null) {
            action.accept(convertTemp(temp.getLocalTemperature()),
                          convertTemp(temp.getSkinTemperature()),
                          getSeverity(temp.getSkinTemperature(), temp.getLocalTemperature())
                          );
        }
    }

    @Override
    public int getBodyTempSev(float data, int severity) {
        int tempTex = (severity >> 16) & 0xFFFF;
        int sev = 0;
        if (data < BodyTemperature.LOW)
            sev = 1;
        if (data > BodyTemperature.HIGH)
            sev = 1;
        return sev | (tempTex << 16);
    }

    @Override
    public int getWorldTempSev(float data, int severity) {
        int severe = severity & 0xF;
        int temp;
        if (data < Environment.EXTREME_COLD) temp = 0;
        else if (data < Environment.PARITY_LOW) temp = 1;
        else if (data < Environment.PARITY_HIGH) temp = 2;
        else if (data < Environment.HOT) temp = 3;
        else temp = 4;
        return severe | (temp << 16);
    }

    @Override
    public int convertTemp(float data) {
        return (int) Math.round(TempHelper.convertMcTemp(data, ConfigHandler.Client.useFahrenheit()));
    }

    @Override
    public void getTemperature(Player player, BiConsumer<Float, Float> action) {
        Temperature temp = player.getCapability(TEMP).orElse(null);
        //noinspection ConstantConditions
        if (temp != null) {
            action.accept(temp.getLocalTemperature(), temp.getSkinTemperature());
        }
    }

    private int getSeverity(float coreTemp, float localTemp) {
        int sev = 0;
        if (coreTemp < BodyTemperature.LOW)
            sev = 1;
        if (coreTemp > BodyTemperature.HIGH)
            sev = 1;
        int temp;
        if (localTemp < Environment.EXTREME_COLD) temp = 0;
        else if (localTemp < Environment.PARITY_LOW) temp = 1;
        else if (localTemp < Environment.PARITY_HIGH) temp = 2;
        else if (localTemp < Environment.HOT) temp = 3;
        else temp = 4;
        return sev | (temp << 16);
    }


}
