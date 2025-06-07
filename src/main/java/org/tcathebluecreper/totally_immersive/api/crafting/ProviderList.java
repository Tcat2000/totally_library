package org.tcathebluecreper.totally_immersive.api.crafting;

import java.util.ArrayList;
import java.util.Optional;

public class ProviderList<P extends TIRecipeSerializer.Provider<?>> extends ArrayList<P> {
    public Optional<P> get(String field) {
        return stream().filter(pProvider -> pProvider.field.equals(field)).findFirst();
    }
}
