package io.sedu.mc.parties.api.coldsweat;

import dev.momostudios.coldsweat.api.util.Temperature;
import dev.momostudios.coldsweat.common.capability.ITemperatureCap;
import dev.momostudios.coldsweat.config.ClientSettingsConfig;
import dev.momostudios.coldsweat.util.config.ConfigSettings;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.function.BiConsumer;

public class CSHandler implements ICSHandler {

    private static final Capability<ITemperatureCap> TEMP = CapabilityManager.get(new CapabilityToken<>(){});
    static {
        Parties.LOGGER.info("Initializing Compatibility with Cold Sweat.");
    }

    public boolean exists() {
        return TEMP != null;
    }

    @Override
    public void setTempRender(Boolean renderTemp) {
        //Oh geez
    }

    @Override
    public float getWorldTemp(Player player) {
        ITemperatureCap temp = player.getCapability(TEMP).orElse(null);
        //noinspection ConstantConditions
        if (temp != null ) {
            return (float) temp.getTemp(Temperature.Type.WORLD);
        }
        return 0;
    }

    @Override
    public float getBodyTemp(Player player) {
        ITemperatureCap temp = player.getCapability(TEMP).orElse(null);
        //noinspection ConstantConditions
        if (temp != null ) {
            return (float) temp.getTemp(Temperature.Type.BODY);
        }
        return 0;
    }

    @Override
    public void convertTemp(float temp, BiConsumer<Integer, Integer> action) {
        action.accept((int) (ClientSettingsConfig.getInstance().celsius() ? temp * 23.333333333 : temp * 42 + 32), getRenderType(temp));
    }

    @Override
    public void getClientWorldTemp(Player clientPlayer, TriConsumer<Integer, Integer, Integer> action) {
        convertTemp(getWorldTemp(clientPlayer), (temp, sev) -> {
            action.accept(temp, sev, (int) getBodyTemp(clientPlayer));
        });
    }

    private int getRenderType(float worldTemp) {
        if (worldTemp < ConfigSettings.MIN_TEMP.get()) return 0;
        if (worldTemp > ConfigSettings.MAX_TEMP.get()) return 2;
        return 1;
    }
}
