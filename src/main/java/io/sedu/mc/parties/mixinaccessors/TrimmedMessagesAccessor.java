package io.sedu.mc.parties.mixinaccessors;

import net.minecraft.client.GuiMessage;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public interface TrimmedMessagesAccessor {

    List<GuiMessage<FormattedCharSequence>> getTrimmedMessages();
}
