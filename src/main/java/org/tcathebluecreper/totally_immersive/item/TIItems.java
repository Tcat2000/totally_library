package org.tcathebluecreper.totally_immersive.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class TIItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);

    public static final RegistryObject<SprayCanItem> SPRAY_CAN = ITEMS.register("spray_can", () -> new SprayCanItem(new Item.Properties()));
    public static final RegistryObject<TrackBlueprintsItem> TRACK_BLUEPRINTS = ITEMS.register("track_blueprints.json", TrackBlueprintsItem::new);
}