package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.config.Config;
import io.sedu.mc.parties.client.overlay.*;
import io.sedu.mc.parties.util.ColorUtils;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.util.RenderUtils.*;

public class SettingsScreen extends Screen {
    private final ResourceLocation MENU_LOC = new ResourceLocation("textures/block/deepslate_tiles.png");
    private final ResourceLocation MOD_LOC = new ResourceLocation("textures/block/polished_basalt_side.png");
    public static ResourceLocation INNER_LOC;
    private final ResourceLocation OPTIONS_LOC = new ResourceLocation("textures/block/spruce_log.png");
    private final ResourceLocation SEARCH_LOC = new ResourceLocation("textures/block/deepslate_tiles.png");

    int screenW;
    int screenH;
    int screenX = 0;
    int screenY = 0;

    int eleBoxX;
    int eleBoxY;
    int eleBoxW;
    int eleBoxH;
    int eleOffsetX;
    static int selEle = 0;
    private static int offEle = 0;
    int maxEles = 0;
    HashMap<String, TabButton> tabs = new HashMap<>();
    ArrayList<String> tabsOrder = new ArrayList<>();

    TabButton presetButton;


    private ConfigOptionsList options;
    ArrayList<InputBox> tickables = new ArrayList<>();

    int modBoxX;
    int modBoxY;
    int modBoxW;
    int modBoxH;
    boolean modVisible = false;

    int optBoxX;
    int optBoxY;
    int optBoxW;
    int optBoxH;

    int presetBoxX;
    int presetBoxY;
    int presetBoxW;
    int presetBoxH;

    private static String nameHolder = null;
    private static String descHolder = null;

    Pattern alphaNumeric = Pattern.compile("^[a-zA-Z0-9_-]*$");

    //TODO: Save changes into a new class that tracks the component and the subtype and the value of the change. Disable clearing until they press X

    private final Button left = new ColorButton(0xbb8f44, 0, 0, 20, 20, new TextComponent("◄"), b -> cycleElements(true), tip(this, "Cycle Elements Left"));
    private final Button right = new ColorButton(0xbb8f44, 0, 0, 20, 20, new TextComponent("►"), b -> cycleElements(false), tip(this, "Cycle Elements Right"));
    private final ArrayList<Button> miscButtons;

    private final MutableComponent confirmPrompt;
    private boolean isConfirmed = false;
    public int buttonCooldown = 0;
    public int revertCooldown = 0;
    private boolean triggeredPrompt = false;




    protected SettingsScreen() {
        super(new TextComponent("Party Advanced Settings"));
        miscButtons = new ArrayList<>();
        confirmPrompt = new TextComponent("Are you sure? Click again to confirm").withStyle(ChatFormatting.DARK_RED);
    }

    private void savePreset() {
        Config.saveCompletePreset(nameBox.getValue(), descBox.getValue());
        nameBox.setValue("");
        descBox.setValue("");
        if (selEle == -1) {
            selectButton(-1);
        }
    }

    private void resetAll() {
        if (isConfirmed) {
            RenderItem.setDefaultValues();
            isConfirmed = false;
            revertCooldown = 0;
            ((SmallButton)miscButtons.get(6)).setColor(1f, 1f, .5f);
            miscButtons.get(6).setMessage(new TextComponent("↺"));
            refreshCurrentEle();
        } else {
            miscButtons.get(6).active = false;
            triggeredPrompt = true;
            buttonCooldown = 100;
            revertCooldown = 100;
            SmallButton b = (SmallButton) miscButtons.get(6);
            b.setMessage(new TextComponent("↺").withStyle(ChatFormatting.OBFUSCATED));
            b.setColor(1f, 0.5f, 0.5f);
        }

    }

    private void refreshCurrentEle() {
        selectButton(selEle);
    }

    private void resetEle() {
        if (selEle == -1)
            return;
        RenderItem.setElementDefaults(RenderItem.items.get(tabsOrder.get(selEle)), updater);
        refreshCurrentEle();
    }

    private void toggleEles(boolean b) {
        RenderItem.items.values().forEach(i -> i.setEnabled(b));
        RenderItem.clickArea.setEnabled(true);
        RenderItem.items.computeIfPresent(tabsOrder.get(selEle), (s, renderItem) -> renderItem.setEnabled(true));
    }

    protected final HashMap<String, RenderItem.Update> updater = new HashMap<>();

    private void toggleRGB() {
        HexBox.rgbMode = !HexBox.rgbMode;
        options.markDirty();
    }

