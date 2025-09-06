package org.tcathebluecreper.totally_lib.crafting;

import org.tcathebluecreper.totally_lib.recipe.provider.Provider;

import java.util.ArrayList;
import java.util.Optional;

public class ProviderList<P extends Provider<?>> extends ArrayList<P> {
    public Optional<P> get(String field) {
        return stream().filter(pProvider -> pProvider.field.equals(field)).findFirst();
    }
}
