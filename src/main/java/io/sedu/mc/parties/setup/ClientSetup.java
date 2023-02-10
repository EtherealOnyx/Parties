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

import static io.sedu.mc.parties.Parties.MODID;
import static io.sedu.mc.parties.client.overlay.RenderItem.items;
import static net.minecraftforge.client.gui.ForgeIngameGui.HOTBAR_ELEMENT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static final KeyMapping showMouse = new KeyMapping(MODID + ".key.hover", GLFW_KEY_LEFT_ALT, KeyMapping.CATEGORY_INTERFACE);

    private static final IIngameOverlay control = (gui, poseStack, partialTicks, width, height) -> RenderItem.resetPos();

    public static void init(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::onClientLeave);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::onClientJoin);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::ticker);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::keyPress);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::guiOpen);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::guiRender);

        //Icon above all
        items.put("head", new PHead("p_head", 8, 8));
        items.put("name", new PName("p_name", 46, 9, 0xDDF3FF));
        items.put("leader", new PLeaderIcon("p_leader", 34, 33));
        items.put("dim", new PDimIcon("p_dim", 5, 34)); //Includes text!

        //Effects
        items.put("effects", new PEffects("p_effects", 46, 42, 30, 42));
        items.put("effects_b", new PEffectsB("p_effects_b", 46, 42, 30, 42));
        items.put("effects_d", new PEffectsD("p_effects_d", 168, 21, 30, 42));

        items.put("armor_i", new PArmor("p_armor_i", 46, 19));
        items.put("armor_t", new PArmorText("p_armor_t", 57, 20, 0xDDF3FF));
        items.put("chicken_i", new PChicken("p_chicken_i", 140, 19));
        items.put("chicken_t", new PChickenText("p_chicken_t", 152, 20, 0xDDF3FF));
        items.put("lvlbar", new PLevelBar("p_lvlbar", 4, 44, 40, 5));
        items.put("lvl_t", new PLevelText("p_lvl_t", 25, 43, 0x80FF8B));
        items.put("health", new PHealth("p_health", 46, 29, 118, 10));
        items.put("health_t", new PHealthText("p_health_t", 105, 30, 0xFFE3E3, 0xFFF399, 0x530404));

        items.put("offline", new POffline("p_offline", 154, 8));
        items.put("offline_t", new POfflineText("p_offline_t", 85, 20, 0xDDF3FF));

        items.put("dead", new PDead("p_dead", 155, 9));



        //items.put("p_bg1", new PRectO("bg1", 7, 41, 34, 11));



        items.put("bg1", new PRectD("p_bg1", 44, 7, 122, 34));
        items.put("bgc", new PRectC("p_bgc", 7, 7, 159, 34));
        //items.add(new ConfigOverlay("config"));

        EffectHolder.setValues(2, 5,true); //TODO: Make sure buff + debuff = max - 1;

        items.values().forEach(RenderItem::register);

        //Disable Overlays
        OverlayRegistry.enableOverlay(ForgeIngameGui.POTION_ICONS_ELEMENT, false);

        //Register keybinding

        ClientRegistry.registerKeyBinding(showMouse);

        //Controller
        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "ctrl", control);

        //DimConfig.init();
    }

    public static void postInit(FMLLoadCompleteEvent event) {
        DimConfig.init();
    }
}