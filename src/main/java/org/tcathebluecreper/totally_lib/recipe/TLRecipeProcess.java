package org.tcathebluecreper.totally_lib.recipe;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriFunction;
import org.tcathebluecreper.totally_lib.crafting.TIAPIException;

import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;


public class TLRecipeProcess<R extends TLRecipe, S extends IMultiblockState> {
    public final Class<R> type;
    public final List<Action<R, S>> actions;
    public final S state;
    public final BiFunction<TLRecipeProcess<R, S>, Integer, Boolean> tickLogic;
    public int[] tick;
    private boolean needsCheck = true;
    public boolean[] stopped; /// If the machine is disabled, i.e., by redstone control
    public boolean[] stuck; /// If the machine is stuck mid-process, i.e., by running out of power

    public final int maxParallel;
    public final boolean allowDifferentRecipes;

    public R[] recipe;

    public TLRecipeProcess(Class<R> type, List<Action<R, S>> actions, S state, BiFunction<TLRecipeProcess<R, S>, Integer, Boolean> tickLogic) {
        this.type = type;
        this.actions = actions;
        this.state = state;
        this.tickLogic = tickLogic;

        recipe = (R[]) Array.newInstance(type, 1);
        stuck = new boolean[1];
        stopped = new boolean[1];
        tick = new int[1];
        maxParallel = 1;
        allowDifferentRecipes = false;
    }
    public TLRecipeProcess(Class<R> type, List<Action<R, S>> actions, S state, BiFunction<TLRecipeProcess<R, S>, Integer, Boolean> tickLogic, int initialTick) {
        this.type = type;
        this.actions = actions;
        this.state = state;
        this.tickLogic = tickLogic;
        this.tick = new int[]{initialTick};

        recipe = (R[]) Array.newInstance(type, 1);
        stuck = new boolean[1];
        stopped = new boolean[1];
        tick = new int[1];
        maxParallel = 1;
        allowDifferentRecipes = false;
    }
    public TLRecipeProcess(Class<R> type, List<Action<R, S>> actions, S state, BiFunction<TLRecipeProcess<R, S>, Integer, Boolean> tickLogic, int[] initialTick, int maxParallel, boolean allowDifferentRecipes) {
        this.type = type;
        this.actions = actions;
        this.state = state;
        this.tickLogic = tickLogic;
        if(initialTick.length != maxParallel) throw new TIAPIException("TIRecipeProcess can only be instantiated with the same number of initial tick INTs as maxParallel");

        this.maxParallel = maxParallel;
        this.allowDifferentRecipes = allowDifferentRecipes;
        recipe = (R[]) Array.newInstance(type, allowDifferentRecipes ? maxParallel : 1);
        stuck = new boolean[maxParallel];
        stopped = new boolean[maxParallel];
        tick = new int[maxParallel];
    }
    public TLRecipeProcess(Class<R> type, List<Action<R, S>> actions, S state, BiFunction<TLRecipeProcess<R, S>, Integer, Boolean> tickLogic, int maxParallel, boolean allowDifferentRecipes) {
        this.type = type;
        this.actions = actions;
        this.state = state;
        this.tickLogic = tickLogic;

        this.maxParallel = maxParallel;
        this.allowDifferentRecipes = allowDifferentRecipes;
        recipe = (R[]) Array.newInstance(type, allowDifferentRecipes ? maxParallel : 1);
        stuck = new boolean[maxParallel];
        stopped = new boolean[maxParallel];
        tick = new int[maxParallel];
    }

    public void tick(Level level) {
        if(tick.length == 0 || recipe.length != (allowDifferentRecipes ? maxParallel : 1) || stuck.length == 0 || stopped.length == 0) return;
        if(allowDifferentRecipes) {
            for(int p = 0; p < maxParallel; p++) {
                if(tick[p] == 0 && needsCheck || tick[p] != 0 && recipe[p] == null) {
                    recipe[p] = findRecipe(level);
                }
            }
        }
//        else {
//            if(recipe[0] == null || needsCheck) {
//                if(checkAnyRunning()) resumeRecipe(level);
//                else recipe[0] = findRecipe(level);
//            }
//        }
//        needsCheck = false;
        for(int p = 0; p < maxParallel; p++) {
            if(recipe[getRecipeIndex(p)] == null) continue;
            stuck[p] = !tickLogic.apply(this, p);
            int P = getRecipeIndex(p);
            if(recipe[P] != null && !stopped[p]) {
                int finalP = p;
                AtomicBoolean finished = new AtomicBoolean(false);
                actions.forEach((action) -> {
                    if(action.run(tick[finalP], this, finalP, recipe.length)) {
                        finished.set(true);
                        tick[finalP] = 0;
                    }
                });
                if(!stuck[p] && !finished.get()) tick[p]++;
            }
        }
    }

