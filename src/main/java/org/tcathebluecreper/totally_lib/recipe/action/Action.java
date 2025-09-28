package org.tcathebluecreper.totally_lib.recipe.action;

import org.apache.commons.lang3.function.TriFunction;
import org.tcathebluecreper.totally_lib.multiblock.TLTraitMultiblockState;
import org.tcathebluecreper.totally_lib.recipe.TLRecipe;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeProcess;

public class Action<R extends TLRecipe, S extends TLTraitMultiblockState> {
    public final TriFunction<Integer, TLRecipeProcess<R, S>, Integer, Boolean> logic;

    public Action(TriFunction<Integer, TLRecipeProcess<R, S>, Integer, Boolean> logic) {
        this.logic = logic;
    }

    public boolean run(int tick, TLRecipeProcess<R, S> context, int parallel, int length) {
        return logic.apply(tick, context, parallel);
    }
}
