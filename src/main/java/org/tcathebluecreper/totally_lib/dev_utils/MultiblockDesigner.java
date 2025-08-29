package org.tcathebluecreper.totally_lib.dev_utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_lib.TotallyLibrary;

public class MultiblockDesigner extends Block implements EntityBlock {
    private static RegistryObject<Block> self;
    private static RegistryObject<Item> selfItem;
    public static Block getBlock() {
        return self.get();
    }
    public static Item getItem() {
        return selfItem.get();
    }
    public static void init() {
        self = TotallyLibrary.regManager.register(ForgeRegistries.BLOCKS.getRegistryKey(), TotallyLibrary.MODID, "multiblock_designer", MultiblockDesigner::new);
        selfItem = TotallyLibrary.regManager.register(ForgeRegistries.ITEMS.getRegistryKey(), TotallyLibrary.MODID, "multiblock_designer", () -> new BlockItem(self.get(), new Item.Properties()));
    }
    public MultiblockDesigner() {
        super(BlockBehaviour.Properties.of());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MultiblockDesignerEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if(be instanceof MultiblockDesignerEntity){
            ((MultiblockDesignerEntity)be).openGUI(pPlayer);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }
}
