package io.sedu.mc.parties.mixin.vanilla;

import io.sedu.mc.parties.api.helper.PartyAPI;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.client.overlay.gui.GUIRenderer;
import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.data.ServerConfigData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin implements GUIRenderer {

    private boolean renderUI = false;

    @ModifyVariable(at = @At("HEAD"), method = "giveExperiencePoints(I)V", argsOnly = true)
    private int modifiedExperiencePoints(int pXpPoints) {
        if (pXpPoints < 0 ) return pXpPoints;
        if (!ServerConfigData.enableShare.get()) return pXpPoints;
        ServerPlayerData pd;
        if ((pd = PlayerAPI.getNormalPlayer(((Player)(Object)this).getUUID())) == null) return pXpPoints;
        List<Player> members = ServerConfigData.globalShare.get() ? pd.getOnlineMembers() : pd.getNearbyMembers();
        if (members.size() > 0) { //Has party
            PartyData party = PartyAPI.getPartyFromMember(((Player)(Object)this).getUUID());
            assert party != null;
            pXpPoints += party.getXpOverflow();
            int sharedXp = pXpPoints / (members.size()+1);
            party.setXpOverflow(pXpPoints - (sharedXp*(members.size()+1))); //Any remainder xp is saved.
            int selfXp = sharedXp;
            for (Player member : members) {
                if (!givePartyExperiencePoints(member, sharedXp))
                    selfXp += sharedXp;
            }
            return selfXp;
        }
        return pXpPoints;

    }

    public void setRenderMode(boolean uiRendering) {
        this.renderUI = uiRendering;
    }

    public boolean isRenderingUI() {
        return renderUI;
    }

    private boolean givePartyExperiencePoints(Player partyPlayer, int pXpPoints) {
        net.minecraftforge.event.entity.player.PlayerXpEvent.XpChange event = new net.minecraftforge.event.entity.player.PlayerXpEvent.XpChange(((Player)(Object)this), pXpPoints);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return false;
        pXpPoints = event.getAmount();

        partyPlayer.increaseScore(pXpPoints);
        partyPlayer.experienceProgress += (float)pXpPoints / (float)partyPlayer.getXpNeededForNextLevel();
        partyPlayer.totalExperience = Mth.clamp(partyPlayer.totalExperience + pXpPoints, 0, Integer.MAX_VALUE);

        while(partyPlayer.experienceProgress < 0.0F) {
            float f = partyPlayer.experienceProgress * (float)partyPlayer.getXpNeededForNextLevel();
            if (partyPlayer.experienceLevel > 0) {
                partyPlayer.giveExperienceLevels(-1);
                partyPlayer.experienceProgress = 1.0F + f / (float)partyPlayer.getXpNeededForNextLevel();
            } else {
                partyPlayer.giveExperienceLevels(-1);
                partyPlayer.experienceProgress = 0.0F;
            }
        }

        while(partyPlayer.experienceProgress >= 1.0F) {
            partyPlayer.experienceProgress = (partyPlayer.experienceProgress - 1.0F) * (float)partyPlayer.getXpNeededForNextLevel();
            partyPlayer.giveExperienceLevels(1);
            partyPlayer.experienceProgress /= (float)partyPlayer.getXpNeededForNextLevel();
        }
        return true;
    }
}
