package io.sedu.mc.parties.client.overlay.gui;

import Util.Render;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.PDimIcon;
import io.sedu.mc.parties.client.overlay.PHead;
import io.sedu.mc.parties.client.overlay.PName;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.StringTag;
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

import static Util.Render.renderBg;
import static Util.Render.tip;

public class SettingsScreen extends Screen {
    private final ResourceLocation MENU_LOC = new ResourceLocation("textures/block/spruce_planks.png");
    private final ResourceLocation MOD_LOC = new ResourceLocation("textures/block/polished_basalt_side.png");
    public static ResourceLocation INNER_LOC = new ResourceLocation("textures/block/deepslate_bricks.png");
    private final ResourceLocation OPTIONS_LOC = new ResourceLocation("textures/block/polished_basalt_side.png");
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

    int modBoxX;
    int modBoxY;
    int modBoxW;
    int modBoxH;
    boolean modVisible = true;

    int optBoxX;
    int optBoxY;
    int optBoxW;
    int optBoxH;

    int searchBoxX;
    int searchBoxY;
    int searchBoxW;
    int searchBoxH;

    //TODO: Save changes into a new class that tracks the component and the subtype and the value of the change. Disable clearing until they press X

    private Button left = new ColorButton(0xbb8f44, 0, 0, 20, 20, new TextComponent("◄"), b -> cycleElements(true), tip(this, "Cycle Elements Left"));
    private Button right = new ColorButton(0xbb8f44,0, 0, 20, 20, new TextComponent("►"), b -> cycleElements(false), tip(this, "Cycle Elements Right"));
    private Button showModBox = new ColorButton(0x6536c3,0, 0, 20, 20, new TextComponent("►"), b -> toggleModBox(true), tip(this, "Show Mod Filters"));
    private Button hideModBox = new ColorButton(0x6536c3,0, 0, 20, 20, new TextComponent("◄"), b -> toggleModBox(false), tip(this, "Hide Mod Filters"));

