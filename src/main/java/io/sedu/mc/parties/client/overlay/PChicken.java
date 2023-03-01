package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;
import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PChicken extends RenderIconTextItem {


    public PChicken(String name) {
        super(name);
        width = 9;
        height = 9;
    }

    @Override
    int getColor() {
        return 0xb88458;
    }

    @Override
    public String getType() {
        return "Icon";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        poseStack.pushPose();
        poseStack.scale(2f,2f,0);
        poseStack.translate(-.5f, 1, 0);
        setup(GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        assert Minecraft.getInstance().player != null;
        int hunger = Minecraft.getInstance().player.getFoodData().getFoodLevel();
        if (hunger > 16) {
            blit(poseStack, (b.x>>1)+4, b.y>>1, 16, 27, 9, 9);
            blit(poseStack, (b.x>>1)+4, b.y>>1, 52, 27, 9, 9);
        }
        else if (hunger > 12) {
            blit(poseStack, (b.x>>1)+4, b.y>>1, 16, 27, 9, 9);
            blit(poseStack, (b.x>>1)+4, b.y>>1, 61 - (gui.getGuiTicks() >> 4 & 1)*9, 27, 9, 9);
        } else if (hunger > 4) {
            blit(poseStack, (b.x>>1)+4, b.y>>1, 16, 27, 9, 9);
            if ((gui.getGuiTicks() >> 4 & 1) == 0)
                blit(poseStack, (b.x>>1)+4,b.y>>1, 61, 27, 9, 9);
        } else
            blit(poseStack, (b.x>>1)+4, b.y>>1, 16 + (gui.getGuiTicks() >> 3 & 1)*9, 27, 9, 9);
        poseStack.popPose();
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        renderChicken(i, gui, poseStack, id.getHungerForced(), id.alpha);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderChicken(i, gui, poseStack, id.getHunger(), id.alpha);

    }

    void renderChicken(int i, ForgeIngameGui gui, PoseStack poseStack, int hunger, float alpha) {
        if (iconEnabled) {
            useAlpha(alpha);
            setup(Gui.GUI_ICONS_LOCATION);
            RenderSystem.enableDepthTest();

            if (hunger > 16) {
                blit(poseStack, x(i), y(i), 16, 27, 9, 9);
                blit(poseStack, x(i), y(i), 52, 27, 9, 9);
            }
            else if (hunger > 12) {
                blit(poseStack, x(i), y(i), 16, 27, 9, 9);
                blit(poseStack, x(i), y(i), 61 - (gui.getGuiTicks() >> 4 & 1)*9, 27, 9, 9);
            } else if (hunger > 4) {
                blit(poseStack, x(i), y(i), 16, 27, 9, 9);
                if ((gui.getGuiTicks() >> 4 & 1) == 0)
                    blit(poseStack, x(i), y(i), 61, 27, 9, 9);
            } else
                blit(poseStack, x(i), y(i), 16 + (gui.getGuiTicks() >> 3 & 1)*9, 27, 9, 9);


            resetColor();
            if (notEditing() && withinBounds(xNormal(i), yNormal(i), 9, 9, 2, scale)) {
                renderTooltip(poseStack, gui, 10, 0, "Hunger: " + hunger + "/20", 0xb88458, 0x613c1b, 0xffd5b0);
            }
        }
        if (textEnabled)
            text(gui, poseStack, String.valueOf(hunger), tX(i), tY(i), color);
    }

    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + 11;
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) + 1;
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("general");
        c.addBooleanEntry("display", isEnabled());
        c.addSliderEntry("scale", 1, () -> 3, getScale(), true);
        c.addSliderEntry("zpos", 0, () -> 10, zPos);
        c.addTitleEntry("icon");
        c.addBooleanEntry("idisplay", iconEnabled);
        c.addSliderEntry("xpos", 0, this::maxX, this.x);
        c.addSliderEntry("ypos", 0, this::maxY, this.y);
        c.addTitleEntry("text");
        c.addBooleanEntry("tdisplay", textEnabled);
        c.addBooleanEntry("tshadow", textShadow);
        c.addColorEntry("tcolor", color);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("xtpos", 0, this::maxX, textX));
        entries.add(c.addSliderEntry("ytpos", 0, () -> this.maxY() - minecraft.font.lineHeight, textY));
        toggleTextAttach(entries);
        return c;
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true);
        e.addEntry("scale", 2);
        e.addEntry("zpos", 0);
        e.addEntry("idisplay", true);
        e.addEntry("xpos", 143);
        e.addEntry("ypos", 19);
        e.addEntry("tdisplay", true);
        e.addEntry("tshadow", false);
        e.addEntry("tcolor", 0xddf3ff);
        e.addEntry("tattached", true);
        e.addEntry("xtpos", 0);
        e.addEntry("ytpos", 0);
        return e;
    }

}
