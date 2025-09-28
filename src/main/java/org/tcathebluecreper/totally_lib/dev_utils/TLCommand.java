package org.tcathebluecreper.totally_lib.dev_utils;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.kubejs.Plugin;
import org.tcathebluecreper.totally_lib.kubejs.TLMultiblockRegistrationEventJS;
import org.tcathebluecreper.totally_lib.multiblock.ModMultiblocks;

import java.io.IOException;
import java.util.Optional;

public class TLCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tl_utils").requires(s -> s.hasPermission(2)).then(
            Commands.literal("selection")
            .executes(context -> {
                return 0;
            })
            .then(Commands.argument("pos1", BlockPosArgument.blockPos())
                .executes(context -> {
                    if((SelectionManager.firstPos == null) == (SelectionManager.secondPos == null)) SelectionManager.firstPos = BlockPosArgument.getBlockPos(context, "pos1");
                    else SelectionManager.secondPos = BlockPosArgument.getBlockPos(context, "pos1");
                    return 0;
                })
                .then(Commands.argument("pos2", BlockPosArgument.blockPos())
                    .executes(context -> {
                        SelectionManager.firstPos = BlockPosArgument.getBlockPos(context, "pos1");
                        SelectionManager.secondPos = BlockPosArgument.getBlockPos(context, "pos2");
                        return 0;
                    })
                )
            )
                .then(Commands.literal("clear"))
            )
                .then(Commands.literal("generate")
                    .then(Commands.literal("form").then(Commands.argument("structure", ResourceLocationArgument.id()).then(Commands.argument("offset", Vec3Argument.vec3())
                        .executes(context -> {
                            Optional<Resource> st = StructureArg.getStructure(context, "structure", "structures", ".nbt");
                            if(st.isEmpty()) return 0;
    
                            try {
                                StringBuilder outputList = new StringBuilder();
                                outputList.append(".form([");
    
                                CompoundTag tag = NbtIo.readCompressed(st.get().open());
                                Vec3 offset = Vec3Argument.getVec3(context, "offset");
                                for(Tag block : (ListTag) tag.get("blocks")) {
                                    ListTag pos = ((CompoundTag) block).getList("pos", Tag.TAG_INT);
                                    outputList.append("[" + (pos.getInt(0) - offset.x) + "," + (pos.getInt(1) - offset.y) + "," + (pos.getInt(2) - offset.z) + "],");
                                }
    
                                outputList.append("])");
                                String outputText = outputList.toString();
                                context.getSource().getPlayer().sendSystemMessage(
                                    Component.literal("Click to copy: ").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, outputText))).append(
                                        Component.literal(outputText).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, outputText)))
                                    )
                                );
                            } catch(IOException e) {
                                throw new RuntimeException(e);
                            }
                            return 1;
                        })
                    )))
            )
                .then(Commands.literal("reload").executes(context -> {
                    try {
                        Plugin.multiblockRegisterEventJS.post(new TLMultiblockRegistrationEventJS(TotallyLibrary.regManager, ModMultiblocks.allMultiblocks::add, true));
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                    return 1;
                }))
        );
        dispatcher.register(Commands.literal("tl_editor").executes(context -> {

            /// copied from MBD2

            var holder = new IUIHolder() {
                @Override
                public ModularUI createUI(Player entityPlayer) {
                    return null;
                }

                @Override
                public boolean isInvalid() {
                    return true;
                }

                @Override
                public boolean isRemote() {
                    return true;
                }

                @Override
                public void markAsDirty() {

                }
            };

            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer entityPlayer = minecraft.player;
            ModularUI uiTemplate  = new ModularUI(holder, entityPlayer).widget(new MultiblockEditor());
            uiTemplate.initWidgets();
            ModularUIGuiContainer ModularUIGuiContainer = new ModularUIGuiContainer(uiTemplate, entityPlayer.containerMenu.containerId);
            minecraft.setScreen(ModularUIGuiContainer);
            entityPlayer.containerMenu = ModularUIGuiContainer.getMenu();

            return 1;
        }));
    }
}
