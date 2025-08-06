package org.tcathebluecreper.totally_immersive.mod.item;

import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.tcathebluecreper.totally_immersive.mod.TIBlocks;
import org.tcathebluecreper.totally_immersive.mod.block.markings.Marking;
import org.tcathebluecreper.totally_immersive.mod.block.markings.MarkingBlock;

import java.util.ArrayList;
import java.util.List;

public class SprayCanItem extends Item implements IUIHolder.ItemUI {
    public SprayCanItem(Properties p_41383_) {
        super(p_41383_);
    }

    static final SoundManager sm = Minecraft.getInstance().getSoundManager();

    @Override
    public InteractionResult useOn(UseOnContext context) {
//        if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
//            HeldItemUIFactory.INSTANCE.openUI(serverPlayer, context.getHand());
//        }
//        return InteractionResult.SUCCESS;

//        if(context.getPlayer() instanceof ServerPlayer serverPlayer) {
//            HeldItemUIFactory.INSTANCE.openUI(serverPlayer, context.getHand());
//            return InteractionResult.PASS;
//        }

        Direction side = context.getClickedFace().getOpposite();
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);
        if(state.getBlock() != TIBlocks.MARKINGS_BLOCK.get()) {
            pos = pos.relative(side.getOpposite());
            state = context.getLevel().getBlockState(pos);
        }

        CompoundTag nbt = context.getItemInHand().getTag();
        String marking = nbt.getString("marking");

        if(state.getBlock() == Blocks.AIR) {
            context.getLevel().setBlock(pos, TIBlocks.MARKINGS_BLOCK.get().defaultBlockState(), 3);
            state = context.getLevel().getBlockState(pos);
        }

        if(state.getBlock() == TIBlocks.MARKINGS_BLOCK.get()) {
            state = state.setValue(MarkingBlock.MARKING_DIRECTIONS.get(side), MarkingBlock.MARKING_DIRECTIONS.get(side).getValue(marking).orElse(TIBlocks.NONE));
            context.getLevel().setBlock(pos, state, 1);
            return InteractionResult.PASS;
        }
        context.getPlayer().playSound(SoundEvents.SAND_BREAK, 1, 1.5f);
        return InteractionResult.FAIL;
    }

//    @Override
//    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
//        if(player instanceof ServerPlayer serverPlayer) {
//            HeldItemUIFactory.INSTANCE.openUI(serverPlayer, hand);
//        }
//        return InteractionResultHolder.pass(player.getItemInHand(hand));
//    }

    @Override
    public ModularUI createUI(Player player, HeldItemUIFactory.HeldItemHolder holder) {
        System.out.println("trying to open gui");
        WidgetGroup root = new WidgetGroup(new Position(0,0), new Size(100, 80));
        WidgetGroup panel = new DraggableScrollableWidgetGroup(10, 20, 80, 50);
        root.addWidget(panel);

        List<SwitchWidget> entries = new ArrayList<>();

        Marking.ALL_MARKINGS.forEach(marking -> {
            SwitchWidget sw = new SwitchWidget(0,0,60,10, (clickData, aBoolean) -> {});
            sw.setBaseTexture(new GuiTextureGroup(sw.getBackgroundTexture(), new TextTexture(marking.name())));
            entries.add(sw);
            panel.addWidget(sw);
        });


        return new ModularUI(root, holder, player);
    }
}
