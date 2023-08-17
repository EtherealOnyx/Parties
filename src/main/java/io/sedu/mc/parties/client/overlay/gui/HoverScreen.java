package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.helper.ColorAPI;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.PEffects;
import io.sedu.mc.parties.client.overlay.RenderItem;
import io.sedu.mc.parties.data.ClientConfigData;
import io.sedu.mc.parties.mixinaccessors.TrimmedMessagesAccessor;
import net.minecraft.ChatFormatting;
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
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.sedu.mc.parties.client.overlay.ClientPlayerData.playerOrderedList;
import static io.sedu.mc.parties.client.overlay.RenderItem.renderSelfFrame;
import static io.sedu.mc.parties.client.overlay.RenderItem.*;
import static io.sedu.mc.parties.util.RenderUtils.renderSelfFrame;
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
    private static int partyDisplay = 0;
    static boolean notEditing = true;
    private int botLim = 0;
    private int rightLim = 0;
    private boolean outOfBounds = false;
    private boolean enableBoundaries = false;

    public static boolean showInfo = false;

    //public boolean rendered;
    public HoverScreen(int value) {
        super(new TextComponent("Mouse Hover"));
        key = value;
    }

    @Override
    protected void init() {
        draggedItem = -1;
        if (partyDisplay < 2 || (partyDisplay > 4 && playerOrderedList != null && partyDisplay != playerOrderedList.size() - 2)) {
            partyDisplay = Math.max(1, playerOrderedList != null ? playerOrderedList.size() - 1 : 1);
        }
        assert minecraft != null;
        trimmedMessages = ((TrimmedMessagesAccessor) minecraft.gui.getChat()).getTrimmedMessages();
        selfFrameX = (int) Mth.clamp(selfFrameX, 0, width / playerScale - frameEleW);
        selfFrameY = (int) Mth.clamp(selfFrameY,0,  height/ playerScale - frameEleH);
        partyFrameX = (int) Mth.clamp(partyFrameX, 0, width / partyScale - frameEleW);
        partyFrameY = (int) Mth.clamp(partyFrameY,0,  height/ partyScale - frameEleH);
        botLim = (frameEleH + framePosH*(partyDisplay-1));
        rightLim = (frameEleW + framePosW*(partyDisplay-1));
        outOfBounds = partyFrameX > width / partyScale - rightLim || partyFrameY > height/ partyScale - botLim || partyFrameX < 0 || partyFrameY < 0;

        ColorAPI.setColorCycle(true);
        int y = (int) Math.max(0, clickArea.t(0)* playerScale - 10);
        int x = (int) (clickArea.l(0)* playerScale);
        int oY = (int) Math.max(0, clickArea.t(1)* partyScale - 10);
        int oX = (int) (clickArea.l(1)* partyScale);
        settingsButton = addRenderableWidget(new SmallButton(x, y, "⚙", p -> doTask(1), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.partysettings")), 0, .5f, .5f, .5f, 1f));
        partySettingsButton = addRenderableWidget(new SmallButton(oX, oY, "⚙", p -> doTask(1), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.partysettings")), 0, .5f, .5f, .5f, 1f));
        presetButton = addRenderableWidget(new SmallButton(x+11, y, "☰", p -> doTask(5), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.usepreset")), .5f, 1, 0f, 1f, 1f));
        goBackButton = addRenderableWidget(new SmallButton(x, y,"x", p -> doTask(1), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.close")), 1f, .5f, .5f));
        initMenuButtons(x, y, oX, oY);
        initDragButtons((int) ((selfFrameX)* playerScale), (int) Math.max(0, (selfFrameY)* playerScale - 10), (int) ((partyFrameX)* partyScale), (int) Math.max(0, (partyFrameY)* partyScale - 10));
        doTask(0);
    }

    private void initMenuButtons(int x, int y, int oX, int oY) {
        menu.add(addRenderableWidget(new SmallButton(x, y, "x", p -> doTask(0), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.close")), .5f, 0f, 1f, .5f, .5f)));
        menu.add(addRenderableWidget(new SmallButton(x+11, y,"✥", p -> doTask(3), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.move")), 0, 1, .5f, .5f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(x+22, y,"⚙", p -> doTask(4), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.advsettings")), 0, .5f, .5f, 1f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(oX, oY, "x", p -> doTask(0), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.close")), .5f, 0f, 1f, .5f, .5f)));
        menu.add(addRenderableWidget(new SmallButton(oX+11, oY,"✥", p -> doTask(3), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.move")), 0, 1, .5f, .5f, 1f)));
        menu.add(addRenderableWidget(new SmallButton(oX+22, oY,"⚙", p -> doTask(4), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.advsettings")), 0, .5f, .5f, 1f, 1f)));
    }

    private void initDragButtons(int x, int y, int oX, int oY) {
        moveFrame.add(addRenderableWidget(new SmallButton(x, y, "x", p -> revertPos(true), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.rclose")), .5f, 0f, 1, .5f, .5f)));
        moveFrame.add(addRenderableWidget(new SmallButton(x+11, y, "↺", p -> defaultPos(true), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.dclose")),  1, .5f, .5f)));
        Button b = addRenderableWidget(new SmallButton(x+22, y, "◄", p -> updatePos(true, true), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.umove")), 1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        b = addRenderableWidget(new SmallButton(x+33, y, "►", p -> updatePos(false, true), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.rmove")), 1, 1, .5f));
        b.active = false;
        moveFrame.add(b);
        moveFrame.add(addRenderableWidget(new SmallButton(x+44, y, "✓", p -> acceptPos(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.sclose")), .5f, 1, .5f)));
        moveFrame.add(addRenderableWidget(new SpecialButton(x+55, y, getCurrentScale(true), p -> toggleScale(true, true), p -> toggleScale(true, false), triTransTip(this,
                                                                                                                                                                     new TranslatableComponent("gui.sedparties.tooltip.scale"),
                                                                                                                                                                     new TranslatableComponent("gui.sedparties.tooltip.scale-l").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC),
                                                                                                                                                                     new TranslatableComponent("gui.sedparties.tooltip.scale-r").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC)), 0f, 1f, .25f, .75f, 1f)));

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
        partyMoveFrame.add(addRenderableWidget(new SpecialButton(oX+55, oY, partyDisplay + "", p -> cyclePartyDisplay(true), p -> cyclePartyDisplay(false), triTransTip(this,
                                                                                                                                                                        new TranslatableComponent("gui.sedparties.tooltip.pcycle"),
                                                                                                                                                                        new TranslatableComponent("gui.sedparties.tooltip.pcycle-l").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC),
                                                                                                                                                                        new TranslatableComponent("gui.sedparties.tooltip.pcycle-r").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC)), .5f, 1f, .75f, .75f, .75f)));
        partyMoveFrame.add(addRenderableWidget(new SpecialButton(x+66, y, getCurrentScale(false), p -> toggleScale(false, true), p -> toggleScale(false, false), triTransTip(this,
                                                                                                                                                                             new TranslatableComponent("gui.sedparties.tooltip.scale"),
                                                                                                                                                                             new TranslatableComponent("gui.sedparties.tooltip.scale-l").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC),
                                                                                                                                                                             new TranslatableComponent("gui.sedparties.tooltip.scale-r").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC)), 0f, 1f, .25f, .75f, 1f)));
        partyMoveFrame.add(addRenderableWidget(new SmallButton(x+77, y, "▫", p -> toggleLock(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.lock")), 0f, 1f, .25f, .75f, 1f)));
    }

    private void toggleLock() {
        enableBoundaries = !enableBoundaries;
        if (enableBoundaries) {
            partyMoveFrame.get(7).setMessage(new TextComponent("▪"));
            updateLimits();
        } else {
            partyMoveFrame.get(7).setMessage(new TextComponent("▫"));
        }
    }

    private void toggleScale(boolean selfFrame, boolean increasing) {
        float scale = selfFrame ? playerScale : partyScale;
        if (!selfFrame && increasing) {
            enableBoundaries = false;
            partyMoveFrame.get(7).setMessage(new TextComponent("▫"));
        }


        if (selfFrame) {
            if (increasing) {
                if (scale <= .25f) playerScale =  .5f;
                else if (scale <= .5f) playerScale = 1f;
                else if (scale <= 1f) playerScale =  2f;
            } else {
                if (scale <= .5f) playerScale = .25f;
                else if (scale <= 1f) playerScale =  .5f;
                else if (scale <= 2f) playerScale =  1f;
            }


            moveFrame.get(5).setMessage(new TextComponent(getCurrentScale(true)));
            selfFrameX = (int) Mth.clamp(selfFrameX * scale / playerScale, 0, width / playerScale - frameEleW);
            selfFrameY = (int) Mth.clamp(selfFrameY * scale / playerScale, 0, height/ playerScale - frameEleH);

            index = 0;
            fX.clear();
            fY.clear();
            fX.add(selfFrameX);
            fY.add(selfFrameY);
            moveFrame.get(2).active = false;
            moveFrame.get(3).active = false;
            refreshDragButtons(true);
        } else {
            if (increasing) {
                if (scale <= .25f) partyScale =  .5f;
                else if (scale <= .5f) partyScale = 1f;
                else if (scale <= 1f) partyScale =  2f;
            } else {
                if (scale <= .5f) partyScale = .25f;
                else if (scale <= 1f) partyScale =  .5f;
                else if (scale <= 2f) partyScale =  1f;
            }
            partyMoveFrame.get(6).setMessage(new TextComponent(getCurrentScale(false)));
            partyFrameX *= scale / partyScale;
            partyFrameY *= scale / partyScale;
            updateLimits();
            indexP = 0;
            fXP.clear();
            fYP.clear();
            fXP.add(partyFrameX);
            fYP.add(partyFrameY);
            partyMoveFrame.get(2).active = false;
            partyMoveFrame.get(3).active = false;
        }
    }

    private String getCurrentScale(boolean selfFrame) {
        float scale = selfFrame ? playerScale : partyScale;
        if (scale <= .25f) return "¼";
        else if (scale <= .5f) return "½";
        else if (scale <= 1f) return "1";
        else return "2";
    }

    private void cyclePartyDisplay(boolean forward) {
        boolean flag = false; //Only update party display if it changes.
        if (forward) {
            if (partyDisplay < 5 || partyDisplay < (playerOrderedList != null ? playerOrderedList.size() - 1 : 5)) {

                partyDisplay++;
                flag = true;
                enableBoundaries = false;
                partyMoveFrame.get(7).setMessage(new TextComponent("▫"));
            }

        } else {
            if (partyDisplay > 1) {
                flag = true;
                partyDisplay--;
            }
        }
        if (!flag) return;
        partyMoveFrame.get(5).setMessage(new TextComponent(partyDisplay + ""));
        updateLimits();
        indexP = 0;
        fXP.clear();
        fYP.clear();
        fXP.add(partyFrameX);
        fYP.add(partyFrameY);
        partyMoveFrame.get(2).active = false;
        partyMoveFrame.get(3).active = false;


    }

    private void acceptPos() {
        //TODO: One universal button near center of screen?
        if (index != 0) {
            selfFrameX = fX.get(index);
            selfFrameY = fY.get(index);
        }
        if (indexP != 0) {
            partyFrameX = fXP.get(indexP);
            partyFrameY = fYP.get(indexP);
        }
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
            checkIndex(true);
        } else {
            if (back) { //undo
                indexP--;
            } else { //redo
                indexP++;
            }
            partyFrameX = fXP.get(indexP);
            partyFrameY = fYP.get(indexP);
            refreshDragButtons(false);
            checkIndex(false);
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
            if (indexP == 0)
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
            partyFrameX = revertXP;
            partyFrameY = revertYP;
            fXP.clear();
            fYP.clear();
        }
        refreshDragButtons(selfFrame);
        doTask(1);
    }

    private void defaultPos(boolean selfFrame) {
        if (selfFrame) {
            selfFrameX = 8;
            selfFrameY = 16;
            fX.clear();
            fY.clear();

        } else {
            partyFrameX = 16;
            partyFrameY = 192;
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
                oldXP = partyFrameX;
                oldYP = partyFrameY;
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
            if (!enableBoundaries) {
                partyFrameX = x - oldMXP + oldXP;
                partyFrameY = y - oldMYP + oldYP;
                outOfBounds = partyFrameX > width / partyScale - rightLim || partyFrameY > height/ partyScale - botLim || partyFrameX < 0 || partyFrameY < 0;
                return;
            }
            int tempFrame = x - oldMXP + oldXP;
            if (tempFrame < 0) {
                partyFrameX = 0;
            } else if (tempFrame + rightLim > this.width/ partyScale) {
                partyFrameX = (int) (this.width/ partyScale - rightLim);
            } else {
                partyFrameX = x - oldMXP + oldXP;
            }

            tempFrame = y - oldMYP + oldYP;
            if (tempFrame < 0) {
                partyFrameY = 0;
            } else if (tempFrame + botLim > this.height/ partyScale) {
                partyFrameY = (int) (this.height/ partyScale - botLim);
            } else {
                partyFrameY = y - oldMYP + oldYP;
            }
        }

    }


    private void refreshDragButtons(boolean selfFrame) {
        if (selfFrame) {
            int x = (int) Mth.clamp(selfFrameX* playerScale, 0, this.width - 66);
            int y = (int) Mth.clamp(selfFrameY* playerScale - 10, 0, this.height - 10);
            for (int i = 0; i < moveFrame.size(); i++) {
                moveFrame.get(i).x = x+(i*11);
                moveFrame.get(i).y = y;
            }
        } else {
            int x = (int) Mth.clamp(partyFrameX * partyScale, 0, this.width - 88);
            int y = (int) Mth.clamp(partyFrameY * partyScale - 10, 0, this.height - 10);
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
            checkIndex(true);
        } else {
            draggedItem = -1;
            oldMYP = null;
            oldMXP = null;
            if (indexP >= fXP.size() || (fXP.get(indexP) == partyFrameX && fYP.get(indexP) == partyFrameY))
                return;

            indexP++;
            if (indexP != fXP.size()) {
                fXP = new ArrayList<>(fXP.subList(0, indexP));
                fYP = new ArrayList<>(fYP.subList(0, indexP));
            }
            fXP.add(partyFrameX);
            fYP.add(partyFrameY);
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
                notEditing = false;
            }

            case 2 -> { //Arranging screen
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
                revertXP = partyFrameX;
                revertYP = partyFrameY;
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
                updateLimits();
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

    private void updateLimits() {
        botLim = (frameEleH + framePosH*(partyDisplay-1));
        rightLim = (frameEleW + framePosW*(partyDisplay-1));
        if (enableBoundaries) {
            partyFrameX = (int) Mth.clamp(partyFrameX, 0, width / partyScale - rightLim);
            partyFrameY = (int) Mth.clamp(partyFrameY, 0, height/ partyScale - botLim);
        }

        outOfBounds = partyFrameX > width / partyScale - rightLim || partyFrameY > height/ partyScale - botLim || partyFrameX < 0 || partyFrameY < 0;

        refreshDragButtons(false);
    }


    public void render(PoseStack poseStack, int mX, int mY, float partialTick) {

        super.render(poseStack, mX, mY, partialTick);

        checkFrameRender(poseStack);

        if (isDragging()) {
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
        if (isMoving || isArranging) return;
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
        partyMouseX -= partyFrameX;
        partyMouseY -= partyFrameY;


        for (int i = 0; i < partyDisplay; i++) {
            if (partyMouseX < 0 || partyMouseY < 0) return;
            if (partyMouseX < frameEleW && partyMouseY < frameEleH) {
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
            renderPartyFrame(poseStack, outOfBounds);
            if (draggedItem == 2) {
                renderPartyFrameOutline(poseStack, outOfBounds);
            }
        }
    }

    public static int getPartyDisplay() {
        return partyDisplay;
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

    public static boolean notEditing() {
        return notEditing;
    }



    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 341 || pKeyCode == 345) {
            showInfo = !showInfo;
            return true;
        }
        if (pKeyCode != key) {
            this.onClose();
            Minecraft.getInstance().setScreen(null);
        }
        return true;
    }

    @Override
    public void onClose() {
        ColorAPI.setColorCycle(false);
        notEditing = false;
        if (isMoving) {
            revertPos(true);
            revertPos(false);
            isMoving = false;
        }
        draggedItem = -1;
        isArranging = false;
        Parties.LOGGER.debug("Saving frame position and scale...");
        saveValue(ClientConfigData.xPos, selfFrameX);
        saveValue(ClientConfigData.yPos, selfFrameY);
        saveValue(ClientConfigData.xPosParty, partyFrameX);
        saveValue(ClientConfigData.yPosParty, partyFrameY);
        saveValue(ClientConfigData.scale, (double) playerScale);
        saveValue(ClientConfigData.partyScale, (double) partyScale);
        showInfo = false;
        super.onClose();
    }

    private <T> void saveValue(ForgeConfigSpec.ConfigValue<T> configValue , T value) {
        if (!configValue.get().equals(value)) {
            configValue.set(value);
            configValue.save();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}