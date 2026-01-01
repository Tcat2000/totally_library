package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.google.gson.*;
import com.lowdragmc.lowdraglib.client.renderer.impl.IModelRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.tcathebluecreper.totally_lib.client.animation.Easing;
import org.tcathebluecreper.totally_lib.client.animation.IEasingMethod;
import org.tcathebluecreper.totally_lib.client.animation.ProgressMode;
import org.tcathebluecreper.totally_lib.client.animation.editor.AnimationElement;
import org.tcathebluecreper.totally_lib.lib.AnimationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class MachineAnimation {
    public List<AnimatedModelElement> parts = new ArrayList<>();

    public void render(float frame, PoseStack poseStack, MultiBufferSource bufferSource, Level level) {
        var lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();
        RenderSystem.setShader(GameRenderer::getRendertypeTranslucentShader);

        RenderSystem.clearColor(1,1,1,1);
        if(parts != null) for(AnimatedModelElement part : parts) {
            part.render(frame, poseStack, bufferSource, level);
        }
    }

    public void renderWireframe(AnimatedModelElement animationPart, float frame, PoseStack poseStack, MultiBufferSource bufferSource) {
        if(animationPart != null && parts.contains(animationPart) && animationPart instanceof AnimatedModelPart) ((AnimatedModelPart) animationPart).renderWireframe(frame, poseStack, bufferSource);
    }

    public JsonObject serialize() {
        JsonObject base = new JsonObject();

        JsonArray elements = new JsonArray();
        for(AnimatedModelElement element : parts) {
            elements.add(element.serialize());
        }
        base.add("elements", elements);

        return base;
    }

    public void deserialize(String data) {
        JsonObject jsonObject = new Gson().fromJson(data, JsonObject.class);
        JsonArray elements = jsonObject.getAsJsonArray("elements");
        for(JsonElement element : elements) {
            if(!(element instanceof JsonObject)) return;
            parts.add(AnimatedModelElement.deserialize((JsonObject) element));
        }
    }

    public void forAllParts(Consumer<AnimatedModelElement> consumer) {
        AtomicReference<Consumer<? super AnimatedModelElement>> atomicForLoop = new AtomicReference<>();
        Consumer<? super AnimatedModelElement> forLoop = element -> {
            consumer.accept(element);
            if(element instanceof AnimatedModelGroup) {
                ((AnimatedModelGroup) element).subElements.forEach(atomicForLoop.get());
            }
        };
        atomicForLoop.set(forLoop);
        parts.forEach(forLoop);
    }

    public static abstract class AnimatedModelElement {
        public final List<Frame> positionFrames;
        public final List<OriginFrame> rotationFrames;
        public final List<OriginFrame> scaleFrames;
        ProgressMode progressMode = ProgressMode.SYNC_TO_PROGRESS;
        public int animationLength;
        public ProgressMode mode;

        protected AnimatedModelElement(List<Frame> positionFrames, List<OriginFrame> rotationFrames, List<OriginFrame> scaleFrames, ProgressMode mode) {
            this.positionFrames = positionFrames;
            this.rotationFrames = rotationFrames;
            this.scaleFrames = scaleFrames;
            this.progressMode = mode;
            positionFrames.sort((a,b) -> (int) (a.getTime() - b.getTime()));
            rotationFrames.sort((a,b) -> (int) (a.getTime() - b.getTime()));
            scaleFrames.sort((a,b) -> (int) (a.getTime() - b.getTime()));
        }
        protected AnimatedModelElement() {
            this.positionFrames = new ArrayList<>();
            this.rotationFrames = new ArrayList<>();
            this.scaleFrames = new ArrayList<>();
        }

        public abstract void render(float frame, PoseStack poseStack, MultiBufferSource bufferSource, Level level);


        public void applyFrameTranslations(float frame, PoseStack poseStack) {
            positionFrames.sort(Comparator.comparingDouble(Frame::getTime));
            rotationFrames.sort(Comparator.comparingDouble(Frame::getTime));
            scaleFrames.sort(Comparator.comparingDouble(Frame::getTime));

            int posIndex = positionFrames.indexOf(positionFrames.stream().filter(f -> f.getTime() <= frame).reduce((first, second) -> second).orElse(null));
            Vector4f startPos = posIndex != -1 ? positionFrames.get(posIndex).getData() : null;
            Vector4f endPos = posIndex != -1 && positionFrames.size() > posIndex + 1 ? positionFrames.get(posIndex + 1).getData() : null;

            int rotIndex = rotationFrames.indexOf(rotationFrames.stream().filter(f -> f.getTime() <= frame).reduce((first, second) -> second).orElse(null));
            Vector4f startRot = rotIndex != -1 ? rotationFrames.get(rotIndex).getData() : null;
            Vector4f endRot = rotIndex != -1 && rotationFrames.size() > rotIndex + 1 ? rotationFrames.get(rotIndex + 1).getData() : null;

            int sizeIndex = scaleFrames.indexOf(scaleFrames.stream().filter(f -> f.getTime() <= frame).reduce((first, second) -> second).orElse(null));
            Vector4f startSize = sizeIndex != -1 ? scaleFrames.get(sizeIndex).getData() : null;
            Vector4f endSize = sizeIndex != -1 && scaleFrames.size() > sizeIndex + 1 ? scaleFrames.get(sizeIndex + 1).getData() : null;

            poseStack.pushPose();
            try {
                if(startPos != null) {
                    Vector4f pos;
                    if(endPos != null) {
                        pos = ((Vector4f)startPos.clone()).lerp(endPos, AnimationUtils.amount(frame - startPos.w, endPos.w - startPos.w));
                    }
                    else pos = startPos;
                    poseStack.translate(pos.x, pos.y, pos.z);
                }
                if(startRot != null) {
                    Vector4f rot;
                    if(endRot != null) {
                        rot = ((Vector4f)startRot.clone()).lerp(endRot, AnimationUtils.amount(frame - startRot.w, endRot.w - startRot.w));
                    }
                    else rot = startRot;
                    Vector3f origin = rotationFrames.get(rotIndex).getOrigin();
                    poseStack.translate(origin.x, origin.y, origin.z);
                    poseStack.mulPose(new Quaternionf().rotateAxis(rot.x * Mth.DEG_TO_RAD, 1,0,0));
                    poseStack.mulPose(new Quaternionf().rotateAxis(rot.y * Mth.DEG_TO_RAD, 0,1,0));
                    poseStack.mulPose(new Quaternionf().rotateAxis(rot.z * Mth.DEG_TO_RAD, 0,0,1));
                    poseStack.translate(-origin.x, -origin.y, -origin.z);
                }
                if(startSize != null) {
                    Vector4f size;
                    if(endSize != null) {
                        size = ((Vector4f)startSize.clone()).lerp(endSize, AnimationUtils.amount(frame - startSize.w, endSize.w - startSize.w));
                    }
                    else size = startSize;
                    Vector3f origin = rotationFrames.get(rotIndex).getOrigin();
                    poseStack.translate(origin.x * size.x, origin.y * size.y, origin.z * size.z);
                    poseStack.scale(size.x, size.y, size.z);
                    poseStack.translate(-origin.x * size.x, -origin.y * size.y, -origin.z * size.z);
                }
            } catch(CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public JsonObject serialize() {
            JsonObject object = new JsonObject();

            JsonArray posKeyframes = new JsonArray();
            for(Frame frame : positionFrames) {
                posKeyframes.add(frame.serialize());
            }
            JsonArray rotKeyframes = new JsonArray();
            for(Frame frame : rotationFrames) {
                rotKeyframes.add(frame.serialize());
            }
            JsonArray sizeKeyframes = new JsonArray();
            for(Frame frame : scaleFrames) {
                sizeKeyframes.add(frame.serialize());
            }

            object.add("position", posKeyframes);
            object.add("rotation", rotKeyframes);
            object.add("scale", sizeKeyframes);
            object.addProperty("mode", progressMode.toString());

            return object;
        }

        public static AnimatedModelElement deserialize(JsonObject element) {
            String type = element.get("type").getAsString();
            if(Objects.equals(type, "group")) {
                JsonArray posKeyframes = element.getAsJsonArray("position");
                JsonArray rotKeyframes = element.getAsJsonArray("rotation");
                JsonArray sizeKeyframes = element.getAsJsonArray("scale");
                JsonArray children = element.getAsJsonArray("children");

                List<Frame> posFrames = new ArrayList<>();
                for(JsonElement pos : posKeyframes) {
                    posFrames.add(Frame.deserialize((JsonObject) pos));
                }

                List<OriginFrame> rotFrames = new ArrayList<>();
                for(JsonElement rot : rotKeyframes) {
                    rotFrames.add(OriginFrame.deserialize((JsonObject) rot));
                }

                List<OriginFrame> sizeFrames = new ArrayList<>();
                for(JsonElement size : sizeKeyframes) {
                    sizeFrames.add(OriginFrame.deserialize((JsonObject) size));
                }

                List<AnimatedModelElement> subElements = new ArrayList<>();
                for(JsonElement size : children) {
                    subElements.add(AnimatedModelElement.deserialize((JsonObject) size));
                }

                ProgressMode mode = ProgressMode.valueOf(element.get("mode").getAsString());

                return new AnimatedModelGroup(posFrames, rotFrames, sizeFrames, subElements, mode);
            }
            else if(Objects.equals(type, "part")) {

                JsonArray posKeyframes = element.getAsJsonArray("position");
                JsonArray rotKeyframes = element.getAsJsonArray("rotation");
                JsonArray sizeKeyframes = element.getAsJsonArray("scale");
                ResourceLocation model = ResourceLocation.parse(element.get("model").getAsString());

                List<Frame> posFrames = new ArrayList<>();
                for(JsonElement pos : posKeyframes) {
                    posFrames.add(Frame.deserialize((JsonObject) pos));
                }

                List<OriginFrame> rotFrames = new ArrayList<>();
                for(JsonElement rot : rotKeyframes) {
                    rotFrames.add(OriginFrame.deserialize((JsonObject) rot));
                }

                List<OriginFrame> sizeFrames = new ArrayList<>();
                for(JsonElement size : sizeKeyframes) {
                    sizeFrames.add(OriginFrame.deserialize((JsonObject) size));
                }

                ProgressMode mode = ProgressMode.valueOf(element.get("mode").getAsString());

                List<BakedQuad> quads = new ArrayList<>();
                IModelRenderer loadModel = new IModelRenderer(model);
                Level level = Minecraft.getInstance().level;

                quads.addAll(loadModel.renderModel(level, new BlockPos(0, 4, 0), null, null, Minecraft.getInstance().level.random));
                quads.addAll(loadModel.renderModel(level, new BlockPos(0, 4, 0), null, Direction.UP, Minecraft.getInstance().level.random));
                quads.addAll(loadModel.renderModel(level, new BlockPos(0, 4, 0), null, Direction.DOWN, Minecraft.getInstance().level.random));
                quads.addAll(loadModel.renderModel(level, new BlockPos(0, 4, 0), null, Direction.NORTH, Minecraft.getInstance().level.random));
                quads.addAll(loadModel.renderModel(level, new BlockPos(0, 4, 0), null, Direction.EAST, Minecraft.getInstance().level.random));
                quads.addAll(loadModel.renderModel(level, new BlockPos(0, 4, 0), null, Direction.SOUTH, Minecraft.getInstance().level.random));
                quads.addAll(loadModel.renderModel(level, new BlockPos(0, 4, 0), null, Direction.WEST, Minecraft.getInstance().level.random));


                return new AnimatedModelPart(posFrames, rotFrames, sizeFrames, quads, mode);
            }
            throw new JsonParseException("Error parsing machine animation part: invalid type '" + type + "', expected ['group','part'] in element: " + element);
        }
    }

    public static class AnimatedModelPart extends AnimatedModelElement {
        public final List<BakedQuad> quads;
        public IModelRenderer model;

        public AnimatedModelPart fromV4(List<Vector4f> positionFrames, List<Vector4f> rotationFrames, List<Vector4f> scaleFrames, List<BakedQuad> quads, ProgressMode mode) {
            return new AnimatedModelPart(positionFrames.stream().map(Frame::linear).toList(), rotationFrames.stream().map(OriginFrame::linear).toList(), scaleFrames.stream().map(OriginFrame::linear).toList(), quads, mode);
        }
        public AnimatedModelPart(List<Frame> positionFrames, List<OriginFrame> rotationFrames, List<OriginFrame> scaleFrames, List<BakedQuad> quads, ProgressMode mode) {
            super(positionFrames, rotationFrames, scaleFrames, mode);
            this.quads = quads;
            this.progressMode = mode;
        }

        public void render(float frame, PoseStack poseStack, MultiBufferSource bufferSource, Level level) {
            poseStack.pushPose();
            applyFrameTranslations(frame, poseStack);

            RenderUtils.renderModelTESRFancy(quads, bufferSource.getBuffer(RenderType.solid()), poseStack, level, BlockPos.ZERO, false,-1, 15);
            poseStack.popPose();
        }

        @Override
        public JsonObject serialize() {
            JsonObject object = super.serialize();

            object.addProperty("type","part");
            object.addProperty("model", model.getModelLocation().toString());

            return object;
        }

        public void renderWireframe(float frame, PoseStack poseStack, MultiBufferSource bufferSource) {
            poseStack.pushPose();
            applyFrameTranslations(frame, poseStack);

            var matrix4f = poseStack.last().pose();
            var normal = poseStack.last().normal();

            var buffer = bufferSource.getBuffer(RenderType.lines());
            RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
            RenderSystem.lineWidth(5);
            RenderSystem.disableCull();

            for(int i = 0; i < quads.size(); i++) {
                BakedQuad quad = quads.get(i);
                int[] verts = quad.getVertices();

                for(int j = 0; j < 32; j += 8) {
                    float x0 = Float.intBitsToFloat(verts[j]);
                    float y0 = Float.intBitsToFloat(verts[j + 1]);
                    float z0 = Float.intBitsToFloat(verts[j + 2]);

                    float x1 = Float.intBitsToFloat(verts[(j + 8) % 32]);
                    float y1 = Float.intBitsToFloat(verts[(j + 9) % 32]);
                    float z1 = Float.intBitsToFloat(verts[(j + 10) % 32]);

                    float f = x1 - x0;
                    float f1 = y1 - y0;
                    float f2 = z1 - z0;
                    float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
                    f /= f3;
                    f1 /= f3;
                    f2 /= f3;

                    buffer.vertex(matrix4f, x0, y0, z0).color(255, 0, 0, 255).normal(normal, f, f1, f2).endVertex();
                    buffer.vertex(matrix4f, x1, y1, z1).color(255, 0, 0, 255).normal(normal, f, f1, f2).endVertex();
                }
            }

            poseStack.popPose();
        }
    }

    public static class AnimatedModelGroup extends AnimatedModelElement {
        public final List<AnimatedModelElement> subElements;

        public AnimatedModelGroup(List<Frame> positionFrames, List<OriginFrame> rotationFrames, List<OriginFrame> scaleFrames, List<AnimatedModelElement> subElements, ProgressMode mode) {
            super(positionFrames, rotationFrames, scaleFrames, mode);
            this.subElements = subElements;
        }
        public AnimatedModelGroup(List<Frame> positionFrames, List<OriginFrame> rotationFrames, List<OriginFrame> scaleFrames, ProgressMode mode) {
            super(positionFrames, rotationFrames, scaleFrames, mode);
            this.subElements = new ArrayList<>();
        }
        public AnimatedModelGroup(List<AnimatedModelElement> subElements) {
            super();
            this.subElements = subElements;
        }
        public AnimatedModelGroup() {
            super();
            this.subElements = new ArrayList<>();
        }

        @Override
        public void render(float frame, PoseStack poseStack, MultiBufferSource bufferSource, Level level) {
            poseStack.pushPose();
            applyFrameTranslations(frame, poseStack);
            for(AnimatedModelElement subElement : subElements) {
                subElement.render(frame, poseStack, bufferSource, level);
            }
            poseStack.popPose();
        }

        @Override
        public JsonObject serialize() {
            JsonObject object = super.serialize();
            object.addProperty("type","group");

            JsonArray subParts = new JsonArray();
            for(AnimatedModelElement part : subElements) {
                subParts.add(part.serialize());
            }
            object.add("children", subParts);

            return object;
        }
    }

    public static class Frame {
        Vector3f value;
        float time;
        IEasingMethod easing;

        public Frame(Vector3f value, IEasingMethod easing, float time) {
            this.value = value;
            this.easing = easing;
            this.time = time;
        }

        public Vector3f getValue() {return value;}
        public Vector4f getData() {return new Vector4f(value, time);}
        public float getTime() {return time;}
        public void setTime(float value) {time = value;}
        public IEasingMethod getEasing() {return easing;}

        public static Frame linear(Vector4f data) {
            return new Frame(new Vector3f(data.x, data.y, data.z), Easing.LINEAR, data.w);
        }
        public JsonObject serialize() {
            JsonObject object = new JsonObject();
            JsonArray valueArray = new JsonArray();
            valueArray.add(value.x);
            valueArray.add(value.y);
            valueArray.add(value.z);
            object.add("value", valueArray);
            object.addProperty("time", time);
            return object;
        }

        public static Frame deserialize(JsonObject data) {
            JsonArray array = data.getAsJsonArray("value");
            Vector3f value = new Vector3f();
            if(array.size() == 3 && data.has("time")) {
                value.x = array.get(0).getAsFloat();
                value.y = array.get(1).getAsFloat();
                value.z = array.get(2).getAsFloat();
                return new Frame(value, Easing.LINEAR, data.get("time").getAsFloat());
            }
            throw new JsonParseException("Error parsing machine animation part: Keyframe must have array 'value' with size 3, and float 'time': " + data);
        }
    }

    public static class OriginFrame extends Frame {
        Vector3f origin;
        public OriginFrame(Vector3f value, IEasingMethod easing, Float time, Vector3f origin) {
            super(value, easing, time);
            this.origin = origin;
        }
        public Vector3f getOrigin() {return origin;}

        public static OriginFrame linear(Vector4f data) {
            return new OriginFrame(new Vector3f(data.x, data.y, data.z), Easing.LINEAR, data.w, new Vector3f(0.5f,0.5f,0.5f));
        }

        @Override
        public JsonObject serialize() {
            JsonObject object = super.serialize();
            JsonArray originArray = new JsonArray();
            originArray.add(origin.x);
            originArray.add(origin.y);
            originArray.add(origin.z);
            object.add("origin", originArray);
            return object;
        }

        public static OriginFrame deserialize(JsonObject data) {
            JsonArray valueArray = data.getAsJsonArray("value");
            JsonArray originArray = data.getAsJsonArray("value");
            Vector3f value = new Vector3f();
            Vector3f origin = new Vector3f();
            if(valueArray.size() == 3 && originArray.size() == 3 && data.has("time")) {
                value.x = valueArray.get(0).getAsFloat();
                value.y = valueArray.get(1).getAsFloat();
                value.z = valueArray.get(2).getAsFloat();
                origin.x = originArray.get(0).getAsFloat();
                origin.y = originArray.get(1).getAsFloat();
                origin.z = originArray.get(2).getAsFloat();
                return new OriginFrame(value, Easing.LINEAR, data.get("time").getAsFloat(), origin);
            }
            throw new JsonParseException("Error parsing machine animation part: Keyframe must have array 'value' & 'origin' with size 3, and float 'time': " + data);
        }
    }
}
