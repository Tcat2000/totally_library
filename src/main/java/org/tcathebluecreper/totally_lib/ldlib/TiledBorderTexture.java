package org.tcathebluecreper.totally_lib.ldlib;

import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.utils.Size;
import net.minecraft.client.gui.GuiGraphics;

public class TiledBorderTexture extends ResourceBorderTexture {
    public TiledBorderTexture(String imageLocation, int imageWidth, int imageHeight, int cornerWidth, int cornerHeight) {
        super(imageLocation, imageWidth, imageHeight, cornerWidth, cornerHeight);
        this.borderSize = new Size(cornerWidth, cornerHeight);
        this.imageSize = new Size(imageWidth, imageHeight);
    }
    @Override
    protected void drawSubAreaInternal(GuiGraphics graphics, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        //compute relative sizes
        float cornerWidth = borderSize.width * 1f / imageSize.width;
        float cornerHeight = borderSize.height * 1f / imageSize.height;
        //draw up corners
        super.drawSubAreaInternal(graphics, x, y, borderSize.width, borderSize.height, 0, 0, cornerWidth, cornerHeight);
        super.drawSubAreaInternal(graphics, x + width - borderSize.width, y, borderSize.width, borderSize.height, 1 - cornerWidth, 0, cornerWidth, cornerHeight);
        //draw down corners
        super.drawSubAreaInternal(graphics, x, y + height - borderSize.height, borderSize.width, borderSize.height, 0, 1 - cornerHeight, cornerWidth, cornerHeight);
        super.drawSubAreaInternal(graphics, x + width - borderSize.width, y + height - borderSize.height, borderSize.width, borderSize.height, 1 - cornerWidth, 1 - cornerHeight, cornerWidth, cornerHeight);
        //draw horizontal connections
        super.drawSubAreaInternal(graphics, x + borderSize.width, y, width - 2 * borderSize.width, borderSize.height,
            cornerWidth, 0, 1 - 2 * cornerWidth, cornerHeight);
        super.drawSubAreaInternal(graphics, x + borderSize.width, y + height - borderSize.height, width - 2 * borderSize.width, borderSize.height,
            cornerWidth, 1 - cornerHeight, 1 - 2 * cornerWidth, cornerHeight);
        //draw vertical connections
        super.drawSubAreaInternal(graphics, x, y + borderSize.height, borderSize.width, height - 2 * borderSize.height,
            0, cornerHeight, cornerWidth, 1 - 2 * cornerHeight);
        super.drawSubAreaInternal(graphics, x + width - borderSize.width, y + borderSize.height, borderSize.width, height - 2 * borderSize.height,
            1 - cornerWidth, cornerHeight, cornerWidth, 1 - 2 * cornerHeight);
        //draw central body
//        drawSubAreaInternalTiled(graphics, x + borderSize.width, y + borderSize.height,
//            width - 2 * borderSize.width, height - 2 * borderSize.height,
//            cornerWidth, cornerHeight, 1 - 2 * cornerWidth, 1 - 2 * cornerHeight, 100, 100);

        super.drawSubAreaInternal(graphics,
            x + (float)this.borderSize.width,
            y + (float)this.borderSize.height,
            width - (float)(2 * this.borderSize.width),
            height - (float)(2 * this.borderSize.height),
            cornerWidth,
            cornerHeight,
            1.0F - 2.0F * cornerWidth,
            1.0F - 2.0F * cornerHeight);
    }

    private void drawSubAreaInternalTiled(GuiGraphics graphics, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight, float tileWidth, float tileHeight) {
        for(float X = 0; X < width; X += tileWidth) {
            super.drawSubAreaInternal(graphics, x + X, y, tileWidth, tileHeight, drawnU, drawnV, tileWidth, tileHeight);
        }
    }
}
