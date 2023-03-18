package io.sedu.mc.parties.setup;

import io.sedu.mc.parties.client.config.Config;
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

import java.util.HashMap;

import static io.sedu.mc.parties.Parties.MODID;
import static io.sedu.mc.parties.client.overlay.RenderItem.items;
import static net.minecraftforge.client.gui.ForgeIngameGui.HOTBAR_ELEMENT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static final KeyMapping showMouse = new KeyMapping("key." + MODID + ".hover", GLFW_KEY_LEFT_ALT, KeyMapping.CATEGORY_INTERFACE);

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

        //Disable Overlays
        OverlayRegistry.enableOverlay(ForgeIngameGui.POTION_ICONS_ELEMENT, false);

        //Register keybinding

        ClientRegistry.registerKeyBinding(showMouse);

        //Controller
        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "ctrl", control);

        //DimConfig.init();
        //TODO: Save preset in folder. read file names in preset folder as valid presets. When loading preset, try to load. Revert to current if failed.
        RenderItem.setDefaultValues();
        Config.init();
        saveDefaultPresets();

    }

    private static void saveDefaultPresets() {
        Config.saveDefaultPreset("standard", "The standard preset of the mod.");
        final HashMap<String, RenderItem.Update> updater = new HashMap<>();
        RenderItem.initUpdater(updater);
        String load = """
                {"general":{"gen_x":16,"gen_y":16,"gen_w":248,"gen_h":52,"gen_pw":0,"gen_ph":51},"head":{"display":true,"htype":1,"xpos":8,"ypos":8,"zpos":0,"scale":2},"name":{"display":true,"tshadow":true,"tcolor":14545919,"tmax":16,"xpos":46,"ypos":9,"zpos":0,"scale":2},"leader":{"display":true,"xpos":34,"ypos":33,"zpos":2,"scale":2},"dim":{"display":true,"tdisplay":true,"danim":true,"xpos":4,"ypos":32,"zpos":1},"effects":{"display":true,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":19,"ypos":170,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":5,"totalmax":5,"bsep":false,"dfirst":true,"dlim":4,"blim":0},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":46,"ypos":41,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":46,"ypos":41,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"armor":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":19,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":143,"ypos":19,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":4,"ypos":44,"width":40,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":29,"width":120,"height":10,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"offline":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":156,"ypos":8,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":false,"xtpos":86,"ytpos":20},"dead":{"display":true,"xpos":157,"ypos":9,"zpos":0,"scale":2},"bg1":{"display":true,"xpos":44,"ypos":7,"width":124,"height":34},"bgc":{"xpos":7,"ypos":7,"width":159,"height":34}}
                """;
        Config.saveDefaultPresetFromString(updater, "sidebuffs", "A preset with the combined buff bar on the right of the frame. Adds more space.", load);

        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":232,"gen_h":64,"gen_pw":0,"gen_ph":63},"head":{"display":true,"htype":1,"xpos":8,"ypos":8,"zpos":0,"scale":2},"name":{"display":true,"tshadow":true,"tcolor":14545919,"tmax":16,"xpos":46,"ypos":9,"zpos":0,"scale":2},"leader":{"display":true,"xpos":34,"ypos":33,"zpos":2,"scale":2},"dim":{"display":true,"tdisplay":true,"danim":true,"xpos":4,"ypos":32,"zpos":1},"effects":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":46,"ypos":41,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8,"bsep":false,"dfirst":true,"dlim":4,"blim":3},"effects_b":{"display":true,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":46,"ypos":41,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":true,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":170,"ypos":19,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":19,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":143,"ypos":19,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":4,"ypos":44,"width":40,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":29,"width":120,"height":10,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"offline":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":156,"ypos":8,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":false,"xtpos":86,"ytpos":20},"dead":{"display":true,"xpos":157,"ypos":9,"zpos":0,"scale":2},"bg1":{"display":true,"xpos":44,"ypos":7,"width":124,"height":34},"bgc":{"xpos":7,"ypos":7,"width":159,"height":34}}
               """;
        Config.saveDefaultPresetFromString(updater, "sepbuffs", "A preset with the buff and debuff bar separated.", load);
        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":34,"gen_h":72,"gen_pw":36,"gen_ph":0},"head":{"display":true,"htype":0,"xpos":1,"ypos":7,"zpos":0,"scale":2},"name":{"display":true,"tshadow":true,"tcolor":14545919,"tmax":1,"xpos":2,"ypos":8,"zpos":0,"scale":2},"leader":{"display":true,"xpos":2,"ypos":1,"zpos":2,"scale":1},"dim":{"display":true,"tdisplay":true,"danim":true,"xpos":11,"ypos":0,"zpos":3},"effects":{"display":true,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":2,"ypos":48,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":2,"totalmax":2,"bsep":false,"dfirst":true,"dlim":0,"blim":1},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":0,"ypos":10,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":10,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":23,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":28,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":1,"ypos":42,"width":32,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":34,"width":64,"height":10,"tdisplay":true,"tshadow":true,"ttype":2,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"offline":{"display":true,"scale":1,"zpos":2,"idisplay":true,"xpos":5,"ypos":41,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":8,"ytpos":27},"dead":{"display":true,"xpos":12,"ypos":16,"zpos":1,"scale":2},"bg1":{"display":true,"xpos":0,"ypos":0,"width":34,"height":48},"bgc":{"xpos":0,"ypos":0,"width":32,"height":40}}
               """;
        Config.saveDefaultPresetFromString(updater, "minimalistic", "A preset intended to be taking up little screen space. Also expands horizontally instead of vertically.", load);
        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":70,"gen_h":64,"gen_pw":76,"gen_ph":0},"head":{"display":true,"htype":2,"xpos":0,"ypos":0,"zpos":0,"scale":3},"name":{"display":false,"tshadow":true,"tcolor":14545919,"tmax":1,"xpos":1,"ypos":1,"zpos":0,"scale":2},"leader":{"display":true,"xpos":1,"ypos":19,"zpos":2,"scale":2},"dim":{"display":true,"tdisplay":true,"danim":false,"xpos":50,"ypos":44,"zpos":3},"effects":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":2,"totalmax":2,"bsep":false,"dfirst":true,"dlim":0,"blim":1},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":1,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":1,"ypos":11,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":2,"zpos":1,"idisplay":false,"xpos":56,"ypos":2,"width":8,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":51,"width":36,"height":23,"tdisplay":true,"tshadow":true,"ttype":2,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"offline":{"display":true,"scale":2,"zpos":2,"idisplay":true,"xpos":10,"ypos":47,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":8,"ytpos":27},"dead":{"display":false,"xpos":22,"ypos":23,"zpos":1,"scale":3},"bg1":{"display":false,"xpos":0,"ypos":0,"width":32,"height":1},"bgc":{"xpos":0,"ypos":0,"width":64,"height":64}}
               """;
        Config.saveDefaultPresetFromString(updater, "large-h", "Large models with very minimal information. Horizontal orientation.", load);
        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":70,"gen_h":64,"gen_pw":0,"gen_ph":72},"head":{"display":true,"htype":2,"xpos":0,"ypos":0,"zpos":0,"scale":3},"name":{"display":false,"tshadow":true,"tcolor":14545919,"tmax":1,"xpos":1,"ypos":1,"zpos":0,"scale":2},"leader":{"display":true,"xpos":1,"ypos":19,"zpos":2,"scale":2},"dim":{"display":true,"tdisplay":true,"danim":false,"xpos":50,"ypos":44,"zpos":3},"effects":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":2,"totalmax":2,"bsep":false,"dfirst":true,"dlim":0,"blim":1},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":1,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":1,"ypos":11,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":2,"zpos":1,"idisplay":false,"xpos":56,"ypos":2,"width":8,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":51,"width":36,"height":23,"tdisplay":true,"tshadow":true,"ttype":2,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"offline":{"display":true,"scale":2,"zpos":2,"idisplay":true,"xpos":10,"ypos":47,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":8,"ytpos":27},"dead":{"display":false,"xpos":22,"ypos":23,"zpos":1,"scale":3},"bg1":{"display":false,"xpos":0,"ypos":0,"width":32,"height":1},"bgc":{"xpos":0,"ypos":0,"width":64,"height":64}}
               """;
        Config.saveDefaultPresetFromString(updater, "large-v", "Large models with very minimal information. Vertical orientation.", load);

        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":36,"gen_h":40,"gen_pw":40,"gen_ph":0},"head":{"display":true,"htype":2,"xpos":0,"ypos":0,"zpos":0,"scale":2},"name":{"display":false,"tshadow":true,"tcolor":14545919,"tmax":1,"xpos":1,"ypos":1,"zpos":0,"scale":2},"leader":{"display":true,"xpos":27,"ypos":0,"zpos":2,"scale":1},"dim":{"display":true,"tdisplay":true,"danim":true,"xpos":24,"ypos":27,"zpos":3},"effects":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":2,"totalmax":2,"bsep":false,"dfirst":true,"dlim":0,"blim":1},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":6,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":2,"ypos":38,"width":54,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":0,"ypos":32,"width":64,"height":8,"tdisplay":true,"tshadow":true,"ttype":2,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"offline":{"display":true,"scale":1,"zpos":2,"idisplay":true,"xpos":4,"ypos":26,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":8,"ytpos":27},"dead":{"display":false,"xpos":12,"ypos":0,"zpos":1,"scale":2},"bg1":{"display":false,"xpos":0,"ypos":0,"width":32,"height":1},"bgc":{"xpos":0,"ypos":0,"width":32,"height":32}}
               """;
        Config.saveDefaultPresetFromString(updater, "models-h", "A minimalistic preset focused on rendering all party member's models. Horizontal orientation.", load);

        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":36,"gen_h":40,"gen_pw":0,"gen_ph":48},"head":{"display":true,"htype":2,"xpos":0,"ypos":0,"zpos":0,"scale":2},"name":{"display":false,"tshadow":true,"tcolor":14545919,"tmax":1,"xpos":1,"ypos":1,"zpos":0,"scale":2},"leader":{"display":true,"xpos":27,"ypos":0,"zpos":2,"scale":1},"dim":{"display":true,"tdisplay":true,"danim":true,"xpos":24,"ypos":27,"zpos":3},"effects":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":2,"totalmax":2,"bsep":false,"dfirst":true,"dlim":0,"blim":1},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":6,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":2,"ypos":38,"width":54,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":0,"ypos":32,"width":64,"height":8,"tdisplay":true,"tshadow":true,"ttype":2,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"offline":{"display":true,"scale":1,"zpos":2,"idisplay":true,"xpos":4,"ypos":26,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":8,"ytpos":27},"dead":{"display":false,"xpos":12,"ypos":0,"zpos":1,"scale":2},"bg1":{"display":false,"xpos":0,"ypos":0,"width":32,"height":1},"bgc":{"xpos":0,"ypos":0,"width":32,"height":32}}
               """;
        Config.saveDefaultPresetFromString(updater, "models-v", "A minimalistic preset focused on rendering all party member's models. Vertical orientation.", load);

    }

    public static void postInit(FMLLoadCompleteEvent event) {
        DimConfig.init();
    }
}