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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigOptionsList extends AbstractWindowList<ConfigOptionsList.Entry> {
    SettingsScreen s;
    EntryColor entryColor;
    ArrayList<SliderEntry> sliders = new ArrayList<>();
    private boolean parsing;

    public interface EntryColor {
        int getColor();
    }

    //TODO: Combine entry lists

    public ConfigOptionsList(EntryColor color, SettingsScreen s, Minecraft pMinecraft, int x, int y, int w, int h, boolean parsing) {
        super(pMinecraft, w, h, x, y, 20);
        this.entryColor = color;
        this.s = s;
        //Change x0 and x1
        s.resetTickables();
        this.parsing = parsing;
    }

    public Entry addTitleEntry(String title) {
        Entry e = new ConfigOptionsList.TitleEntry(title);
        this.addEntry(e);
        return e;
    }

    public Entry addBooleanEntry(String name, boolean defaultState) {
        Entry e = new ConfigOptionsList.CheckboxEntry(name, defaultState);
        this.addEntry(e);
        return e;
    }

    public Entry addBooleanEntry(String name, boolean defaultState, Entry.OuterUpdate toggleLimSliders) {
        Entry e = new ConfigOptionsList.CheckboxEntry(name, defaultState);
        e.outerUpdate = toggleLimSliders;
        this.addEntry(e);
        return e;
    }

    public Entry addSliderEntry(String name, int lowBound, SliderEntry.Bound upBound, int defaultState) {
        SliderEntry e = new ConfigOptionsList.SliderEntry(name, lowBound, upBound, defaultState);
        this.addEntry(e);
        sliders.add(e);
        return e;
    }

    public void addSliderEntry(String name, int lowBound, SliderEntry.Bound upBound, int defaultState, boolean doesRefresh) {
        SliderEntry e = new ConfigOptionsList.SliderEntry(name, lowBound, upBound, defaultState, doesRefresh);
        this.addEntry(e);
        sliders.add(e);
    }

    public SliderEntry addSliderWithUpdater(String name, int lowBound, SliderEntry.Bound upBound, int defaultState, Entry.OuterUpdate sliderUpdate, boolean refresh) {
        SliderEntry e = new ConfigOptionsList.SliderEntry(name, lowBound, upBound, defaultState, refresh);
        e.outerUpdate = sliderUpdate;
        this.addEntry(e);
        return e;
    }

    public void addColorEntry(String name, int defaultState) {
        Entry e = new ConfigOptionsList.HexBoxEntry(name, defaultState);
        this.addEntry(e);
    }

    public void addSpaceEntry() {
        this.addEntry(new ConfigOptionsList.SpaceEntry());
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
        this.children().forEach(Entry::markDirty);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    public void markSlidersDirty() {
        sliders.forEach(Entry::updateValues);
    }


    public abstract class Entry extends ContainerObjectWindowList.Entry<ConfigOptionsList.Entry> {
        String internal = "";
        Component name;
        boolean isDirty;
        boolean doesRefresh = false;
        OuterUpdate outerUpdate;

        public void setVisible(boolean enabled) {
            if (parsing) return;
            toggle(enabled);
        }

        abstract void toggle(boolean enabled);

        public interface OuterUpdate {
            void outerUpdate();
        }

        abstract void updateValues(int pTop, int pLeft, int pWidth, int pHeight);
        abstract void updateValues();

        void updateData(Object data) {

        }

        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            if (isDirty) {
                updateValues(pTop, pLeft, pWidth, pHeight);
                isDirty = false;
            }

            if (pIsMouseOver)
            {
                Render.horizRect(pPoseStack.last().pose(), 0, pLeft, pTop, pLeft + pWidth, pTop + pHeight, entryColor.getColor() | 100 << 24, entryColor.getColor());
                ConfigOptionsList.this.minecraft.font.draw(pPoseStack, name, pLeft+10, (float)(pTop + pHeight / 2 - 9 / 2), entryColor.getColor());
                ConfigOptionsList.this.minecraft.font.draw(pPoseStack, name, pLeft+10, (float)(pTop + pHeight / 2 - 9 / 2), 0xAAFFFFFF);
            } else {
                ConfigOptionsList.this.minecraft.font.draw(pPoseStack, name, pLeft+10, (float)(pTop + pHeight / 2 - 9 / 2), entryColor.getColor());
            }
        }

        public void markDirty() {
            this.isDirty = true;
        }

        protected String getConfigName() {
            return internal;
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

        CheckboxEntry(String name, boolean isEnabled) {
            if (parsing) {
                RenderItem.parser.add(name);
                return;
            }
            this.name = new TranslatableComponent("config.sedparties.name." + name);
            this.isEnabled = isEnabled;
            disable = new SmallButton(0, 0, "✓", pButton -> updateVal(false), Render.tip(s, "Enabled"), .5f, 1f, .5f, .5f);
            enable = new SmallButton(0, 0, "x", pButton -> updateVal(true), Render.tip(s, "Disabled"), 1f, .5f, .5f, .5f);
            enable.visible = !isEnabled;
            disable.visible = isEnabled;
            internal = name;
        }

        @Override
        void toggle(boolean enabled) {
            enable.active = enabled;
            disable.active = enabled;
        }

        private void updateVal(boolean enabled) {
            s.finalizeUpdate(getConfigName(), enabled, doesRefresh);
            if (outerUpdate != null) outerUpdate.outerUpdate();
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
        void updateValues() {

        }

        @Override
        void updateData(Object data) {
            updateVal((Boolean) data);
        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            super.render(pPoseStack, pIndex, pTop, pLeft, pWidth, pHeight, pMouseX, pMouseY, pIsMouseOver, pPartialTick);

            this.enable.x = pLeft + pWidth - 16;
            this.enable.y = pTop + 3;
            this.disable.x = this.enable.x;
            this.disable.y = pTop + 3;
            this.enable.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.disable.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    }

    public class SliderEntry extends ConfigOptionsList.Entry {
        private InputBox input;
        private SliderButton slider;
        private int lowBound;
        private int upBound;
        private int boundWidth;
        private int value;
        private Bound maxBound;

        public interface Bound {
            int updateBound();
        }

        SliderEntry(String name, int lowBound, Bound maxBound, int currentValue) {
            if (parsing) {
                RenderItem.parser.add(name);
                return;
            }
            this.name = new TranslatableComponent("config.sedparties.name." + name);
            slider = new SliderButton(0xFFFFFF,5, this::updateVal, this::finalizeVal, Button.NO_TOOLTIP, 1f);
            this.lowBound = lowBound;
            this.maxBound = maxBound;
            this.value = currentValue;
            this.upBound = maxBound.updateBound();
            this.boundWidth = upBound - lowBound;
            input = new InputBox(0xFFFFFF, minecraft.font, 30, 12, this.name, this::updateInputVal, true);
            s.addTickableEntry(input);
            this.markDirty();
            internal = name;
        }


        SliderEntry(String name, int lowBound, Bound maxBound, int currentValue, boolean doesRefresh) {
            this(name, lowBound, maxBound, currentValue);
            this.doesRefresh = doesRefresh;
        }

        @Override
        void toggle(boolean enabled) {
            input.visible = enabled;
            slider.active = enabled;
        }

        private void updateVal(float percent) {
            if (updateActualValue(percent)) {
                s.triggerUpdate(getConfigName(), value);
                if (outerUpdate != null)
                    outerUpdate.outerUpdate();
            }
        }

        private void updateInputVal(String text) {
            int upVal = Mth.clamp(Integer.parseInt(text), lowBound, upBound);
            if (value != upVal) {
                value = upVal;
                slider.updateValue((float) (value - lowBound) / boundWidth);
                s.finalizeUpdate(getConfigName(), value, doesRefresh);
            }
            input.setValue(String.valueOf(upVal));
        }

        private boolean updateActualValue(float percent) {
            int upVal = Math.round(lowBound + boundWidth*percent);
            if (value != upVal) {
                value = upVal;
                input.setValue(String.valueOf(value));
                return true;
            }
            return false;
        }

        private void finalizeVal(float percent) {
            //updateActualValue(percent); TODO: Figure out if this is needed here.
            s.finalizeUpdate(getConfigName(), value, doesRefresh);
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
            updateValues();
        }

        @Override
        void updateValues() {
            this.upBound = maxBound.updateBound();
            this.boundWidth = upBound - lowBound;
            slider.visible = this.boundWidth > 0;
            updateData(value);
            updateSliderPosition();
        }

        public SliderEntry forceUpdate(int data) {
            this.upBound = maxBound.updateBound();
            this.boundWidth = upBound - lowBound;
            slider.visible = this.boundWidth > 0;
            this.value = data;
            input.setValue(String.valueOf(data));
            updateSliderPosition();
            return this;
        }

        @Override
        void updateData(Object data) {
            int upVal = Mth.clamp((int) data, lowBound, upBound);
            if (value != upVal) {
                value = upVal;
                slider.updateValue((float) (value - lowBound) / boundWidth);
                s.finalizeUpdate(getConfigName(), value, doesRefresh);
            }
            input.setValue(String.valueOf(upVal));
        }

        private void updateSliderPosition() {
            slider.updateValue((float) (value - lowBound) / boundWidth);
        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            super.render(pPoseStack, pIndex, pTop, pLeft, pWidth, pHeight, pMouseX, pMouseY, pIsMouseOver, pPartialTick);

            //Render Slider BG

            this.slider.updateX();
            this.slider.color = entryColor.getColor();
            this.slider.y = pTop + 3;
            this.input.y = pTop + 4;
            this.slider.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.input.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    }

    public class HexBoxEntry extends ConfigOptionsList.Entry {
        private HexBox input;
        private InputBox r;
        private InputBox g;
        private InputBox b;
        private int value;
        private int rI;
        private int gI;
        private int bI;

        HexBoxEntry(String pName, int currentValue) {
            if (parsing) {
                RenderItem.parser.add(pName);
                return;
            }
            this.name = new TranslatableComponent("config.sedparties.name." + pName);
            r = new InputBox(0xFF8888, minecraft.font, 15, 12, name, this::updateRVal, true);
            g = new InputBox(0x88FF88, minecraft.font, 15, 12, name, this::updateGVal, true);
            b = new InputBox(0x8888FF, minecraft.font, 15, 12, name, this::updateBVal, true);
            this.value = currentValue;
            updateIndValues();
            input = new HexBox(0xFFFFFF, minecraft.font, 39, 12, name, this::updateInputVal);
            if (currentValue == 0)
                input.setValue("");
            else
                finalizeAndGetValue();
            s.addTickableEntry(input);
            s.addTickableEntry(r);
            s.addTickableEntry(g);
            s.addTickableEntry(b);
            this.markDirty();
            internal = pName;
        }

        private void updateRVal(String text) {
            int upVal = Mth.clamp(Integer.parseInt(text), 0, 255);
            if (rI != upVal) {
                rI = upVal;
                s.finalizeUpdate(getConfigName(), finalizeAndGetValue(), doesRefresh);
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
                s.finalizeUpdate(getConfigName(), finalizeAndGetValue(), doesRefresh);
            }
            g.setValue(String.valueOf(upVal));
        }

        private void updateBVal(String text) {
            int upVal = Mth.clamp(Integer.parseInt(text), 0, 255);
            if (bI != upVal) {
                bI = upVal;
                s.finalizeUpdate(getConfigName(), finalizeAndGetValue(), doesRefresh);
            }
            b.setValue(String.valueOf(upVal));
        }

        private void updateComValue() {
            input.setValue(Integer.toHexString(value));
        }

        private void updateInputVal(int num) {
            if (value != num) {
                value = num;
                s.finalizeUpdate(getConfigName(), value, doesRefresh);
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
        void toggle(boolean enabled) {
            input.visible = enabled;
            r.visible = enabled;
            g.visible = enabled;
            b.visible = enabled;
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
            input.x = pLeft + pWidth - 60;
            int inWidth = Math.max(15, pWidth>>3);
            r.setWidth(inWidth);
            g.setWidth(inWidth);
            b.setWidth(inWidth);
            b.x = pLeft + pWidth - 21 - inWidth;
            g.x = b.x - inWidth - 6;
            r.x = g.x - inWidth - 6;
        }

        @Override
        void updateValues() {

        }

        @Override
        void updateData(Object data) {
            value = (int) data;
            updateIndValues();
            updateComValue();
        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            super.render(pPoseStack, pIndex, pTop, pLeft, pWidth, pHeight, pMouseX, pMouseY, pIsMouseOver, pPartialTick);

            //Render Slider BG
            int color = entryColor.getColor();
            this.input.y = pTop + 4;
            this.input.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.r.y = this.g.y = this.b.y = this.input.y;
            this.r.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.g.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.b.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            Render.sizeRectNoA(pPoseStack.last().pose(), pLeft + pWidth - 16, pTop + 3, 0, 10,10, (color  & 0xfefefe) >> 1, color);
            Render.sizeRectNoA(pPoseStack.last().pose(), pLeft + pWidth - 15, pTop + 4, 8, 8, value);
        }
    }


    public class TitleEntry extends ConfigOptionsList.Entry {
        private int x;

        TitleEntry(String title) {
            this.name = new TranslatableComponent("config.sedparties.title." + title);
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
        void toggle(boolean enabled) {

        }

        @Override
        void updateValues(int pTop, int pLeft, int pWidth, int pHeight) {
            x = pLeft + ((pWidth - minecraft.font.width(name))>>1);
        }

        @Override
        void updateValues() {

        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            if (isDirty) {
                updateValues(pTop, pLeft, pWidth, pHeight);
                isDirty = false;
            }
            minecraft.font.draw(pPoseStack, name, x, pTop + 4, entryColor.getColor());
            Render.horizRect(pPoseStack.last().pose(), 0, pLeft, pTop, pLeft + (pWidth>>1), pTop + 1, entryColor.getColor(), entryColor.getColor() | 255 << 24);
            Render.horizRect(pPoseStack.last().pose(), 0, pLeft + (pWidth>>1), pTop, pLeft + pWidth, pTop + 1, entryColor.getColor() | 255 << 24, entryColor.getColor());
            Render.horizRect(pPoseStack.last().pose(), 0, pLeft, pTop+15, pLeft + (pWidth>>1), pTop + 16, entryColor.getColor(), entryColor.getColor() | 255 << 24);
            Render.horizRect(pPoseStack.last().pose(), 0, pLeft + (pWidth>>1), pTop+15, pLeft + pWidth, pTop + 16, entryColor.getColor() | 255 << 24, entryColor.getColor());
        }
    }

    public class SpaceEntry extends ConfigOptionsList.Entry { ;

        SpaceEntry() {
            this.name = new TextComponent("");

        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarrationPriority.NONE;
                }

                public void updateNarration(NarrationElementOutput p_193906_) {
                }
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        void toggle(boolean enabled) {

        }

        @Override
        void updateValues(int pTop, int pLeft, int pWidth, int pHeight) {

        }

        @Override
        void updateValues() {

        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
        }
    }


}
