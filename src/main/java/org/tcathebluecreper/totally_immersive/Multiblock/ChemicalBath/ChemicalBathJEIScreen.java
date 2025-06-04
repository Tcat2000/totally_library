package org.tcathebluecreper.totally_immersive.Multiblock.ChemicalBath;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.tcathebluecreper.totally_immersive.AccessableFluidInfoArea;
import org.tcathebluecreper.totally_immersive.IIContainerScreen;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class ChemicalBathJEIScreen extends IIContainerScreen<ChemicalBathJEIMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/chemical_bath.png");
    public ChemicalBathJEIScreen(ChemicalBathJEIMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_, TEXTURE);
    }

    @Override
    protected void makeInfoAreas() {
//        addInfoArea(
//                new AccessableFluidInfoArea(menu.tank, new Rect2i(leftPos+50, topPos+18, 8, 47), 195, 0, 20, 51, TEXTURE)
//        );
    }
}
