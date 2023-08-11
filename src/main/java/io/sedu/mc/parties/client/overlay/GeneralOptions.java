package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.api.mod.ironspellbooks.ISSCompatManager;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.HashMap;

public class GeneralOptions extends RenderItem {

    public static final GeneralOptions INSTANCE = new GeneralOptions("options");

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
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, b.x+4, b.y, 0);
    }

    @Override
    void updateDefaultPositionForMods(HashMap<String, Update> updater) {
        //Change width for Iron's Spells n Spellbooks.
        if (ISSCompatManager.active()) {
            updater.get("gen_w").onUpdate(this, 256);
        }
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("gen_w", 178, 12);
        e.addEntry("gen_h", 64, 12);
        e.addEntry("gen_pw", 0, 12);
        e.addEntry("gen_ph", 63, 12);
        return e;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("gen_f");
        //c.addSliderEntry("gen_x", 0, () -> s.width - framePosW*(ClientPlayerData.playerOrderedList.size()-1) - frameEleW, frameX);
        //c.addSliderEntry("gen_y", 0, () -> s.height - framePosH*(ClientPlayerData.playerOrderedList.size()-1) - frameEleH, frameY);
        c.addSliderWithUpdater("gen_w", 0, () -> s.width - selfFrameX, frameEleW, this::ensureBounds, true);
        c.addSliderWithUpdater("gen_h", 0, () -> s.height - selfFrameY, frameEleH, this::ensureBounds, true);
        c.addSliderEntry("gen_pw", 0, () -> s.width - selfFrameX, framePosW, true);
        c.addSliderEntry("gen_ph", 0, () -> s.height - selfFrameY, framePosH, true);
        return c;
    }

    private void ensureBounds() {
        RenderItem.items.values().forEach(RenderItem::updateValues);
    }
}
