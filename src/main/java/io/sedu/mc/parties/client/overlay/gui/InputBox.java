package io.sedu.mc.parties.client.overlay.gui;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static Util.Render.borderRectNoA;
import static Util.Render.sizeRect;

public class InputBox extends AbstractWidget implements Widget, GuiEventListener {

    protected final Font font;
    /** Has the current text being edited on the textbox. */
    private String value = "";
    private int maxLength = 32;
    private int frame;
    /** If this value is true along with isFocused, keyTyped will process the keys. */
    private boolean isEditable = true;
    private boolean shiftPressed;
    /** The current character index that should be used as start of the rendered text. */
    private int displayPos;
    private int cursorPos;
    /** other selection position, may be the same as the cursor */
    private int highlightPos;
    protected final int color;
    /** Called to check if the text is valid */
    protected Predicate<String> filter = Objects::nonNull;
    @Nullable
    private Consumer<String> responder;
    private BiFunction<String, Integer, FormattedCharSequence> formatter = (p_94147_, p_94148_) -> FormattedCharSequence.forward(p_94147_, Style.EMPTY);

    InputBox.OnTextInput onInput;


    public InputBox(int color, Font pFont, int pWidth, int pHeight, Component pMessage, InputBox.OnTextInput onInput, boolean numbersOnly) {
        super(0, 0, pWidth, pHeight, pMessage);
        this.font = pFont;
        this.color = color;
        this.onInput = onInput;
        if (numbersOnly) {
            filter = f -> Objects.nonNull(f) && isNumber(f);
        }
    }
    public InputBox(int maxSize, int color, Font pFont, int pWidth, int pHeight, Component pMessage, InputBox.OnTextInput onInput, boolean numbersOnly) {
        super(0, 0, pWidth, pHeight, pMessage);
        this.maxLength = maxSize;
        this.font = pFont;
        this.color = color;
        this.onInput = onInput;
        if (numbersOnly) {
            filter = f -> Objects.nonNull(f) && isNumber(f);
        }
    }

