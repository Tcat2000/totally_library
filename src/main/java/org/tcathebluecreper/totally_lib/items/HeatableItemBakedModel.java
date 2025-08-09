package org.tcathebluecreper.totally_lib.items;

import net.minecraftforge.client.model.BakedModelWrapper;

public class HeatableItemBakedModel extends BakedModelWrapper<HeatableItemBakedModel> {
    public HeatableItemBakedModel(HeatableItemBakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }
}
