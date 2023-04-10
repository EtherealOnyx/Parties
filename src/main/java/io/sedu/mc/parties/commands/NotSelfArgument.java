package io.sedu.mc.parties.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.Util;
import io.sedu.mc.parties.mixin.EntitySelectorMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NotSelfArgument extends EntityArgument {
    private final boolean partyOnly;

    protected NotSelfArgument(boolean partyOnly) {
        super(true, true);
        this.partyOnly = partyOnly;
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, @NotNull SuggestionsBuilder pBuilder) {
        if (pContext.getSource() instanceof ClientSuggestionProvider provider) {
            if (partyOnly && ClientPlayerData.partySize() == 0) {

                return Suggestions.empty();
            }


            StringReader stringreader = new StringReader(pBuilder.getInput());
            stringreader.setCursor(pBuilder.getStart());
            EntitySelectorParser entityselectorparser = new EntitySelectorParser(stringreader, false);

            try {
                entityselectorparser.parse();
            } catch (CommandSyntaxException ignored) {
            }

            return entityselectorparser.fillSuggestions(pBuilder, (p_91457_) -> {
                Iterable<String> iterable;
                if (partyOnly) {
                    ArrayList<String> names = new ArrayList<>();
                    for (int i = 1; i < ClientPlayerData.playerOrderedList.size(); i++) {
                        names.add(ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)).getName());
                    }
                    iterable = names;
                } else {
                    Collection<String> collection = provider.getOnlinePlayerNames();
                    assert Minecraft.getInstance().player != null;
                    collection.remove(Minecraft.getInstance().player.getName().getContents());
                    iterable = collection;
                }
                SharedSuggestionProvider.suggest(iterable, p_91457_);
            });
        } else {
            return Suggestions.empty();
        }
    }

    public static class Serializer implements ArgumentSerializer<NotSelfArgument> {
        public void serializeToNetwork(NotSelfArgument pArgument, @NotNull FriendlyByteBuf pBuffer) {
            byte b0 = 0;
            if (pArgument.partyOnly) {
                b0 = (byte)(b0 | 1);
            }
            pBuffer.writeByte(b0);
        }

        public @NotNull NotSelfArgument deserializeFromNetwork(FriendlyByteBuf pBuffer) {
            byte b0 = pBuffer.readByte();
            return new NotSelfArgument((b0 & 1) != 0);
        }

        public void serializeToJson(NotSelfArgument pArgument, JsonObject pJson) {
            pJson.addProperty("partyonly", pArgument.partyOnly ? "yes" : "no");
        }
    }

    public static UUID getPlayerUUID(CommandContext<CommandSourceStack> pContext, String pName, UUID senderId) throws CommandSyntaxException {
        PartyData pD;
        if ((pD = Util.getPartyFromMember(senderId)) == null) return UUID.randomUUID();
        return findPlayer(pD, ((EntitySelectorMixin)pContext.getArgument(pName, EntitySelector.class)).getPlayerName());
    }

    private static UUID findPlayer(PartyData party, String pName) {
        for (UUID member : party.getMembers()) {
            if (Util.getName(member).equals(pName)) {
                return member;
            }
        };
        return null;
    }
}
