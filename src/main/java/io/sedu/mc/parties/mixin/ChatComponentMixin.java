package io.sedu.mc.parties.mixin;

import io.sedu.mc.parties.mixinaccessors.TrimmedMessagesAccessor;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin implements TrimmedMessagesAccessor {

    @Shadow
    @Final
    public List<GuiMessage<FormattedCharSequence>> trimmedMessages;

    @Override
    public List<GuiMessage<FormattedCharSequence>> getTrimmedMessages() {
        return trimmedMessages;
    }

}
