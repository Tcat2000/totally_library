package org.tcathebluecreper.totally_lib.dev_utils.widgets;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

public class ScreenSpaceWidget extends WidgetGroup {
    public int minX;
    public int minY;
    public int maxX;
    public int maxY;

    public ScreenSpaceWidget(int minX, int minY, int maxX, int maxY) {
        super(0, 0, 0, 0);
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        initTemplate();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        int posX = minX < 0 ? gui.getScreenWidth() + minX : minX;
        int posY = minY < 0 ? gui.getScreenHeight() + minY : minY;
        int width = maxX <= 0 ? gui.getScreenWidth() + maxX - posX : maxX;
        int height = maxY <= 0 ? gui.getScreenHeight() + maxY - posY : maxY;
        setSelfPosition(posX, posY);
        setSize(width, height);
        sizeChanged(width, height);
    }

    protected void sizeChanged(int width, int height) {}
}
