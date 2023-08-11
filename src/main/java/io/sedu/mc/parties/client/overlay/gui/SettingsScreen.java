package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.helper.ColorAPI;
import io.sedu.mc.parties.client.config.Config;
import io.sedu.mc.parties.client.overlay.*;
import io.sedu.mc.parties.network.PartiesPacketHandler;
import io.sedu.mc.parties.network.StringPacketData;
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

import static io.sedu.mc.parties.client.overlay.RenderItem.playerScale;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.util.RenderUtils.*;

public class SettingsScreen extends Screen {
    private final ResourceLocation MENU_LOC = new ResourceLocation("textures/block/deepslate_tiles.png");
    private final ResourceLocation OPTIONS_LOC = new ResourceLocation("textures/block/spruce_log.png");
    private final ResourceLocation SEARCH_LOC = new ResourceLocation("textures/block/deepslate_tiles.png");

    int screenW;
    int screenH;
    int screenX = 0;
    int screenY = 0;
    private int oldX = 0;
    private int oldY = 0;
    private Integer oldMX = null;
    private Integer oldMY = null;
    private int maxScreenX;
    private int maxScreenY;
    private boolean draggingWindow = false;

    int eleBoxX;
    int eleBoxY;
    int eleBoxW;
    int eleBoxH;
    static int selEle = 0;
    int maxEles = 0;
    HashMap<String, TabButton> tabs = new HashMap<>();
    ArrayList<String> tabsOrder = new ArrayList<>();

    TabButton presetButton;


    ConfigOptionsList options;
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
    private final boolean isPresetOnly;




    protected SettingsScreen(boolean b) {
        super(new TextComponent("Party Advanced Settings"));
        miscButtons = new ArrayList<>();
        isPresetOnly = b;
        confirmPrompt = new TranslatableComponent("gui.sedparties.tooltip.confirmreset").withStyle(ChatFormatting.DARK_RED);
    }



