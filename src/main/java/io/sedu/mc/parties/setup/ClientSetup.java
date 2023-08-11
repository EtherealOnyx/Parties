package io.sedu.mc.parties.setup;

import io.sedu.mc.parties.api.helper.ColorAPI;
import io.sedu.mc.parties.api.mod.appleskin.ASCompatManager;
import io.sedu.mc.parties.api.mod.origins.OCompatManager;
import io.sedu.mc.parties.api.mod.origins.OEventHandler;
import io.sedu.mc.parties.client.config.Config;
import io.sedu.mc.parties.client.config.DimConfig;
import io.sedu.mc.parties.client.overlay.*;
import io.sedu.mc.parties.events.ClientEvent;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import static io.sedu.mc.parties.Parties.MODID;
import static io.sedu.mc.parties.client.overlay.RenderItem.items;
import static net.minecraftforge.client.gui.ForgeIngameGui.HOTBAR_ELEMENT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static final KeyMapping showMouse = new KeyMapping("key." + MODID + ".hover", GLFW_KEY_LEFT_ALT, KeyMapping.CATEGORY_INTERFACE);

    private static final IIngameOverlay control = (gui, poseStack, partialTicks, width, height) -> {
        RenderItem.resetPos();
        ColorAPI.tick();
    };

    public static void init(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(ClientEvent::onClientLeave);
        MinecraftForge.EVENT_BUS.register(ClientEvent.class);




        //Vanilla
        items.put("head", new PHead("p_head"));
        items.put("name", new PName("p_name"));
        items.put("leader", new PLeader("p_leader"));
        items.put("dim", new PDimIcon("p_dim")); //Includes text!
        items.put("armor", new PArmor("p_armor"));
        items.put("offline", new POffline("p_offline"));
        items.put("dead", new PDead("p_dead"));

        //Effects
        items.put("effects", new PEffectsBoth("p_effects"));
        items.put("effects_b", new PEffectsBene("p_effects_b"));
        items.put("effects_d", new PEffectsBad("p_effects_d"));

        //Bars
        items.put("lvlbar", new PLevelBar("p_lvlbar"));
        items.put("health", new PHealth("p_health"));
        items.put("hunger", new PHunger("p_hunger"));
        items.put("thirst", new PThirst("p_thirst"));
        items.put("stam", new PStamina("p_stam"));
        items.put("mana", new PMana("p_mana"));
        items.put("mana_ss", new PManaSS("p_mana_ss"));
        items.put("mana_i", new PManaI("p_mana_i"));
        items.put("castbar", new PCastBar("p_castbar"));

        //Modded Items
        items.put("temp", new PTemp("p_temp"));
        items.put("origin", new POrigin("p_origin"));

        //Backgrounds
        items.put("bg1", new PRectD("p_bg1"));
        items.put("bgc", new ClickArea("p_bgc"));


        //items.put("p_bg1", new PRectO("bg1", 7, 41, 34, 11));




        RenderItem.register();

        //Register keybinding

        ClientRegistry.registerKeyBinding(showMouse);

        //Controller
        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "ctrl", control);

        //DimConfig.init();
        Config.init();
        Config.loadDefaultPreset();
    }

    public static void postInit(FMLLoadCompleteEvent event) {
        DimConfig.init();
        event.enqueueWork(() -> {

            if (ModList.get().isLoaded("incapacitated") || ModList.get().isLoaded("playerrevive") || ModList.get().isLoaded("hardcorerevival")) {
                ((PHead)RenderItem.items.get("head")).updateRendererForMods();
                ((PDead)RenderItem.items.get("dead")).updateRendererForMods();
                BarBase.updateRendererForMods();
            }
            if (ModList.get().isLoaded("homeostatic")) {
                ((PTemp)RenderItem.items.get("temp")).updateRendererForHomeostatic();
            }
            else if (ModList.get().isLoaded("toughasnails")) {
                ((PTemp)RenderItem.items.get("temp")).updateRendererForTAN();
            }
        });
        Config.reloadClientConfigs();

        if (ModList.get().isLoaded("origins")) {
            MinecraftForge.EVENT_BUS.addListener(OEventHandler::onClientJoin);
            OCompatManager.initClientEvent();
        }
        ASCompatManager.init();
    }
}