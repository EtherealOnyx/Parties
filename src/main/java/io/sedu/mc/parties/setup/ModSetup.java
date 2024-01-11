package io.sedu.mc.parties.setup;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.mod.arsnoveau.ANCompatManager;
import io.sedu.mc.parties.api.mod.coldsweat.CSCompatManager;
import io.sedu.mc.parties.api.mod.dietarystats.DSCompatManager;
import io.sedu.mc.parties.api.mod.epicfight.EFCompatManager;
import io.sedu.mc.parties.api.mod.feathers.FCompatManager;
import io.sedu.mc.parties.api.mod.gamestages.GSCompatManager;
import io.sedu.mc.parties.api.mod.hardcorerevival.HRCompatManager;
import io.sedu.mc.parties.api.mod.homeostatic.HCompatManager;
import io.sedu.mc.parties.api.mod.incapacitated.ICompatManager;
import io.sedu.mc.parties.api.mod.ironspellbooks.ISSCompatManager;
import io.sedu.mc.parties.api.mod.openpac.PACCompatManager;
import io.sedu.mc.parties.api.mod.origins.OCompatManager;
import io.sedu.mc.parties.api.mod.playerrevive.PRCompatManager;
import io.sedu.mc.parties.api.mod.spellsandshields.SSCompatManager;
import io.sedu.mc.parties.api.mod.tfc.TFCCompatManager;
import io.sedu.mc.parties.api.mod.thirstmod.TMCompatManager;
import io.sedu.mc.parties.api.mod.toughasnails.TANCompatManager;
import io.sedu.mc.parties.commands.NotSelfArgument;
import io.sedu.mc.parties.network.PartiesPacketHandler;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Parties.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static void init(FMLCommonSetupEvent event) {
        PartiesPacketHandler.register();
        ArgumentTypes.register(Parties.MODID + ":notself", NotSelfArgument.class, new NotSelfArgument.Serializer());

        //Init Mod Support
        event.enqueueWork(ModSetup::initSupport);
    }

    private static void initSupport() {
        Parties.LOGGER.info("[Parties] Initializing mod support...");
        Parties.LOGGER.debug("[Parties] Checking for PlayerRevive...");
        PRCompatManager.init(); //Player Revive Support
        Parties.LOGGER.debug("[Parties] Checking for HardcoreRevival...");
        HRCompatManager.init(); //Hardcore Revival Support
        Parties.LOGGER.debug("[Parties] Checking for Tough As Nails...");
        TANCompatManager.init();//Tough as Nails Support
        Parties.LOGGER.debug("[Parties] Checking for Thirst was Taken...");
        TMCompatManager.init(); //Thirst was Taken Support
        Parties.LOGGER.debug("[Parties] Checking for Cold Sweat...");
        CSCompatManager.init(); //Cold Sweat Support
        Parties.LOGGER.debug("[Parties] Checking for Ars Nouveau...");
        ANCompatManager.init(); //Ars Nouveau Support
        Parties.LOGGER.debug("[Parties] Checking for Epic Fight Mod...");
        EFCompatManager.init(); //Epic Fight Support
        Parties.LOGGER.debug("[Parties] Checking for Feathers...");
        FCompatManager.init(); //Feathers Support
        Parties.LOGGER.debug("[Parties] Checking for Spells and Shields...");
        SSCompatManager.init(); //Spells and Shields Support
        Parties.LOGGER.debug("[Parties] Checking for Open Parties and Claims...");
        PACCompatManager.init(); //Open-PAC Support
        Parties.LOGGER.debug("[Parties] Checking for Homeostatic...");
        HCompatManager.init(); //Homeostatic Support
        Parties.LOGGER.debug("[Parties] Checking for Dietary Statistics...");
        DSCompatManager.init(); //Dietary Stats Support
        Parties.LOGGER.debug("[Parties] Checking for Origins...");
        OCompatManager.init(); //Origins Mod Support
        Parties.LOGGER.debug("[Parties] Checking for Iron's Spells n' Spellbooks...");
        ISSCompatManager.init(); //Iron's Spells n' Spellbooks Support
        Parties.LOGGER.debug("[Parties] Checking for Incapacitated...");
        ICompatManager.init(); //Incapacitated Support
        Parties.LOGGER.debug("[Parties] Checking for Game Stages...");
        GSCompatManager.init(); //Game Stages Support
        Parties.LOGGER.debug("[Parties] Checking for TerraFirmaCraft...");
        TFCCompatManager.init(); //Game Stages Support
        Parties.LOGGER.info("[Parties] Mod support initialization complete!");
    }
}