package io.sedu.mc.parties.client.overlay.gui;

import Util.Render;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

import java.util.Collections;
import java.util.List;

public class ConfigOptionsList extends AbstractWindowList<ConfigOptionsList.Entry> {
    SettingsScreen s;
    int entryColor;

    //TODO: Combine entry lists

    public ConfigOptionsList(int color, SettingsScreen s, Minecraft pMinecraft, int x, int y, int w, int h) {
        super(pMinecraft, w, h, x, y, 20);
        this.entryColor = color;
        this.s = s;
        //Change x0 and x1
        s.resetTickables();
        for (int i = 0; i < 5; i++) {
            this.addEntry(new ConfigOptionsList.TitleEntry("Test"));
        }
    }



    public void resetPosition(int x, int y, int w, int h) {
        this.width = w;
        this.height = h;
        this.left = x;
        this.top = y;
        this.right = left +w;
        this.bottom = top +h;
        markDirty();
    }

    protected void markDirty() {
        this.children().forEach(entry -> entry.markDirty());
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }


    public abstract class Entry extends ContainerObjectWindowList.Entry<ConfigOptionsList.Entry> {
        Component name;
        boolean isDirty;

        abstract void updateValues(int pTop, int pLeft, int pWidth, int pHeight);

        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            if (isDirty) {
                updateValues(pTop, pLeft, pWidth, pHeight);
                isDirty = false;
            }

            if (pIsMouseOver)
            {
                RenderItem.drawRectHorizontal(pPoseStack.last().pose(), 0, pLeft, pTop, pLeft + pWidth, pTop + pHeight, entryColor | 100 << 24, entryColor);
                ConfigOptionsList.this.minecraft.font.draw(pPoseStack, name, pLeft+10, (float)(pTop + pHeight / 2 - 9 / 2), entryColor);
                ConfigOptionsList.this.minecraft.font.draw(pPoseStack, name, pLeft+10, (float)(pTop + pHeight / 2 - 9 / 2), 0xAAFFFFFF);
            } else {
                ConfigOptionsList.this.minecraft.font.draw(pPoseStack, name, pLeft+10, (float)(pTop + pHeight / 2 - 9 / 2), entryColor);
            }
        }

