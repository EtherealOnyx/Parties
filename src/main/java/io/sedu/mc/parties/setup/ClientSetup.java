package io.sedu.mc.parties.setup;

//import io.sedu.mc.parties.client.PartyOverlay;
import io.sedu.mc.parties.client.overlay.*;
import io.sedu.mc.parties.events.PartyEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.List;

import static io.sedu.mc.parties.Parties.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    private static final List<RenderItem> items = new ArrayList<>();
    public static void init(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(PartyEvent::onClientLeave);
        MinecraftForge.EVENT_BUS.addListener(PartyEvent::onClientJoin);

        //Text
        items.add(new PName("p_name", 46, 9, 0xDDF3FF));
        items.add(new PArmorText("p_armor_t", 57, 20, 0xDDF3FF));
        items.add(new PChickenText("p_chicken_t", 144, 20, 0xDDF3FF));
        items.add(new PLevelText("p_lvl_t", 26, 42, 0x80FF8B));
        items.add(new PHealthText("p_health_t", 102, 30, 0xFFE3E3, 0xFFF399, 0x530404));
        items.add(new PDimIcon("p_dim", 8, 8)); //Includes text!

        //Icons
        items.add(new PLeaderIcon("p_leader", 34, 33));
        items.add(new PArmor("p_armor_i", 46, 19));
        items.add(new PChicken("p_chicken_i", 132, 19));
        items.add(new PLevelOrb("p_lvl", 16, 41));

        //Containers
        items.add(new PHead("p_head", 8, 8));
        items.add(new PHealth("p_health", 46, 29, 112, 10));

        //Backgrounds
        items.add(new PRectO("p_bg1", 7, 41, 34, 11));
        items.add(new PRectD("p_bg2", 44, 7, 116, 34));



        items.forEach(RenderItem::register);
        /*final PartyOverlay p = new PartyOverlay();
        p.register();*/
    }
}