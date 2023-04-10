package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.api.coldsweat.CSCompatManager;
import io.sedu.mc.parties.api.toughasnails.TANCompatManager;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

public class PTemp extends RenderIconTextItem implements TooltipItem {

    private final TranslatableComponent tipName = new TranslatableComponent("ui.sedparties.tooltip.temp");
    private final TranslatableComponent tipName2 = new TranslatableComponent("ui.sedparties.tooltip.temp2");

    private Renderer render;
    private TooltipRender tooltip;
    public PTemp(String name) {
        super(name);
        width = 9;
        height = 9;
        updateRendererForDefault();
    }

    @Override
    int getColor() {
        return 0xAAAAFF;
    }

    @Override
    public String getType() {
        return "Icon";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        setup(partyPath);
        RenderSystem.enableDepthTest();
        blit(poseStack, b.x+8, b.y+3, 44, 19, 9, 9);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (!id.isSpectator) {
            render.render(i,id,gui,poseStack,partialTicks);
        }

    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline && !id.isSpectator) {
            render.render(i,id,gui,poseStack,partialTicks);
        }

    }

    void renderTemp(int i, ForgeIngameGui gui, PoseStack poseStack, int worldTemp, int sev, float alpha, boolean extremeTemps) {
        if (iconEnabled) {
            useAlpha(alpha);
            setup(partyPath);
            RenderSystem.enableDepthTest();
            blit(poseStack, x(i), y(i), (sev+1)*18, extremeTemps ? 36 : 18, 9, 9, 18, 18);
            resetColor();
        }
        if (textEnabled)
            text(gui, poseStack, worldTemp + "°", tX(i), tY(i), getSevColor(sev));
    }

    void renderTANTemp(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float alpha) {
        if (iconEnabled) {
            setup(partyPath);
            useAlpha(alpha);
            RenderSystem.enableDepthTest();
            blit(poseStack, x(i), y(i), id.worldTemp*18, 18, 9, 9, 18, 18);
            resetColor();
        }
        if (textEnabled)
            text(gui, poseStack, id.tempType, tX(i), tY(i), getTANColor(id.worldTemp));
    }

    private int getSevColor(int sev) {
        switch(sev) {
            case 0 -> {return 0x49C3FF;}
            case 1 -> {return 0xCBFFE0;}
            default -> {return 0xFF9870;}
        }
    }

    private int getTANColor(int temp) {
        switch(temp) {
            case 0 -> {return 0x3780b5;}
            case 1 -> {return 0x49C3FF;}
            case 2 -> {return 0xCBFFE0;}
            case 3 -> {return 0xFF9870;}
            default -> {return 0xd35240;}
        }
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
        c.addBooleanEntry("display", elementEnabled);
        c.addSliderEntry("scale", 1, () -> 3, getScale(), true);
        c.addSliderEntry("zpos", 0, () -> 10, zPos);
        c.addTitleEntry("icon");
        c.addBooleanEntry("idisplay", iconEnabled);
        c.addSliderEntry("xpos", 0, this::maxX, this.x);
        c.addSliderEntry("ypos", 0, this::maxY, this.y);
        c.addTitleEntry("text");
        c.addBooleanEntry("tdisplay", textEnabled);
        c.addBooleanEntry("tshadow", textShadow);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("xtpos", 0, () -> Math.max(0, frameEleW), textX));
        entries.add(c.addSliderEntry("ytpos", 0, () -> Math.max(0, frameEleH - (int)(minecraft.font.lineHeight*scale)), textY));
        toggleTextAttach(entries);
        return c;
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("scale", 2, 2);
        e.addEntry("zpos", 0, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("xpos", 80, 12);
        e.addEntry("ypos", 19, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", true, 1);
        e.addEntry("tattached", true, 1);
        e.addEntry("xtpos", 0, 12);
        e.addEntry("ytpos", 0, 12);
        return e;
    }

    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        tooltip.render(poseStack, gui, index, mouseX, mouseY);
    }

    public void updateRendererForTAN() {
        render = (i, id, gui, poseStack, partialTicks) -> renderTANTemp(i, id, gui, poseStack, id.severity == 1 ? id.alpha * (float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks)/4f)/3f) : id.alpha);
        tooltip = (poseStack, gui, index, mouseX, mouseY) -> ClientPlayerData.getOrderedPlayer(index, p -> {
            if (p.isOnline && !p.isSpectator) {
                renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName.getString() + p.tempType, 0xD6E9D9, 0x88938A, getTANColor(p.worldTemp));
            }
        });
    }

    private interface Renderer {
        void render(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks);
    }

    private interface TooltipRender {
        void render(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY);
    }

    @Override
    public boolean isEnabled() {
        if (TANCompatManager.getHandler().tempExists()) {
            updateRendererForTAN();
            return elementEnabled;
        } else if (CSCompatManager.getHandler().exists()) {
            updateRendererForDefault();
            return elementEnabled;
        }
        return false;
    }

    private void updateRendererForDefault() {
        render = (i, id, gui, poseStack, partialTicks) -> {
            if (Math.abs(id.bodyTemp) < 20) {
                renderTemp(i, gui, poseStack, id.worldTemp, id.severity, id.alpha, false);
            } else if (Math.abs(id.bodyTemp) < 60) {
                renderTemp(i, gui, poseStack, id.worldTemp, id.severity, id.alpha * (float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks)/8f)/3f), true);
            } else {
                renderTemp(i, gui, poseStack, id.worldTemp, id.severity, id.alpha * (float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks)/3f)/3f), true);
            }
        };
        tooltip = (poseStack, gui, index, mouseX, mouseY) -> ClientPlayerData.getOrderedPlayer(index, p -> {
            if (p.isOnline && !p.isSpectator) {
                renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName.getString() + p.worldTemp + "°", 0xD6E9D9, 0x88938A, getSevColor(p.severity));
                renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName2.getString() + p.bodyTemp + "", 0xD6E9D9, 0x88938A, getSevColor(p.severity));
            }
        });
    }
}