    private void toggleModBox(boolean show) {
        if (show) {
            modVisible = true;
            miscButtons.get(1).visible = true;
            miscButtons.get(0).visible = false;
        } else {
            modVisible = false;
            miscButtons.get(1).visible = false;
            miscButtons.get(0).visible = true;
        }
        updateOptionsBounds();
    }

    private void cycleElements(boolean isLeftCycle) {
        removeRenderButtons();
        if (isLeftCycle && left.active) {
            //To prevent errors??

            offEle--;
            addRenderButtons();
            checkArrows();
            return;
        }
        if (!isLeftCycle && right.active) {
            offEle++;
            addRenderButtons();
            checkArrows();
        }
    }

    public void tick() {
        tickables.forEach(InputBox::tick);
        if (triggeredPrompt && buttonCooldown > 0) {
            if (--buttonCooldown == 0) {
                triggeredPrompt = false;
                isConfirmed = true;
                miscButtons.get(6).active = true;
            }
        }
        if (isConfirmed && revertCooldown > 0) {
            if (--revertCooldown == 0) {
                isConfirmed = false;
                ((SmallButton)miscButtons.get(6)).setColor(1f, 1f, .5f);
                miscButtons.get(6).setMessage(new TextComponent("↺"));
            }
        }
    }




    @Override
    public void onClose() {
        ColorUtils.colorCycle = false;
        notEditing = true;
        PHead.icon = null;
        PName.nameTag = null;
        PDimIcon.icon = null;
        GeneralOptions.icon = null;
        PresetOptions.icon = null;
        INNER_LOC = null;
        nameHolder = nameBox.getValue().isEmpty() ? null : nameBox.getValue();
        descHolder = descBox.getValue().isEmpty() ? null : descBox.getValue();
        super.onClose();
    }

    void addTickableEntry(InputBox input) {
        tickables.add(input);
    }

    void resetTickables() {
        tickables.clear();
        tickables.add(nameBox);
        tickables.add(descBox);
    }

    private void removeRenderButtons() {
        tabs.values().forEach(tabButton -> tabButton.visible = false);
    }

    public void render(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (triggeredPrompt) {
            poseStack.translate(0,0,1);
            this.renderTooltip(poseStack, confirmPrompt, pMouseX, pMouseY + 16);
            poseStack.translate(0,0,-1);
        }
        renderFrameOutline(poseStack);
        renderConfig();
        RenderSystem.enableDepthTest();
        this.options.render(poseStack, pMouseX, pMouseY, pPartialTick);
        assert minecraft != null;
        renderElementBox(poseStack);
        //TODO: ModBox
        //if (modVisible)
            //renderModBox();

        //Settings
        renderOptionsBox();

        //Presets
        renderPresetBox();
        font.drawShadow(poseStack, "Save Preset", presetBoxX + (presetBoxW>>1) - 31, presetBoxY + 3, ColorUtils.getRainbowColor());
        font.drawShadow(poseStack, "Name:", nameBox.x - 29, nameBox.y, 0xFFFFFF);
        font.drawShadow(poseStack, "Desc:", descBox.x - 29, descBox.y, 0xFFFFFF);
        renderShadows(poseStack);

        super.render(poseStack, pMouseX, pMouseY, pPartialTick);

    }

    private void renderConfig() {
        //renderBg( 0,0,0,0,0,0, 255, INNER_LOC);
    }

    private void renderPresetBox() {
        renderBg(presetBoxX, presetBoxY, presetBoxX + presetBoxW, presetBoxY + presetBoxH, presetBoxW, presetBoxH, 200, SEARCH_LOC);
    }

    private void renderOptionsBox() {
        renderBg(optBoxX, optBoxY, optBoxX + optBoxW, optBoxY + optBoxH, optBoxW, optBoxH, 255, OPTIONS_LOC);
    }

    private void renderModBox() {
        //RenderItem.drawRect(poseStack.last().pose(), 0,modBoxX - 40, modBoxY, modBoxX + modBoxW, modBoxY + modBoxH, 0x33000000, 0x33000000);
        renderBg(modBoxX, modBoxY, modBoxX + modBoxW, modBoxY + modBoxH, modBoxW, modBoxH, 175, MOD_LOC);
    }

    private void renderElementBox(PoseStack poseStack) {
        //RenderItem.drawRect(poseStack.last().pose(), 0,eleBoxX, eleBoxY, eleBoxX + eleBoxW, eleBoxY + eleBoxH, 0x33000000, 0x33000000);
        renderBg(screenX, screenY, screenX + screenW, screenY + eleBoxH, screenW, eleBoxH, 255, MENU_LOC);

        //With Arrows

    }

