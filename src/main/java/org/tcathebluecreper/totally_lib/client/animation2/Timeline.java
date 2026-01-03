package org.tcathebluecreper.totally_lib.client.animation2;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.tcathebluecreper.totally_lib.lib.AnimationUtils;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class Timeline<TYPE extends Frame> {
    protected ArrayList<TYPE> list;

    public void addFrame(TYPE frame) {
        list.add(frame);
        sort();
    }

    public void removeFrame(TYPE frame) {
        list.remove(frame);
    }

    public void sort() {
        list.sort(Comparator.comparingInt(a -> a.frame));
    }

    public abstract void applyFrameTranslations(int frame, PoseStack poseStack, AnimationInstance instance, AnimationElement element);

    public static class PosTimeline extends Timeline<Frame> {
        public void applyFrameTranslations(int frame, PoseStack poseStack, AnimationInstance instance, AnimationElement element) {
            int nextFrame = frame;
            ElementState state = instance.states.get(element);

            Frame startFrame = null;
            Frame endFrame = null;
            for(int i = 0; i < list.size(); i++) {
                if(list.get(i).frame >= frame) {
                    endFrame = list.get(i);
                    break;
                }
            }
            for(int i = list.size() - 1; i > 0 ; i--) {
                if(list.get(i).frame <= nextFrame) {
                    startFrame = list.get(i);
                    break;
                }
            }

            poseStack.pushPose();
            try {
                if(startFrame != null) {
                    Frame pos;
                    if(endFrame != null) {
                        pos = ((Vector4f)startFrame.clone()).lerp(endFrame, AnimationUtils.amount(frame - startFrame.frame, endFrame.frame - startFrame.frame));
                    }
                    else pos = startFrame;
                    poseStack.translate(pos.value.x, pos.value.y, pos.value.z);
                }
            } catch(CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

    }
    public static class RotationTimeline extends Timeline<OriginFrame> {
        public void applyFrameTranslations(int frame, PoseStack poseStack, AnimationInstance instance, AnimationElement element) {
            int nextFrame = frame;
            ElementState state = instance.states.get(element);

            Frame startFrame = null;
            Frame endFrame = null;
            for(int i = 0; i < list.size(); i++) {
                if(list.get(i).frame >= frame) {
                    endFrame = list.get(i);
                    break;
                }
            }
            for(int i = list.size() - 1; i > 0 ; i--) {
                if(list.get(i).frame <= nextFrame) {
                    startFrame = list.get(i);
                    break;
                }
            }

            poseStack.pushPose();
            try {
                if(startFrame != null) {
                    Vector4f rot;
                    if(endFrame != null) {
                        rot = ((Vector4f)startFrame.clone()).lerp(endFrame, AnimationUtils.amount(frame - startFrame.w, endFrame.w - startFrame.w));
                    }
                    else rot = startFrame;
                    Vector3f origin = list.get(rotIndex).getOrigin();
                    poseStack.translate(origin.x, origin.y, origin.z);
                    poseStack.mulPose(new Quaternionf().rotateAxis(rot.x * Mth.DEG_TO_RAD, 1,0,0));
                    poseStack.mulPose(new Quaternionf().rotateAxis(rot.y * Mth.DEG_TO_RAD, 0,1,0));
                    poseStack.mulPose(new Quaternionf().rotateAxis(rot.z * Mth.DEG_TO_RAD, 0,0,1));
                    poseStack.translate(-origin.x, -origin.y, -origin.z);
                }
            } catch(CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static class ScaleTimeline extends Timeline<OriginFrame> {
        public void applyFrameTranslations(int frame, PoseStack poseStack, AnimationInstance instance, AnimationElement element) {
            int nextFrame = frame;
            ElementState state = instance.states.get(element);

            Frame startFrame = null;
            Frame endFrame = null;
            for(int i = 0; i < list.size(); i++) {
                if(list.get(i).frame >= frame) {
                    endFrame = list.get(i);
                    break;
                }
            }
            for(int i = list.size() - 1; i > 0 ; i--) {
                if(list.get(i).frame <= nextFrame) {
                    startFrame = list.get(i);
                    break;
                }
            }

            poseStack.pushPose();
            try {
                if(startFrame != null) {
                    Vector4f size;
                    if(endFrame != null) {
                        size = ((Vector4f)startFrame.clone()).lerp(endFrame, AnimationUtils.amount(frame - startFrame.w, endFrame.w - startFrame.w));
                    }
                    else size = startFrame;
                    Vector3f origin = list.get(rotIndex).getOrigin();
                    poseStack.translate(origin.x * size.x, origin.y * size.y, origin.z * size.z);
                    poseStack.scale(size.x, size.y, size.z);
                    poseStack.translate(-origin.x * size.x, -origin.y * size.y, -origin.z * size.z);
                }
            } catch(CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
