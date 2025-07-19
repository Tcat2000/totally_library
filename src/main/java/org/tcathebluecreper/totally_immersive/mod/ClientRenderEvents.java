package org.tcathebluecreper.totally_immersive.mod;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientRenderEvents {
    int v = 0;
    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
//        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
//            return;
//
//        PoseStack ms = event.getPoseStack();
//        ms.pushPose();
//        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//        PoseStack stack = event.getPoseStack();
//        VertexConsumer linesBuffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES);
//        event.getLevelRenderer().


//        LevelRenderer.renderLineBox(stack, linesBuffer, new AABB(0,0,0,1,1,1), 1,1,1,1);

//        TrackBlockOutline.drawCurveSelection(ms, buffer, camera);

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void MouseScrollEvent(InputEvent.MouseScrollingEvent event) {
        v = (int) event.getMouseY();
//        Player player = Minecraft.getInstance().player;
//        double scroll = event.getDeltaY();
//
//        if(player == null || !modeKeyCombDown(player) || scroll == 0) return;
//
//        ItemStack wand = WandUtil.holdingWand(player);
//        if(wand == null) return;
//
//        WandOptions wandOptions = new WandOptions(wand);
//        wandOptions.lock.next(scroll < 0);
//        ModMessages.sendToServer(new PacketWandOption(wandOptions.lock, true));
//        event.setCanceled(true);
    }
}
