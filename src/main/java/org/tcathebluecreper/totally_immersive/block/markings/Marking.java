package org.tcathebluecreper.totally_immersive.block.markings;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public abstract class Marking implements Comparable<Marking> {
    protected static final List<Marking> ALL_MARKINGS = new ArrayList<>();
    public static final Registry<Marking> REGISTRY = new Registry<Marking>() {
        @Override
        public ResourceKey<? extends Registry<Marking>> key() {
            return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "markings"));
        }

        @Nullable
        @Override
        public ResourceLocation getKey(Marking p_123006_) {
            return null;
        }

        @Override
        public Optional<ResourceKey<Marking>> getResourceKey(Marking p_123008_) {
            return Optional.empty();
        }

        @Override
        public int getId(@Nullable Marking p_122977_) {
            return 0;
        }

        @Nullable
        @Override
        public Marking get(@Nullable ResourceKey<Marking> p_122980_) {
            return null;
        }

        @Nullable
        @Override
        public Marking get(@Nullable ResourceLocation p_123002_) {
            return null;
        }

        @Override
        public Lifecycle lifecycle(Marking p_123012_) {
            return null;
        }

        @Override
        public Lifecycle registryLifecycle() {
            return null;
        }

        @Override
        public Set<ResourceLocation> keySet() {
            return null;
        }

        @Override
        public Set<Map.Entry<ResourceKey<Marking>, Marking>> entrySet() {
            return null;
        }

        @Override
        public Set<ResourceKey<Marking>> registryKeySet() {
            return null;
        }

        @Override
        public Optional<Holder.Reference<Marking>> getRandom(RandomSource p_235781_) {
            return Optional.empty();
        }

        @Override
        public boolean containsKey(ResourceLocation p_123011_) {
            return false;
        }

        @Override
        public boolean containsKey(ResourceKey<Marking> p_175475_) {
            return false;
        }

        @Override
        public Registry<Marking> freeze() {
            return null;
        }

        @Override
        public Holder.Reference<Marking> createIntrusiveHolder(Marking p_206068_) {
            return null;
        }

        @Override
        public Optional<Holder.Reference<Marking>> getHolder(int p_206051_) {
            return Optional.empty();
        }

        @Override
        public Optional<Holder.Reference<Marking>> getHolder(ResourceKey<Marking> p_206050_) {
            return Optional.empty();
        }

        @Override
        public Holder<Marking> wrapAsHolder(Marking p_263382_) {
            return null;
        }

        @Override
        public Stream<Holder.Reference<Marking>> holders() {
            return null;
        }

        @Override
        public Optional<HolderSet.Named<Marking>> getTag(TagKey<Marking> p_206052_) {
            return Optional.empty();
        }

        @Override
        public HolderSet.Named<Marking> getOrCreateTag(TagKey<Marking> p_206045_) {
            return null;
        }

        @Override
        public Stream<Pair<TagKey<Marking>, HolderSet.Named<Marking>>> getTags() {
            return null;
        }

        @Override
        public Stream<TagKey<Marking>> getTagNames() {
            return null;
        }

        @Override
        public void resetTags() {

        }

        @Override
        public void bindTags(Map<TagKey<Marking>, List<Holder<Marking>>> p_205997_) {

        }

        @Override
        public HolderOwner<Marking> holderOwner() {
            return null;
        }

        @Override
        public HolderLookup.RegistryLookup<Marking> asLookup() {
            return null;
        }

        @Nullable
        @Override
        public Marking byId(int p_122651_) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @NotNull
        @Override
        public Iterator<Marking> iterator() {
            return null;
        }
    };

    public Marking() {
        ALL_MARKINGS.add(this);
    }

    public abstract String name();

    @Override
    public int compareTo(@NotNull Marking o) {
        return 0;
    }
}
