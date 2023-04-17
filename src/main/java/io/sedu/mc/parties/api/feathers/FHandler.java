package io.sedu.mc.parties.api.feathers;

import com.elenai.feathers.api.FeathersHelper;
import com.elenai.feathers.client.ClientFeathersData;
import com.elenai.feathers.config.FeathersCommonConfig;
import de.cas_ual_ty.spells.capability.IManaHolder;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class FHandler implements IFHandler {

    private static final Capability<IManaHolder> FEATHERS = CapabilityManager.get(new CapabilityToken<>(){});

    public static boolean exists() {
       return FEATHERS != null;
    }

    static {
        Parties.LOGGER.info("Initializing Compatibility with Feathers Mod.");
        if (FEATHERS == null) {
            Parties.LOGGER.error("Failed to load Feathers compatibility...");
        }
    }


    @Override
    public void getClientFeathers(TriConsumer<Integer, Integer, Integer> action) {
        if (FeathersCommonConfig.ENABLE_ARMOR_WEIGHTS.get()) {
            int weight = ClientFeathersData.getWeight();
            action.accept(Math.max(0, ClientFeathersData.getFeathers() - weight), Math.max(0, 20 - weight), ClientFeathersData.getEnduranceFeathers());
        } else {
            action.accept(ClientFeathersData.getFeathers(), 20, ClientFeathersData.getEnduranceFeathers());
        }

    }

    @Override
    public void getServerFeathers(ServerPlayer player, TriConsumer<Integer, Integer, Integer> action) {
        int weight = ClientFeathersData.getWeight();
        action.accept(Math.max(0, FeathersHelper.getFeathers(player) - weight), Math.max(0, 20 - weight), FeathersHelper.getEndurance(player));

    }

    @Override
    public void setFeathersRender(Boolean aBoolean) {
        FCompatManager.enableOverlay = aBoolean;
    }
}
