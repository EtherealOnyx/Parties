package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.Config;
import io.sedu.mc.parties.client.overlay.*;
import io.sedu.mc.parties.network.PartiesPacketHandler;
import io.sedu.mc.parties.network.StringPacketData;
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

    int selBoxX;
    int selBoxY;
    int selBoxW;
    int selBoxH;
    boolean renderSelBox = false;

    private static String nameHolder = null;
    private static String descHolder = null;

    Pattern alphaNumeric = Pattern.compile("^[a-zA-Z0-9_-]*$");

    private final ArrayList<Button> miscButtons;

    private final MutableComponent confirmPrompt;
    private boolean isConfirmed = false;
    public int buttonCooldown = 0;
    public boolean sentMessage = false;
    public int messageCooldown = 0;
    public int revertCooldown = 0;
    private boolean triggeredPrompt = false;




    protected SettingsScreen() {
        super(new TextComponent("Party Advanced Settings"));
        miscButtons = new ArrayList<>();
        confirmPrompt = new TextComponent("Are you sure? Click again to confirm").withStyle(ChatFormatting.DARK_RED);
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
        if (selEle == 0)
            RenderItem.getGeneralDefaults().forEachEntry((s, v) -> updater.get(s.getName()).onUpdate(null, v));
        else
            RenderItem.setElementDefaults(RenderItem.items.get(tabsOrder.get(selEle)), updater);
        refreshCurrentEle();
    }

    private void toggleEles(boolean b) {
        RenderItem.items.values().forEach(i -> i.setEnabled(b));
        RenderItem.clickArea.setEnabled(true);
        RenderItem.items.computeIfPresent(tabsOrder.get(selEle), (s, renderItem) -> renderItem.setEnabled(true));
    }

    protected final HashMap<String, RenderItem.Update> updater = new HashMap<>();
    protected final HashMap<String, RenderItem.Getter> getter = new HashMap<>();

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

        if (sentMessage && messageCooldown-- < 0) {
            sentMessage = false;
            miscButtons.get(10).active = true;
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

    public void render(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (triggeredPrompt) {
            poseStack.translate(0,0,1);
            this.renderTooltip(poseStack, confirmPrompt, pMouseX, pMouseY + 16);
            poseStack.translate(0,0,-1);
        }
        renderFrameOutline(poseStack);
        renderBg(-5, screenX, screenY, screenX + screenW, screenY + eleBoxH, screenW, eleBoxH, 200, MENU_LOC);
        if (renderSelBox)
            renderSelection(poseStack);
        RenderSystem.enableDepthTest();
        this.options.render(poseStack, pMouseX, pMouseY, pPartialTick);
        assert minecraft != null;
        RenderUtils.offRectNoA(poseStack.last().pose(), screenX, screenY, -1, -2, screenW, screenH, ColorUtils.getRainbowColor(), 0x232323);
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

    private void renderSelection(PoseStack poseStack) {
        RenderUtils.borderRectNoA(poseStack.last().pose(), 0, 1, selBoxX, selBoxY, selBoxW, selBoxH, ColorUtils.getRainbowColor());
    }


    private void renderPresetBox() {
        renderBg(presetBoxX, presetBoxY, presetBoxX + presetBoxW, presetBoxY + presetBoxH, presetBoxW, presetBoxH, 200, SEARCH_LOC);
    }

    private void renderOptionsBox() {
        renderBg(optBoxX, optBoxY, optBoxX + optBoxW, optBoxY + optBoxH, optBoxW, optBoxH, 255, OPTIONS_LOC);
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
        RenderItem.initGetter(getter);
        notEditing = false;
        //INNER_LOC = tabs.get(tabsOrder.get(selEle)).getInnerBg();
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
        miscButtons.add(new SmallButton(0, 0, "c", b -> toggleRGB(), tip(this, "Toggle RGB Input Mode"), 0.5f, 0.5f, 0.5f));
        miscButtons.add(new SmallButton(0, 0, "x", b -> toggleEles(false), tip(this, "Turn All Other Elements Off"), 1f, 0.5f, 0.5f));
        miscButtons.add(new SmallButton(0, 0, "✓", b -> toggleEles(true), tip(this, "Turn All Other Elements On"), 0.5f, 1f, 0.5f));
        miscButtons.add(new SmallButton(0,0, "↺", b -> resetEle(), tip(this, "Reset Current Element to Default"), .5f, 1f, 1f));
        miscButtons.add(new SmallButton(0,0, "↺", b -> resetAll(), tip(this, "Reset Everything to Default"), 1f, 1f, 0.5f));
        miscButtons.add(new SmallButton(0,0, "s", b -> savePreset(), tip(this, "Save Preset"), 0.5f, 1f, 0.5f));
        miscButtons.add(new SmallButton(0,0, "c", b -> copyPreset(), tip(this, "Copy Preset to Clipboard"), 1f, 0.5f, 1f));
        miscButtons.add(new SmallButton(0,0, "p", b -> pastePreset(), tip(this, "Load Preset from Clipboard"), 1f, 0.5f, 1f));
        miscButtons.add(new SmallButton(0, 0, "✎", b -> sendPresetToChat(), tip(this, "Link Preset to Chat"), 0, 1, 1f, 0.5f, 1f));
        nameBox = new InputBox(0xFFFFFF, font, 0, 12, new TextComponent("Name"), (s) -> checkSaveFlags(), false);
        descBox = new InputBox(0xFFFFFF, font, 0, 12, new TextComponent("Desc"), (s) -> checkSaveFlags(), false);
        nameBox.filter = s -> alphaNumeric.matcher(s).find();
        nameBox.insertText(nameHolder == null ? "" : nameHolder);
        descBox.insertText(descHolder == null ? "" : descHolder);
        descBox.setMaxLength(128);
        checkSaveFlags();
    }

    private void sendPresetToChat() {
        if (sentMessage) return;
        PartiesPacketHandler.sendToServer(new StringPacketData(0, Config.getPresetString(minecraft, getter)));
        sentMessage = true;
        messageCooldown = 300;
        miscButtons.get(10).active = false;
    }

    private void pastePreset() {
        assert minecraft != null;
        if (Config.pastePreset(minecraft, updater)) {
            minecraft.player.displayClientMessage(new TextComponent("Preset loaded from clipboard successfully.").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC), true);
        } else {
            minecraft.player.displayClientMessage(new TextComponent("The clipboard does not contain a valid preset.").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), true);
        }
    }

    private void savePreset() {
        if (Config.saveCompletePreset(nameBox.getValue(), descBox.getValue(), getter)) {
            minecraft.player.displayClientMessage(new TextComponent("Preset saved as " + nameBox.getValue() + "successfully.").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC), true);
            nameBox.setValue("");
            descBox.setValue("");
            if (selEle == -1) {
                selectButton(-1);
            }
        } else {
            minecraft.player.displayClientMessage(new TextComponent("There was an issue saving the preset.").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), true);
        }
    }

    private void copyPreset() {
        assert minecraft != null;
        Config.copyPreset(minecraft, getter);
        minecraft.player.displayClientMessage(new TextComponent("Preset has been copied to clipboard.").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC), true);
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
        tabs.put("general", new TabButton(0, 0, 0, 32, 16, b -> this.selectButton(((TabButton)b).index),
                                          RenderUtils.tip(this, new TranslatableComponent("gui.sedparties.name.general")),
                                          new GeneralOptions("general").render((ForgeIngameGui) minecraft.gui),
                                          "Main", true
                                          ));
        while (iter.hasNext()) {
            item = iter.next();
            if (item.getValue().isTabRendered()) {
                tabsOrder.add(item.getKey());
                tabs.put(item.getKey(), new TabButton(i, 0, 0, 32, 16, b -> this.selectButton(((TabButton)b).index),
                                                      RenderUtils.tip(this, new TranslatableComponent(item.getValue().translateName())),
                                                      item.getValue().render((ForgeIngameGui) minecraft.gui),
                                                      item.getValue().getType(), true

                ));
                i++;
            }
        }
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
        organizeTabButtons();
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
        eleBoxX = screenX + 32;
        eleBoxY = screenY;
        eleBoxH = Math.min(32, screenH);

        organizeTabButtons();

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

        int butX = optBoxX + 3;
        miscButtons.get(2).x = butX;
        miscButtons.get(2).y = optBoxY + 6;
        miscButtons.get(3).x = butX;
        miscButtons.get(3).y = optBoxY + 22;
        miscButtons.get(4).x = butX;
        miscButtons.get(4).y = optBoxY + 34;
        miscButtons.get(5).x = butX;
        miscButtons.get(5).y = optBoxY + 50;
        miscButtons.get(6).x = butX;
        miscButtons.get(6).y = optBoxY + 62;
        miscButtons.get(7).x = butX;
        miscButtons.get(8).x = butX;
        miscButtons.get(8).y = optBoxY + 78;
        miscButtons.get(9).x = butX;
        miscButtons.get(9).y = optBoxY + 90;
        miscButtons.get(10).x = butX;
        miscButtons.get(10).y = optBoxY + 102;


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

    private void organizeTabButtons() {
        TabButton b;
        int eleCounter = 0;
        int xOffset = eleBoxX + (eleBoxW - (32*maxEles))/2;
        int yOffset = eleBoxY+16;
        int currEle = 0;
        for (String tab : tabsOrder) {
            b = tabs.get(tab);
            if (eleCounter++ == maxEles) {
                yOffset -= 16;
                currEle += maxEles;
                xOffset = eleBoxX + (eleBoxW - (32*Math.min(maxEles, tabs.size() - currEle)))/2;
                eleCounter = 1;
            }
            b.x = xOffset;
            b.y = yOffset;
            xOffset += 32;
        }
    }

    private void updateOptionsBounds() {
        if (this.options == null) {
            if (selEle == -1) {
                miscButtons.get(5).active = false;
                this.options = presetButton.getOptions(this, minecraft, 0, 0, 0, 0);
                this.options.setItemHeight(28);
                this.options.setBackground(presetButton.getInnerBg());
                renderSelBox = false;
            } else {
                miscButtons.get(5).active = true;
                TabButton b = tabs.get(tabsOrder.get(selEle));
                this.options = b.getOptions(this, minecraft, 0, 0, 0, 0);
                this.options.setBackground(b.getInnerBg());

                //Set Bounds
                RenderItem.ItemBound bound = b.getBounds();
                this.selBoxX = bound.getX();
                this.selBoxY = bound.getY();
                this.selBoxW = bound.getWidth();
                this.selBoxH = bound.getHeight();
                renderSelBox = selEle != 0;
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
        triggerUpdate(name, data);

        if (markDirty)
            markDirty();
    }

    public void markDirty() {
        options.markSlidersDirty();
    }

    public void triggerUpdate(String name, Object data) {
        RenderItem.SmallBound upval;
        if ((upval = updater.get(name).onUpdate(RenderItem.items.get(tabsOrder.get(selEle)), data)) != null) {
            upval.update((type, value) -> {
                switch(type) {
                    case 0 -> selBoxX = value;
                    case 1 -> selBoxY = value;
                    case 2 -> selBoxW = value;
                    case 3 -> selBoxH = value;
                }
            });
        }
    }
}