    private void renderElementTab(PoseStack poseStack, int i, int offset) {
        //tabs.get(tabsOrder.get(i/*+currentIndex*/)).render(poseStack,minecraft.gui, eleBoxX + offset, eleBoxY, eleBoxX + offset + 32, eleBoxY + 32);
        //RenderItem.drawRectCO(poseStack.last().pose(), 0, eleBoxX + offset, eleBoxY, eleBoxX + offset + 32, eleBoxY + 32, 0xFFFF | (255*(i%2)) <<16, 0x777777);
    }


    private void renderShadows(PoseStack poseStack) {
        //renderBg(screenX + modBoxW, screenY + eleBoxH, screenX + screenW, screenY + screenH, screenW - modBoxW, screenH - eleBoxH, 80);
        //Top Shadow
        RenderUtils.rect(poseStack.last().pose(), 0, screenX, screenY + eleBoxH, screenX + screenW, screenY + eleBoxH+10, 0xAA000000, 0x00000000);
        //Left Shadow
        //TODO: Implement ModBox Later.
        //if (modVisible) {
            //RenderUtils.horizRect(poseStack.last().pose(), 0, screenX + modBoxW, screenY + eleBoxH, screenX + modBoxW + 10, screenY + screenH - presetBoxH, 0xAA000000, 0x00000000);
        //}
        //Bottom Shadow
        RenderUtils.rect(poseStack.last().pose(), 0, screenX, screenY + screenH - 10 - presetBoxH, screenX + screenW, screenY + screenH - presetBoxH, 0x00000000, 0xAA000000);


        //Right Shadow
        RenderUtils.horizRect(poseStack.last().pose(), 0, screenX + screenW - 10 - optBoxW, screenY + eleBoxH, screenX + screenW - optBoxW, screenY + screenH - presetBoxH, 0x00000000, 0xAA000000);
        //RenderItem.drawRect(poseStack.last().pose(), 0, screenX + modBoxW, screenY + eleBoxH, screenX + screenW - optBoxW, screenY + screenH - searchBoxH, 0x66000000, 0x66000000);
    }


    protected void init() {
        initMiscButtons();
        ColorUtils.colorCycle = true;
        RenderItem.initUpdater(updater);
        notEditing = false;
        INNER_LOC = new ResourceLocation("textures/block/deepslate_bricks.png");
        PHead.icon = new ItemStack(Items.PLAYER_HEAD);
        GeneralOptions.icon = new ItemStack(Items.COMPARATOR);
        PresetOptions.icon = new ItemStack(Items.CHEST_MINECART);
        assert Minecraft.getInstance().player != null;
        PHead.icon.addTagElement("SkullOwner", StringTag.valueOf(Minecraft.getInstance().player.getName().getContents()));
        PName.nameTag = Items.NAME_TAG.getDefaultInstance();
        PDimIcon.icon = Items.END_PORTAL_FRAME.getDefaultInstance();

        //Setup Data.
        initTabButtons();
        setBounds(width, height, true);
        super.init();
    }


    private InputBox nameBox;
    private InputBox descBox;
    private void initMiscButtons() {
        miscButtons.add(new ColorButton(0x6536c3, 0, 0, 20, 20, new TextComponent("►"), b -> toggleModBox(true), tip(this, "Show Mod Filters")));
        miscButtons.add(new ColorButton(0x6536c3, 0, 0, 20, 20, new TextComponent("◄"), b -> toggleModBox(false), tip(this, "Hide Mod Filters")));
        miscButtons.add(new SmallButton(0, 0, "c", b -> toggleRGB(), tip(this, "Toggle RGB Input Mode"), 1f, 1f, 1f));
        miscButtons.add(new SmallButton(0, 0, "x", b -> toggleEles(false), tip(this, "Turn All Other Elements Off"), 1f, 0.5f, 0.5f));
        miscButtons.add(new SmallButton(0, 0, "✓", b -> toggleEles(true), tip(this, "Turn All Other Elements On"), 0.5f, 1f, 0.5f));
        miscButtons.add(new SmallButton(0,0, "↺", b -> resetEle(), tip(this, "Reset Current Element to Default"), .5f, 1f, 1f));
        miscButtons.add(new SmallButton(0,0, "↺", b -> resetAll(), tip(this, "Reset Everything to Default"), 1f, 1f, 0.5f));
        miscButtons.add(new SmallButton(0,0, "s", b -> savePreset(), tip(this, "Save Preset"), 0.5f, 1f, 0.5f));
        nameBox = new InputBox(0xFFFFFF, font, 0, 12, new TextComponent("Name"), (s) -> checkSaveFlags(), false);
        descBox = new InputBox(0xFFFFFF, font, 0, 12, new TextComponent("Desc"), (s) -> checkSaveFlags(), false);
        nameBox.filter = s -> alphaNumeric.matcher(s).find();
        nameBox.insertText(nameHolder == null ? "" : nameHolder);
        descBox.insertText(descHolder == null ? "" : descHolder);
        descBox.setMaxLength(128);
        checkSaveFlags();
    }

