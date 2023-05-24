package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.PEffects;
import io.sedu.mc.parties.client.overlay.RenderItem;
import io.sedu.mc.parties.data.ClientConfigData;
import io.sedu.mc.parties.api.helper.ColorAPI;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.sedu.mc.parties.client.overlay.RenderItem.*;
import static io.sedu.mc.parties.client.overlay.RenderSelfItem.selfIndex;
import static io.sedu.mc.parties.util.RenderUtils.*;

public class HoverScreen extends Screen {

    private ArrayList<Integer> fX = new ArrayList<>();
    private ArrayList<Integer> fY = new ArrayList<>();
    private int revertX = 0;
    private int revertY = 0;
    private int oldX = 0;
    private int oldY = 0;
    private int botLim = 0;
    private int rightLim = 0;
    private Integer oldMX = null;
    private Integer oldMY = null;
    private int index = 0;
    private static int key;
    private List<Button> moveParty = new ArrayList<>();
    private List<Button> menu = new ArrayList<>();
    private List<Button> moveFrame = new ArrayList<>();
    private Button settingsButton;
    private Button presetButton;
    private Button goBackButton;
    private List<GuiMessage<FormattedCharSequence>> trimmedMessages;

    private static boolean isArranging = false;
    private static boolean isMoving = false;
    static boolean notEditing = true;

    //public boolean rendered;
    public HoverScreen(int value) {
        super(new TextComponent("Mouse Hover"));
        key = value;
    }