    private void toggleModBox(boolean show) {
        if (show) {
            modVisible = true;
            hideModBox.visible = true;
            showModBox.visible = false;
        } else {
            modVisible = false;
            hideModBox.visible = false;
            showModBox.visible = true;
        }
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




    @Override
    public void onClose() {
        PHead.playerHead = null;
        PName.nameTag = null;
        PDimIcon.dimIcon = null;
        INNER_LOC = null;
        super.onClose();
    }

    private void removeRenderButtons() {
        for (int i = offEle; i < offEle + maxEles - 2; i++)
            removeWidget(tabs.get(tabsOrder.get(i)));
    }

    ;

    protected SettingsScreen() {
        super(new TextComponent("Party Advanced Settings"));
    }

    public void render(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        //this.renderBackground(poseStack);
        //renderBg();

        renderConfig();
        assert minecraft != null;
        tabs.get(tabsOrder.get(selEle)).renderInner(poseStack, (ForgeIngameGui) minecraft.gui, screenX + modBoxW, screenY + eleBoxH, screenW - 64, screenH - 56);
        renderElementBox(poseStack);
        if (modVisible)
            renderModBox();
        renderOptionsBox();
        renderSearchBox();
        renderShadows(poseStack);


        super.render(poseStack, pMouseX, pMouseY, pPartialTick);

    }

    private void renderConfig() {
        renderBg(screenX, screenY + eleBoxH, screenX + screenW - optBoxW, screenY + screenH - searchBoxH, screenW - modBoxW - optBoxW, screenH - eleBoxH - searchBoxH, 110, INNER_LOC);
    }

    private void renderSearchBox() {
        renderBg(searchBoxX, searchBoxY, searchBoxX + searchBoxW, searchBoxY + searchBoxH, searchBoxW, searchBoxH, 200, SEARCH_LOC);
    }

    private void renderOptionsBox() {
        renderBg(optBoxX, optBoxY, optBoxX + optBoxW, optBoxY + optBoxH, optBoxW, optBoxH, 175, OPTIONS_LOC);
    }

    private void renderModBox() {
        //RenderItem.drawRect(poseStack.last().pose(), 0,modBoxX - 40, modBoxY, modBoxX + modBoxW, modBoxY + modBoxH, 0x33000000, 0x33000000);
        renderBg(modBoxX, modBoxY, modBoxX + modBoxW, modBoxY + modBoxH, modBoxW, modBoxH, 175, MOD_LOC);
    }

    private void renderElementBox(PoseStack poseStack) {
        //RenderItem.drawRect(poseStack.last().pose(), 0,eleBoxX, eleBoxY, eleBoxX + eleBoxW, eleBoxY + eleBoxH, 0x33000000, 0x33000000);
        renderBg(eleBoxX, eleBoxY, eleBoxX + eleBoxW, eleBoxY + eleBoxH, eleBoxW+32, eleBoxH, 255, MENU_LOC);

        //With Arrows
        if (tabs.size() > maxEles) {
            for (int i = 0; i < maxEles-2; i++) {
                renderElementTab(poseStack, i, 34 + i*32);
            }
        }

    }

    private void renderElementTab(PoseStack poseStack, int i, int offset) {
        //tabs.get(tabsOrder.get(i/*+currentIndex*/)).render(poseStack,minecraft.gui, eleBoxX + offset, eleBoxY, eleBoxX + offset + 32, eleBoxY + 32);
        //RenderItem.drawRectCO(poseStack.last().pose(), 0, eleBoxX + offset, eleBoxY, eleBoxX + offset + 32, eleBoxY + 32, 0xFFFF | (255*(i%2)) <<16, 0x777777);
    }


    private void renderShadows(PoseStack poseStack) {
        //renderBg(screenX + modBoxW, screenY + eleBoxH, screenX + screenW, screenY + screenH, screenW - modBoxW, screenH - eleBoxH, 80);
        //Top Shadow
        RenderItem.drawRect(poseStack.last().pose(), 0, screenX, screenY + eleBoxH, screenX + screenW, screenY + eleBoxH+10, 0xAA000000, 0x00000000);
        //Left Shadow
        if (modVisible) {
            RenderItem.drawRectHorizontal(poseStack.last().pose(), 0, screenX + modBoxW, screenY + eleBoxH, screenX + modBoxW + 10, screenY + screenH - searchBoxH, 0xAA000000, 0x00000000);
        }
        //Bottom Shadow
        RenderItem.drawRect(poseStack.last().pose(), 0, screenX, screenY + screenH - 10 - searchBoxH, screenX + screenW, screenY + screenH - searchBoxH, 0x00000000, 0xAA000000);


        //Right Shadow
        RenderItem.drawRectHorizontal(poseStack.last().pose(), 0, screenX + screenW - 10 - optBoxW, screenY + eleBoxH, screenX + screenW - optBoxW, screenY + screenH - searchBoxH, 0x00000000, 0xAA000000);
        //RenderItem.drawRect(poseStack.last().pose(), 0, screenX + modBoxW, screenY + eleBoxH, screenX + screenW - optBoxW, screenY + screenH - searchBoxH, 0x66000000, 0x66000000);
    }


    protected void init() {
        INNER_LOC = new ResourceLocation("textures/block/deepslate_bricks.png");
        PHead.playerHead = new ItemStack(Items.PLAYER_HEAD);
        assert Minecraft.getInstance().player != null;
        PHead.playerHead.addTagElement("SkullOwner", StringTag.valueOf(Minecraft.getInstance().player.getName().getContents()));
        PName.nameTag = Items.NAME_TAG.getDefaultInstance();
        PDimIcon.dimIcon = Items.END_PORTAL_FRAME.getDefaultInstance();

        setBounds(width, height);
        //Setup Data.
        initTabButtons();
        super.init();
    }

    private void initTabButtons() {
        int i = 0;
        Iterator<Map.Entry<String, RenderItem>> iter = RenderItem.items.entrySet().iterator();
        Map.Entry<String, RenderItem> item;
        while (iter.hasNext()) {
            item = iter.next();
            if (item.getValue().isTabRendered()) {
                tabsOrder.add(item.getKey());
                assert minecraft != null;
                tabs.put(item.getKey(), new TabButton(i, 0, 0, 32, 32, b -> this.selectButton(((TabButton)b).index),
                                                      Render.tip(this, new TranslatableComponent(item.getValue().translateName())),
                                                      item.getValue().render((ForgeIngameGui) minecraft.gui),
                                                      item.getValue().getType()

                ));
                i++;
            }
        }
        initRenderButtons();
    }

    private void selectButton(int i) {
        selEle = i;
    }

    private void initRenderButtons() {
        if (maxEles < tabsOrder.size()) {
            left.x = eleBoxX + 6;
            left.y = eleBoxY + 6;
            right.x = eleBoxX + 6 + (maxEles-1)*32;
            right.y = eleBoxY + 6;
            left.visible = true;
            right.visible = true;
            addRenderableWidget(left);
            addRenderableWidget(right);
            //TODO: Test this in future: Scroll to right most, then expand window.
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
            b.x = eleBoxX + (i+1)*32;
            b.y = eleBoxY;
            addRenderableWidget(b);
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

    private void setBounds(int width, int height) {
        this.screenW = Math.min(width>>1, 320);
        this.screenH = Math.min((height>>2)*3, 300);
        if (width > 720)
            screenX = (width-screenW)>>1;
        else
            screenX = width - screenW - 10;
        screenY = (height-screenH)>>1;


        eleBoxW = screenW;
        maxEles = (eleBoxW) / 32;
        eleBoxX = screenX;
        eleBoxY = screenY;
        eleBoxH = Math.min(32, screenH);

        clearWidgets();
        initRenderButtons();

        modBoxW = Math.min(32, screenW);
        modBoxX = screenX;
        modBoxY = screenY+32;
        modBoxH = screenH - 32;

        //Show/Hide Mod Box Buttons
        showModBox.x = screenX + 6;
        hideModBox.x = showModBox.x;
        showModBox.y = screenY + screenH - 22;
        hideModBox.y = showModBox.y;
        showModBox.visible = !modVisible;
        hideModBox.visible = modVisible;
        addRenderableWidget(showModBox);
        addRenderableWidget(hideModBox);

        optBoxH = screenH - eleBoxH;
        optBoxW = Math.min(32, screenW);
        optBoxX = screenX + screenW - optBoxW;
        optBoxY = screenY + eleBoxH;

        searchBoxH = Math.min(24, screenH);
        searchBoxW = screenW;
        searchBoxX = screenX;
        searchBoxY = screenY + screenH - searchBoxH;

    }


    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        setBounds(pWidth, pHeight);
    }

}