    private void checkSaveFlags() {
        miscButtons.get(7).active = !nameBox.getValue().isEmpty() && !descBox.getValue().isEmpty();
    }

    private void initTabButtons() {
        int i = 1;
        Iterator<Map.Entry<String, RenderItem>> iter = RenderItem.items.entrySet().iterator();
        Map.Entry<String, RenderItem> item;
        assert minecraft != null;
        //Preset Button
        presetButton = new TabButton(-1, 0, 0, 32, 32, b -> this.selectButton(((TabButton)b).index),
                             RenderUtils.tip(this, new TranslatableComponent("gui.sedparties.name.preset")),
                             new PresetOptions("Load").render((ForgeIngameGui) minecraft.gui), "Load");
        //General Settings
        tabsOrder.add("general");
        tabs.put("general", new TabButton(0, 0, 0, 32, 32, b -> this.selectButton(((TabButton)b).index),
                                          RenderUtils.tip(this, new TranslatableComponent("gui.sedparties.name.general")),
                                          new GeneralOptions("general").render((ForgeIngameGui) minecraft.gui),
                                          "Main"
                                          ));
        while (iter.hasNext()) {
            item = iter.next();
            if (item.getValue().isTabRendered()) {
                tabsOrder.add(item.getKey());
                tabs.put(item.getKey(), new TabButton(i, 0, 0, 32, 32, b -> this.selectButton(((TabButton)b).index),
                                                      RenderUtils.tip(this, new TranslatableComponent(item.getValue().translateName())),
                                                      item.getValue().render((ForgeIngameGui) minecraft.gui),
                                                      item.getValue().getType()

                ));
                i++;
            }
        }
        removeRenderButtons();
        //initRenderButtons();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        tickables.forEach(inputBox -> inputBox.setFocus(false));
        return super.mouseClicked(pMouseX,pMouseY, pButton);
    }

    protected void selectButton(int i) {
        selEle = i;
        removeWidget(options);
        options = null;
        updateOptionsBounds();
    }

    private void initRenderButtons() {
        if (maxEles < tabsOrder.size()) {
            left.x = eleBoxX + 6 + eleOffsetX;
            left.y = eleBoxY + 6;
            right.x = eleBoxX + 6 + (maxEles-1)*32 + eleOffsetX;
            right.y = eleBoxY + 6;
            left.visible = true;
            right.visible = true;
            if (offEle + maxEles - 2 > tabsOrder.size()) {
                offEle = tabsOrder.size() - maxEles + 2;
            }
            addRenderButtons();
            checkArrows();
        } else {
            left.visible = false;
            right.visible = false;
        }
    }

    private void addRenderButtons() {
        Button b;
        for (int i = 0; i < maxEles - 2; i++) {
            b = tabs.get(tabsOrder.get(offEle+i));
            b.x = eleOffsetX + eleBoxX + (i+1)*32;
            b.y = eleBoxY;
            b.visible = true;
        }
    }

    private void checkArrows() {
        if (offEle == 0)
            left.active = false;
        else
            left.active = true;
        if (offEle + maxEles - 2 >= tabsOrder.size()) {
            right.active = false;
        } else
            right.active = true;
    }

