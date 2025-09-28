package org.tcathebluecreper.totally_lib.recipe.action;

import org.tcathebluecreper.totally_lib.multiblock.TLTraitMultiblockState;
import org.tcathebluecreper.totally_lib.recipe.TLRecipe;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeProcess;

import java.util.function.BiFunction;

public class TickRangeAction<R extends TLRecipe, S extends TLTraitMultiblockState> extends Action<R, S> {
    public final int start;
    public final int stop;

    public TickRangeAction(int start, int stop, BiFunction<TLRecipeProcess<R, S>, Integer, Boolean> logic) {
        super((t, context, parallel) -> logic.apply(context, parallel));
        this.start = start;
        this.stop = stop;
    }

    @Override
    public boolean run(int tick, TLRecipeProcess<R, S> context, int parallel, int length) {
        if((tick >= start && tick <= stop) || start == stop) return super.run(tick, context, parallel, length);
        return true;
    }
}
