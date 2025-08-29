package org.tcathebluecreper.totally_lib.dev_utils;

import com.lowdragmc.lowdraglib.gui.editor.data.UIProject;
import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Size;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.tcathebluecreper.totally_lib.TotallyLibrary;

import java.util.Set;
import java.util.function.Supplier;

public class MultiblockDesignerEntity extends BlockEntity implements IUIHolder.BlockEntityUI {
    private static RegistryObject<BlockEntityType<?>> self;
    public static BlockEntityType<MultiblockDesignerEntity> getBET() {
        return (BlockEntityType<MultiblockDesignerEntity>) self.get();
    }
    public static void init() {
        self = TotallyLibrary.regManager.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), TotallyLibrary.MODID, "multiblock_designer", () -> new BlockEntityType<>((pos, state) -> new MultiblockDesignerEntity(pos, state), Set.of(MultiblockDesigner.getBlock()), null));
    }
    public MultiblockDesignerEntity(BlockPos pPos, BlockState pBlockState) {
        super(self.get(), pPos, pBlockState);
    }

    @Override
    public ModularUI createUI(Player player) {
        WidgetGroup root = UIProject.loadUIFromFile(ResourceLocation.fromNamespaceAndPath("ldlib", "multiblock_designer_ui")).get();
        Supplier<WidgetGroup> createTraitEntry = UIProject.loadUIFromFile(ResourceLocation.fromNamespaceAndPath("ldlib", "trait_entry"));
        ModularUI ui = new ModularUI(new WidgetGroup(0,0,200,200).addWidget(root), this, player);
        ui.setFullScreen();

        Widget traitSelectionWindow = ui.getFirstWidgetById("window_new_trait");
        WidgetGroup traitPanel = (WidgetGroup) ui.getFirstWidgetById("trait_panel");
        ButtonWidget addTraitButton = (ButtonWidget) ui.getFirstWidgetById("button_add");
        ButtonWidget removeTraitButton = (ButtonWidget) ui.getFirstWidgetById("button_remove");

        addTraitButton.setOnPressCallback((clickData) -> {
            traitPanel.addWidget(createTraitEntry.get());
        });

        root.setSelfPosition(0,0);
        traitSelectionWindow.setSelfPosition(0,0);
        traitSelectionWindow.setVisible(false);

        return ui;
    }

    public void openGUI(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            BlockEntityUIFactory.INSTANCE.openUI(this, serverPlayer);
        }
    }
}
