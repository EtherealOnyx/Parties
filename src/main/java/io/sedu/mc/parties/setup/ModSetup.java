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
        PRCompatManager.init(); //Player Revive Support
        HRCompatManager.init(); //Hardcore Revival Support
        TANCompatManager.init();//Tough as Nails Support
        TMCompatManager.init(); //Thirst was Taken Support
        CSCompatManager.init(); //Cold Sweat Support
        ANCompatManager.init(); //Ars Nouveau Support
        EFCompatManager.init(); //Epic Fight Support
        FCompatManager.init(); //Feathers Support
        SSCompatManager.init(); //Spells and Shield Support
        PACCompatManager.init(); //Open-PAC Support
        HCompatManager.init(); //Homeostatic Support
        DSCompatManager.init(); //Dietary Stats Support
        OCompatManager.init(); //Origins Mod Support
        ISSCompatManager.init(); //Iron's Spells n' Spellbooks Support
        ICompatManager.init(); //Incapacitated Support
        GSCompatManager.init(); //Game Stages Support
        Parties.LOGGER.info("[Parties] Mod support initialization complete!");
    }
}