        public void markDirty() {
            this.isDirty = true;
        }
    }
    public class CheckboxEntry extends ConfigOptionsList.Entry {
        /**
         * The Config value for this specific entry.
         */
        private boolean isEnabled;
        /**
         * The localized key description for entry.
         */
        private SmallButton enable;
        private SmallButton disable;

        CheckboxEntry(final Component pName, boolean isEnabled) {
            this.name = pName;
            this.isEnabled = isEnabled;
            disable = new SmallButton(0, 0, "x", pButton -> updateVal(false), Render.tip(s, "Disable"), 1f, .5f, .5f, .5f);
            enable = new SmallButton(0, 0, "✓", pButton -> updateVal(true), Render.tip(s, "Enable"), .5f, 1f, .5f, .5f);
            enable.visible = !isEnabled;
            disable.visible = isEnabled;
        }

        private void updateVal(boolean enabled) {
            s.finalizeUpdate(name.getContents(), 0, enabled);
            isEnabled = enabled;
            enable.visible = !isEnabled;
            disable.visible = isEnabled;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                public void updateNarration(NarrationElementOutput p_193906_) {
                    p_193906_.add(NarratedElementType.TITLE,ConfigOptionsList.CheckboxEntry.this.name);
                }
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.enable, this.disable);
        }


        @Override
        void updateValues(int pTop, int pLeft, int pWidth, int pHeight) {

        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            super.render(pPoseStack, pIndex, pTop, pLeft, pWidth, pHeight, pMouseX, pMouseY, pIsMouseOver, pPartialTick);

            this.enable.x = pLeft + pWidth - 20;
            this.enable.y = pTop + 3;
            this.disable.x = this.enable.x;
            this.disable.y = pTop + 3;
            this.enable.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.disable.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    }

    public class SliderEntry extends ConfigOptionsList.Entry {
        private final InputBox input;
        private final SliderButton slider;
        private final int lowBound;
        private final int upBound;
        private final int boundWidth;
        private int value;

        SliderEntry(final Component pName, int lowBound, int upBound, int currentValue) {
            this.name = pName;
            slider = new SliderButton(entryColor,5, this::updateVal, this::finalizeVal, Button.NO_TOOLTIP, 1f);
            this.lowBound = lowBound;
            this.upBound = upBound;
            this.boundWidth = upBound - lowBound;
            this.value = currentValue;
            input = new InputBox(entryColor, minecraft.font, 30, 12, pName, this::updateInputVal, true);
            input.setValue(String.valueOf(value));
            s.addTickableEntry(input);
            this.markDirty();
        }

        private void updateVal(float percent) {
            updateActualValue(percent);
            s.triggerUpdate(name.getContents(), 1, value);
            //isEnabled = enabled;
            //enable.visible = !isEnabled;
            //disable.visible = isEnabled;
        }

        private void updateInputVal(String text) {
            int upVal = Mth.clamp(Integer.parseInt(text), lowBound, upBound);
            if (value != upVal) {
                value = upVal;
                slider.updateValue((float) (value - lowBound) / boundWidth);
                s.finalizeUpdate(name.getContents(), 1, value);
            }
            input.setValue(String.valueOf(upVal));
        }

        private void updateActualValue(float percent) {
            value = (int) (lowBound + boundWidth*percent);
            input.setValue(String.valueOf(value));
        }

        private void finalizeVal(float percent) {
            //updateActualValue(percent); TODO: Figure out if this is needed here.
            s.finalizeUpdate(name.getContents(), 1, value);
            //isEnabled = enabled;
            //enable.visible = !isEnabled;
            //disable.visible = isEnabled;
        }


        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                public void updateNarration(NarrationElementOutput p_193906_) {
                    p_193906_.add(NarratedElementType.TITLE,ConfigOptionsList.SliderEntry.this.name);
                }
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.slider, this.input);
        }

        @Override
        void updateValues(int pTop, int pLeft, int pWidth, int pHeight) {
            slider.leftBound = pLeft + Math.max((pWidth>>1) - 50, minecraft.font.width(name)+15);
            slider.rightBound = pLeft + pWidth - 50; //Minus width
            slider.boundWidth = slider.rightBound - slider.leftBound;
            input.x = pLeft + pWidth - 38;
            updateSliderPosition();
        }

        private void updateSliderPosition() {
            slider.updateValue((float) (value - lowBound) / boundWidth);
        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            super.render(pPoseStack, pIndex, pTop, pLeft, pWidth, pHeight, pMouseX, pMouseY, pIsMouseOver, pPartialTick);

            //Render Slider BG

            this.slider.updateX();
            this.slider.y = pTop + 3;
            this.input.y = pTop + 4;
            this.slider.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.input.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    }

    public class HexBoxEntry extends ConfigOptionsList.Entry {
        private final HexBox input;
        private final InputBox r;
        private final InputBox g;
        private final InputBox b;
        private int value;
        private int rI;
        private int gI;
        private int bI;

        HexBoxEntry(final Component pName, int currentValue) {
            this.name = pName;
            this.value = currentValue;
            input = new HexBox(entryColor, minecraft.font, 39, 12, pName, this::updateInputVal);
            if (currentValue == 0)
                input.setValue("");
            else
                input.setValue(Integer.toHexString(currentValue));
            s.addTickableEntry(input);
            r = new InputBox(0xFF8888, minecraft.font, 15, 12, pName, this::updateRVal, true);
            g = new InputBox(0x88FF88, minecraft.font, 15, 12, pName, this::updateGVal, true);
            b = new InputBox(0x8888FF, minecraft.font, 15, 12, pName, this::updateBVal, true);
            updateIndValues();
            s.addTickableEntry(r);
            s.addTickableEntry(g);
            s.addTickableEntry(b);
            this.markDirty();
        }

        private void updateRVal(String text) {
            int upVal = Mth.clamp(Integer.parseInt(text), 0, 255);
            if (rI != upVal) {
                rI = upVal;
                s.finalizeUpdate(name.getContents(), 2, finalizeAndGetValue());
            }
            r.setValue(String.valueOf(upVal));
        }

        private int finalizeAndGetValue() {
            this.value = rI;
            this.value = (value << 8) + gI;
            this.value = (value << 8) + bI;
            updateComValue();
            return value;
        }

        private void updateGVal(String text) {
            int upVal = Mth.clamp(Integer.parseInt(text), 0, 255);
            if (gI != upVal) {
                gI = upVal;
                s.finalizeUpdate(name.getContents(), 2, finalizeAndGetValue());
            }
            g.setValue(String.valueOf(upVal));
        }

        private void updateBVal(String text) {
            int upVal = Mth.clamp(Integer.parseInt(text), 0, 255);
            if (bI != upVal) {
                bI = upVal;
                s.finalizeUpdate(name.getContents(), 2, finalizeAndGetValue());
            }
            b.setValue(String.valueOf(upVal));
        }

        private void updateComValue() {
            input.setValue(Integer.toHexString(value));
        }

        private void updateInputVal(int num) {
            if (value != num) {
                value = num;
                s.finalizeUpdate(name.getContents(), 2, value);
                updateIndValues();
            }
        }

        private void updateIndValues() {
            rI = Render.getRI(value);
            gI = Render.getGI(value);
            bI = Render.getBI(value);
            r.setValue(String.valueOf(rI));
            g.setValue(String.valueOf(gI));
            b.setValue(String.valueOf(bI));
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                public void updateNarration(NarrationElementOutput p_193906_) {
                    p_193906_.add(NarratedElementType.TITLE,ConfigOptionsList.HexBoxEntry.this.name);
                }
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.input, this.r, this.g, this.b);
        }

        @Override
        void updateValues(int pTop, int pLeft, int pWidth, int pHeight) {
            if (HexBox.rgbMode) {
                input.visible = false;
                r.visible = true;
                g.visible = true;
                b.visible = true;
            } else {
                input.visible = true;
                r.visible = false;
                g.visible = false;
                b.visible = false;
            }
            input.x = pLeft + pWidth - 63;
            int inWidth = Math.max(15, pWidth>>3);
            r.setWidth(inWidth);
            g.setWidth(inWidth);
            b.setWidth(inWidth);
            b.x = pLeft + pWidth - 24 - inWidth;
            g.x = b.x - inWidth - 6;
            r.x = g.x - inWidth - 6;
        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            super.render(pPoseStack, pIndex, pTop, pLeft, pWidth, pHeight, pMouseX, pMouseY, pIsMouseOver, pPartialTick);

            //Render Slider BG
            this.input.y = pTop + 4;
            this.input.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.r.y = this.g.y = this.b.y = this.input.y;
            this.r.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.g.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.b.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            Render.sizeRectNoA(pPoseStack.last().pose(), pLeft + pWidth - 20, pTop + 3, 10,10, (entryColor  & 0xfefefe) >> 1, entryColor);
            Render.sizeRectNoA(pPoseStack.last().pose(), pLeft + pWidth - 19, pTop + 4, 8, 8, value);
        }
    }


    public class TitleEntry extends ConfigOptionsList.Entry {
        private int x;

        TitleEntry(String title) {
            this.name = new TranslatableComponent(title);
            this.markDirty();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                public void updateNarration(NarrationElementOutput p_193906_) {
                    p_193906_.add(NarratedElementType.TITLE,ConfigOptionsList.TitleEntry.this.name);
                }
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        void updateValues(int pTop, int pLeft, int pWidth, int pHeight) {
            x = pLeft + ((pWidth - minecraft.font.width(name))>>1);
        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            if (isDirty) {
                updateValues(pTop, pLeft, pWidth, pHeight);
                isDirty = false;
            }
            minecraft.font.draw(pPoseStack, name, x, pTop + 4, entryColor);
            RenderItem.drawRectHorizontal(pPoseStack.last().pose(), 0, pLeft, pTop, pLeft + (pWidth>>1), pTop + 1, entryColor, entryColor | 255 << 24);
            RenderItem.drawRectHorizontal(pPoseStack.last().pose(), 0, pLeft + (pWidth>>1), pTop, pLeft + pWidth, pTop + 1, entryColor | 255 << 24, entryColor);
            RenderItem.drawRectHorizontal(pPoseStack.last().pose(), 0, pLeft, pTop+15, pLeft + (pWidth>>1), pTop + 16, entryColor, entryColor | 255 << 24);
            RenderItem.drawRectHorizontal(pPoseStack.last().pose(), 0, pLeft + (pWidth>>1), pTop+15, pLeft + pWidth, pTop + 16, entryColor | 255 << 24, entryColor);
        }
    }


}
