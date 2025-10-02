package org.tcathebluecreper.totally_lib.lib;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;


public class GuiDrawer {
    public final GuiGraphics g;
    public final Widget e;

    public static final Color light = new Color(255,255,255);
    public static final Color mid = new Color(198, 198, 198);
    public static final Color dark = new Color(85, 85, 85);
    public static final Color text_dark = new Color(63, 63, 63);
    public static final Color text_light = new Color(252, 252, 252);

    public GuiDrawer(GuiGraphics graphics, Widget element) {
        this.g = graphics;
        this.e = element;
    }

    public GuiDrawer drawRect(int posX, int posY, int sizeX, int sizeY, Color color) {
        g.fill(e.getPositionX() + posX, e.getPositionY() + posY, e.getPositionX() + posX + sizeX, e.getPositionY() + posY + sizeY, color.getRGB());
        return this;
    }

    public GuiDrawer drawString(String string, int x, int y, Color color) {
        g.drawString(Minecraft.getInstance().font, string, e.getPositionX() + x, e.getPositionY() + y, color.getRGB());
        return this;
    }

    public GuiDrawer drawString(Font font, String string, int x, int y, Color color) {
        g.drawString(font, string, e.getPositionX() + x, e.getPositionY() + y, color.getRGB());
        return this;
    }

    public GuiDrawer drawString(String string, int x, int y, Color color, boolean shadow) {
        g.drawString(Minecraft.getInstance().font, string, e.getPositionX() + x, e.getPositionY() + y, color.getRGB(), shadow);
        return this;
    }

    public GuiDrawer drawString(Font font, String string, int x, int y, Color color, boolean shadow) {
        g.drawString(font, string, e.getPositionX() + x, e.getPositionY() + y, color.getRGB(), shadow);
        return this;
    }
}
