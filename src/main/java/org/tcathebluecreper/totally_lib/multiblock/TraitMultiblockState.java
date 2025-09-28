package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tcathebluecreper.totally_lib.multiblock.trait.ITrait;
import org.tcathebluecreper.totally_lib.multiblock.trait.TraitList;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class TraitMultiblockState implements IMultiblockState {
    private static final Logger log = LoggerFactory.getLogger(TraitMultiblockState.class);
    public final TraitList traits;
    public CompoundTag customData = new CompoundTag();
    private Supplier<Level> levelSupplier;
    public Level getLevel() {return levelSupplier.get();}

    public TraitMultiblockState(TraitList traits) {
        this.traits = traits;
    }

    public TraitMultiblockState(IInitialMultiblockContext capSource, List<ITrait> traits) {
        this.traits = new TraitList(traits);
        this.traits.setOnValueChanged(trait -> capSource.getSyncRunnable().run());
        levelSupplier = capSource.levelSupplier();
    }


    @Override
    public void writeSaveNBT(CompoundTag nbt) {
        nbt.put("data", customData);
        traits.save(nbt);
    }

    @Override
    public void writeSyncNBT(CompoundTag nbt) {
        nbt.put("data", customData);
        traits.save(nbt);
    }

    @Override
    public void readSaveNBT(CompoundTag nbt) {
        customData = nbt.getCompound("data");
        traits.load(nbt);
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        customData = nbt.getCompound("data");
        traits.load(nbt);
    }

    public ITrait getTrait(String id) {
        try {
            return traits.get(id).get();
        } catch(NoSuchElementException e) {
            log.error("Recipe does not have provider '{}': {}", id, e);
            return null;
        }
    }
}
