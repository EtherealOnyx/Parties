package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.api.helper.ColorAPI;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.PEffects;
import io.sedu.mc.parties.client.overlay.RenderItem;
import io.sedu.mc.parties.data.ClientConfigData;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.sedu.mc.parties.client.overlay.RenderItem.*;
import static io.sedu.mc.parties.util.RenderUtils.*;

public class HoverScreen extends Screen {

    private ArrayList<Integer> fX = new ArrayList<>();
    private ArrayList<Integer> fY = new ArrayList<>();
    private int revertX = 0;
    private int revertY = 0;
    private int oldX = 0;
    private int oldY = 0;

    private Integer oldMX = null;
    private Integer oldMY = null;
    private int index = 0;

    private ArrayList<Integer> fXP = new ArrayList<>();
    private ArrayList<Integer> fYP = new ArrayList<>();
    private int revertXP = 0;
    private int revertYP = 0;
    private int oldXP = 0;
    private int oldYP = 0;
    private Integer oldMXP = null;
    private Integer oldMYP = null;
    private int indexP = 0;
    private static int key;
    private final List<Button> moveParty = new ArrayList<>();
    private final List<Button> menu = new ArrayList<>();
    private final List<Button> moveFrame = new ArrayList<>();
    private final List<Button> partyMoveFrame = new ArrayList<>();
    private Button settingsButton;
    private Button partySettingsButton;
    private Button presetButton;
    private Button goBackButton;
    private List<GuiMessage<FormattedCharSequence>> trimmedMessages;

    private static boolean isArranging = false;
    private static boolean isMoving = false;
    private static int draggedItem = -1;
    static boolean notEditing = true;

    //public boolean rendered;
    public HoverScreen(int value) {
        super(new TextComponent("Mouse Hover"));
        key = value;
    }

