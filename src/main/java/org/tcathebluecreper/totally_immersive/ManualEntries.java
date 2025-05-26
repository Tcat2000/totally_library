package org.tcathebluecreper.totally_immersive;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree;
import net.minecraft.resources.ResourceLocation;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class ManualEntries {
    private static Tree.InnerNode<ResourceLocation, ManualEntry> CATEGORY;
    public static void AddManualEntries() {
        ManualInstance manual = ManualHelper.getManual();
        CATEGORY = manual.getRoot().getOrCreateSubnode(ResourceLocation.fromNamespaceAndPath(MODID, "main"), 100);

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(manual);
        builder.addSpecialElement(new ManualEntry.SpecialElementData("chemical_bath", 0, () -> new ManualElementMultiblock(manual, TIContent.TIMultiblocks.Multiblock.CHEMICAL_BATH.get())));
        builder.readFromFile(ResourceLocation.fromNamespaceAndPath(MODID, "chemical_bath"));
        manual.addEntry(CATEGORY, builder.create(), 0);
    }
}
