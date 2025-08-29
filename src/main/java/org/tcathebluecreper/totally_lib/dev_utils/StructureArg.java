package org.tcathebluecreper.totally_lib.dev_utils;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.tcathebluecreper.totally_lib.TotallyLibrary;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class StructureArg implements ArgumentType<ResourceLocation> {
    private static RegistryObject<ArgumentTypeInfo<?, ?>> self;
    public static StructureArg instance;
    public static StructureArg getArg() {
        return instance;
    }
    public static void init() {
        instance = structure();
        self = TotallyLibrary.regManager.register(ForgeRegistries.COMMAND_ARGUMENT_TYPES.getRegistryKey(), TotallyLibrary.MODID, "structure_arg", () -> SingletonArgumentInfo.contextFree(() -> instance));
    }
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for(Map.Entry<ResourceLocation, Resource> r : TotallyLibrary.server.getResourceManager().listResources("structures", (r) -> true).entrySet()) {
            builder.suggest(r.toString());
        }
        return builder.buildFuture();
    }

    public ResourceLocation parse(StringReader pReader) throws CommandSyntaxException {
        return ResourceLocation.read(pReader);
    }

    public static Optional<Resource> getStructure(CommandContext<CommandSourceStack> context, String name) {
        return TotallyLibrary.server.getResourceManager().getResource(ResourceLocationArgument.getId(context, name));
    }

    public static Optional<Resource> getStructure(CommandContext<CommandSourceStack> context, String name, String folder, String extension) {
        return TotallyLibrary.server.getResourceManager().getResource(ResourceLocationArgument.getId(context, name).withPrefix(folder).withSuffix(extension));
    }

    public static StructureArg structure() {
        return new StructureArg();
    }
}