    @Override
    protected void init() {
        draggedItem = -1;
        trimmedMessages = minecraft.gui.getChat().trimmedMessages;
        selfFrameX = (int) Math.min(ClientConfigData.xPos.get(), width / playerScale - frameEleW);
        selfFrameY = (int) Math.min(ClientConfigData.yPos.get(), height/ playerScale - frameEleH);
        //TODO: Add for otherFrameX/Y.
        ColorAPI.colorCycle = true;
        int y = (int) Math.max(0, clickArea.t(0)* playerScale - 10);
        int x = (int) (clickArea.l(0)* playerScale);
        int oY = (int) Math.max(0, clickArea.t(1)* partyScale - 10);
        int oX = (int) (clickArea.l(1)* partyScale);
        settingsButton = addRenderableWidget(new SmallButton(x, y, "⚙", p -> doTask(1), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.partysettings")), 0, .5f, .5f, .5f, 1f));
        partySettingsButton = addRenderableWidget(new SmallButton(oX, oY, "⚙", p -> doTask(1), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.partysettings")), 0, .5f, .5f, .5f, 1f));
        presetButton = addRenderableWidget(new SmallButton(x+11, y, "☰", p -> doTask(5), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.usepreset")), .5f, 1, 0f, 1f, 1f));
        goBackButton = addRenderableWidget(new SmallButton(x, y,"x", p -> doTask(1), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.close")), 1f, .5f, .5f));
        initPartyButtons();
        initMenuButtons(x, y, oX, oY);
        initDragButtons((int) ((selfFrameX)* playerScale), (int) Math.max(0, (selfFrameY)* playerScale - 10), (int) ((otherFrameX)* partyScale), (int) Math.max(0, (otherFrameY)* partyScale - 10));
        doTask(0);
    }

    private Button createScaledButton(int pX, int pY, Component text, Button.OnPress press, Button.OnTooltip tip) {
        return new ScaledButton(pX, pY, text, press, tip);
    }

    private static class ScaledButton extends Button {

        int originX;
        int originY;

        public ScaledButton(int pX, int pY, Component pMessage, OnPress pOnPress,
                            OnTooltip pOnTooltip) {
            super((int) (pX* playerScale), (int) (pY* playerScale), (int) (20* playerScale), (int) (20* playerScale), pMessage, pOnPress, pOnTooltip);
            this.originX = pX;
            this.originY = pY;
        }

        @Override
        public void renderButton(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            int i = this.getYImage(this.isHoveredOrFocused());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            pPoseStack.pushPose();
            pPoseStack.scale(playerScale, playerScale, 1f);
            this.blit(pPoseStack,originX, originY, 0, 46 + i * 20, 10, 20);
            this.blit(pPoseStack, originX + 10, originY, 200 - 10, 46 + i * 20, 10, 20);
            this.renderBg(pPoseStack, minecraft, pMouseX, pMouseY);

            int j = getFGColor();
            drawCenteredString(pPoseStack, font, this.getMessage(), originX + 10, originY + 6, j | Mth.ceil(this.alpha * 255.0F) << 24);
            pPoseStack.popPose();
            if (this.isHoveredOrFocused()) {
                this.renderToolTip(pPoseStack, pMouseX, pMouseY);
            }
        }
    }

    protected void initPartyButtons() {
        //TODO: Re-implement for other party member list ONLY.
    }

    private void initMenuButtons(int x, int y, int oX, int oY) {
        menu.add(addRenderableWidget(new SmallButton(x, y, "x", p -> doTask(0), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.close")), .5f, 0f, 1f, .5f, .5f)));
        //TODO: Implement in next version.
        menu.add(addRenderableWidget(new SmallButton(oX+11, oY,"⬆⬇", p -> doTask(2), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.rearrange")), .5f, .25f, .5f, .5f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(x+11, y,"✥", p -> doTask(3), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.move")), 0, 1, .5f, .5f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(x+22, y,"⚙", p -> doTask(4), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.advsettings")), 0, .5f, .5f, 1f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(oX, oY, "x", p -> doTask(0), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.close")), .5f, 0f, 1f, .5f, .5f)));
        menu.add(addRenderableWidget(new SmallButton(oX+22, oY,"✥", p -> doTask(3), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.move")), 0, 1, .5f, .5f, 1f)));

    }

    private void initDragButtons(int x, int y, int oX, int oY) {
        moveFrame.add(addRenderableWidget(new SmallButton(x, y, "x", p -> revertPos(true), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.rclose")), .5f, 0f, 1, .5f, .5f)));
        moveFrame.add(addRenderableWidget(new SmallButton(x+11, y, "↺", p -> defaultPos(true), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.dclose")), .5f, 1f, 1f)));
        Button b = addRenderableWidget(new SmallButton(x+22, y, "◄", p -> updatePos(true, true), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.umove")), 1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        b = addRenderableWidget(new SmallButton(x+33, y, "►", p -> updatePos(false, true), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.rmove")), 1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        moveFrame.add(addRenderableWidget(new SmallButton(x+44, y, "✓", p -> acceptPos(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.sclose")), .5f, 1, .5f)));

        //Other members
        partyMoveFrame.add(addRenderableWidget(new SmallButton(oX, oY, "x", p -> revertPos(false), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.rclose")), .5f, 0f, 1, .5f, .5f)));
        partyMoveFrame.add(addRenderableWidget(new SmallButton(oX+11, oY, "↺", p -> defaultPos(false), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.dclose")), .5f, 1f, 1f)));
        b = addRenderableWidget(new SmallButton(oX+22, oY, "◄", p -> updatePos(true, false), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.umove")), 1, 1, .5f));
        b.active = false;
        partyMoveFrame.add(b);
        b = addRenderableWidget(new SmallButton(oX+33, oY, "►", p -> updatePos(false, false), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.rmove")), 1, 1, .5f));
        b.active = false;
        partyMoveFrame.add(b);
        partyMoveFrame.add(addRenderableWidget(new SmallButton(oX+44, oY, "✓", p -> acceptPos(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.sclose")), .5f, 1, .5f)));
    }

    private void acceptPos() {
        //TODO: One universal button near center of screen?
        selfFrameX = fX.get(index);
        selfFrameY = fY.get(index);
        otherFrameX = fXP.get(indexP);
        otherFrameY = fYP.get(indexP);
        ClientConfigData.xPos.set(selfFrameX);
        ClientConfigData.xPos.save();
        ClientConfigData.yPos.set(selfFrameY);
        ClientConfigData.yPos.save();
        ClientConfigData.xPosParty.set(otherFrameX);
        ClientConfigData.xPosParty.save();
        ClientConfigData.yPosParty.set(otherFrameY);
        ClientConfigData.yPosParty.save();
        index = 0;
        indexP = 0;
        fX.clear();
        fY.clear();
        fXP.clear();
        fYP.clear();
        refreshAllButtons();
        doTask(1);

    }

    private void refreshAllButtons() {
        clearWidgets();
        moveParty.clear();
        moveFrame.clear();
        partyMoveFrame.clear();
        menu.clear();
        init();
        doTask(1);
    }

    private void updatePos(boolean back, boolean selfFrame) {
        if (selfFrame) {
            if (back) { //undo
                index--;
            } else { //redo
                index++;
            }
            selfFrameX = fX.get(index);
            selfFrameY = fY.get(index);
            refreshDragButtons(true);
            checkIndex(true); //TODO: Add non-self frame.
        } else {
            if (back) { //undo
                indexP--;
            } else { //redo
                indexP++;
            }
            otherFrameX = fXP.get(indexP);
            otherFrameY = fYP.get(indexP);
            refreshDragButtons(false);
            checkIndex(false); //TODO: Add non-self frame.
        }



    }

    private void checkIndex(boolean selfFrame) {
        if (selfFrame) {
            if (index == 0)
                moveFrame.get(2).active = false;
            else if (index <= fX.size())
                moveFrame.get(2).active = true;

            if (index >= fX.size()-1)
                moveFrame.get(3).active = false;
            else if (index >= 0)
                moveFrame.get(3).active = true;
        } else {
            if (indexP == 0) //TODO: another index variable for the party move frame.
                partyMoveFrame.get(2).active = false;
            else if (indexP <= fXP.size())
                partyMoveFrame.get(2).active = true;

            if (indexP >= fXP.size()-1)
                partyMoveFrame.get(3).active = false;
            else if (indexP >= 0)
                partyMoveFrame.get(3).active = true;
        }

    }

    private void revertPos(boolean selfFrame) {
        if (selfFrame) {
            selfFrameX = revertX;
            selfFrameY = revertY;
            fX.clear();
            fY.clear();

        } else {
            otherFrameX = revertXP;
            otherFrameY = revertYP;
            fXP.clear();
            fYP.clear();
        }
        refreshDragButtons(selfFrame);
        doTask(1);
    }

    private void defaultPos(boolean selfFrame) {
        if (selfFrame) {
            selfFrameX = 16;
            selfFrameY = 16;
            ClientConfigData.xPos.set(16);
            ClientConfigData.xPos.save();
            ClientConfigData.yPos.set(16);
            ClientConfigData.yPos.save();
            fX.clear();
            fY.clear();

        } else {
            otherFrameX = 16;
            otherFrameY = 256;
            ClientConfigData.xPosParty.set(16);
            ClientConfigData.xPosParty.save();
            ClientConfigData.yPosParty.set(256);
            ClientConfigData.yPosParty.save();
            fXP.clear();
            fYP.clear();
        }

        refreshAllButtons();
        doTask(1);

    }

    private void move(int x, int y) {
        if (draggedItem == 1) {
            x /= playerScale;
            y /= playerScale;
            if (oldMX == null) {
                oldMX = x;
                oldMY = y;
                oldX = selfFrameX;
                oldY = selfFrameY;
            }

            checkLimits(x, y, true);
            refreshDragButtons(true);
        } else if (draggedItem == 2) {
            x /= partyScale;
            y /= partyScale;
            if (oldMXP == null) {
                oldMXP = x;
                oldMYP = y;
                oldXP = otherFrameX;
                oldYP = otherFrameY;
            }

            checkLimits(x, y, false);
            refreshDragButtons(false);
        }

    }

    private void checkLimits(int x, int y, boolean selfFrame) {
        if (selfFrame) {
            int tempFrame = x - oldMX + oldX;
            if (tempFrame < 0) {
                selfFrameX = 0;
            } else if (tempFrame + frameEleW > this.width/ playerScale) {
                selfFrameX = (int) (this.width/ playerScale - frameEleW);
            } else {
                selfFrameX = x - oldMX + oldX;
            }

            tempFrame = y - oldMY + oldY;
            if (tempFrame < 0) {
                selfFrameY = 0;
            } else if (tempFrame + frameEleH > this.height/ playerScale) {
                selfFrameY = (int) (this.height/ playerScale - frameEleH);
            } else {
                selfFrameY = y - oldMY + oldY;
            }
        } else {
            int tempFrame = x - oldMXP + oldXP;
            if (tempFrame < 0) {
                otherFrameX = 0;
            } else if (tempFrame + frameEleW > this.width/ partyScale) {
                otherFrameX = (int) (this.width/ partyScale - frameEleW);
            } else {
                otherFrameX = x - oldMXP + oldXP;
            }

            tempFrame = y - oldMYP + oldYP;
            if (tempFrame < 0) {
                otherFrameY = 0;
            } else if (tempFrame + frameEleH > this.height/ partyScale) {
                otherFrameY = (int) (this.height/ partyScale - frameEleH);
            } else {
                otherFrameY = y - oldMYP + oldYP;
            }
        }

    }


    private void refreshDragButtons(boolean selfFrame) {
        if (selfFrame) {
            int x = (int) (selfFrameX* playerScale);
            int y = (int) Math.max(0, selfFrameY* playerScale - 10);
            for (int i = 0; i < moveFrame.size(); i++) {
                moveFrame.get(i).x = x+(i*11);
                moveFrame.get(i).y = y;
            }
        } else {
            int x = (int) (otherFrameX* partyScale);
            int y = (int) Math.max(0, otherFrameY* partyScale - 10);
            for (int i = 0; i < partyMoveFrame.size(); i++) {
                partyMoveFrame.get(i).x = x+(i*11);
                partyMoveFrame.get(i).y = y;
            }
        }

    }

    private void save() {
        if (draggedItem == 1) {
            draggedItem = -1;
            oldMY = null;
            oldMX = null;
            if (index >= fX.size() || (fX.get(index) == selfFrameX && fY.get(index) == selfFrameY))
                return;

            index++;
            if (index != fX.size()) {
                fX = new ArrayList<>(fX.subList(0, index));
                fY = new ArrayList<>(fY.subList(0, index));
            }
            fX.add(selfFrameX);
            fY.add(selfFrameY);
            ClientConfigData.xPos.set(selfFrameX);
            ClientConfigData.xPos.save();
            ClientConfigData.yPos.set(selfFrameY);
            ClientConfigData.yPos.save();
            checkIndex(true);
        } else {
            draggedItem = -1;
            oldMYP = null;
            oldMXP = null;
            if (indexP >= fXP.size() || (fXP.get(indexP) == otherFrameX && fYP.get(indexP) == otherFrameY))
                return;

            indexP++;
            if (indexP != fXP.size()) {
                fXP = new ArrayList<>(fXP.subList(0, indexP));
                fYP = new ArrayList<>(fYP.subList(0, indexP));
            }
            fXP.add(otherFrameX);
            fYP.add(otherFrameY);
            //ClientConfigData.xPosP.set(otherFrameX);
            //ClientConfigData.xPosP.save();
            //ClientConfigData.yPosP.set(otherFrameY);
            //ClientConfigData.yPosP.save();
            checkIndex(false);
        }

    }

    protected void doTask(int task) {
        isArranging = false;
        isMoving = false;
        notEditing = true;
        settingsButton.visible = false;
        partySettingsButton.visible = false;
        goBackButton.visible = false;
        presetButton.visible = false;
        menu.forEach(b -> b.visible = false);
        moveParty.forEach(b -> b.visible = false);
        moveFrame.forEach(b -> b.visible = false);
        partyMoveFrame.forEach(b -> b.visible = false);

        switch (task) {
            case 0 -> {
                //Standard screen
                presetButton.visible = true;
                settingsButton.visible = true;
                partySettingsButton.visible = true;
            }

            case 1 -> //Settings screen
            {
                menu.forEach(b -> b.visible = true);
                menu.get(1).active = false;
                notEditing = false;
            }

            case 2 -> { //Arranging screen
                moveParty.forEach(b -> b.visible = true);
                goBackButton.visible = true;
                isArranging = true;
                notEditing = false;
            }
            case 3 -> {
                isMoving = true;
                moveFrame.forEach(b -> b.visible = true);
                partyMoveFrame.forEach(b -> b.visible = true);
                revertX = selfFrameX;
                revertY = selfFrameY;
                revertXP = otherFrameX;
                revertYP = otherFrameY;
                fX.clear();
                fY.clear();
                fXP.clear();
                fYP.clear();
                index = 0;
                indexP = 0;
                fX.add(revertX);
                fY.add(revertY);
                fXP.add(revertXP);
                fYP.add(revertYP);
                notEditing = false;
            }
            case 4 -> {
                //Still technically active?
                Minecraft.getInstance().setScreen(new SettingsScreen(false));
            }

            case 5 -> {
                Minecraft.getInstance().setScreen(new SettingsScreen(true));
            }
        }
    }

    public void render(PoseStack poseStack, int mX, int mY, float partialTick) {

        //TODO: Unbind options from the party list.
        super.render(poseStack, mX, mY, partialTick);

        checkFrameRender(poseStack);

        if (isDragging()) { //TODO: add boundary checks to know what element was being dragged. Maybe store active element on click and check if element != null or -1?
            if (draggedItem == -1) {
                calculateItem(mX, mY);
            }
            move(mX, mY);
            return;
        } else {
            if (draggedItem == 0) {
                draggedItem = -1;
            } else if (draggedItem > 0) {
                save();
                draggedItem = -1;
                return;
            }

        }
        Style style = getClickedText(mX, mY);
        if (style != null && style.getHoverEvent() != null) {
            this.renderComponentHoverEffect(poseStack, style, mX, mY);
        }

        assert minecraft != null;
        RenderItem.getCurrentMouseFrame(mX, mY, (index, posX, posY) -> {
            RenderItem.checkTooltip(posX, posY, (tooltipItem) -> tooltipItem.renderTooltip(poseStack, (ForgeIngameGui) minecraft.gui, index, mX, mY));
            ClientPlayerData.getOrderedPlayer(index, player -> PEffects.checkEffectTooltip(posX, posY, (effectItem, buffIndex) -> effectItem.renderTooltip(poseStack, (ForgeIngameGui) minecraft.gui, player.effects, buffIndex, mX, mY)));
        });
    }

    private void calculateItem(int mouseXR, int mouseYR) {

        int mouseX = (int) (mouseXR /playerScale);
        int mouseY = (int) (mouseYR / playerScale);
        if (mouseX < selfFrameX || mouseY < selfFrameY) {
            calculateParty(mouseXR, mouseYR);
            return;
        }

        mouseX = mouseX - selfFrameX;
        mouseY = mouseY - selfFrameY;
        if (mouseX < frameEleW && mouseY < frameEleH) {
            draggedItem = 1;
            return;
        }

        calculateParty(mouseXR, mouseYR);

    }

    private void calculateParty(int mouseX, int mouseY) {
        int partyMouseX = mouseX;
        int partyMouseY = mouseY;
        partyMouseX /= partyScale;
        partyMouseY /= partyScale;
        partyMouseX -= otherFrameX;
        partyMouseY -= otherFrameY;


        for (int i = 1; i < ClientPlayerData.playerOrderedList.size(); i++) {
            if (partyMouseX < 0 || partyMouseY < 0) return;
            if (partyMouseX < frameEleW && partyMouseY < frameEleH) {
                System.out.println("2");
                draggedItem = 2;
                return;
            }
            partyMouseX -= framePosW;
            partyMouseY -= framePosH;
        }
        draggedItem = 0;
    }

    private void checkFrameRender(PoseStack poseStack) {
        if (isArranging) {
            //TODO: Implement for party members.
            //renderClickableArea(poseStack);
            return;
        }
        if (isMoving) {
            if (renderSelfFrame) {
                renderSelfFrame(poseStack);
                if (draggedItem == 1)
                    renderSelfFrameOutline(poseStack);
            }
            renderPartyFrame(poseStack);
            if (draggedItem == 2) {
                renderPartyFrameOutline(poseStack);
            }
        }
    }

    @Nullable
    public Style getClickedText(double pMouseX, double pMouseY) {
        if (trimmedMessages == null) return null;
        assert minecraft != null;
        ChatComponent chat = minecraft.gui.getChat();
            if (!minecraft.options.hideGui) {
                double d0 = pMouseX - 2.0D;
                double d1 = (double) this.minecraft.getWindow().getGuiScaledHeight() - pMouseY - 40.0D;
                d0 = Mth.floor(d0 / chat.getScale());
                d1 = Mth.floor(d1 / (chat.getScale() * (this.minecraft.options.chatLineSpacing + 1.0D)));
                if (!(d0 < 0.0D) && !(d1 < 0.0D)) {
                    int i = Math.min(chat.getLinesPerPage(), trimmedMessages.size());
                    if (d0 <= (double) Mth.floor((double) chat.getWidth() / chat.getScale()) && d1 < (double) (9 * i + i)) {
                        int j = (int) (d1 / 9.0D);
                        if (j >= 0 && j < trimmedMessages.size()) {
                            GuiMessage<FormattedCharSequence> guimessage = trimmedMessages.get(j);
                            if (200 + guimessage.getAddedTime() - this.minecraft.gui.getGuiTicks() > 0)
                                return this.minecraft.font.getSplitter().componentStyleAtWidth(guimessage.getMessage(), (int) d0);
                        }
                    }

                }
            }
        return null;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for(GuiEventListener guieventlistener : this.children()) {
            if (guieventlistener.mouseClicked(pMouseX, pMouseY, pButton)) {
                this.setFocused(guieventlistener);
                return true;
            }
        }

        if (pButton == 0) {
            Style style = getClickedText(pMouseX, pMouseY);
            if (style != null && this.handleComponentClicked(style)) {
                return true;
            }

            this.setDragging(isMoving);
        }

        return false;
    }

    public static void reInit() {
        if (Minecraft.getInstance().screen instanceof HoverScreen hS) {
            hS.clearPartyButtons();
            hS.initPartyButtons();
            if (!isArranging)
                hS.moveParty.forEach(button -> button.visible = false);
        }
    }

    private void clearPartyButtons() {
        for (Button w : moveParty)
            this.removeWidget(w);
        moveParty.clear();
    }

    public static boolean notEditing() {
        return notEditing;
    }



    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode != key) {
            //TODO: Fix when you press escape.

            Minecraft.getInstance().setScreen(null);
        }
        return true;
    }

    @Override
    public void onClose() {
        ColorAPI.colorCycle = false;
        notEditing = false;
        if (isMoving) {
            revertPos(true);
            revertPos(false);
            isMoving = false;
        }
        draggedItem = -1;
        isArranging = false;
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}