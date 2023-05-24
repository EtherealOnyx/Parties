package io.sedu.mc.parties.api.mod.dietarystats;

import com.starrysky.rikka.dietarystatistics.DietaryStatistics;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class DSHandler implements IDSHandler {

    @Override
    public void getMaxHunger(Player p, Consumer<Float> action) {
        action.accept((float) p.getAttributeValue(DietaryStatistics.FOOD_ATTRIBUTE.get()));
    }
}
