package io.sedu.mc.parties.setup;

import io.sedu.mc.parties.client.config.DimConfig;
import io.sedu.mc.parties.client.overlay.*;
import io.sedu.mc.parties.events.ClientEvent;
import io.sedu.mc.parties.util.ColorUtils;
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

    private static final IIngameOverlay control = (gui, poseStack, partialTicks, width, height) -> {
        RenderItem.resetPos();
        ColorUtils.tick();
    };

    public static void init(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::onClientLeave);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::onClientJoin);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::ticker);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::keyPress);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::guiOpen);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::guiRender);
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::mouseReleased);

        //Icon above all
        items.put("head", new PHead("p_head"));
        items.put("name", new PName("p_name"));
        items.put("leader", new PLeader("p_leader"));
        items.put("dim", new PDimIcon("p_dim")); //Includes text!

        //Effects
        items.put("effects", new PEffectsBoth("p_effects"));
        items.put("effects_b", new PEffectsBene("p_effects_b"));
        items.put("effects_d", new PEffectsBad("p_effects_d"));

        //TODO: Add blinker overlay. Set static size (or try to not create static) and update x and y when elements move.
        items.put("armor", new PArmor("p_armor"));
        items.put("chicken", new PChicken("p_chicken"));
        items.put("lvlbar", new PLevelBar("p_lvlbar"));
        items.put("health", new PHealth("p_health"));
        items.put("offline", new POffline("p_offline"));

        items.put("dead", new PDead("p_dead"));



        //items.put("p_bg1", new PRectO("bg1", 7, 41, 34, 11));



        items.put("bg1", new PRectD("p_bg1"));
        items.put("bgc", new ClickArea("p_bgc"));
        items.values().forEach(RenderItem::register);
        RenderItem.setDefaultValues();

        //Disable Overlays
        OverlayRegistry.enableOverlay(ForgeIngameGui.POTION_ICONS_ELEMENT, false);

        //Register keybinding

        ClientRegistry.registerKeyBinding(showMouse);

        //Controller
        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "ctrl", control);

        //DimConfig.init();
        //TODO: Save preset in folder. read file names in preset folder as valid presets. When loading preset, try to load. Revert to current if failed.
    }

    public static void postInit(FMLLoadCompleteEvent event) {
        DimConfig.init();
    }
}