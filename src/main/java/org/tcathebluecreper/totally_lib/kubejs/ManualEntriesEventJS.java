package org.tcathebluecreper.totally_lib.kubejs;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.SpecialManualElement;
import blusunrize.lib.manual.Tree;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.ldlib.ManualElementLdlib;
import org.tcathebluecreper.totally_lib.multiblock.TLModMultiblocks;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblock;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ManualEntriesEventJS extends EventJS {
    public final Tree.InnerNode<ResourceLocation, ManualEntry> page;
    public final ManualInstance manual;
    public ManualEntriesEventJS(Tree.InnerNode<ResourceLocation, ManualEntry> page, ManualInstance manual) {
        this.page = page;
        this.manual = manual;
    }

    @Info("Adds a new category or gets and existing category with the provided id. Weight, if provided, affects where on the list the category is.")
    public ManualEntriesEventJS category(ResourceLocation category, double weight) {
        return new ManualEntriesEventJS(page.getOrCreateSubnode(category, () -> weight), manual);
    }

    @Info("Adds a new category or gets and existing category with the provided id. Weight, if provided, affects where on the list the category is.")
    public ManualEntriesEventJS category(ResourceLocation category) {
        return category(category, 0);
    }

    @Info("Adds a new category or gets and existing category with the provided id, once for each element")
    public ManualEntriesEventJS categories(List<ResourceLocation> categories) {
        AtomicReference<Tree.InnerNode<ResourceLocation, ManualEntry>> page = new AtomicReference<>(this.page);
        categories.forEach(rl -> page.set(page.get().getOrCreateSubnode(rl)));
        return new ManualEntriesEventJS(page.get(), manual);
    }

    @Info("Creates a page builder. Does NOT need to be run on the category it will be added to. End with .create()")
    public ManualEntry.ManualEntryBuilder builder() {
        return new ManualEntry.ManualEntryBuilder(manual);
    }

    @Info("Adds the page to to current category. Weight, if provided, affects where on the list the page is.")
    public void addPage(ManualEntry entry) {
        addPage(entry, 0);
    }

    @Info("Adds the page to to current category. Weight, if provided, affects where on the list the page is.")
    public void addPage(ManualEntry entry, double weight) {
        page.addNewLeaf(entry, () -> weight);
    }

    @Info("Adds a special element, can be provided from any of `manualElement...()`, or custom (advanced)")
    public ManualEntry.SpecialElementData specialElementData(String name, int offset, Supplier<SpecialManualElement> element) {
        return new ManualEntry.SpecialElementData(name, offset, element);
    }

    @Info("Creates a multiblock panel using the id. Id MUST be of a machine added with Totally Library. For non-tlib multiblocks, use `manualElementMultiblock`.")
    @RemapForJS("manualElementMultiblockId")
    public ManualElementMultiblock manualElementMultiblock(ResourceLocation mb) {
        TLMultiblock multiblock = TLModMultiblocks.byId(mb).getMultiblock();
        if(multiblock == null) {
            throw new IllegalArgumentException("No multiblock with id " + mb + ", if this is the id of a multiblock not added with tlib, provide the multiblock, not the id.");
        }
        return new ManualElementMultiblock(manual, multiblock);
    }
    @Info("Creates a multiblock panel using the multiblock. The machine can be gotten from other mod's ModMultiblock classes (advanced)")
    public ManualElementMultiblock manualElementMultiblock(MultiblockHandler.IMultiblock multiblock) {
        return new ManualElementMultiblock(manual, multiblock);
    }

    @Info("Allows for custom LDLib ui in the manual")
    public ManualElementLdlib manualElementLdlib(int yOffset, ManualElementLdlib.ManualElementWidgetCreator creator) {
        return new ManualElementLdlib(yOffset, creator);
    }
    @Info("Allows for custom LDLib ui in the manual")
    public ManualElementLdlib manualElementLdlib(int yOffset, ManualElementLdlib.ManualElementWidgetCreator creator, Predicate<String> searchCheck) {
        return new ManualElementLdlib(yOffset, creator, searchCheck);
    }
}
