package io.sedu.mc.parties.setup;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.arsnoveau.ANCompatManager;
import io.sedu.mc.parties.api.coldsweat.CSCompatManager;
import io.sedu.mc.parties.api.hardcorerevival.HRCompatManager;
import io.sedu.mc.parties.api.playerrevive.PRCompatManager;
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

        //Player Revive Support
        event.enqueueWork(PRCompatManager::init);

        //Hardcore Revival Support
        event.enqueueWork(HRCompatManager::init);

        //TAN Support
        event.enqueueWork(TANCompatManager::init);

        //Thirst Was Taken Support
        event.enqueueWork(TMCompatManager::init);

        //Cold Sweat Support
        event.enqueueWork(CSCompatManager::init);

        //Ars Noveau Support
        event.enqueueWork(ANCompatManager::init);


    }
}