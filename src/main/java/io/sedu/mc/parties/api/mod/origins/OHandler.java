package io.sedu.mc.parties.api.mod.origins;

import com.google.common.collect.ImmutableMap;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.sedu.mc.parties.Parties;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class OHandler implements IOHandler {

    public static boolean ready = false;

    static {
        Parties.LOGGER.info("Initializing Compatibility with Origins (Forge).");
    }

    public List<Origin> getOriginList() {
        return OriginsAPI.getOriginsRegistry().stream().toList();
    }


    @Override
    public boolean isPresent() {
        List<Origin> list = getOriginList();
        return (list.size() > 2 || (list.size() == 1 && list.get(0) != Origin.EMPTY));
    }

    @Override
    public String getMainOrigin(@Nonnull Player player) {
        AtomicInteger lowestOrder = new AtomicInteger(Integer.MAX_VALUE);
        AtomicReference<String> originReg = new AtomicReference<>("");
        IOriginContainer.get(player).map(IOriginContainer::getOrigins).orElseGet(ImmutableMap::of).forEach((originLayer, origin) -> {
            if (originLayer.order() < lowestOrder.get() && origin.getRegistryName() != null) {
                lowestOrder.set(originLayer.order());
                originReg.set(origin.getRegistryName().toString());
            }
        });
        return originReg.get();
    }

    @Override
    public String getMainOriginClient(Player player) {
        System.out.println(ready);
        if (!ready) return "";
        else return getMainOrigin(player);
    }
}
