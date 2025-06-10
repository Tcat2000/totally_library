package org.tcathebluecreper.totally_immersive.api.crafting;

import org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath.ChemicalBathState;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;


public class TIRecipeProcess<R extends TIRecipe> {
    public final Class<R> type;
    public final List<Action<R>> actions;
    public final ChemicalBathState state;
    public final Function<TIRecipeProcess<R>, Boolean> tickLogic;
    public int tick = 0;
    private boolean needsCheck = true;
    public boolean stopped;
    public boolean stuck;

    public R recipe;

    public TIRecipeProcess(Class<R> type, List<Action<R>> actions, ChemicalBathState state, Function<TIRecipeProcess<R>, Boolean> tickLogic) {
        this.type = type;
        this.actions = actions;
        this.state = state;
        this.tickLogic = tickLogic;
    }
    public TIRecipeProcess(Class<R> type, List<Action<R>> actions, ChemicalBathState state, Function<TIRecipeProcess<R>, Boolean> tickLogic, int initialTick) {
        this.type = type;
        this.actions = actions;
        this.state = state;
        this.tickLogic = tickLogic;
        this.tick = initialTick;
    }

    public void tick() {

        if(tickLogic.apply(this)) {
            actions.forEach((action) -> action.run(tick, this));
            tick++;
        }
    }

    public void setEnabled(boolean enabled) {
        stuck = !enabled;
    }

    public void setRunning(boolean running) {
        stopped = !running;
    }

    public void triggerUpdate() {
        needsCheck = true;
    }

    public static class Action<R extends TIRecipe> {
        public final BiConsumer<Integer, TIRecipeProcess<R>> logic;

        public Action(BiConsumer<Integer, TIRecipeProcess<R>> logic) {
            this.logic = logic;
        }
        public void run(int tick, TIRecipeProcess<R> context) {
            logic.accept(tick, context);
        }
    }
    public static class TickAction<R extends TIRecipe> extends Action<R> {
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
    public static class TickRangeAction<R extends TIRecipe> extends Action<R> {
        public final int start;
        public final int stop;
        public TickRangeAction(int start, int stop, Consumer<TIRecipeProcess<R>> logic) {
            super((t, context) -> logic.accept((TIRecipeProcess<R>) context));
            this.start = start;
            this.stop = stop;
        }

        @Override
        public void run(int tick, TIRecipeProcess<R> context) {
            if((tick >= start && tick <= stop) || start == stop) super.run(tick, context);
        }
    }
    public R findRecipe() {
        return (R) TIRecipeSerializer.SERIALIZERS.get(type).apply(state);
    }
}
