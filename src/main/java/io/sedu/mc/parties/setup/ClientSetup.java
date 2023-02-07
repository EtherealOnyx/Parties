package io.sedu.mc.parties.setup;

import io.sedu.mc.parties.client.config.DimConfig;
import io.sedu.mc.parties.client.overlay.*;
import io.sedu.mc.parties.client.overlay.effects.EffectHolder;
import io.sedu.mc.parties.events.ClientEvent;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.ArrayList;
import java.util.List;

import static io.sedu.mc.parties.Parties.MODID;
import static net.minecraftforge.client.gui.ForgeIngameGui.HOTBAR_ELEMENT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static final KeyMapping showMouse = new KeyMapping(MODID + ".key.hover", GLFW_KEY_LEFT_ALT, KeyMapping.CATEGORY_INTERFACE);
    public static final List<RenderItem> items = new ArrayList<>();

    private static final IIngameOverlay control = (gui, poseStack, partialTicks, width, height) -> RenderItem.resetPos();

    public static void init(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::onClientLeave);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::onClientJoin);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::ticker);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::keyPress);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::guiOpen);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::guiRender);

        //Icon above all
        items.add(new PLeaderIcon("p_leader", 34, 33));


        //Text
        items.add(new PName("p_name", 46, 9, 0xDDF3FF));
        items.add(new PArmorText("p_armor_t", 57, 20, 0xDDF3FF));
        items.add(new PChickenText("p_chicken_t", 152, 20, 0xDDF3FF));
        items.add(new PLevelText("p_lvl_t", 25, 43, 0x80FF8B));
        items.add(new PHealthText("p_health_t", 105, 30, 0xFFE3E3, 0xFFF399, 0x530404));
        items.add(new POfflineText("p_offline_t", 85, 20, 0xDDF3FF));
        items.add(new PDimIcon("p_dim", 5, 34)); //Includes text!

        //Icons
        items.add(new PArmor("p_armor_i", 46, 19));
        items.add(new PChicken("p_chicken_i", 140, 19));
        items.add(new PLevelBar("p_lvlbar", 4, 44, 40, 5));
        items.add(new POffline("p_offline", 164, 8));
        items.add(new PDead("p_dead", 155, 9));

        //Containers
        items.add(new PHead("p_head", 8, 8));
        items.add(new PHealth("p_health", 46, 29, 118, 10));

        //Backgrounds
        //items.add(new PRectO("p_bg1", 7, 41, 34, 11));
        items.add(new PRectD("p_bg2", 44, 7, 122, 34));

        //Effects
        //items.add(new PEffects("p_effects", 46, 42, 30, 42));
        EffectHolder.setValues(2, 5,true); //TODO: Make sure buff + debuff = max - 1;
        //TODO: Make bottom ones auto disabled.
        items.add(new PEffectsB("p_effects_b", 46, 42, 30, 42));
        items.add(new PEffectsD("p_effects_d", 168, 21, 30, 42));


        items.forEach(RenderItem::register);

        //Disable Overlays
        OverlayRegistry.enableOverlay(ForgeIngameGui.POTION_ICONS_ELEMENT, false);

        //Register keybinding

        ClientRegistry.registerKeyBinding(showMouse);

        //Controller
        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "p_ctrl", control);

        //DimConfig.init();
    }

    public static void postInit(FMLLoadCompleteEvent event) {
        DimConfig.init();
    }
}