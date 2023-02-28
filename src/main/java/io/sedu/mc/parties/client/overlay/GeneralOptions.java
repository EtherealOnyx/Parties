package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class GeneralOptions extends RenderItem {

    public static ItemStack icon = null;

    public GeneralOptions(String name) {
        super(name);

    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {

    }

    @Override
    int getColor() {
        return 0xFFFFFF;
    }

    @Override
    public String getType() {
        return "";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, b.x+8, b.y+4, 0);
    }

    @Override
    ConfigEntry getDefaults() {

        return new ConfigEntry();
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("gen_f");
        c.addSliderEntry("gen_x", 0, () -> s.width - framePosW*(ClientPlayerData.playerOrderedList.size()-1) - frameEleW, frameX);
        c.addSliderEntry("gen_y", 0, () -> s.height - framePosH*(ClientPlayerData.playerOrderedList.size()-1) - frameEleH, frameY);
        c.addSliderWithUpdater("gen_w", 0, () -> s.width - frameX, frameEleW, this::ensureBounds, true);
        c.addSliderWithUpdater("gen_h", 0, () -> s.height - frameY, frameEleH, this::ensureBounds, true);
        c.addSliderEntry("gen_pw", 0, () -> s.width - frameX, framePosW, true);
        c.addSliderEntry("gen_ph", 0, () -> s.height - frameY, framePosH, true);
        return c;
    }

    private void ensureBounds() {
        RenderItem.items.values().forEach(RenderItem::updateValues);
    }
}
