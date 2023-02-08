package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.PRectC;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class HoverScreen extends Screen {


    private static int mouseX;
    private static int mouseY;
    private ArrayList<Integer> fX = new ArrayList<>();
    private ArrayList<Integer> fY = new ArrayList<>();
    private int revertX = 0;
    private int revertY = 0;
    private int oldX = 0;
    private int oldY = 0;
    private Integer oldMX = null;
    private Integer oldMY = null;
    private int index = 0;
    private static boolean active = false;
    private static int key;
    private static RenderItem clickArea;
    private List<Button> moveParty = new ArrayList<>();
    private List<Button> menu = new ArrayList<>();
    private List<Button> moveFrame = new ArrayList<>();
    private Button settingsButton;
    private Button goBackButton;
    private List<Widget> smallRender = new ArrayList<>();

    private static boolean isArranging = false;
    private static boolean isMoving = false;

    //public boolean rendered;
    public HoverScreen(int value) {
        super(new TextComponent("Mouse Hover"));
        key = value;

    }

    public static void addClickable(PRectC pRectC) {
        clickArea = pRectC;
    }

    public static boolean arranging() {
        return isArranging;
    }

    public static boolean moving() {
        return isMoving;
    }

    @Override
    protected void init() {
        //TODO: Add 'rearranging' boolean to know when config is in this state or not. helps with 2nd todo.
        int y = Math.max(0, clickArea.t(0) - 10);
        settingsButton = addRenderableWidget(new SmallButton(clickArea.l(0), y, "⚙", p -> doTask(1), Button.NO_TOOLTIP, .5f, .5f, 1f, .5f));
        goBackButton = addRenderableWidget(new SmallButton(clickArea.l(0), y,"x", p -> doTask(1), Button.NO_TOOLTIP, 1f, .5f, .5f));
        initPartyButtons();
        initMenuButtons(y);
        initDragButtons(y);
        //TODO: Add a way to reinitialize buttons in case party comp changes. HoverScreen.reInit();
        //Perhaps force close the screen. Easy!
        doTask(0);
    }

    private void initDragButtons(int y) {
        moveFrame.add(addRenderableWidget(new SmallButton(clickArea.l(0), y, "x", p -> revertPos(), Button.NO_TOOLTIP, 1, .5f, .5f)));
        Button b = addRenderableWidget(new SmallButton(clickArea.l(0)+11, y,"◄", p -> updatePos(true), Button.NO_TOOLTIP, 1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        b = addRenderableWidget(new SmallButton(clickArea.l(0)+22, y, "►", p -> updatePos(false), Button.NO_TOOLTIP,1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        moveFrame.add(addRenderableWidget(new SmallButton(clickArea.l(0)+33, y,"✓", p -> acceptPos(), Button.NO_TOOLTIP, .5f, 1, .5f)));
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
            moveFrame.get(1).active = false;
        else if (index <= fX.size())
            moveFrame.get(1).active = true;

        if (index >= fX.size()-1)
            moveFrame.get(2).active = false;
        else if (index >= 0)
            moveFrame.get(2).active = true;
    }


    private void initMenuButtons(int y) {
        menu.add(addRenderableWidget(new SmallButton(clickArea.l(0), y,"x", p -> doTask(0), Button.NO_TOOLTIP, 1f, .5f, .5f)));
        menu.add(addRenderableWidget(new SmallButton(clickArea.l(0)+11, y,"⬆⬇", p -> doTask(2), Button.NO_TOOLTIP, .5f, .5f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(clickArea.l(0)+22, y,"✥", p -> doTask(3), Button.NO_TOOLTIP, 0, 1, .5f, .5f, 1f)));

    }

    private void revertPos() {
        RenderItem.frameX = revertX;
        RenderItem.frameY = revertY;
        fX.clear();
        fY.clear();
        refreshDragButtons();
        doTask(1);
    }

    private void move(int x, int y) {
        if (oldMX == null) {
            oldMX = x;
            oldMY = y;
            oldX = RenderItem.frameX;
            oldY = RenderItem.frameY;
        }
        RenderItem.frameX = x - oldMX + oldX;
        RenderItem.frameY = y - oldMY + oldY;
        refreshDragButtons();
    }

    private void refreshDragButtons() {
        int y = Math.max(0, clickArea.t(0) - 10);
        for (int i = 0; i < moveFrame.size(); i++) {
            moveFrame.get(i).x = clickArea.l(0)+(i*11);
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
        settingsButton.visible = false;
        goBackButton.visible = false;
        menu.forEach(b -> b.visible = false);
        moveParty.forEach(b -> b.visible = false);
        moveFrame.forEach(b -> b.visible = false);
        switch (task) {
            case 0 -> //Standard screen
                    settingsButton.visible = true;
            case 1 -> //Settings screen
                menu.forEach(b -> b.visible = true);
            case 2 -> { //Arranging screen
                moveParty.forEach(b -> b.visible = true);
                goBackButton.visible = true;
                isArranging = true;
            }
            case 3 -> {
                isMoving = true;
                moveFrame.forEach(b -> b.visible = true);
                revertX = RenderItem.frameX;
                revertY = RenderItem.frameY;
                fX.add(revertX);
                fY.add(revertY);
            }
        }
    }

    protected void initPartyButtons() {
        if (ClientPlayerData.partySize() > 1) {
            Button b;
            //moveParty.add(addRenderableWidget(new Button(clickArea.l(0) + (clickArea.w() >> 1) - 9, clickArea.t(0) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("⬇"), pButton -> ClientPlayerData.swap(finalI, finalI+1))));
            for (int i = 0; i < ClientPlayerData.partySize(); i++) {
                int finalI = i;
                if (i == ClientPlayerData.partySize()-1) {
                    b = addRenderableWidget(new Button(clickArea.r(i)-20, clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▼"), pButton -> {}));
                    b.active = false;
                    moveParty.add(b);
                }
                else
                    moveParty.add(addRenderableWidget(new Button(clickArea.r(i)-20, clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▼"), pButton -> ClientPlayerData.swap(finalI, finalI+1))));

                if (i == 0) {
                    b = addRenderableWidget(new Button(clickArea.l(i) , clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▲"), pButton -> {}));
                    b.active = false;
                    moveParty.add(b);
                }
                else
                    moveParty.add(addRenderableWidget(new Button(clickArea.l(i), clickArea.t(i) + (clickArea.h()>>1) - 10, 20, 20, new TextComponent("▲"), pButton -> ClientPlayerData.swap(finalI-1, finalI))));
                //RenderItem.rectCO(poseStack, 5, 5,  frameX + frameW*i + frameW>>1, frameH + frameH*i + frameH>>1, frameX + frameW*i + frameW>>1, frameH + frameH*i + frameH>>1, 0xFFFFFF, 0xAAAAAA);
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

    public static boolean withinBounds(int left, int top, int right, int bottom, int expand) {
        return mouseX > left - expand && mouseX < right + expand && mouseY > top - expand && mouseY < bottom + expand;
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
        mouseX = -1;
        mouseY = -1;
    }

    public static void activate() {
        active = true;
        mouseX = -1;
        mouseY = -1;
    }

    public static boolean isActive() {
        return active;
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode != key) {
            active = false;
            if (isMoving) {
                revertPos();
                isMoving = false;
            }
            isArranging = false;
            //TODO: Fix when you press escape.

            Minecraft.getInstance().setScreen(null);
        }
        return true;
    }

    private class SmallButton extends Button {

        private float startAlpha = 1f;
        private float r = 1f;
        private float g = 1f;
        private float b = 1f;
        private int offX = 0;
        private int offY = 0;
        public SmallButton(int pX, int pY, String m, OnPress pOnPress, OnTooltip pOnTooltip, float a) {
            super(pX, pY, 10,10, new TextComponent(m), pOnPress, pOnTooltip);
            startAlpha = a;
        }

        public SmallButton(int pX, int pY, String m, OnPress pOnPress, OnTooltip pOnTooltip) {
            super(pX, pY, 10,10, new TextComponent(m), pOnPress, pOnTooltip);
        }

        public SmallButton(int pX, int pY, String m, OnPress pOnPress, OnTooltip pOnTooltip, int offX, int offY) {
            super(pX, pY, 10,10, new TextComponent(m), pOnPress, pOnTooltip);
            this.offX = offX;
            this.offY = offY;
        }

        public SmallButton(int pX, int pY, String m, OnPress pOnPress, OnTooltip pOnTooltip, float r, float g, float b) {
            super(pX, pY, 10,10, new TextComponent(m), pOnPress, pOnTooltip);
            this.r = r;
            this.g = g;
            this.b = b;

        }

        public SmallButton(int pX, int pY, String m, OnPress pOnPress, OnTooltip pOnTooltip, float r, float g, float b, float a) {
            super(pX, pY, 10,10, new TextComponent(m), pOnPress, pOnTooltip);
            this.r = r;
            this.g = g;
            this.b = b;
            this.startAlpha = a;
        }

        public SmallButton(int pX, int pY, String m, OnPress pOnPress, OnTooltip pOnTooltip, int offX, int offY, float r, float g, float b) {
            super(pX, pY, 10,10, new TextComponent(m), pOnPress, pOnTooltip);
            this.offX = offX;
            this.offY = offY;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);

            int i;
            if (this.isHoveredOrFocused()) {
                i = this.getYImage(true);
                RenderSystem.setShaderColor(r, g, b, 1f);
            } else {
                i = this.getYImage(false);
                RenderSystem.setShaderColor(r, g, b, startAlpha);
            }
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            pPoseStack.scale(.5f,.5f,.5f);
            this.blit(pPoseStack, this.x<<1, this.y<<1, 0, 46 + i * 20, 10, 20);
            this.blit(pPoseStack, (this.x<<1) + 10, this.y<<1, 190, 46 + i * 20, 10, 20);
            pPoseStack.scale(2f,2f,2f);
            this.renderBg(pPoseStack, minecraft, pMouseX, pMouseY);
            int j = getFGColor();
            drawCenteredString(pPoseStack, font, this.getMessage(), this.x+5+offX, this.y+offY, j);
            //Might have to add tooltip logic.
        }


    }
}
