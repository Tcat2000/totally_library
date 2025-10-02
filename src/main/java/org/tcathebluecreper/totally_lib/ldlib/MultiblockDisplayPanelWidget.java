package org.tcathebluecreper.totally_lib.ldlib;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.common.util.IELogger;
import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.tcathebluecreper.totally_lib.multiblock.TLModMultiblocks;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockInfo;

import java.util.Optional;
import java.util.function.Consumer;

public class MultiblockDisplayPanelWidget extends ScreenSpaceWidget {
    private final SceneWidget scene;
    public TrackedDummyWorld level;

    public Consumer<SceneWidget> postRender;

    public MultiblockDisplayPanelWidget(int minX, int minY, int maxX, int maxY, Consumer<SceneWidget> postRender) {
        super(minX, minY, maxX, maxY);
        this.postRender = postRender;
        
        scene = new SceneWidget(0,0,0,0, level);
        scene.setRenderFacing(false);
        scene.setRenderSelect(false);
        scene.setAfterWorldRender(postRender);
        addWidget(scene);
    }

    public void loadMultiblock(ResourceLocation id) {
        Optional<TLMultiblockInfo> op = TLModMultiblocks.allMultiblocks.stream().filter(m -> m.getId().equals(id)).findFirst();
        if(op.isEmpty()) return;
        TLMultiblockInfo mb = op.get();

        level = new TrackedDummyWorld();
        scene.createScene(level);
        scene.setRenderedCore(mb.getMultiblock().getTemplate(level).blocksWithoutAir().stream().map(StructureTemplate.StructureBlockInfo::pos).toList(), null);

        scene.getRenderer().setOnLookingAt(null);
        scene.setAfterWorldRender(postRender);
        scene.getRenderer().setEndBatchLast(false);

        mb.getMultiblock().getTemplate(level).blocksWithoutAir().forEach(block -> {
            BlockState state = mb.getMultiblock().getBlock().defaultBlockState();
            state = state.setValue(IEProperties.MULTIBLOCKSLAVE, !mb.getMultiblock().masterFromOrigin.equals(block.pos()));
            if (state.hasProperty(IEProperties.MIRRORED)) {
                state = state.setValue(IEProperties.MIRRORED, false);
            }

            if (state.hasProperty(IEProperties.FACING_HORIZONTAL)) {
                state = state.setValue(IEProperties.FACING_HORIZONTAL, Direction.NORTH);
            }

            level.setBlockAndUpdate(block.pos(), state);
            BlockEntity curr = level.getBlockEntity(block.pos());
            if (curr instanceof MultiblockBlockEntityDummy<?> dummy) {
                dummy.getHelper().setPositionInMB(block.pos());
            } else if (!(curr instanceof MultiblockBlockEntityMaster)) {
                IELogger.logger.error("Expected mb.getMultiblock() TE at {} during placement", block.pos());
            }
        });
    }

    public TextFieldWidget crateInputField(int posX, int posY, int sizeX, int sizeY) {
        return new TextFieldWidget(posX, posY, sizeX, sizeY, null, null).setTextResponder(text -> {
            if(ResourceLocation.isValidResourceLocation(text)) {
                ResourceLocation id = ResourceLocation.parse(text);
                this.loadMultiblock(id);
            }
        });
    }

    @Override
    protected void sizeChanged(int width, int height) {
        scene.setSize(width - 8, height - 8);
        scene.setSelfPosition(4,4);
    }
}
