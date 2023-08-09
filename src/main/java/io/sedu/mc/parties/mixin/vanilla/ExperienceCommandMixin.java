package io.sedu.mc.parties.mixin.vanilla;

import io.sedu.mc.parties.data.ServerConfigData;
import io.sedu.mc.parties.events.PartyEvent;
import net.minecraft.server.commands.ExperienceCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceCommand.class)
public abstract class ExperienceCommandMixin {

    @Inject(at = @At("HEAD"), method = "addExperience")
    private static void disableXpShare(CallbackInfoReturnable<Integer> cir) {
        PartyEvent.ignoreXpShare = ServerConfigData.ignoreCommand.get();
    }

    @Inject(at = @At("TAIL"), method = "addExperience")
    private static void enableXpShare(CallbackInfoReturnable<Integer> cir) {
        PartyEvent.ignoreXpShare = false;
    }
}
