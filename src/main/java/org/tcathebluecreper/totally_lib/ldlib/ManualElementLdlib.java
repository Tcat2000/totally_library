package org.tcathebluecreper.totally_lib.ldlib;

import blusunrize.lib.manual.SpecialManualElement;
import blusunrize.lib.manual.gui.ManualScreen;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

import java.util.List;
import java.util.function.Predicate;

public class ManualElementLdlib extends SpecialManualElement {
    public int yOffset;
    public ManualElementWidgetCreator creator;
    private Widget ui;
    public Predicate<String> searchCheck = string -> false;

    public ManualElementLdlib(int yOffset, ManualElementWidgetCreator creator) {
        this.yOffset = yOffset;
        this.creator = creator;
    }

    public ManualElementLdlib(int yOffset, ManualElementWidgetCreator creator, Predicate<String> searchCheck) {
        this.yOffset = yOffset;
        this.creator = creator;
        this.searchCheck = searchCheck;
    }

    @Override
    public int getPixelsTaken() {
        return yOffset;
    }

    @Override
    public void onOpened(ManualScreen manualScreen, int x, int y, List<Button> buttons) {
        ui = creator.onOpen(manualScreen, x, y, buttons);
    }

    @Override
    public void render(GuiGraphics guiGraphics, ManualScreen manualScreen, int x, int y, int mouseX, int mouseY) {
        ui.drawInBackground(guiGraphics, mouseX, mouseY, Minecraft.getInstance().getPartialTick());
    }

    @Override
    public void mouseDragged(int x, int y, double clickX, double clickY, double mouseX, double mouseY, double lastX, double lastY, int mouseButton) {
        ui.mouseDragged(mouseX, mouseY, mouseButton, lastX, lastY);
    }

    @Override
    public boolean listForSearch(String s) {
        return searchCheck.test(s);
    }

    @Override
    public void recalculateCraftingRecipes() {

    }

    public interface ManualElementWidgetCreator {
        Widget onOpen(ManualScreen manualScreen, int x, int y, List<Button> buttons);
    }
}
