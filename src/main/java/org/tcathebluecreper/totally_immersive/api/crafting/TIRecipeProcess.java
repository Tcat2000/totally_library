package org.tcathebluecreper.totally_immersive.api.crafting;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TIRecipeProcess<R extends TIRecipe> {
    public void tick() {

    }

    public class Action {
        public final BiConsumer<Integer, TIRecipeProcess<R>> logic;

        public Action(BiConsumer<Integer, TIRecipeProcess<R>> logic) {
            this.logic = logic;
        }
        public void run(int tick, TIRecipeProcess<R> context) {
            logic.accept(tick, context);
        }
    }
    public class TickAction extends Action {
        public final int tick;
        public TickAction(int tick, Consumer<TIRecipeProcess<R>> logic) {
            super((t, context) -> logic.accept(context));
            this.tick = tick;
        }

        @Override
        public void run(int tick, TIRecipeProcess<R> context) {
            if(this.tick == tick) super.run(tick, context);
        }
    }
    public class TickRangeAction extends Action {
        public final int start;
        public final int stop;
        public TickRangeAction(int start, int stop, Consumer<TIRecipeProcess<R>> logic) {
            super((t, context) -> logic.accept(context));
            this.start = start;
            this.stop = stop;
        }

        @Override
        public void run(int tick, TIRecipeProcess<R> context) {
            if(tick >= start && tick <= stop) super.run(tick, context);
        }
    }
}
