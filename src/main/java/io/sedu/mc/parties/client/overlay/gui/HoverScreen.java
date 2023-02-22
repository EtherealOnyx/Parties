package io.sedu.mc.parties.client.overlay.gui;

import Util.Render;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

import static Util.Render.tip;
import static io.sedu.mc.parties.client.overlay.RenderItem.clickArea;

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
    private Button goBackButton;

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
        Render.colorCycle = true;
        //TODO: Add 'rearranging' boolean to know when config is in this state or not. helps with 2nd todo.
        int y = Math.max(0, clickArea.t(0) - 10);
        settingsButton = addRenderableWidget(new SmallButton(clickArea.l(0), y, "⚙", p -> doTask(1), tip(this, "Open Party Settings"), .5f, .5f, 1f, .5f));
        goBackButton = addRenderableWidget(new SmallButton(clickArea.l(0), y,"x", p -> doTask(1), tip(this, "Close"), 1f, .5f, .5f));
        initPartyButtons();
        initMenuButtons(y);
        initDragButtons();
        //TODO: Add a way to reinitialize buttons in case party comp changes. HoverScreen.reInit();
        //Perhaps force close the screen. Easy!
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
                //RenderItem.rectCO(poseStack, 5, 5,  frameX + frameW*i + frameW>>1, frameH + frameH*i + frameH>>1, frameX + frameW*i + frameW>>1, frameH + frameH*i + frameH>>1, 0xFFFFFF, 0xAAAAAA);
            }
        }
    }

    private void initMenuButtons(int y) {
        menu.add(addRenderableWidget(new SmallButton(clickArea.l(0), y,"x", p -> doTask(0), tip(this, "Close Menu"), 1f, .5f, .5f)));
        menu.add(addRenderableWidget(new SmallButton(clickArea.l(0)+11, y,"⬆⬇", p -> doTask(2), tip(this, "Change Party Order"), .5f, .5f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(clickArea.l(0)+22, y,"✥", p -> doTask(3), tip(this, "Reposition Party Frame"), 0, 1, .5f, .5f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(clickArea.l(0)+33, y,"⚙", p -> doTask(4), tip(this, "Open Advanced Settings"), 0, 1, .5f, 1f, 1f)));

    }

    private void initDragButtons() {
        int y = Math.max(0, RenderItem.frameY - 10);
        moveFrame.add(addRenderableWidget(new SmallButton(RenderItem.frameX, y, "x", p -> revertPos(), tip(this, "Revert & Close"), 1, .5f, .5f)));
        moveFrame.add(addRenderableWidget(new SmallButton(RenderItem.frameX+11, y,"↺", p -> defaultPos(), tip(this, "Reset To Default & Close"), .5f, 1f, 1f)));
        Button b = addRenderableWidget(new SmallButton(RenderItem.frameX+22, y,"◄", p -> updatePos(true), tip(this, "Undo Move"), 1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        b = addRenderableWidget(new SmallButton(RenderItem.frameX+33, y, "►", p -> updatePos(false), tip(this, "Redo Move"),1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        moveFrame.add(addRenderableWidget(new SmallButton(RenderItem.frameX+44, y,"✓", p -> acceptPos(), tip(this, "Save Position & Close"), .5f, 1, .5f)));

    }

    private void acceptPos() {
        RenderItem.frameX = fX.get(index);
        RenderItem.frameY = fY.get(index);
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

        RenderItem.frameX = fX.get(index);
        RenderItem.frameY = fY.get(index);
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
        RenderItem.frameX = revertX;
        RenderItem.frameY = revertY;
        fX.clear();
        fY.clear();
        refreshDragButtons();
        doTask(1);
    }

    private void defaultPos() {
        RenderItem.frameX = 16;
        RenderItem.frameY = 16;
        fX.clear();
        fY.clear();
        refreshAllButtons();
        doTask(1);
    }

    private void move(int x, int y) {

        if (oldMX == null) {
            oldMX = x;
            oldMY = y;
            oldX = RenderItem.frameX;
            oldY = RenderItem.frameY;
        }

        checkLimits(x, y);
        refreshDragButtons();
    }

    private void checkLimits(int x, int y) {
        int tempFrame = x - oldMX + oldX;
        if (tempFrame < 0) {
            RenderItem.frameX = 0;
        } else if (tempFrame + rightLim > this.width) {
            RenderItem.frameX = this.width - rightLim;
        } else {
            RenderItem.frameX = x - oldMX + oldX;
        }

        tempFrame = y - oldMY + oldY;
        if (tempFrame < 0) {
            RenderItem.frameY = 0;
        } else if (tempFrame + botLim > this.height) {
            RenderItem.frameY = this.height - botLim;
        } else {
            RenderItem.frameY = y - oldMY + oldY;
        }

    }


    private void refreshDragButtons() {
        int y = Math.max(0, RenderItem.frameY - 10);
        for (int i = 0; i < moveFrame.size(); i++) {
            moveFrame.get(i).x = RenderItem.frameX+(i*11);
            moveFrame.get(i).y = y;
        }
    }

    private void save() {
        oldMY = null;
        oldMX = null;
        if (fX.get(index) == RenderItem.frameX && fY.get(index) == RenderItem.frameY)
            return;

        index++;
        if (index != fX.size()) {
            fX = new ArrayList<>(fX.subList(0, index));
            fY = new ArrayList<>(fY.subList(0, index));
        }
        fX.add(RenderItem.frameX);
        fY.add(RenderItem.frameY);
        checkIndex();

    }

    protected void doTask(int task) {
        isArranging = false;
        isMoving = false;
        notEditing = true;
        settingsButton.visible = false;
        goBackButton.visible = false;
        menu.forEach(b -> b.visible = false);
        moveParty.forEach(b -> b.visible = false);
        moveFrame.forEach(b -> b.visible = false);
        switch (task) {
            case 0 -> //Standard screen
                    settingsButton.visible = true;
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
                revertX = RenderItem.frameX;
                revertY = RenderItem.frameY;
                fX.clear();
                fY.clear();
                index = 0;
                fX.add(revertX);
                fY.add(revertY);
                notEditing = false;
                botLim = clickArea.b(ClientPlayerData.playerOrderedList.size()-1) - RenderItem.frameY;
                rightLim = clickArea.r(ClientPlayerData.playerOrderedList.size()-1)  - RenderItem.frameX;
            }
            case 4 -> {
                //Still technically active?
                Minecraft.getInstance().setScreen(new SettingsScreen());
            }
        }
    }


    public void render(PoseStack poseStack, int mX, int mY, float partialTick) {
        super.render(poseStack, mX, mY, partialTick);

        if (isDragging()) {
            move(mX, mY);
        } else if (oldMX != null) {
            save();
        }
            //RenderItem.rectCO(poseStack, 5, 5,  frameX + frameW*i + frameW>>1, frameH + frameH*i + frameH>>1, frameX + frameW*i + frameW>>1, frameH + frameH*i + frameH>>1, 0xFFFFFF, 0xAAAAAA);

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
            this.setDragging(isMoving);
        }

        return false;
    }

    public static void reInit() {
        if (Minecraft.getInstance().screen instanceof HoverScreen hS) {
            hS.clearPartyButtons();
            hS.initPartyButtons();
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
        Render.colorCycle = false;
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
