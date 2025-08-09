package org.tcathebluecreper.totally_immersive;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class ManualEntries {
    private static Tree.InnerNode<ResourceLocation, ManualEntry> CATEGORY;
    public static void AddManualEntries() {
        ManualInstance manual = ManualHelper.getManual();
        CATEGORY = manual.getRoot().getOrCreateSubnode(ResourceLocation.fromNamespaceAndPath(MODID, "main_category"), 100);

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(manual);
        builder.addSpecialElement(new ManualEntry.SpecialElementData("chemical_bath", 0, () -> new ManualElementMultiblock(manual, TIMultiblocks.Multiblock.CHEMICAL_BATH.get())));
        builder.readFromFile(ResourceLocation.fromNamespaceAndPath(MODID, "chemical_bath"));
        manual.addEntry(CATEGORY, builder.create(), 0);


        builder = new ManualEntry.ManualEntryBuilder(manual);
        builder.addSpecialElement(new ManualEntry.SpecialElementData("grinder", 0, () -> new ManualElementMultiblock(manual, TIMultiblocks.Multiblock.GRINDER.get())));
        builder.readFromFile(ResourceLocation.fromNamespaceAndPath(MODID, "grinder"));
        manual.addEntry(CATEGORY, builder.create(), 0);


        builder = new ManualEntry.ManualEntryBuilder(manual);
        builder.readFromFile(ResourceLocation.fromNamespaceAndPath(MODID, "concrete_production"));
        manual.addEntry(CATEGORY, builder.create(), 0);
    }
    public static void RegisterModels(ModelEvent.RegisterAdditional ev) {
        ev.register(ResourceLocation.fromNamespaceAndPath(MODID, "multiblocks/chemical_bath"));
    }
}
