package io.sedu.mc.parties.api.mod.toughasnails;

import io.sedu.mc.parties.Parties;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.OverlayRegistry;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.temperature.TemperatureOverlayHandler;
import toughasnails.thirst.ThirstOverlayHandler;

import java.util.function.BiConsumer;

public class TANHandler implements ITANHandler {

    static {
        Parties.LOGGER.info("[Parties] Initializing Compatibility with Tough as Nails.");
    }

    private int getTempLevel(TemperatureLevel temp) {
        return switch (temp) {
            case ICY -> 0;
            case COLD -> 1;
            case NEUTRAL -> 2;
            case WARM -> 3;
            default -> 4;
        };
    }

    @Override
    public void getPlayerTemp(Player player, BiConsumer<Integer, String> action) {
        TemperatureLevel temp = TemperatureHelper.getTemperatureForPlayer(player);
        action.accept(getTempLevel(temp), getTempString(temp));
    }

    @Override
    public int getPlayerTemp(Player player) {
        TemperatureLevel temp = TemperatureHelper.getTemperatureForPlayer(player);
        return getTempLevel(temp);
    }

    private String getTempString(TemperatureLevel temp) {
        return switch (temp) {
            case ICY -> "Icy";
            case COLD -> "Cold";
            case NEUTRAL -> "Cool";
            case WARM -> "Warm";
            default -> "Hot";
        };
    }

    @Override
    public int getPlayerThirst(Player player) {
        return ThirstHelper.getThirst(player).getThirst();
    }

    @Override
    public boolean tempExists() {
        return TemperatureHelper.isTemperatureEnabled();
    }

    @Override
    public void setRenderers(Boolean thirstEnabled, Boolean tempEnabled) {
        OverlayRegistry.enableOverlay(ThirstOverlayHandler.THIRST_LEVEL_ELEMENT, thirstEnabled);
        OverlayRegistry.enableOverlay(TemperatureOverlayHandler.TEMPERATURE_LEVEL_ELEMENT, tempEnabled);
    }

}
