package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.HashMap;

public class PName extends RenderItem {
    public static ItemStack sign = null;

    int color;
    int length = 16;

    public PName(String name) {
        super(name);
        width = 0;
        height = 9;//Minecraft font is height of 9.
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        text(i, gui, poseStack, truncateName(id.getName()), color);
    }

    private String truncateName(String name) {
        return name.length() < length ? name : name.substring(0, length);
    }

    @Override
    int getColor() {
        return color;
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        poseStack.pushPose();
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(sign, b.x+4, b.y-1, 0);
        poseStack.popPose();

    }

    @Override
    void updateDefaultPositionForMods(HashMap<String, Update> updater) {
        if (RenderItem.barModsPresent() > 0) {
            //Move text up to make space for bar array.
            updater.get("ypos").onUpdate(this, 8);
        }
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        addDisplaySettings(c);
        c.addBooleanEntry("tshadow", textShadow);
        c.addColorEntry("tcolor", color);
        c.addSliderEntry("tmax", 1, () -> 16, length);
        addPositionalSettings(c, true, true, true);
        return c;
    }

    @Override
    public SmallBound setColor(int type, int data) {
        this.color = data;
        return null;
    }


    @Override
    public int getColor(int type) {
        return color;
    }

    public SmallBound setMaxTextSize(int data) {
        this.length = data;
        if (ClientPlayerData.playerOrderedList.size() > 0)
            return new SmallBound(2,
                                  (int) (Minecraft.getInstance().font.width(ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(0))
                                                                                                          .getName()
                                                                                                          .substring(0, Math.min(length, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(0))
                                                                                                                                                                    .getName()
                                                                                                                                                                    .length()))) * scale));
        return new SmallBound(2, (int) (width * scale));
    }


    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("tshadow", true, 1);
        e.addEntry("tcolor", 0xddf3ff, 24);
        e.addEntry("tmax", 16, 5);
        e.addEntry("xpos", 46, 12);
        e.addEntry("ypos", 9, 12);
        e.addEntry("zpos", 0, 4);
        e.addEntry("scale", 2, 2);
        return e;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public ItemBound getRenderItemBound() {
        if (ClientPlayerData.playerOrderedList.size() > 0)
            return new ItemBound(selfFrameX + x, selfFrameY + y - 1,
                                 (int) (Minecraft.getInstance().font.width(ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(0))
                                                                                                                                  .getName()
                                                                                                                                  .substring(0, Math.min(length, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(0))
                                                                                                                                                                                            .getName()
                                                                                                                                                                                            .length()))) * scale), (int) (height * scale));
        return new ItemBound(selfFrameX + x, selfFrameY + y - 1, (int) (width * scale), (int) (height * scale));
    }

}