    @Override
    protected void init() {
        trimmedMessages = minecraft.gui.getChat().trimmedMessages;
        frameX = Math.min(ClientConfigData.xPos.get(), width - framePosW*(ClientPlayerData.playerOrderedList.size()-1) - frameEleW);
        frameY = Math.min(ClientConfigData.yPos.get(), height - framePosH*(ClientPlayerData.playerOrderedList.size()-1) - frameEleH);
        ColorAPI.colorCycle = true;
        int y = Math.max(0, clickArea.t(0) - 10);
        int x = clickArea.l(0);
        settingsButton = addRenderableWidget(new SmallButton(x, y, "⚙", p -> doTask(1), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.partysettings")), 0, .5f, .5f, .5f, 1f));
        presetButton = addRenderableWidget(new SmallButton(x+11, y, "☰", p -> doTask(5), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.usepreset")), .5f, 1, 0f, 1f, 1f));
        goBackButton = addRenderableWidget(new SmallButton(x, y,"x", p -> doTask(1), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.close")), 1f, .5f, .5f));
        initPartyButtons();
        initMenuButtons(x, y);
        initDragButtons();
        doTask(0);
    }

    protected void initPartyButtons() {
        if (ClientPlayerData.partySize() > 1) {
            Button b;
            //moveParty.add(addRenderableWidget(new Button(clickArea.l(0) + (clickArea.w() >> 1) - 9, clickArea.t(0) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("⬇"), pButton -> ClientPlayerData.swap(finalI, finalI+1))));
            if (ClientConfigData.renderSelfFrame.get()) {
                Parties.LOGGER.debug("Initializing arrange buttons for when rendering self...");
                for (int i = 0; i < ClientPlayerData.partySize(); i++) {
                    int finalI = i;
                    if (i == ClientPlayerData.partySize()-1) {
                        b = addRenderableWidget(new Button(clickArea.r(i)-20, clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▼"), pButton -> {}, transTip(this, new TranslatableComponent("gui.sedparties.tooltip.movedown"))));
                        b.active = false;
                        moveParty.add(b);
                    }
                    else
                        moveParty.add(addRenderableWidget(new Button(clickArea.r(i)-20, clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▼"), pButton -> ClientPlayerData.swap(finalI, finalI+1), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.movedown")))));

                    if (i == 0) {
                        b = addRenderableWidget(new Button(clickArea.l(i) , clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▲"), pButton -> {}, transTip(this, new TranslatableComponent("gui.sedparties.tooltip.moveup"))));
                        b.active = false;
                        moveParty.add(b);
                    }
                    else
                        moveParty.add(addRenderableWidget(new Button(clickArea.l(i), clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▲"), pButton -> ClientPlayerData.swap(finalI-1, finalI), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.moveup")))));
                    //rectCO(poseStack, 5, 5,  frameX + frameW*i + frameW>>1, frameH + frameH*i + frameH>>1, frameX + frameW*i + frameW>>1, frameH + frameH*i + frameH>>1, 0xFFFFFF, 0xAAAAAA);
                }
            } else {
                Parties.LOGGER.debug("Initializing arrange buttons for when NOT rendering self...");
                for (int i = 0; i < selfIndex; i++) {
                    Parties.LOGGER.debug("Current Index: " + i);
                    int finalI = i;
                    if (i == ClientPlayerData.partySize() - 1) {
                        Parties.LOGGER.debug("Adding down disabled for " + i);
                        b = addRenderableWidget(new Button(clickArea.r(i) - 20, clickArea.t(i) + (clickArea.h() >> 1) - 10, 20, 20, new TextComponent("▼"), pButton -> {}, transTip(this, new TranslatableComponent("gui.sedparties.tooltip.movedown"))));
                        b.active = false;
                        moveParty.add(b);
                    } else {
                        if (selfIndex == finalI + 1) {
                            if (finalI + 2 > ClientPlayerData.partySize() - 1) {
                                Parties.LOGGER.debug("Adding down disabled for " + i);
                                b = addRenderableWidget(new Button(clickArea.r(i) - 20, clickArea.t(i) + (clickArea.h() >> 1) - 10, 20, 20, new TextComponent("▼"), pButton -> {}, transTip(this, new TranslatableComponent("gui.sedparties.tooltip.movedown"))));
                                b.active = false;
                                moveParty.add(b);
                            } else {
                                Parties.LOGGER.debug("Adding down enabled for " + i);
                                moveParty.add(addRenderableWidget(new Button(clickArea.r(i) - 20, clickArea.t(i) + (clickArea.h() >> 1) - 10, 20, 20, new TextComponent("▼"), pButton -> ClientPlayerData.swap(finalI, finalI + 2), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.movedown")))));
                            }
                        } else {
                            Parties.LOGGER.debug("Adding down enabled for " + i);
                            moveParty.add(addRenderableWidget(new Button(clickArea.r(i) - 20, clickArea.t(i) + (clickArea.h() >> 1) - 10, 20, 20, new TextComponent("▼"), pButton -> ClientPlayerData.swap(finalI, finalI + 1), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.movedown")))));
                        }
                    }
                    if (i == 0) {
                        Parties.LOGGER.debug("Adding up disabled for " + i);
                        b = addRenderableWidget(new Button(clickArea.l(i), clickArea.t(i) + (clickArea.h() >> 1) - 10, 20, 20, new TextComponent("▲"), pButton -> {}, transTip(this, new TranslatableComponent("gui.sedparties.tooltip.moveup"))));
                        b.active = false;
                        moveParty.add(b);
                    } else {
                        Parties.LOGGER.debug("Adding up enabled for " + i);
                        moveParty.add(addRenderableWidget(new Button(clickArea.l(i), clickArea.t(i) + (clickArea.h() >> 1) - 10, 20, 20, new TextComponent("▲"), pButton -> ClientPlayerData.swap(finalI - 1, finalI), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.moveup")))));
                    }

                }
                Parties.LOGGER.debug("Skipping self index value of " + selfIndex);
                for (int i = selfIndex+1; i < ClientPlayerData.partySize(); i++) {
                    int finalI = i;
                    if (i == ClientPlayerData.partySize() - 1) {
                        Parties.LOGGER.debug("Adding down disabled for " + i);
                        b = addRenderableWidget(new Button(clickArea.r(i-1) - 20, clickArea.t(i-1) + (clickArea.h() >> 1) - 10, 20, 20, new TextComponent("▼"), pButton -> {}, transTip(this, new TranslatableComponent("gui.sedparties.tooltip.movedown"))));
                        b.active = false;
                        moveParty.add(b);
                    } else {
                        Parties.LOGGER.debug("Adding down enabled for " + i);
                        moveParty.add(addRenderableWidget(new Button(clickArea.r(i-1) - 20, clickArea.t(i-1) + (clickArea.h() >> 1) - 10, 20, 20, new TextComponent("▼"), pButton -> ClientPlayerData.swap(finalI, finalI + 1), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.movedown")))));
                    }

                    if (selfIndex == finalI - 1) {
                        if (finalI - 2 < 0) {
                            Parties.LOGGER.debug("Adding up disabled for " + i);
                            b = addRenderableWidget(new Button(clickArea.l(i-1), clickArea.t(i-1) + (clickArea.h() >> 1) - 10, 20, 20, new TextComponent("▲"), pButton -> {}, transTip(this, new TranslatableComponent("gui.sedparties.tooltip.moveup"))));
                            b.active = false;
                            moveParty.add(b);
                        } else {
                            Parties.LOGGER.debug("Adding up enabled for " + i);
                            moveParty.add(addRenderableWidget(new Button(clickArea.l(i-1), clickArea.t(i-1) + (clickArea.h() >> 1) - 10, 20, 20, new TextComponent("▲"), pButton -> ClientPlayerData.swap(finalI - 2, finalI), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.moveup")))));
                        }
                    } else {
                        Parties.LOGGER.debug("Adding up enabled for " + i);
                        moveParty.add(addRenderableWidget(new Button(clickArea.l(i-1), clickArea.t(i-1) + (clickArea.h() >> 1) - 10, 20, 20, new TextComponent("▲"), pButton -> ClientPlayerData.swap(finalI - 1, finalI), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.moveup")))));
                    }
                }
            }

        }
    }

    private void initMenuButtons(int x, int y) {
        menu.add(addRenderableWidget(new SmallButton(x, y, "x", p -> doTask(0), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.close")), .5f, 0f, 1f, .5f, .5f)));
        menu.add(addRenderableWidget(new SmallButton(x+11, y,"⬆⬇", p -> doTask(2), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.rearrange")), .5f, .25f, .5f, .5f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(x+22, y,"✥", p -> doTask(3), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.move")), 0, 1, .5f, .5f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(x+33, y,"⚙", p -> doTask(4), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.advsettings")), 0, .5f, .5f, 1f, 1f)));

    }

    private void initDragButtons() {
        int y = Math.max(0, frameY - 10);
        moveFrame.add(addRenderableWidget(new SmallButton(frameX, y, "x", p -> revertPos(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.rclose")),.5f, 0f, 1, .5f, .5f)));
        moveFrame.add(addRenderableWidget(new SmallButton(frameX+11, y,"↺", p -> defaultPos(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.dclose")), .5f, 1f, 1f)));
        Button b = addRenderableWidget(new SmallButton(frameX+22, y,"◄", p -> updatePos(true), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.umove")), 1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        b = addRenderableWidget(new SmallButton(frameX+33, y, "►", p -> updatePos(false), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.rmove")),1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        moveFrame.add(addRenderableWidget(new SmallButton(frameX+44, y,"✓", p -> acceptPos(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.sclose")), .5f, 1, .5f)));

    }

    private void acceptPos() {
        frameX = fX.get(index);
        frameY = fY.get(index);
        ClientConfigData.xPos.set(frameX);
        ClientConfigData.xPos.save();
        ClientConfigData.yPos.set(frameY);
        ClientConfigData.yPos.save();
        index = 0;
        fX.clear();
        fY.clear();
        refreshAllButtons();
        doTask(1);
    }

    private void refreshAllButtons() {
        clearWidgets();
        moveParty.clear();
        moveFrame.clear();
        menu.clear();
        init();
        doTask(1);
    }

    private void updatePos(boolean b) {
        if (b) { //undo
            index--;
        } else { //redo
            index++;
        }

        frameX = fX.get(index);
        frameY = fY.get(index);
        refreshDragButtons();
        checkIndex();
    }

    private void checkIndex() {
        if (index == 0)
            moveFrame.get(2).active = false;
        else if (index <= fX.size())
            moveFrame.get(2).active = true;

        if (index >= fX.size()-1)
            moveFrame.get(3).active = false;
        else if (index >= 0)
            moveFrame.get(3).active = true;
    }

    private void revertPos() {
        frameX = revertX;
        frameY = revertY;
        fX.clear();
        fY.clear();
        refreshDragButtons();
        doTask(1);
    }

    private void defaultPos() {
        frameX = 16;
        frameY = 16;
        ClientConfigData.xPos.set(16);
        ClientConfigData.xPos.save();
        ClientConfigData.yPos.set(16);
        ClientConfigData.yPos.save();
        fX.clear();
        fY.clear();
        refreshAllButtons();
        doTask(1);
    }

    private void move(int x, int y) {

        if (oldMX == null) {
            oldMX = x;
            oldMY = y;
            oldX = frameX;
            oldY = frameY;
        }

        checkLimits(x, y);
        refreshDragButtons();
    }

    private void checkLimits(int x, int y) {
        int tempFrame = x - oldMX + oldX;
        if (tempFrame < 0) {
            frameX = 0;
        } else if (tempFrame + rightLim > this.width) {
            frameX = this.width - rightLim;
        } else {
            frameX = x - oldMX + oldX;
        }

        tempFrame = y - oldMY + oldY;
        if (tempFrame < 0) {
            frameY = 0;
        } else if (tempFrame + botLim > this.height) {
            frameY = this.height - botLim;
        } else {
            frameY = y - oldMY + oldY;
        }

    }


    private void refreshDragButtons() {
        int y = Math.max(0, frameY - 10);
        for (int i = 0; i < moveFrame.size(); i++) {
            moveFrame.get(i).x = frameX+(i*11);
            moveFrame.get(i).y = y;
        }
    }

    private void save() {
        oldMY = null;
        oldMX = null;
        if (fX.get(index) == frameX && fY.get(index) == frameY)
            return;

        index++;
        if (index != fX.size()) {
            fX = new ArrayList<>(fX.subList(0, index));
            fY = new ArrayList<>(fY.subList(0, index));
        }
        fX.add(frameX);
        fY.add(frameY);
        ClientConfigData.xPos.set(frameX);
        ClientConfigData.xPos.save();
        ClientConfigData.yPos.set(frameY);
        ClientConfigData.yPos.save();
        checkIndex();

    }

    protected void doTask(int task) {
        isArranging = false;
        isMoving = false;
        notEditing = true;
        settingsButton.visible = false;
        goBackButton.visible = false;
        presetButton.visible = false;
        menu.forEach(b -> b.visible = false);
        moveParty.forEach(b -> b.visible = false);
        moveFrame.forEach(b -> b.visible = false);

        switch (task) {
            case 0 -> {
                //Standard screen
                presetButton.visible = true;
                settingsButton.visible = true;
            }

            case 1 -> //Settings screen
            {
                menu.forEach(b -> b.visible = true);
                if (renderSelfFrame) {
                    menu.get(1).active = true;
                    menu.get(2).active = true;
                } else {
                    if (ClientPlayerData.playerList.size() > 1) {
                        menu.get(1).active = true;
                        menu.get(2).active = true;
                    } else {
                        menu.get(1).active = false;
                        menu.get(2).active = false;
                    }
                }
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
                revertX = frameX;
                revertY = frameY;
                fX.clear();
                fY.clear();
                index = 0;
                fX.add(revertX);
                fY.add(revertY);
                notEditing = false;
                if (renderSelfFrame) {
                    botLim = frameEleH + framePosH*(ClientPlayerData.playerList.size() - 1);
                    rightLim = frameEleW + framePosW*(ClientPlayerData.playerList.size() - 1);
                } else {
                    botLim = frameEleH + framePosH*(Math.min(ClientPlayerData.playerList.size() - 2, 1));
                    rightLim = frameEleW + framePosW*(Math.min(ClientPlayerData.playerList.size() - 2, 1));
                }
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
        super.render(poseStack, mX, mY, partialTick);

        checkFrameRender(poseStack);

        if (isDragging()) {
            move(mX, mY);
            return;
        } else if (oldMX != null) {
            save();
            return;
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

    private void checkFrameRender(PoseStack poseStack) {
        if (isArranging) {
            renderClickableArea(poseStack);
            return;
        }
        if (isMoving) {
            renderFrame(poseStack);
            renderFrameOutline(poseStack);
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
            revertPos();
            isMoving = false;
        }
        isArranging = false;
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}