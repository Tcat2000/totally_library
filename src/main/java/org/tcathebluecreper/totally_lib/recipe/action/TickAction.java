package org.tcathebluecreper.totally_lib.recipe.action;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import org.tcathebluecreper.totally_lib.recipe.TLRecipe;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeProcess;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class TickAction<R extends TLRecipe, S extends IMultiblockState> extends Action<R, S> {
    public final int tick;

    public TickAction(int tick, BiFunction<TLRecipeProcess<R, S>, Integer, Boolean> logic) {
        super((t, context, paral) -> logic.apply(context, paral));
        this.tick = tick;
    }

    public TickAction(int tick, BiConsumer<TLRecipeProcess<R, S>, Integer> logic) {
        super((t, context, paral) -> {
            logic.accept(context, paral);
            return false;
        });
        this.tick = tick;
    }

    @Override
    public boolean run(int tick, TLRecipeProcess<R, S> context, int parallel, int length) {
        if(this.tick == tick || length + this.tick + 1 == tick) return super.run(this.tick, context, parallel, length);
        return false;
    }
}
