package org.tcathebluecreper.totally_immersive;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.tcathebluecreper.totally_immersive.item.SprayCanItem;
import org.tcathebluecreper.totally_immersive.item.TrackBlueprintsItem;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class TIItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);

    public static final RegistryObject<SprayCanItem> SPRAY_CAN = ITEMS.register("spray_can", () -> new SprayCanItem(new Item.Properties()));
    public static final RegistryObject<TrackBlueprintsItem> TRACK_BLUEPRINTS = ITEMS.register("track_blueprints", TrackBlueprintsItem::new);
    public static final RegistryObject<Item> CEMENT_ITEM = ITEMS.register("cement", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CEMENT_POWDER_ITEM = ITEMS.register("cement_powder", () -> new Item(new Item.Properties()));
}