package io.sedu.mc.parties.setup;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.arsnoveau.ANCompatManager;
import io.sedu.mc.parties.api.coldsweat.CSCompatManager;
import io.sedu.mc.parties.api.epicfight.EFCompatManager;
import io.sedu.mc.parties.api.feathers.FCompatManager;
import io.sedu.mc.parties.api.hardcorerevival.HRCompatManager;
import io.sedu.mc.parties.api.homeostatic.HCompatManager;
import io.sedu.mc.parties.api.openpac.PACCompatManager;
import io.sedu.mc.parties.api.playerrevive.PRCompatManager;
import io.sedu.mc.parties.api.spellsandshields.SSCompatManager;
import io.sedu.mc.parties.api.thirstmod.TMCompatManager;
import io.sedu.mc.parties.api.toughasnails.TANCompatManager;
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
        Parties.LOGGER.info("Initializing mod support...");
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
        Parties.LOGGER.info("Mod support initialization complete!");
    }
}