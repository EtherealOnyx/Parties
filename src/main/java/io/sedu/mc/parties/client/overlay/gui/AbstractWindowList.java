package io.sedu.mc.parties.client.overlay.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static io.sedu.mc.parties.client.overlay.gui.SettingsScreen.INNER_LOC;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractWindowList<E extends AbstractWindowList.Entry<E>> extends AbstractContainerEventHandler implements Widget, NarratableEntry {
    protected final Minecraft minecraft;
    protected final int itemHeight;
    private final List<E> children = new AbstractWindowList.TrackedList();
    protected int width;
    protected int height;
    protected int top;
    private boolean renderSelection = true; //TODO: Check usage.
    protected int bottom;
    protected int right;
    protected int left;
    private double scrollAmount;
    private boolean scrolling;
    @Nullable
    private E selected;
    @Nullable
    private E hovered;

    public AbstractWindowList(Minecraft pMinecraft, int pWidth, int pHeight, int x, int y, int pItemHeight) {
        this.minecraft = pMinecraft;
        this.width = pWidth;
        this.height = pHeight;
        this.top = y;
        this.bottom = y + pHeight;
        this.itemHeight = pItemHeight;
        this.left = x;
        this.right = x + pWidth;
    }

    public void setRenderSelection(boolean pRenderSelection) {
        this.renderSelection = pRenderSelection;
    }

    @Nullable
    public E getSelected() {
        return this.selected;
    }

    public void setSelected(@Nullable E pSelected) {
        this.selected = pSelected;
    }

    @Nullable
    public E getFocused() {
        return (E)(super.getFocused());
    }

    public final List<E> children() {
        return this.children;
    }

    protected final void clearEntries() {
        this.children.clear();
    }

    protected void replaceEntries(Collection<E> pEntries) {
        this.children.clear();
        this.children.addAll(pEntries);
    }

    protected E getEntry(int pIndex) {
        return this.children().get(pIndex);
    }

    protected int addEntry(E pEntry) {
        this.children.add(pEntry);
        return this.children.size() - 1;
    }

    protected int getItemCount() {
        return this.children().size();
    }

    protected boolean isSelectedItem(int pIndex) {
        return Objects.equals(this.getSelected(), this.children().get(pIndex));
    }

    @Nullable
    protected final E getEntryAtPosition(double pMouseX, double pMouseY) {
        int i = this.width / 2;
        int j = this.left + this.width / 2;
        int k = j - i;
        int l = j + i;
        int i1 = Mth.floor(pMouseY - (double)this.top) + (int)this.getScrollAmount() - 4;
        int j1 = i1 / this.itemHeight;
        return (E)(pMouseX < (double)this.getScrollbarPosition() && pMouseX >= (double)k && pMouseX <= (double)l && j1 >= 0 && i1 >= 0 && j1 < this.getItemCount() ? this.children().get(j1) : null);
    }

    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight;
    }



    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        int i = this.getScrollbarPosition();
        int j = i + 6;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        this.hovered = this.isMouseOver((double)pMouseX, (double)pMouseY) ? this.getEntryAtPosition((double)pMouseX, (double)pMouseY) : null;
        //Background
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, INNER_LOC);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex((double)this.left, (double)this.bottom, 0.0D).uv((float)this.left / 32.0F, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0F).color(100, 100, 100, 255).endVertex();
        bufferbuilder.vertex((double)this.right, (double)this.bottom, 0.0D).uv((float)this.right / 32.0F, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0F).color(100, 100, 100, 255).endVertex();
        bufferbuilder.vertex((double)this.right, (double)this.top, 0.0D).uv((float)this.right / 32.0F, (float)(this.top + (int)this.getScrollAmount()) / 32.0F).color(100, 100, 100, 255).endVertex();
        bufferbuilder.vertex((double)this.left, (double)this.top, 0.0D).uv((float)this.left / 32.0F, (float)(this.top + (int)this.getScrollAmount()) / 32.0F).color(100, 100, 100, 255).endVertex();
        tesselator.end();

        int j1 = this.getRowLeft();
        int k = this.top + 4 - (int)this.getScrollAmount();


        int k1 = this.getMaxScroll();
        if (k1 > 0) {
            this.renderList(pPoseStack, j1, k, pMouseX, pMouseY, pPartialTick, true);
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            int l1 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            l1 = Mth.clamp(l1, 32, this.bottom - this.top - 8);
            int i2 = (int)this.getScrollAmount() * (this.bottom - this.top - l1) / k1 + this.top;
            if (i2 < this.top) {
                i2 = this.top;
            }

            RenderSystem.enableDepthTest();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferbuilder.vertex((double)i, (double)this.bottom, 5.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex((double)j, (double)this.bottom, 5.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex((double)j, (double)this.top, 5.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex((double)i, (double)this.top, 5.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex((double)i, (double)(i2 + l1), 5.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex((double)j, (double)(i2 + l1), 5.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex((double)j, (double)i2, 5.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex((double)i, (double)i2, 5.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex((double)i, (double)(i2 + l1 - 1), 5.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex((double)(j - 1), (double)(i2 + l1 - 1), 5.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex((double)(j - 1), (double)i2, 5.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex((double)i, (double)i2, 5.0D).color(192, 192, 192, 255).endVertex();
            tesselator.end();
        } else {
            this.renderList(pPoseStack, j1, k, pMouseX, pMouseY, pPartialTick, false);
        }

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    protected void centerScrollOn(E pEntry) {
        this.setScrollAmount((double)(this.children().indexOf(pEntry) * this.itemHeight + this.itemHeight / 2 - (this.bottom - this.top) / 2));
    }

    protected void ensureVisible(E pEntry) {
        int i = this.getRowTop(this.children().indexOf(pEntry));
        int j = i - this.top - 4 - this.itemHeight;
        if (j < 0) {
            this.scroll(j);
        }

        int k = this.bottom - i - this.itemHeight - this.itemHeight;
        if (k < 0) {
            this.scroll(-k);
        }

    }

    private void scroll(int pScroll) {
        this.setScrollAmount(this.getScrollAmount() + (double)pScroll);
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double pScroll) {
        this.scrollAmount = Mth.clamp(pScroll, 0.0D, (double)this.getMaxScroll());
    }

    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
    }

    public int getScrollBottom() {
        return (int)this.getScrollAmount() - this.height;
    }

    protected void updateScrollingState(double pMouseX, double pMouseY, int pButton) {
        this.scrolling = pButton == 0 && pMouseX >= (double)this.getScrollbarPosition() && pMouseX < (double)(this.getScrollbarPosition() + 6);
    }

    protected int getScrollbarPosition() {
        return this.right - 6;
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.updateScrollingState(pMouseX, pMouseY, pButton);
        if (!this.isMouseOver(pMouseX, pMouseY)) {
            return false;
        } else {
            E e = this.getEntryAtPosition(pMouseX, pMouseY);
            if (e != null) {
                if (e.mouseClicked(pMouseX, pMouseY, pButton)) {
                    this.setFocused(e);
                    this.setDragging(true);
                    return true;
                }
            } else if (pButton == 0) {
                return true;
            }

            return this.scrolling;
        }
    }

    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(pMouseX, pMouseY, pButton);
        }

        return false;
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) {
            return true;
        } else if (pButton == 0 && this.scrolling) {
            if (pMouseY < (double)this.top) {
                this.setScrollAmount(0.0D);
            } else if (pMouseY > (double)this.bottom) {
                this.setScrollAmount((double)this.getMaxScroll());
            } else {
                double d0 = (double)Math.max(1, this.getMaxScroll());
                int i = this.bottom - this.top;
                int j = Mth.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
                double d1 = Math.max(1.0D, d0 / (double)(i - j));
                this.setScrollAmount(this.getScrollAmount() + pDragY * d1);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        this.setScrollAmount(this.getScrollAmount() - pDelta * (double)this.itemHeight / 2.0D);
        return true;
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (super.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        } else if (pKeyCode == 264) {
            this.moveSelection(AbstractWindowList.SelectionDirection.DOWN);
            return true;
        } else if (pKeyCode == 265) {
            this.moveSelection(AbstractWindowList.SelectionDirection.UP);
            return true;
        } else {
            return false;
        }
    }

    protected void moveSelection(AbstractWindowList.SelectionDirection pOrdering) {
        this.moveSelection(pOrdering, (p_93510_) -> true);
    }

    protected void refreshSelection() {
        E e = this.getSelected();
        if (e != null) {
            this.setSelected(e);
            this.ensureVisible(e);
        }

    }

    protected void moveSelection(AbstractWindowList.SelectionDirection pOrdering, Predicate<E> pFilter) {
        int i = pOrdering == AbstractWindowList.SelectionDirection.UP ? -1 : 1;
        if (!this.children().isEmpty()) {
            int j = this.children().indexOf(this.getSelected());

            while(true) {
                int k = Mth.clamp(j + i, 0, this.getItemCount() - 1);
                if (j == k) {
                    break;
                }

                E e = this.children().get(k);
                if (pFilter.test(e)) {
                    this.setSelected(e);
                    this.ensureVisible(e);
                    break;
                }

                j = k;
            }
        }

    }

    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return pMouseY >= (double)this.top && pMouseY <= (double)this.bottom && pMouseX >= (double)this.left && pMouseX <= (double)this.right;
    }

    protected void renderList(PoseStack pPoseStack, int pX, int pY, int pMouseX, int pMouseY, float pPartialTick, boolean hasScroll) {
        int i = this.getItemCount();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        int scrollOffset = hasScroll? -6 : 0;
        for(int j = 0; j < i; ++j) {
            int k = this.getRowTop(j);
            int l = this.getRowBottom(j);
            if (l >= this.top && k <= this.bottom) {
                int i1 = pY + j * this.itemHeight;
                int j1 = this.itemHeight - 4;
                E e = this.getEntry(j);
                int k1 = this.width;
                if (this.renderSelection && this.isSelectedItem(j)) {
                    int l1 = this.left + this.width / 2 - k1 / 2;
                    int i2 = this.left + this.width / 2 + k1 / 2;
                    RenderSystem.disableTexture();
                    RenderSystem.setShader(GameRenderer::getPositionShader);
                    float f = this.isFocused() ? 1.0F : 0.5F;
                    RenderSystem.setShaderColor(f, f, f, 1.0F);
                    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                    bufferbuilder.vertex((double)l1, (double)(i1 + j1 + 2), 0.0D).endVertex();
                    bufferbuilder.vertex((double)i2, (double)(i1 + j1 + 2), 0.0D).endVertex();
                    bufferbuilder.vertex((double)i2, (double)(i1 - 2), 0.0D).endVertex();
                    bufferbuilder.vertex((double)l1, (double)(i1 - 2), 0.0D).endVertex();
                    tesselator.end();
                    RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                    bufferbuilder.vertex((double)(l1 + 1), (double)(i1 + j1 + 1), 0.0D).endVertex();
                    bufferbuilder.vertex((double)(i2 - 1), (double)(i1 + j1 + 1), 0.0D).endVertex();
                    bufferbuilder.vertex((double)(i2 - 1), (double)(i1 - 1), 0.0D).endVertex();
                    bufferbuilder.vertex((double)(l1 + 1), (double)(i1 - 1), 0.0D).endVertex();
                    tesselator.end();
                    RenderSystem.enableTexture();
                }

                int j2 = this.getRowLeft();
                RenderSystem.enableDepthTest();
                e.render(pPoseStack, j, k, j2, k1 + scrollOffset, j1, pMouseX, pMouseY, Objects.equals(this.hovered, e), pPartialTick);
            }
        }

    }

    public int getRowLeft() {
        return this.left;
    }

    public int getRowRight() {
        return this.right;
    }

    protected int getRowTop(int pIndex) {
        return this.top + 4 - (int)this.getScrollAmount() + pIndex * this.itemHeight;
    }

    private int getRowBottom(int pIndex) {
        return this.getRowTop(pIndex) + this.itemHeight;
    }

    protected boolean isFocused() {
        return false;
    }

    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        } else {
            return this.hovered != null ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
        }
    }

    @Nullable
    protected E remove(int pIndex) {
        E e = this.children.get(pIndex);
        return (E)(this.removeEntry(this.children.get(pIndex)) ? e : null);
    }

    protected boolean removeEntry(E pEntry) {
        boolean flag = this.children.remove(pEntry);
        if (flag && pEntry == this.getSelected()) {
            this.setSelected((E)null);
        }

        return flag;
    }

    @Nullable
    protected E getHovered() {
        return this.hovered;
    }

    void bindEntryToSelf(AbstractWindowList.Entry<E> pEntry) {
        pEntry.list = this;
    }

    protected void narrateListElementPosition(NarrationElementOutput pNarrationElementOutput, E pEntry) {
        List<E> list = this.children();
        if (list.size() > 1) {
            int i = list.indexOf(pEntry);
            if (i != -1) {
                pNarrationElementOutput.add(NarratedElementType.POSITION, new TranslatableComponent("narrator.position.list", i + 1, list.size()));
            }
        }

    }

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int getTop() { return this.top; }
    public int getBottom() { return this.bottom; }
    public int getLeft() { return this.left; }
    public int getRight() { return this.right; }

    @OnlyIn(Dist.CLIENT)
    public abstract static class Entry<E extends AbstractWindowList.Entry<E>> implements GuiEventListener {
        /** @deprecated */
        @Deprecated
        protected AbstractWindowList<E> list;

        public abstract void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick);

        public boolean isMouseOver(double pMouseX, double pMouseY) {
            return Objects.equals(this.list.getEntryAtPosition(pMouseX, pMouseY), this);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected enum SelectionDirection {
        UP,
        DOWN;
    }

    @OnlyIn(Dist.CLIENT)
    class TrackedList extends AbstractList<E> {
        private final List<E> delegate = Lists.newArrayList();

        public E get(int pIndex) {
            return this.delegate.get(pIndex);
        }

        public int size() {
            return this.delegate.size();
        }

        public E set(int pIndex, E pEntry) {
            E e = this.delegate.set(pIndex, pEntry);
            AbstractWindowList.this.bindEntryToSelf(pEntry);
            return e;
        }

        public void add(int pIndex, E pEntry) {
            this.delegate.add(pIndex, pEntry);
            AbstractWindowList.this.bindEntryToSelf(pEntry);
        }

        public E remove(int pIndex) {
            return this.delegate.remove(pIndex);
        }
    }
}