    public int getRecipeIndex(int parallel) {
        return allowDifferentRecipes ? parallel : 0;
    }

    public boolean checkAnyRunning() {
        for(int i : tick) if(i != 0) return true;
        return false;
    }

    public void setWorking(boolean working) {
        stuck[0] = !working;
    }

    public void setWorking(int parallel, boolean working) {
        stuck[parallel] = !working;
    }

    public void setEnabled(boolean enabled) {
        stopped[0] = !enabled;
    }

    public void setEnabled(int parallel, boolean enabled) {
        stopped[parallel] = !enabled;
    }

    public void triggerUpdate() {
        needsCheck = true;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeVarIntArray(tick);

        buf.writeVarInt(stopped.length);
        for(boolean b : stopped) {
            buf.writeBoolean(b);
        }

        buf.writeVarInt(stuck.length);
        for(boolean b : stuck) {
            buf.writeBoolean(b);
        }

        buf.writeVarInt(recipe.length);
        for(R r : recipe) {
            buf.writeResourceLocation(r.id);
        }
    }
    public void fromNetwork(FriendlyByteBuf buf) {
        tick = buf.readVarIntArray();

        int len = buf.readVarInt();
        boolean[] array = new boolean[len];
        for(int j = 0; j < array.length; ++j) {
            array[j] = buf.readBoolean();
        }
        stopped = array;

        len = buf.readVarInt();
        array = new boolean[len];
        for(int j = 0; j < array.length; ++j) {
            array[j] = buf.readBoolean();
        }
        stuck = array;

        len = buf.readVarInt();
        recipe = (R[]) Array.newInstance(type, len);
        for(int j = 0; j < array.length; ++j) {
            recipe[j] = buf.readResourceLocation();
        }
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("tick", tick);

        byte[] array = new byte[stopped.length];
        for(int j = 0; j < array.length; ++j) {
            array[j] = (byte) (stopped[j] ? 1 : 0);
        }
        ByteArrayTag stoppedArray = new ByteArrayTag(array);

        tag.put("stopped", stoppedArray);

        array = new byte[stuck.length];
        for(int j = 0; j < array.length; ++j) {
            array[j] = (byte) (stuck[j] ? 1 : 0);
        }
        stoppedArray = new ByteArrayTag(array);

        tag.put("stuck", stoppedArray);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        tick = tag.getIntArray("tick");

        byte[] array = tag.getByteArray("stopped");
        for(int j = 0; j < array.length; ++j) {
            stopped[j] = array[j] == 1;
        }

        array = tag.getByteArray("stuck");
        for(int j = 0; j < array.length; ++j) {
            stuck[j] = array[j] == 1;
        }
    }

    public static class Action<R extends TLRecipe, S extends IMultiblockState> {
        public final TriFunction<Integer, TLRecipeProcess<R, S>, Integer, Boolean> logic;

        public Action(TriFunction<Integer, TLRecipeProcess<R, S>, Integer, Boolean> logic) {
            this.logic = logic;
        }
        public boolean run(int tick, TLRecipeProcess<R, S> context, int parallel, int length) {
            return logic.apply(tick, context, parallel);
        }
    }
    public static class TickAction<R extends TLRecipe, S extends IMultiblockState> extends Action<R, S> {
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
            if(this.tick == tick || length - this.tick - 1 == tick) return super.run(this.tick, context, parallel, length);
            return false;
        }
    }
    public static class TickRangeAction<R extends TLRecipe, S extends IMultiblockState> extends Action<R, S> {
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
    public R findRecipe(Level level) {
        try {
            return (R) TLRecipeSerializer.RecipeFineders.get(type).apply(state, level);
        } catch(ClassCastException e) {
            throw new TIAPIException("A serializer for recipe " + type + " was initialized with an invalid type class, should the the same as the recipe it is for.");
        }
    }
//    public R resumeRecipe(Level level) {
//        try {
//            return (R) TLRecipeSerializer.RecipeResumers.get(type).apply(state, level);
//        } catch(ClassCastException e) {
//            throw new TIAPIException("A serializer for recipe " + type + " was initialized with an invalid type class, should the the same as the recipe it is for.");
//        }
//    }

    public IProbeInfo displayBars(IProbeInfo info, int maxTime, boolean hideEmptyBars, boolean displayMaxProcesses) {
        int count = 0;
        for(int i = 0; i < maxParallel; i++) {
            if(tick[i] != 0 || !hideEmptyBars) {
                info.progress(tick[i], maxTime);
                count++;
            }
        }
        if(displayMaxProcesses) info.text(Component.translatable("top.totally_immersive.process.max_processes").append(" " + count + "/" + maxParallel));
        return info;
    }
}