    protected boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }


    /**
     * Increments the cursor counter
     */
    public void tick() {
        ++this.frame;
    }

    protected MutableComponent createNarrationMessage() {
        Component component = this.getMessage();
        return new TranslatableComponent("gui.narrate.editBox", component, this.value);
    }

    /**
     * Sets the text of the textbox, and moves the cursor to the end.
     */
    public void setValue(String pText) {
        if (this.filter.test(pText)) {
            if (pText.length() > this.maxLength) {
                this.value = pText.substring(0, this.maxLength);
            } else {
                this.value = pText;
            }

            this.moveCursorToEnd();
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(pText);
        }
    }

    /**
     * Returns the contents of the textbox
     */
    public String getValue() {
        return this.value;
    }

    /**
     * returns the text between the cursor and selectionEnd
     */
    public String getHighlighted() {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring(i, j);
    }

    /**
     * Adds the given text after the cursor, or replaces the currently selected text if there is a selection.
     */
    public void insertText(String pTextToWrite) {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        int k = this.maxLength - this.value.length() - (i - j);
        String s = SharedConstants.filterText(pTextToWrite);
        int l = s.length();
        if (k < l) {
            s = s.substring(0, k);
            l = k;
        }

        String s1 = (new StringBuilder(this.value)).replace(i, j, s).toString();
        if (this.filter.test(s1)) {
            this.value = s1;
            this.setCursorPosition(i + l);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(this.value);
            //Addition
            if (!this.value.equals("")) {
                onInput.onInput(this.getValue());
            }
        }
    }


    private void onValueChange(String pNewText) {
        if (this.responder != null) {
            this.responder.accept(pNewText);
        }

    }

    private void deleteText(int pCount) {
        if (Screen.hasControlDown()) {
            this.deleteWords(pCount);
        } else {
            this.deleteChars(pCount);
        }

    }

    /**
     * Deletes the given number of words from the current cursor's position, unless there is currently a selection, in
     * which case the selection is deleted instead.
     */
    public void deleteWords(int pNum) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteChars(this.getWordPosition(pNum) - this.cursorPos);
            }
        }
    }

    /**
     * Deletes the given number of characters from the current cursor's position, unless there is currently a selection,
     * in which case the selection is deleted instead.
     */
    public void deleteChars(int pNum) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int i = this.getCursorPos(pNum);
                int j = Math.min(i, this.cursorPos);
                int k = Math.max(i, this.cursorPos);
                if (j != k) {
                    String s = (new StringBuilder(this.value)).delete(j, k).toString();
                    if (this.filter.test(s)) {
                        this.value = s;
                        this.moveCursorTo(j);
                        //Additions
                        if (!this.value.equals("")) {
                            onInput.onInput(this.getValue());
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the starting index of the word at the specified number of words away from the cursor position.
     */
    public int getWordPosition(int pNumWords) {
        return this.getWordPosition(pNumWords, this.getCursorPosition());
    }

    /**
     * Gets the starting index of the word at a distance of the specified number of words away from the given position.
     */
    private int getWordPosition(int pN, int pPos) {
        return this.getWordPosition(pN, pPos, true);
    }

    /**
     * Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces
     */
    private int getWordPosition(int pN, int pPos, boolean pSkipWs) {
        int i = pPos;
        boolean flag = pN < 0;
        int j = Math.abs(pN);

        for(int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.value.length();
                i = this.value.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while(pSkipWs && i < l && this.value.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while(pSkipWs && i > 0 && this.value.charAt(i - 1) == ' ') {
                    --i;
                }

                while(i > 0 && this.value.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursor(int pDelta) {
        this.moveCursorTo(this.getCursorPos(pDelta));
    }

    private int getCursorPos(int pDelta) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, pDelta);
    }

    /**
     * Sets the current position of the cursor.
     */
    public void moveCursorTo(int pPos) {
        this.setCursorPosition(pPos);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange(this.value);
    }

    public void setCursorPosition(int pPos) {
        this.cursorPos = Mth.clamp(pPos, 0, this.value.length());
    }

    /**
     * Moves the cursor to the very start of this text box.
     */
    public void moveCursorToStart() {
        this.moveCursorTo(0);
    }

    /**
     * Moves the cursor to the very end of this text box.
     */
    public void moveCursorToEnd() {
        this.moveCursorTo(this.value.length());
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (!this.canConsumeInput()) {
            return false;
        } else {
            this.shiftPressed = Screen.hasShiftDown();
            if (Screen.isSelectAll(pKeyCode)) {
                this.moveCursorToEnd();
                this.setHighlightPos(0);
                return true;
            } else if (Screen.isCopy(pKeyCode)) {
                Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                return true;
            } else if (Screen.isPaste(pKeyCode)) {
                if (this.isEditable) {
                    this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                }

                return true;
            } else if (Screen.isCut(pKeyCode)) {
                Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                if (this.isEditable) {
                    this.insertText("");
                }

                return true;
            } else {
                switch(pKeyCode) {
                    case 259:
                        if (this.isEditable) {
                            this.shiftPressed = false;
                            this.deleteText(-1);
                            this.shiftPressed = Screen.hasShiftDown();
                        }

                        return true;
                    case 260:
                    case 264:
                    case 265:
                    case 266:
                    case 267:
                    default:
                        return false;
                    case 261:
                        if (this.isEditable) {
                            this.shiftPressed = false;
                            this.deleteText(1);
                            this.shiftPressed = Screen.hasShiftDown();
                        }

                        return true;
                    case 262:
                        if (Screen.hasControlDown()) {
                            this.moveCursorTo(this.getWordPosition(1));
                        } else {
                            this.moveCursor(1);
                        }

                        return true;
                    case 263:
                        if (Screen.hasControlDown()) {
                            this.moveCursorTo(this.getWordPosition(-1));
                        } else {
                            this.moveCursor(-1);
                        }

                        return true;
                    case 268:
                        this.moveCursorToStart();
                        return true;
                    case 269:
                        this.moveCursorToEnd();
                        return true;
                }
            }
        }
    }

    public boolean canConsumeInput() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (!this.canConsumeInput()) {
            return false;
        } else if (SharedConstants.isAllowedChatCharacter(pCodePoint)) {
            if (this.isEditable) {
                this.insertText(Character.toString(pCodePoint));
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!this.isVisible()) {
            return false;
        } else {
            boolean flag = pMouseX >= (double)this.x && pMouseX < (double)(this.x + this.width) && pMouseY >= (double)this.y && pMouseY < (double)(this.y + this.height);
                this.setFocus(flag);

            if (this.isFocused() && flag && pButton == 0) {
                int i = Mth.floor(pMouseX) - this.x;
                String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
                this.moveCursorTo(this.font.plainSubstrByWidth(s, i).length() + this.displayPos);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Sets focus to this gui element
     */
    public void setFocus(boolean pIsFocused) {
        this.setFocused(pIsFocused);
    }

    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.isVisible()) {
            //Modified Background
            borderRectNoA(pPoseStack.last().pose(), 1, 1, this.x, this.y-1, this.width, 10, (color  & 0xfefefe) >> 1, color);
            sizeRect(pPoseStack.last().pose(), this.x-1, this.y-2, 0, this.width+2, 12,0, color | 64 << 24);

            int i2 = this.isEditable ? this.isFocused() ? 0xFFFF55 : this.color : 0xAAAAAA;
            int j = this.cursorPos - this.displayPos;
            int k = this.highlightPos - this.displayPos;
            String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
            int l = this.x+2;
            int i1 = this.y;
            int j1 = l;
            if (k > s.length()) {
                k = s.length();
            }

            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                RenderSystem.enableDepthTest();
                j1 = this.font.draw(pPoseStack, this.formatter.apply(s1, this.displayPos), (float)l, (float)i1, i2);
            }

            boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
            int k1 = j1;
            if (!flag) {
                k1 = j > 0 ? l + this.width : l;
            } else if (flag2) {
                k1 = j1 - 1;
            }

            if (!s.isEmpty() && flag && j < s.length()) {
                this.font.draw(pPoseStack, this.formatter.apply(s.substring(j), this.cursorPos), (float)j1, (float)i1, i2);
            }

            if (flag1) {
                if (flag2) {
                    GuiComponent.fill(pPoseStack, k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
                } else {
                    this.font.draw(pPoseStack, "_", (float)k1, (float)i1, i2);
                }
            }

            if (k != j) {
                int l1 = l + this.font.width(s.substring(0, k));
                this.renderHighlight(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
            }

        }
    }

    /**
     * Draws the blue selection box.
     */
    private void renderHighlight(int pStartX, int pStartY, int pEndX, int pEndY) {
        if (pStartX < pEndX) {
            int i = pStartX;
            pStartX = pEndX;
            pEndX = i;
        }

        if (pStartY < pEndY) {
            int j = pStartY;
            pStartY = pEndY;
            pEndY = j;
        }

        if (pEndX > this.x + this.width) {
            pEndX = this.x + this.width;
        }

        if (pStartX > this.x + this.width) {
            pStartX = this.x + this.width;
        }

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.vertex((double)pStartX, (double)pEndY, 0.0D).endVertex();
        bufferbuilder.vertex((double)pEndX, (double)pEndY, 0.0D).endVertex();
        bufferbuilder.vertex((double)pEndX, (double)pStartY, 0.0D).endVertex();
        bufferbuilder.vertex((double)pStartX, (double)pStartY, 0.0D).endVertex();
        tesselator.end();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    /**
     * Sets the maximum length for the text in this text box. If the current text is longer than this length, the current
     * text will be trimmed.
     */
    public void setMaxLength(int pLength) {
        this.maxLength = pLength;
        if (this.value.length() > pLength) {
            this.value = this.value.substring(0, pLength);
            this.onValueChange(this.value);
        }

    }

    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    private int getMaxLength() {
        return this.maxLength;
    }

    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition() {
        return this.cursorPos;
    }

    public boolean changeFocus(boolean pFocus) {
        return this.visible && this.isEditable && super.changeFocus(pFocus);
    }

    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return this.visible && pMouseX >= (double)this.x && pMouseX < (double)(this.x + this.width) && pMouseY >= (double)this.y && pMouseY < (double)(this.y + this.height);
    }

    protected void onFocusedChanged(boolean pFocused) {
        if (pFocused) {
            this.frame = 0;
        }
    }

    private boolean isEditable() {
        return this.isEditable;
    }

    /**
     * Sets whether this text box is enabled. Disabled text boxes cannot be typed in.
     */
    public void setEditable(boolean pEnabled) {
        this.isEditable = pEnabled;
    }

    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    public int getInnerWidth() {
        return this.width;
    }

    /**
     * Sets the position of the selection anchor (the selection anchor and the cursor position mark the edges of the
     * selection). If the anchor is set beyond the bounds of the current text, it will be put back inside.
     */
    public void setHighlightPos(int pPosition) {
        int i = this.value.length();
        this.highlightPos = Mth.clamp(pPosition, 0, i);
        if (this.font != null) {
            if (this.displayPos > i) {
                this.displayPos = i;
            }

            int j = this.getInnerWidth();
            String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), j);
            int k = s.length() + this.displayPos;
            if (this.highlightPos == this.displayPos) {
                this.displayPos -= this.font.plainSubstrByWidth(this.value, j, true).length();
            }

            if (this.highlightPos > k) {
                this.displayPos += this.highlightPos - k;
            } else if (this.highlightPos <= this.displayPos) {
                this.displayPos -= this.displayPos - this.highlightPos;
            }

            this.displayPos = Mth.clamp(this.displayPos, 0, i);
        }

    }

    /**
     * returns true if this textbox is visible
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets whether this textbox is visible
     */
    public void setVisible(boolean pIsVisible) {
        this.visible = pIsVisible;
    }

    public void setX(int pX) {
        this.x = pX;
    }

    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, new TranslatableComponent("narration.edit_box", this.getValue()));
    }

    public interface OnTextInput {
        void onInput(String input);
    }
}