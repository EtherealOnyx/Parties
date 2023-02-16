package io.sedu.mc.parties.client.overlay.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ContainerObjectWindowList<E extends ContainerObjectWindowList.Entry<E>> extends AbstractWindowList<E> {
    private boolean hasFocus;

    public ContainerObjectWindowList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight);
    }

    public boolean changeFocus(boolean pFocus) {
        this.hasFocus = super.changeFocus(pFocus);
        if (this.hasFocus) {
            this.ensureVisible(this.getFocused());
        }

        return this.hasFocus;
    }

    public NarratableEntry.NarrationPriority narrationPriority() {
        return this.hasFocus ? NarratableEntry.NarrationPriority.FOCUSED : super.narrationPriority();
    }

    protected boolean isSelectedItem(int pIndex) {
        return false;
    }

    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        E e = this.getHovered();
        if (e != null) {
            e.updateNarration(pNarrationElementOutput.nest());
            this.narrateListElementPosition(pNarrationElementOutput, e);
        } else {
            E e1 = this.getFocused();
            if (e1 != null) {
                e1.updateNarration(pNarrationElementOutput.nest());
                this.narrateListElementPosition(pNarrationElementOutput, e1);
            }
        }

        pNarrationElementOutput.add(NarratedElementType.USAGE, new TranslatableComponent("narration.component_list.usage"));
    }

    @OnlyIn(Dist.CLIENT)
    public abstract static class Entry<E extends ContainerObjectWindowList.Entry<E>> extends AbstractWindowList.Entry<E> implements ContainerEventHandler {
        @Nullable
        private GuiEventListener focused;
        @Nullable
        private NarratableEntry lastNarratable;
        private boolean dragging;

        public boolean isDragging() {
            return this.dragging;
        }

        public void setDragging(boolean pDragging) {
            this.dragging = pDragging;
        }

        public void setFocused(@Nullable GuiEventListener pListener) {
            this.focused = pListener;
        }

        @Nullable
        public GuiEventListener getFocused() {
            return this.focused;
        }

        public abstract List<? extends NarratableEntry> narratables();

        void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            List<? extends NarratableEntry> list = this.narratables();
            Screen.NarratableSearchResult screen$narratablesearchresult = Screen.findNarratableWidget(list, this.lastNarratable);
            if (screen$narratablesearchresult != null) {
                if (screen$narratablesearchresult.priority.isTerminal()) {
                    this.lastNarratable = screen$narratablesearchresult.entry;
                }

                if (list.size() > 1) {
                    pNarrationElementOutput.add(NarratedElementType.POSITION, new TranslatableComponent("narrator.position.object_list", screen$narratablesearchresult.index + 1, list.size()));
                    if (screen$narratablesearchresult.priority == NarratableEntry.NarrationPriority.FOCUSED) {
                        pNarrationElementOutput.add(NarratedElementType.USAGE, new TranslatableComponent("narration.component_list.usage"));
                    }
                }

                screen$narratablesearchresult.entry.updateNarration(pNarrationElementOutput.nest());
            }

        }
    }
}
