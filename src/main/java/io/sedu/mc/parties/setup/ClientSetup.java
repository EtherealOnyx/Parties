package io.sedu.mc.parties.setup;

import io.sedu.mc.parties.api.hardcorerevival.HRHandler;
import io.sedu.mc.parties.api.playerrevive.PRHandler;
import io.sedu.mc.parties.client.config.Config;
import io.sedu.mc.parties.client.config.DimConfig;
import io.sedu.mc.parties.client.overlay.*;
import io.sedu.mc.parties.events.ClientEvent;
import io.sedu.mc.parties.util.ColorUtils;
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
        items.put("offline", new POffline("p_offline"));
        items.put("dead", new PDead("p_dead"));

        items.put("thirst", new PThirst("p_thirst"));
        items.put("temp", new PTemp("p_temp"));
        items.put("bg1", new PRectD("p_bg1"));
        items.put("bgc", new ClickArea("p_bgc"));
        items.put("armor", new PArmor("p_armor"));

        items.put("lvlbar", new PLevelBar("p_lvlbar"));
        items.put("chicken", new PHunger("p_chicken"));
        items.put("health", new PHealth("p_health"));
        items.put("mana", new PMana("p_mana"));
        items.put("mana_ss", new PManaSS("p_mana_ss"));
        items.put("stam_ef", new PStamina("p_stam_ef"));




        //items.put("p_bg1", new PRectO("bg1", 7, 41, 34, 11));




        RenderItem.register();

        //Register keybinding

        ClientRegistry.registerKeyBinding(showMouse);

        //Controller
        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "ctrl", control);

        //DimConfig.init();
        Config.init();
        saveDefaultPresets();
        Config.loadDefaultPreset();
    }

    public static void saveDefaultPresets() {
        RenderItem.setDefaultValues();
        Config.saveDefaultPreset("standard", "The standard preset of the mod. UI is rpg-like.");
        final HashMap<String, RenderItem.Update> updater = new HashMap<>();
        RenderItem.initUpdater(updater);
        String load = """
                {"general":{"gen_x":16,"gen_y":16,"gen_w":248,"gen_h":52,"gen_pw":0,"gen_ph":51},"head":{"display":true,"htype":1,"xpos":8,"ypos":8,"zpos":0,"scale":2,"bleed":true},"name":{"display":true,"tshadow":true,"tcolor":14545919,"tmax":16,"xpos":46,"ypos":9,"zpos":0,"scale":2},"leader":{"display":true,"xpos":34,"ypos":33,"zpos":2,"scale":2},"dim":{"display":true,"tdisplay":true,"danim":true,"xpos":4,"ypos":32,"zpos":1},"effects":{"display":true,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":19,"ypos":170,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":5,"totalmax":5,"bsep":false,"dfirst":true,"dlim":4,"blim":0},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":46,"ypos":41,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":46,"ypos":41,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"armor":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":19,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":143,"ypos":19,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"thirst":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":118,"ypos":19,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"temp":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":80,"ypos":19,"tdisplay":true,"tshadow":true,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":4,"ypos":44,"width":40,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":29,"width":120,"height":10,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"mana":{"display":false,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":29,"width":120,"height":10,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":60,"bcit":10275583,"bcib":7123199,"bcdt":6787286,"bcdb":1915756},"offline":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":156,"ypos":8,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":false,"xtpos":86,"ytpos":20},"dead":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":157,"ypos":9,"tdisplay":true,"tshadow":true,"tcolor":12543590,"tattached":false,"xtpos":25,"ytpos":20},"bg1":{"display":true,"xpos":44,"ypos":7,"width":124,"height":34},"bgc":{"xpos":7,"ypos":7,"width":159,"height":34}}
                """;
        Config.saveDefaultPresetFromString(updater, "sidebuffs", "A preset with the combined buff bar on the right of the frame. Adds more space.", load);
        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":232,"gen_h":64,"gen_pw":0,"gen_ph":63},"head":{"display":true,"htype":1,"xpos":8,"ypos":8,"zpos":0,"scale":2,"bleed":true},"name":{"display":true,"tshadow":true,"tcolor":14545919,"tmax":16,"xpos":46,"ypos":9,"zpos":0,"scale":2},"leader":{"display":true,"xpos":34,"ypos":33,"zpos":2,"scale":2},"dim":{"display":true,"tdisplay":true,"danim":true,"xpos":4,"ypos":32,"zpos":1},"effects":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":46,"ypos":41,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8,"bsep":false,"dfirst":true,"dlim":4,"blim":3},"effects_b":{"display":true,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":46,"ypos":41,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":true,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":170,"ypos":19,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":19,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":143,"ypos":19,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"thirst":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":118,"ypos":19,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"temp":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":80,"ypos":19,"tdisplay":true,"tshadow":true,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":4,"ypos":44,"width":40,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":29,"width":120,"height":10,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"mana":{"display":false,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":29,"width":120,"height":10,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":60,"bcit":10275583,"bcib":7123199,"bcdt":6787286,"bcdb":1915756},"offline":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":156,"ypos":8,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":false,"xtpos":86,"ytpos":20},"dead":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":157,"ypos":9,"tdisplay":true,"tshadow":true,"tcolor":12543590,"tattached":false,"xtpos":25,"ytpos":20},"bg1":{"display":true,"xpos":44,"ypos":7,"width":124,"height":34},"bgc":{"xpos":7,"ypos":7,"width":159,"height":34}}
               """;
        Config.saveDefaultPresetFromString(updater, "sepbuffs", "A preset with the buff and debuff bar separated.", load);
        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":34,"gen_h":72,"gen_pw":36,"gen_ph":0},"head":{"display":true,"htype":0,"xpos":1,"ypos":7,"zpos":0,"scale":2,"bleed":false},"name":{"display":true,"tshadow":true,"tcolor":14545919,"tmax":1,"xpos":2,"ypos":8,"zpos":0,"scale":2},"leader":{"display":true,"xpos":2,"ypos":1,"zpos":2,"scale":1},"dim":{"display":true,"tdisplay":true,"danim":true,"xpos":11,"ypos":0,"zpos":3},"effects":{"display":true,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":2,"ypos":48,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":2,"totalmax":2,"bsep":false,"dfirst":true,"dlim":0,"blim":1},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":0,"ypos":10,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":10,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":23,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":28,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"thirst":{"display":true,"scale":1,"zpos":0,"idisplay":true,"xpos":28,"ypos":7,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"temp":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":2,"ypos":17,"tdisplay":false,"tshadow":true,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":1,"ypos":42,"width":32,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":36,"width":64,"height":10,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"mana":{"display":true,"scale":1,"zpos":0,"idisplay":true,"xpos":1,"ypos":33,"width":64,"height":6,"tdisplay":false,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":60,"bcit":10275583,"bcib":7123199,"bcdt":6787286,"bcdb":1915756},"offline":{"display":true,"scale":1,"zpos":2,"idisplay":true,"xpos":5,"ypos":41,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":8,"ytpos":27},"dead":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":12,"ypos":16,"tdisplay":true,"tshadow":true,"tcolor":12543590,"tattached":false,"xtpos":17,"ytpos":26},"bg1":{"display":true,"xpos":0,"ypos":0,"width":34,"height":48},"bgc":{"xpos":0,"ypos":0,"width":32,"height":40}}
               """;
        Config.saveDefaultPresetFromString(updater, "minimalistic", "A preset intended to be taking up little screen space. Also expands horizontally instead of vertically.", load);
        load = """
              {"general":{"gen_x":16,"gen_y":16,"gen_w":70,"gen_h":64,"gen_pw":76,"gen_ph":0},"head":{"display":true,"htype":2,"xpos":0,"ypos":0,"zpos":0,"scale":3,"bleed":true},"name":{"display":false,"tshadow":true,"tcolor":14545919,"tmax":1,"xpos":1,"ypos":1,"zpos":0,"scale":2},"leader":{"display":true,"xpos":1,"ypos":19,"zpos":2,"scale":2},"dim":{"display":true,"tdisplay":true,"danim":false,"xpos":50,"ypos":44,"zpos":3},"effects":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":2,"totalmax":2,"bsep":false,"dfirst":true,"dlim":0,"blim":1},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":1,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":1,"ypos":11,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"thirst":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":1,"ypos":21,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"temp":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":1,"ypos":32,"tdisplay":false,"tshadow":true,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":2,"zpos":1,"idisplay":false,"xpos":56,"ypos":2,"width":8,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":51,"width":36,"height":23,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"mana":{"display":true,"scale":1,"zpos":0,"idisplay":true,"xpos":1,"ypos":46,"width":36,"height":10,"tdisplay":true,"tshadow":true,"ttype":1,"tattached":true,"xtpos":0,"ytpos":0,"bhue":60,"bcit":10275583,"bcib":7123199,"bcdt":6787286,"bcdb":1915756},"offline":{"display":true,"scale":2,"zpos":2,"idisplay":true,"xpos":10,"ypos":47,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":8,"ytpos":27},"dead":{"display":true,"scale":3,"zpos":1,"idisplay":false,"xpos":22,"ypos":23,"tdisplay":true,"tshadow":true,"tcolor":12543590,"tattached":false,"xtpos":33,"ytpos":24},"bg1":{"display":false,"xpos":0,"ypos":0,"width":32,"height":1},"bgc":{"xpos":0,"ypos":0,"width":64,"height":64}}
              """;
        Config.saveDefaultPresetFromString(updater, "large-h", "Large models with very minimal information. Horizontal orientation.", load);
        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":70,"gen_h":64,"gen_pw":0,"gen_ph":72},"head":{"display":true,"htype":2,"xpos":0,"ypos":0,"zpos":0,"scale":3,"bleed":true},"name":{"display":false,"tshadow":true,"tcolor":14545919,"tmax":1,"xpos":1,"ypos":1,"zpos":0,"scale":2},"leader":{"display":true,"xpos":1,"ypos":19,"zpos":2,"scale":2},"dim":{"display":true,"tdisplay":true,"danim":false,"xpos":50,"ypos":44,"zpos":3},"effects":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":2,"totalmax":2,"bsep":false,"dfirst":true,"dlim":0,"blim":1},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":1,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":1,"ypos":11,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"thirst":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":1,"ypos":21,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"temp":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":1,"ypos":32,"tdisplay":false,"tshadow":true,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":2,"zpos":1,"idisplay":false,"xpos":56,"ypos":2,"width":8,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":51,"width":36,"height":23,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"mana":{"display":true,"scale":1,"zpos":0,"idisplay":true,"xpos":1,"ypos":46,"width":36,"height":10,"tdisplay":true,"tshadow":true,"ttype":1,"tattached":true,"xtpos":0,"ytpos":0,"bhue":60,"bcit":10275583,"bcib":7123199,"bcdt":6787286,"bcdb":1915756},"offline":{"display":true,"scale":2,"zpos":2,"idisplay":true,"xpos":10,"ypos":47,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":8,"ytpos":27},"dead":{"display":true,"scale":3,"zpos":1,"idisplay":false,"xpos":22,"ypos":23,"tdisplay":true,"tshadow":true,"tcolor":12543590,"tattached":false,"xtpos":33,"ytpos":24},"bg1":{"display":false,"xpos":0,"ypos":0,"width":32,"height":1},"bgc":{"xpos":0,"ypos":0,"width":64,"height":64}}
               """;
        Config.saveDefaultPresetFromString(updater, "large-v", "Large models with very minimal information. Vertical orientation.", load);

        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":36,"gen_h":40,"gen_pw":40,"gen_ph":0},"head":{"display":true,"htype":2,"xpos":0,"ypos":0,"zpos":0,"scale":2,"bleed":true},"name":{"display":false,"tshadow":true,"tcolor":14545919,"tmax":1,"xpos":1,"ypos":1,"zpos":0,"scale":2},"leader":{"display":true,"xpos":27,"ypos":0,"zpos":2,"scale":1},"dim":{"display":true,"tdisplay":true,"danim":true,"xpos":24,"ypos":27,"zpos":3},"effects":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":2,"totalmax":2,"bsep":false,"dfirst":true,"dlim":0,"blim":1},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":6,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"thirst":{"display":true,"scale":1,"zpos":0,"idisplay":true,"xpos":1,"ypos":11,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"temp":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":27,"ypos":16,"tdisplay":false,"tshadow":true,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":2,"ypos":38,"width":54,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":0,"ypos":32,"width":64,"height":8,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"mana":{"display":true,"scale":1,"zpos":0,"idisplay":true,"xpos":0,"ypos":29,"width":64,"height":8,"tdisplay":false,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":60,"bcit":10275583,"bcib":7123199,"bcdt":6787286,"bcdb":1915756},"offline":{"display":true,"scale":1,"zpos":2,"idisplay":true,"xpos":4,"ypos":26,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":8,"ytpos":27},"dead":{"display":true,"scale":2,"zpos":1,"idisplay":false,"xpos":11,"ypos":6,"tdisplay":true,"tshadow":true,"tcolor":12543590,"tattached":false,"xtpos":16,"ytpos":13},"bg1":{"display":false,"xpos":0,"ypos":0,"width":32,"height":1},"bgc":{"xpos":0,"ypos":0,"width":32,"height":32}}
               """;
        Config.saveDefaultPresetFromString(updater, "models-h", "A minimalistic preset focused on rendering all party member's models. Horizontal orientation.", load);

        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":36,"gen_h":40,"gen_pw":0,"gen_ph":48},"head":{"display":true,"htype":2,"xpos":0,"ypos":0,"zpos":0,"scale":2,"bleed":true},"name":{"display":false,"tshadow":true,"tcolor":14545919,"tmax":1,"xpos":1,"ypos":1,"zpos":0,"scale":2},"leader":{"display":true,"xpos":27,"ypos":0,"zpos":2,"scale":1},"dim":{"display":true,"tdisplay":true,"danim":true,"xpos":24,"ypos":27,"zpos":3},"effects":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":2,"totalmax":2,"bsep":false,"dfirst":true,"dlim":0,"blim":1},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":0,"ypos":0,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":4,"totalmax":8},"armor":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":1,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":1,"ypos":6,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"thirst":{"display":true,"scale":1,"zpos":0,"idisplay":true,"xpos":1,"ypos":11,"tdisplay":false,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"temp":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":27,"ypos":16,"tdisplay":false,"tshadow":true,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":2,"ypos":38,"width":54,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":1,"zpos":1,"idisplay":true,"xpos":0,"ypos":32,"width":64,"height":8,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"mana":{"display":true,"scale":1,"zpos":0,"idisplay":true,"xpos":0,"ypos":29,"width":64,"height":8,"tdisplay":false,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":60,"bcit":10275583,"bcib":7123199,"bcdt":6787286,"bcdb":1915756},"offline":{"display":true,"scale":1,"zpos":2,"idisplay":true,"xpos":4,"ypos":26,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":8,"ytpos":27},"dead":{"display":true,"scale":2,"zpos":1,"idisplay":false,"xpos":11,"ypos":6,"tdisplay":true,"tshadow":true,"tcolor":12543590,"tattached":false,"xtpos":16,"ytpos":13},"bg1":{"display":false,"xpos":0,"ypos":0,"width":32,"height":1},"bgc":{"xpos":0,"ypos":0,"width":32,"height":32}}
               """;
        Config.saveDefaultPresetFromString(updater, "models-v", "A minimalistic preset focused on rendering all party member's models. Vertical orientation.", load);

        load = """
               {"general":{"gen_x":16,"gen_y":16,"gen_w":168,"gen_h":65,"gen_pw":0,"gen_ph":64},"head":{"display":true,"htype":1,"xpos":8,"ypos":8,"zpos":0,"scale":2,"bleed":true},"name":{"display":true,"tshadow":true,"tcolor":14545919,"tmax":16,"xpos":46,"ypos":7,"zpos":0,"scale":2},"leader":{"display":true,"xpos":34,"ypos":33,"zpos":2,"scale":2},"dim":{"display":true,"tdisplay":true,"danim":true,"xpos":4,"ypos":32,"zpos":1},"effects":{"display":true,"tdisplay":true,"bsize":1,"buffg":11134463,"buffb":16755113,"flash":16777215,"xpos":46,"ypos":43,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8,"bsep":false,"dfirst":true,"dlim":4,"blim":3},"effects_b":{"display":false,"tdisplay":true,"bsize":1,"buffg":11134463,"flash":16777215,"xpos":46,"ypos":43,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"effects_d":{"display":false,"tdisplay":true,"bsize":1,"buffb":16755113,"flash":16777215,"xpos":46,"ypos":43,"zpos":0,"scale":2,"idisplay":true,"spacex":30,"spacey":44,"rowmax":8,"totalmax":8},"armor":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":16,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"chicken":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":143,"ypos":16,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"thirst":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":118,"ypos":16,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":true,"xtpos":0,"ytpos":0},"temp":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":80,"ypos":16,"tdisplay":true,"tshadow":true,"tattached":true,"xtpos":0,"ytpos":0},"lvlbar":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":4,"ypos":44,"width":40,"tdisplay":true,"tshadow":true,"tcolor":8454027,"tattached":true,"xtpos":0,"ytpos":0},"health":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":46,"ypos":26,"width":120,"height":10,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":0,"ohue":11,"bcit":12976069,"bcib":7143276,"bcdt":16762309,"bcdb":16739436},"mana":{"display":true,"scale":1,"zpos":0,"idisplay":true,"xpos":46,"ypos":35,"width":240,"height":12,"tdisplay":true,"tshadow":true,"ttype":0,"tattached":true,"xtpos":0,"ytpos":0,"bhue":60,"bcit":10275583,"bcib":7123199,"bcdt":6787286,"bcdb":1915756},"offline":{"display":true,"scale":2,"zpos":0,"idisplay":true,"xpos":156,"ypos":8,"tdisplay":true,"tshadow":false,"tcolor":14545919,"tattached":false,"xtpos":86,"ytpos":20},"dead":{"display":true,"scale":2,"zpos":1,"idisplay":true,"xpos":157,"ypos":7,"tdisplay":true,"tshadow":true,"tcolor":12543590,"tattached":false,"xtpos":25,"ytpos":20},"bg1":{"display":true,"xpos":44,"ypos":5,"width":124,"height":38},"bgc":{"xpos":6,"ypos":6,"width":162,"height":36}}
               """;
        Config.saveDefaultPresetFromString(updater, "standard-mana", "The standard preset of the mod with support for a mana bar. UI is rpg-like.", load);
    }

    public static void postInit(FMLLoadCompleteEvent event) {
        DimConfig.init();
        event.enqueueWork(() -> {
            if (PRHandler.exists() || HRHandler.exists()) {
                ((PHead)RenderItem.items.get("head")).updateRendererForMods();
                ((PDead)RenderItem.items.get("dead")).updateRendererForMods();
                BarBase.updateRendererForMods();
            }
            if (ModList.get().isLoaded("toughasnails")) {
                ((PTemp)RenderItem.items.get("temp")).updateRendererForTAN();
            }
        });
        Config.reloadClientConfigs();
    }
}