    private void resetAll() {
        if (isConfirmed) {
            Config.createDefaultPreset();
            isConfirmed = false;
            revertCooldown = 0;
            ((SmallButton)miscButtons.get(6)).setColor(1f, 1f, .5f);
            miscButtons.get(6).setMessage(new TextComponent("↺"));
            refreshCurrentEle();
        } else {
            miscButtons.get(6).active = false;
            triggeredPrompt = true;
            buttonCooldown = 60;
            revertCooldown = 120;
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
        RenderItem.isDirty = true; //Update displays.
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
        ColorAPI.colorCycle = false;
        notEditing = true;
        PHead.icon = null;
        PName.sign = null;
        POrigin.icon = null;
        GeneralOptions.icon = null;
        PresetOptions.icon = null;
        nameHolder = nameBox.getValue().isEmpty() ? null : nameBox.getValue();
        descHolder = descBox.getValue().isEmpty() ? null : descBox.getValue();
        Config.saveCurrentPresetAsDefault(getter);
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
        renderSelfFrameOutline(poseStack);
        if (renderSelBox)
            renderSelection(poseStack);
        if (draggingWindow) {
            RenderUtils.borderRectNoA(poseStack.last().pose(), 0, 1, screenX, screenY, screenW, screenH, ColorAPI.getRainbowColor());
            RenderUtils.sizeRect(poseStack.last().pose(), screenX, screenY, 0, screenW, screenH, ColorAPI.getRainbowColor() | 40 << 24);
            move(pMouseX, pMouseY);
            return;
        } else if (oldMX != null) {
            save();
            return;
        }
        RenderUtils.offRectNoA(poseStack.last().pose(), screenX, screenY, -1, -1, screenW, screenH, ColorAPI.getRainbowColor(), 0x232323);
        RenderSystem.enableDepthTest();
        //TODO: Make sure this following line doesn't cause errors anymore.
        this.options.render(poseStack, pMouseX, pMouseY, pPartialTick);
        assert minecraft != null;
        //TODO: ModBox
        //if (modVisible)
            //renderModBox();

        //Settings
        renderOptionsBox();
        //Presets
        renderPresetBox();
        font.drawShadow(poseStack, "Save Preset", presetBoxX + (presetBoxW>>1) - 31, presetBoxY + 3, ColorAPI.getRainbowColor());
        font.drawShadow(poseStack, "Name:", nameBox.x - 29, nameBox.y, 0xFFFFFF);
        font.drawShadow(poseStack, "Desc:", descBox.x - 29, descBox.y, 0xFFFFFF);
        renderShadows(poseStack);

        renderBg(0, screenX, screenY, screenX + screenW, screenY + eleBoxH, screenW, eleBoxH, 200, MENU_LOC);
        super.render(poseStack, pMouseX, pMouseY, pPartialTick);

    }

    private void move(int x, int y) {

        if (oldMX == null) {
            oldMX = x;
            oldMY = y;
            oldX = screenX;
            oldY = screenY;
        }
        checkLimits(x, y);
    }

    private void checkLimits(int x, int y) {
        int tempFrame = x - oldMX + oldX;
        if (tempFrame < 0) {
            screenX = 0;
        } else if (tempFrame + screenW > maxScreenX) {
            screenX = maxScreenX - screenW;
        } else {
            screenX = tempFrame;
        }
        tempFrame = y - oldMY + oldY;
        if (tempFrame < 0) {
            screenY = 0;
        } else if (tempFrame + screenH > maxScreenY) {
            screenY = maxScreenY - screenH;
        } else {
            screenY = tempFrame;
        }

    }

    private void save() {
        oldMY = null;
        oldMX = null;
        //Do other things
        setBounds(width, height, false, true);
    }

    private void renderSelection(PoseStack poseStack) {
        RenderUtils.borderRectNoA(poseStack.last().pose(), 0, 1, (int) (selBoxX* playerScale), (int) (selBoxY* playerScale), (int) Math.ceil(selBoxW* playerScale), (int) Math.ceil(selBoxH* playerScale), ColorAPI.getRainbowColor());
    }


    void renderPresetBox() {
        renderBg(presetBoxX, presetBoxY, presetBoxX + presetBoxW, presetBoxY + presetBoxH, presetBoxW, presetBoxH, 200, SEARCH_LOC);
    }

    private void renderOptionsBox() {
        renderBg(optBoxX, optBoxY, optBoxX + optBoxW, optBoxY + optBoxH, optBoxW, optBoxH, 255, OPTIONS_LOC);
    }


    void renderShadows(PoseStack poseStack) {
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
        ColorAPI.colorCycle = true;
        RenderItem.initUpdater(updater);
        RenderItem.initGetter(getter);
        notEditing = isPresetOnly;
        if (isPresetOnly) {

        }
        PHead.icon = new ItemStack(Items.PLAYER_HEAD);
        GeneralOptions.icon = new ItemStack(Items.COMPARATOR);
        PresetOptions.icon = new ItemStack(Items.CHEST_MINECART);
        assert Minecraft.getInstance().player != null;
        PHead.icon.addTagElement("SkullOwner", StringTag.valueOf(Minecraft.getInstance().player.getName().getContents()));
        PName.sign = Items.SPRUCE_SIGN.getDefaultInstance();
        POrigin.icon = Items.NETHER_STAR.getDefaultInstance();
        POrigin.icon.addTagElement("Enchantments", StringTag.valueOf(""));

        //Setup Data.
        initTabButtons();
        setBounds(width, height, true, false);
        super.init();
    }


    InputBox nameBox;
    InputBox descBox;
    void initMiscButtons() {
        miscButtons.add(new ColorButton(0x6536c3, 0, 0, 20, 20, new TextComponent("►"), b -> toggleModBox(true), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.showfilters"))));
        miscButtons.add(new ColorButton(0x6536c3, 0, 0, 20, 20, new TextComponent("◄"), b -> toggleModBox(false), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.hidefilters"))));
        miscButtons.add(new SmallButton(0, 0, "c", b -> toggleRGB(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.rgbtoggle")), .5f, 0f, 0.5f, 0.5f, 0.5f));
        miscButtons.add(new SmallButton(0, 0, "x", b -> toggleEles(false), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.eleoff")), 1f, 0.5f, 0.5f));
        miscButtons.add(new SmallButton(0, 0, "✓", b -> toggleEles(true), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.eleon")), 0.5f, 1f, 0.5f));
        miscButtons.add(new SmallButton(0,0, "↺", b -> resetEle(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.curdefault")), .5f, 1f, 1f));
        miscButtons.add(new SmallButton(0,0, "↺", b -> resetAll(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.alldefault")), 1f, 1f, 0.5f));
        miscButtons.add(new SmallButton(0,0, "s", b -> savePreset(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.savepreset")), .5f, 0f, 0.5f, 1f, 0.5f));
        miscButtons.add(new SmallButton(0,0, "c", b -> copyPreset(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.copypreset")), .5f, 0f,1f, 0.5f, 1f));
        miscButtons.add(new SmallButton(0,0, "p", b -> pastePreset(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.loadpreset")),.5f, 0f, 1f, 0.5f, 1f));
        miscButtons.add(new SmallButton(0, 0, "✎", b -> sendPresetToChat(), transTip(this, new TranslatableComponent("gui.sedparties.tooltip.linkpreset")), 0, 1, 1f, 0.5f, 1f));
        nameBox = new InputBox(0xFFFFFF, font, 0, 12, new TranslatableComponent("gui.sedparties.name.namebox"), (s) -> checkSaveFlags(), false);
        descBox = new InputBox(0xFFFFFF, font, 0, 12, new TranslatableComponent("gui.sedparties.name.descbox"), (s) -> checkSaveFlags(), false);
        nameBox.filter = s -> alphaNumeric.matcher(s).find();
        nameBox.insertText(nameHolder == null ? "" : nameHolder);
        descBox.insertText(descHolder == null ? "" : descHolder);
        descBox.setMaxLength(128);
        checkSaveFlags();
    }

    private void sendPresetToChat() {
        if (sentMessage) return;
        PartiesPacketHandler.sendToServer(new StringPacketData(0, Config.getPresetString(getter)));
        sentMessage = true;
        messageCooldown = 300;
        miscButtons.get(10).active = false;
    }

    private void pastePreset() {
        assert minecraft != null;
        if (Config.pastePreset(minecraft, updater)) {
            minecraft.player.displayClientMessage(new TranslatableComponent("messages.sedparties.preset.pasteload").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC), true);
        } else {
            minecraft.player.displayClientMessage(new TranslatableComponent("messages.sedparties.preset.pastefail").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), true);
        }
    }

    private void savePreset() {
        if (Config.saveCompletePreset(nameBox.getValue(), descBox.getValue(), getter)) {
            minecraft.player.displayClientMessage(new TranslatableComponent("messages.sedparties.preset.save1").append(nameBox.getValue()).append(new TranslatableComponent("messages.sedparties.preset.save2")).withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC), true);
            nameBox.setValue("");
            descBox.setValue("");
            if (selEle == -1) {
                selectButton(-1);
            }
        } else {
            minecraft.player.displayClientMessage(new TranslatableComponent("messages.sedparties.preset.savefail").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), true);
        }
    }

    private void copyPreset() {
        assert minecraft != null;
        Config.copyPreset(minecraft, getter);
        minecraft.player.displayClientMessage(new TranslatableComponent("messages.sedparties.preset.copy").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC), true);
    }

    private void checkSaveFlags() {
        miscButtons.get(7).active = !nameBox.getValue().isEmpty() && !descBox.getValue().isEmpty();
    }

    private void initTabButtons() {

        assert minecraft != null;
        //Preset Button
        if (isPresetOnly) {
            presetButton = new TabButton(-1, 0, 0, 32, 32, b -> {},
                                         RenderUtils.transTip(this, new TranslatableComponent("gui.sedparties.name.preset")),
                                         new PresetOptions("Load").render((ForgeIngameGui) minecraft.gui));
        } else {
            presetButton = new TabButton(-1, 0, 0, 32, 32, b -> this.selectButton(((TabButton)b).index),
                                         RenderUtils.transTip(this, new TranslatableComponent("gui.sedparties.name.preset")),
                                         new PresetOptions("Load").render((ForgeIngameGui) minecraft.gui));
        }


        if (isPresetOnly) return;

        int i = 1;
        Iterator<Map.Entry<String, RenderItem>> iter = RenderItem.items.entrySet().iterator();
        Map.Entry<String, RenderItem> item;
        //General Settings
        tabsOrder.add("general");
        tabs.put("general", new TabButton(0, 0, 0, 24, 16, b -> this.selectButton(((TabButton)b).index),
                                          RenderUtils.transTip(this, new TranslatableComponent("gui.sedparties.name.general")),
                                          new GeneralOptions("general").render((ForgeIngameGui) minecraft.gui), true
                                          ));
        while (iter.hasNext()) {
            item = iter.next();
            if (item.getValue().isTabRendered()) {
                tabsOrder.add(item.getKey());
                tabs.put(item.getKey(), new TabButton(i, 0, 0, 24, 16, b -> this.selectButton(((TabButton)b).index),
                                                      RenderUtils.transTip(this, new TranslatableComponent(item.getValue().translateName())),
                                                      item.getValue().render((ForgeIngameGui) minecraft.gui), true

                ));
                i++;
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        if (tickables.size() > 0)
            tickables.forEach(inputBox -> inputBox.setFocus(false));

        if (!super.mouseClicked(pMouseX, pMouseY, pButton)) {
            checkWindowDrag(pMouseX, pMouseY);
            return false;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        draggingWindow = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private void checkWindowDrag(double x, double y) {
        draggingWindow = (x < screenX || x > screenX + screenW) || (y < screenY || y > screenY + screenH);
    }

    protected void selectButton(int i) {
        selEle = i;
        removeWidget(options);
        options = null;
        updateOptionsBounds();
        organizeTabButtons();
    }

    void setBounds(int width, int height, boolean init, boolean moved) {
        if (!moved) {
            this.screenW = Math.min(width>>1, 320);
            this.screenH = Math.min((height>>2)*3, 300);
            if (width > 720)
                screenX = (width-screenW)>>1;
            else
                screenX = width - screenW - 10;
            screenY = (height-screenH)>>1;
        }


        eleBoxW = screenW - 32;
        maxEles = eleBoxW / 24;
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
        presetButton.x = isPresetOnly ? screenX + (screenW)/2 - 16 : screenX;
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
            if (isPresetOnly) {
                miscButtons.get(0).active = false;
                miscButtons.get(1).active = false;
                miscButtons.get(2).active = false;
                miscButtons.get(3).active = false;
                miscButtons.get(4).active = false;
                miscButtons.get(5).active = false;
                miscButtons.get(6).active = false;
            }
        }
        updateOptionsBounds();
        maxScreenX = minecraft.getWindow().getGuiScaledWidth();
        maxScreenY = minecraft.getWindow().getGuiScaledHeight();
    }

    private void organizeTabButtons() {
        TabButton b;
        int eleCounter = 0;
        int xOffset = eleBoxX + (eleBoxW - (24*maxEles))/2;
        int yOffset = eleBoxY+16;
        int currEle = 0;
        for (String tab : tabsOrder) {
            b = tabs.get(tab);
            if (eleCounter++ == maxEles) {
                yOffset -= 16;
                currEle += maxEles;
                xOffset = eleBoxX + (eleBoxW - (24*Math.min(maxEles, tabs.size() - currEle)))/2;
                eleCounter = 1;
            }
            b.x = xOffset;
            b.y = yOffset;
            xOffset += 24;
        }
    }

    protected void updateOptionsBounds() {
        if (this.options == null) {
            if (selEle == -1 || isPresetOnly) {
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
        setBounds(pWidth, pHeight, false, false);
        this.width = pWidth;
        this.height = pHeight;
    }

    public void finalizeUpdate(String name, Object data, boolean markDirty) {
        triggerUpdate(name, data);
        Parties.LOGGER.info("Update for {} value {}.", name, data);

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

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
