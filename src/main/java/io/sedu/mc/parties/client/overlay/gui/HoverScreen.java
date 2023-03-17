package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.PEffects;
import io.sedu.mc.parties.client.overlay.RenderItem;
import io.sedu.mc.parties.util.ColorUtils;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.sedu.mc.parties.client.overlay.RenderItem.*;
import static io.sedu.mc.parties.util.RenderUtils.*;

public class HoverScreen extends Screen {


    private static int mouseX;
    private static int mouseY;
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
    private static boolean active = false;
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

    public static boolean arranging() {
        return isArranging;
    }

    public static boolean moving() {
        return isMoving;
    }

    @Override
    protected void init() {
        trimmedMessages = minecraft.gui.getChat().trimmedMessages;

        ColorUtils.colorCycle = true;
        int y = Math.max(0, clickArea.t(0) - 10);
        int x = clickArea.l(0);
        settingsButton = addRenderableWidget(new SmallButton(x, y, "⚙", p -> doTask(1), tip(this, "Open Party Settings"), 0, .5f, .5f, .5f, 1f));
        presetButton = addRenderableWidget(new SmallButton(x+11, y, "☰", p -> doTask(5), tip(this, "Use a Preset"), .5f, 1, 0f, 1f, 1f));
        goBackButton = addRenderableWidget(new SmallButton(x, y,"x", p -> doTask(1), tip(this, "Close"), 1f, .5f, .5f));
        initPartyButtons();
        initMenuButtons(x, y);
        initDragButtons();
        doTask(0);
    }

    protected void initPartyButtons() {
        if (ClientPlayerData.partySize() > 1) {
            Button b;
            //moveParty.add(addRenderableWidget(new Button(clickArea.l(0) + (clickArea.w() >> 1) - 9, clickArea.t(0) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("⬇"), pButton -> ClientPlayerData.swap(finalI, finalI+1))));
            for (int i = 0; i < ClientPlayerData.partySize(); i++) {
                int finalI = i;
                if (i == ClientPlayerData.partySize()-1) {
                    b = addRenderableWidget(new Button(clickArea.r(i)-20, clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▼"), pButton -> {}, tip(this, "Move Down")));
                    b.active = false;
                    moveParty.add(b);
                }
                else
                    moveParty.add(addRenderableWidget(new Button(clickArea.r(i)-20, clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▼"), pButton -> ClientPlayerData.swap(finalI, finalI+1), tip(this, "Move Down"))));

                if (i == 0) {
                    b = addRenderableWidget(new Button(clickArea.l(i) , clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▲"), pButton -> {}, tip(this, "Move Up")));
                    b.active = false;
                    moveParty.add(b);
                }
                else
                    moveParty.add(addRenderableWidget(new Button(clickArea.l(i), clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▲"), pButton -> ClientPlayerData.swap(finalI-1, finalI), tip(this, "Move Up"))));
                //rectCO(poseStack, 5, 5,  frameX + frameW*i + frameW>>1, frameH + frameH*i + frameH>>1, frameX + frameW*i + frameW>>1, frameH + frameH*i + frameH>>1, 0xFFFFFF, 0xAAAAAA);
            }
        }
    }

    private void initMenuButtons(int x, int y) {
        menu.add(addRenderableWidget(new SmallButton(x, y,"x", p -> doTask(0), tip(this, "Close Menu"), .5f, 0f, 1f, .5f, .5f)));
        menu.add(addRenderableWidget(new SmallButton(x+11, y,"⬆⬇", p -> doTask(2), tip(this, "Change Party Order"), .5f, .25f, .5f, .5f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(x+22, y,"✥", p -> doTask(3), tip(this, "Reposition Party Frame"), 0, 1, .5f, .5f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(x+33, y,"⚙", p -> doTask(4), tip(this, "Open Advanced Settings"), 0, .5f, .5f, 1f, 1f)));

    }

    private void initDragButtons() {
        int y = Math.max(0, frameY - 10);
        moveFrame.add(addRenderableWidget(new SmallButton(frameX, y, "x", p -> revertPos(), tip(this, "Revert & Close"),.5f, 0f, 1, .5f, .5f)));
        moveFrame.add(addRenderableWidget(new SmallButton(frameX+11, y,"↺", p -> defaultPos(), tip(this, "Reset To Default & Close"), .5f, 1f, 1f)));
        Button b = addRenderableWidget(new SmallButton(frameX+22, y,"◄", p -> updatePos(true), tip(this, "Undo Move"), 1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        b = addRenderableWidget(new SmallButton(frameX+33, y, "►", p -> updatePos(false), tip(this, "Redo Move"),1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        moveFrame.add(addRenderableWidget(new SmallButton(frameX+44, y,"✓", p -> acceptPos(), tip(this, "Save Position & Close"), .5f, 1, .5f)));

    }

    private void acceptPos() {
        frameX = fX.get(index);
        frameY = fY.get(index);
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
        RenderItem.defaultPos();
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
                botLim = frameEleH + framePosH*(ClientPlayerData.playerList.size() - 1);
                rightLim = frameEleW + framePosW*(ClientPlayerData.playerList.size() - 1);
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
            PEffects.checkEffectTooltip(posX, posY, (effectItem, buffIndex) -> effectItem.renderTooltip(poseStack, (ForgeIngameGui) minecraft.gui, ClientPlayerData.getOrderedPlayer(index).effects, buffIndex, mX, mY));
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

    public static boolean withinBounds(int x, int y, int width, int height, int expand, float scale) {
        return mouseX > x - expand && mouseY > y - expand
                && mouseX < x + expand + width*scale && mouseY < y + expand + height*scale;
    }

    public static int mouseX() {
        return mouseX;
    }

    public static int mouseY() {
        return mouseY;
    }

    public static void updateValues(int x, int y) {
        mouseX = x;
        mouseY = y;
    }

    public static void disable() {
        active = false;
        notEditing = false;
        mouseX = -1;
        mouseY = -1;
    }

    public static void activate() {
        active = true;
        notEditing = true;
        mouseX = -1;
        mouseY = -1;
    }

    public static boolean notEditing() {
        return notEditing;
    }

    public static boolean isActive() {
        return active;
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
        ColorUtils.colorCycle = false;
        active = false;
        notEditing = false;
        if (isMoving) {
            revertPos();
            isMoving = false;
        }
        isArranging = false;
        super.onClose();
    }
}