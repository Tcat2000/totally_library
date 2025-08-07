package org.tcathebluecreper.totally_immersive.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.tcathebluecreper.totally_immersive.api.lib.ITMultiblockBlock;
import org.tcathebluecreper.totally_immersive.mod.TIBlocks;
import org.tcathebluecreper.totally_immersive.mod.TIItems;

public class MultiblockBuilderOld {
    public final ResourceLocation id;
    public IMultiblockLogic<IMultiblockState> logic;
    public IMultiblockState state;
    public MultiblockBuilderOld(ResourceLocation id) {
        this.id = id;
    }

    public MultiblockBuilderOld logic(IMultiblockLogic<IMultiblockState> logic) {
        this.logic = logic;
        return this;
    }

    public MultiblockBuilderOld state(IMultiblockState state) {
        this.state = state;
        return this;
    }

    public MultiblockRegistration<IMultiblockState> buildMetal() {
        return new IEMultiblockBuilder<>(logic, id.getPath())
            .defaultBEs(TIBlocks.BETs)
            .customBlock(
                TIBlocks.BLOCKS, TIItems.ITEMS,
                r -> new ITMultiblockBlock<>(IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get().forceSolidOn(), r),
                MultiblockItem::new).build();
    }

    public MultiblockRegistration<IMultiblockState> buildStone(boolean solid) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(2, 20).forceSolidOn();
        if (!solid)
            properties.noOcclusion();
        return new IEMultiblockBuilder<>(logic, id.getPath())
            .notMirrored()
            .customBlock(
                TIBlocks.BLOCKS, TIItems.ITEMS,
                r -> new NonMirrorableWithActiveBlock<>(properties, r),
                MultiblockItem::new)
            .defaultBEs(TIBlocks.BETs).build();
    }
}