    private void setBounds(int width, int height, boolean init) {
        this.screenW = Math.min(width>>1, 320);
        this.screenH = Math.min((height>>2)*3, 300);
        if (width > 720)
            screenX = (width-screenW)>>1;
        else
            screenX = width - screenW - 10;
        screenY = (height-screenH)>>1;


        eleBoxW = screenW - 32;
        maxEles = eleBoxW / 32;
        eleOffsetX = (eleBoxW - (maxEles*32)) / 2;
        eleBoxX = screenX + 32;
        eleBoxY = screenY;
        eleBoxH = Math.min(32, screenH);


        removeRenderButtons();
        initRenderButtons();

        modBoxW = Math.min(32, screenW);
        modBoxX = screenX;
        modBoxY = screenY+32;
        modBoxH = screenH - 32;

        //Show/Hide Mod Box Buttons
        //TODO: Implement ModBox later;
        //miscButtons.get(0).x = screenX + 6;
        //miscButtons.get(1).x = miscButtons.get(0).x;
        //miscButtons.get(0).y = screenY + screenH - 26;
        //miscButtons.get(1).y = miscButtons.get(0).y;
        //miscButtons.get(0).visible = !modVisible;
        //miscButtons.get(1).visible = modVisible;


        optBoxH = screenH - eleBoxH;
        optBoxW = 16;
        optBoxX = screenX + screenW - optBoxW;
        optBoxY = screenY + eleBoxH;

        miscButtons.get(2).x = optBoxX + 3;
        miscButtons.get(2).y = optBoxY + 8;
        miscButtons.get(3).x = miscButtons.get(2).x;
        miscButtons.get(3).y = optBoxY + 24;
        miscButtons.get(4).x = miscButtons.get(2).x;
        miscButtons.get(4).y = optBoxY + 36;
        miscButtons.get(5).x = miscButtons.get(2).x;
        miscButtons.get(5).y = optBoxY + 52;
        miscButtons.get(6).x = miscButtons.get(2).x;
        miscButtons.get(6).y = optBoxY + 64;
        miscButtons.get(7).x = miscButtons.get(2).x;


        presetBoxH = Math.min(32, screenH);
        presetBoxW = screenW;
        presetBoxX = screenX;
        presetBoxY = screenY + screenH - presetBoxH;
        nameBox.setWidth(Math.max(30, screenW/5));
        nameBox.x = presetBoxX + 34;
        nameBox.y = presetBoxY + 15;

        descBox.setWidth(Math.max(50, screenW*2/5));
        descBox.x = optBoxX - descBox.getWidth() - 1;
        descBox.y = presetBoxY + 15;
        miscButtons.get(7).y = descBox.y - 1;
        presetButton.x = screenX;
        presetButton.y = screenY;


        if (init) {
            addRenderableWidget(left);
            addRenderableWidget(right);
            miscButtons.forEach(this::addRenderableWidget);
            tabs.values().forEach(this::addRenderableWidget);
            addRenderableWidget(presetButton);
            addRenderableWidget(nameBox);
            addRenderableWidget(descBox);
            //Forces the text boxes to update the rendered value...
            nameBox.setHighlightPos(nameBox.getValue().length());
            descBox.setHighlightPos(descBox.getValue().length());

            //TODO: Ignore ModBox implementation for now. Implement later.
            removeWidget(miscButtons.get(0));
            removeWidget(miscButtons.get(1));
        }
        updateOptionsBounds();

    }

    private void updateOptionsBounds() {
        if (this.options == null) {
            if (selEle == -1) {
                this.options = presetButton.getOptions(this, minecraft, 0, 0, 0, 0);
                this.options.setItemHeight(28);
            } else {
                this.options = tabs.get(tabsOrder.get(selEle)).getOptions(this, minecraft, 0, 0, 0, 0);
            }

            this.addWidget(this.options);
        }

        if (modVisible)
            this.options.resetPosition(modBoxX+modBoxW, eleBoxY + eleBoxH, screenW - modBoxW - optBoxW, screenH - eleBoxH - presetBoxH);
        else
            this.options.resetPosition(modBoxX, eleBoxY + eleBoxH, screenW - optBoxW, screenH - eleBoxH - presetBoxH);
    }


    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        setBounds(pWidth, pHeight, false);
        this.width = pWidth;
        this.height = pHeight;
    }

    public void finalizeUpdate(String name, Object data, boolean markDirty) {
        Parties.LOGGER.debug("TRIGGERED FINALIZATION UPDATE FOR: " + tabsOrder.get(selEle) + " | " + name + " | " + data);
        triggerUpdate(name, data);
        if (markDirty)
            markDirty();
    }

    public void markDirty() {
        options.markSlidersDirty();
    }

    public void triggerUpdate(String name, Object data) {
        Parties.LOGGER.debug("TRIGGERED UPDATE FOR: " + tabsOrder.get(selEle) + " | " + name + " | " + data);
        updater.get(name).onUpdate(RenderItem.items.get(tabsOrder.get(selEle)), data);
    }
}
