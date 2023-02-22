package io.sedu.mc.parties.client.overlay;

import Util.Render;
import com.mojang.blaze3d.vertex.PoseStack;
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
        return Render.getRainbowColor();
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
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("gen_f");
        c.addSliderEntry("gen_x", 0, () -> s.width - (clickArea.r(ClientPlayerData.playerOrderedList.size()-1)  - RenderItem.frameX), frameX);
        c.addSliderEntry("gen_y", 0, () -> s.height - (clickArea.b(ClientPlayerData.playerOrderedList.size()-1) - RenderItem.frameY), frameY);
        c.addSliderEntry("gen_w", 0, () -> s.width>>1, frameW, true);
        c.addSliderEntry("gen_h", 0, () -> s.height>>1, frameH, true);
        c.addTitleEntry("gen_c");
        c.addSliderEntry("genc_x", 0, () -> s.width>>1, clickArea.x, true);
        c.addSliderEntry("genc_y", 0, () -> s.height>>1, clickArea.y, true);
        c.addSliderWithUpdater("genc_w", 0, () -> s.width, clickArea.width, this::ensureBounds, true);
        c.addSliderWithUpdater("genc_h", 0, () -> s.height, clickArea.height, this::ensureBounds, true);




        return c;
    }

    private void ensureBounds() {
        RenderItem.items.values().forEach(RenderItem::updateValues);
    }
}
