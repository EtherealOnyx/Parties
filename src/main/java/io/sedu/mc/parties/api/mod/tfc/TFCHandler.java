package io.sedu.mc.parties.api.mod.tfc;

import io.sedu.mc.parties.Parties;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.minecraft.world.entity.player.Player;

public class TFCHandler implements ITFCHandler {


    static {
        Parties.LOGGER.info("[Parties] Initializing Compatibility with TerraFirmaCraft.");
    }

    @Override
    public float getThirstTFC(Player player) {
        if (player.getFoodData() instanceof TFCFoodData data)
        {
            return data.getThirst();
        }
        return 0f;
    }
}
