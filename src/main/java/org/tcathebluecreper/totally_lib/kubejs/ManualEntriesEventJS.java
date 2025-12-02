package org.tcathebluecreper.totally_lib.kubejs;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.SpecialManualElement;
import blusunrize.lib.manual.Tree;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.multiblock.TLModMultiblocks;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblock;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ManualEntriesEventJS extends EventJS {
    public final Tree.InnerNode<ResourceLocation, ManualEntry> page;
    public final ManualInstance manual;
    public ManualEntriesEventJS(Tree.InnerNode<ResourceLocation, ManualEntry> page, ManualInstance manual) {
        this.page = page;
        this.manual = manual;
    }

    public ManualEntriesEventJS category(ResourceLocation category, double weight) {
        return new ManualEntriesEventJS(page.getOrCreateSubnode(category, () -> weight), manual);
    }

    public ManualEntriesEventJS category(ResourceLocation category) {
        return category(category, 0);
    }

    public ManualEntriesEventJS categories(List<ResourceLocation> categories) {
        AtomicReference<Tree.InnerNode<ResourceLocation, ManualEntry>> page = new AtomicReference<>(this.page);
        categories.forEach(rl -> page.set(page.get().getOrCreateSubnode(rl)));
        return new ManualEntriesEventJS(page.get(), manual);
    }

    public ManualEntry.ManualEntryBuilder builder() {
        return new ManualEntry.ManualEntryBuilder(manual);
    }

    public void addLeaf(ManualEntry entry) {
        addLeaf(entry, 0);
    }

    public void addLeaf(ManualEntry entry, double weight) {
        page.addNewLeaf(entry, () -> weight);
    }

    public ManualEntry.SpecialElementData specialElementData(String name, int offset, Supplier<SpecialManualElement> element) {
        return new ManualEntry.SpecialElementData(name, offset, element);
    }

    @RemapForJS("manualElementMultiblockId")
    public SpecialManualElement manualElementMultiblock(ResourceLocation mb) {
        TLMultiblock multiblock = TLModMultiblocks.byId(mb).getMultiblock();
        if(multiblock == null) {
            throw new IllegalArgumentException("No multiblock with id " + mb + ", if this is the id of a multiblock not added with tlib, provide the multiblock, not the id.");
        }
        return new ManualElementMultiblock(manual, multiblock);
    }
    public SpecialManualElement manualElementMultiblock(MultiblockHandler.IMultiblock multiblock) {
        return new ManualElementMultiblock(manual, multiblock);
    }
}
