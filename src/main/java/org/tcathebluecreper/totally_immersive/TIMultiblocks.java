package org.tcathebluecreper.totally_immersive;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.util.Lazy;
import org.tcathebluecreper.totally_lib.multiblock.TOPNonMirrorableWithActiveBlock;
import org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath.ChemicalBathLogic;
import org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath.ChemicalBathMultiblock;
import org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath.ChemicalBathState;
import org.tcathebluecreper.totally_immersive.Multiblock.grinder.GrinderLogic;
import org.tcathebluecreper.totally_immersive.Multiblock.grinder.GrinderMultiblock;
import org.tcathebluecreper.totally_immersive.Multiblock.grinder.GrinderState;
import org.tcathebluecreper.totally_immersive.Multiblock.rotay_kiln.RotaryKilnLogic;
import org.tcathebluecreper.totally_immersive.Multiblock.rotay_kiln.RotaryKilnMultiblock;
import org.tcathebluecreper.totally_immersive.Multiblock.rotay_kiln.RotaryKilnState;
import org.tcathebluecreper.totally_lib.lib.ITMultiblockBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TIMultiblocks {
    public static final List<MultiblockRegistration<?>> MULTIBLOCKS = new ArrayList<>();
    public static final MultiblockRegistration<ChemicalBathState> CHEMICAL_BATH = add(metal(new ChemicalBathLogic(),"chemical_bath")
            .structure(Multiblock.CHEMICAL_BATH)
            .redstone(t -> t.redstoneState, new BlockPos(0,1,0))
            .build());

    public static final MultiblockRegistration<GrinderState> GRINDER = add(metal(new GrinderLogic(),"grinder")
            .structure(Multiblock.GRINDER)
            .build());

    public static final MultiblockRegistration<RotaryKilnState> ROTARY_KILN = add(metal(new RotaryKilnLogic(),"rotary_kiln")
        .structure(Multiblock.ROTARY_KILN)
        .build());



    public static <T extends IMultiblockState> MultiblockRegistration<T> add(MultiblockRegistration<T> res) {
        MULTIBLOCKS.add(res);
        return res;
    }
    private static <S extends IMultiblockState> IEMultiblockBuilder<S> stone(IMultiblockLogic<S> logic, String name, boolean solid) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(2, 20).forceSolidOn();
        if (!solid)
            properties.noOcclusion();
        return new IEMultiblockBuilder<>(logic, name)
                .notMirrored()
                .customBlock(
                        TIBlocks.BLOCKS, TIItems.ITEMS,
                        r -> new TOPNonMirrorableWithActiveBlock<>(properties, r),
                        MultiblockItem::new)
                .defaultBEs(TIBlocks.BETs);
    }

    private static <S extends IMultiblockState> IEMultiblockBuilder<S> metal(IMultiblockLogic<S> logic, String name) {
        return new IEMultiblockBuilder<>(logic, name)
                .defaultBEs(TIBlocks.BETs)
                .customBlock(
                        TIBlocks.BLOCKS, TIItems.ITEMS,
                        r -> new ITMultiblockBlock<>(IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get().forceSolidOn(), r),
                        MultiblockItem::new);
    }

    public static void init() {
        Multiblock.init();
    }
    public static class Multiblock {
        private static final List<Lazy<? extends MultiblockHandler.IMultiblock>> toRegister = new ArrayList<>();


        public static final Lazy<TemplateMultiblock> CHEMICAL_BATH = registerLazily(ChemicalBathMultiblock::new);
        public static final Lazy<TemplateMultiblock> GRINDER = registerLazily(GrinderMultiblock::new);
        public static final Lazy<TemplateMultiblock> ROTARY_KILN = registerLazily(RotaryKilnMultiblock::new);


        public static void init() {
            toRegister.forEach(r->MultiblockHandler.registerMultiblock(r.get()));
        }
        public static <T extends MultiblockHandler.IMultiblock> Lazy<T> registerLazily(Supplier<T> mb) {
            Lazy<T> r = Lazy.of(mb);
            toRegister.add(r);
            return r;
        }

    }
}