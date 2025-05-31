package org.tcathebluecreper.totally_immersive;

import net.minecraft.core.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.tcathebluecreper.totally_immersive.block.markings.Marking;
import org.tcathebluecreper.totally_immersive.block.markings.MarkingBlock;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerator {
    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        System.out.println("generating data");
        net.minecraft.data.DataGenerator gen = event.getGenerator();
        ExistingFileHelper efh = event.getExistingFileHelper();
        gen.addProvider(
                event.includeClient(),
                new BlockModelProvider(event.getGenerator().getPackOutput(), MODID, efh) {
                    @Override
                    protected void registerModels() {
                        Marking.ALL_MARKINGS.forEach(marking -> {
                            if(marking.model() == null) {
                                if(marking.texture() == null) throw new RuntimeException("Marking " + marking.name() + " Does not have assigned model or texture; cannot auto generate");

                                this.withExistingParent("totally_immersive:block/marking/template", "markings:block/marking/" + marking.name())
                                        .texture("texture", marking.texture());

                            }
                        });
                    }
                }
        );
        gen.addProvider(
                event.includeClient(),
                new BlockStateProvider(event.getGenerator().getPackOutput(), MODID, efh) {
                    @Override
                    protected void registerStatesAndModels() {
                        MultiPartBlockStateBuilder builder = this.getMultipartBuilder(TIContent.TIBlocks.MARKINGS_BLOCK.get());

                        Marking.ALL_MARKINGS.forEach(marking -> {
                            if(marking.allowOnSide(Direction.DOWN)) builder
                                    .part()
                                    .modelFile(new BlockModelBuilder(marking.model(), efh))
                                    .addModel()
                                    .condition(MarkingBlock.MARKING_BOTTOM, TIContent.TIBlocks.STRIPES_YELLOW)
                                    .end();
                            if(marking.allowOnSide(Direction.UP)) builder
                                    .part()
                                    .modelFile(new BlockModelBuilder(marking.model(), efh))
                                    .addModel()
                                    .condition(MarkingBlock.MARKING_TOP, TIContent.TIBlocks.STRIPES_YELLOW)
                                    .end();
                            if(marking.allowOnSide(Direction.NORTH)) builder
                                    .part()
                                    .modelFile(new BlockModelBuilder(marking.sideModel(), efh))
                                    .addModel()
                                    .condition(MarkingBlock.MARKING_NORTH, TIContent.TIBlocks.STRIPES_YELLOW)
                                    .end();
                            if(marking.allowOnSide(Direction.EAST)) builder
                                    .part()
                                    .modelFile(new BlockModelBuilder(marking.sideModel(), efh))
                                    .addModel()
                                    .condition(MarkingBlock.MARKING_EAST, TIContent.TIBlocks.STRIPES_YELLOW)
                                    .end();
                            if(marking.allowOnSide(Direction.SOUTH)) builder
                                    .part()
                                    .modelFile(new BlockModelBuilder(marking.sideModel(), efh))
                                    .addModel()
                                    .condition(MarkingBlock.MARKING_SOUTH, TIContent.TIBlocks.STRIPES_YELLOW)
                                    .end();
                            if(marking.allowOnSide(Direction.WEST)) builder
                                    .part()
                                    .modelFile(new BlockModelBuilder(marking.sideModel(), efh))
                                    .addModel()
                                    .condition(MarkingBlock.MARKING_WEST, TIContent.TIBlocks.STRIPES_YELLOW)
                                    .end();
                            System.out.println("generated blockstate file" + builder.toJson());
                        });
                    }
                }
        );
        }
}
