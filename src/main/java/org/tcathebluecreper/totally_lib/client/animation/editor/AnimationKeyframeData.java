package org.tcathebluecreper.totally_lib.client.animation.editor;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.tcathebluecreper.totally_lib.client.animation.Easing;
import org.tcathebluecreper.totally_lib.client.animation.ProgressMode;
import org.tcathebluecreper.totally_lib.ldlib.InspectorViewable;
import org.tcathebluecreper.totally_lib.lib.GuiDrawer;
import org.tcathebluecreper.totally_lib.multiblock.MachineAnimation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AnimationKeyframeData implements InspectorViewable {
    public int frame;
    public float subFrame = 0;
    public DyeColor color = null;
    public final String type;
    public final MachineAnimation.Frame value;
    private final List<? extends MachineAnimation.Frame> keyframes;
    private final AnimationElement element;

    public final BiConsumer<ClickData, AnimationKeyframeData> remove;

    public AnimationKeyframeData(int frame, String type, int defaultValue, BiConsumer<ClickData, AnimationKeyframeData> remove, List<MachineAnimation.Frame> keyframes, AnimationElement element) {
        this.frame = frame;
        this.type = type;
        this.remove = remove;
        this.keyframes = keyframes;
        this.element = element;

        value = new MachineAnimation.Frame(new Vector3f(defaultValue, defaultValue, defaultValue), Easing.LINEAR, frame);
        keyframes.add(value);
    }

    public AnimationKeyframeData(int frame, String type, int defaultValue, BiConsumer<ClickData, AnimationKeyframeData> remove, List<MachineAnimation.OriginFrame> keyframes, Vector3f origin, AnimationElement element) {
        this.frame = frame;
        this.type = type;
        this.remove = remove;
        this.keyframes = keyframes;
        this.element = element;

        value = new MachineAnimation.OriginFrame(new Vector3f(defaultValue, defaultValue, defaultValue), Easing.LINEAR, frame + subFrame, origin);
        keyframes.add((MachineAnimation.OriginFrame) value);
    }

    public AnimationKeyframeData(String type, MachineAnimation.Frame frame, BiConsumer<ClickData, AnimationKeyframeData> remove, List<MachineAnimation.Frame> keyframes, AnimationElement element) {
        this.frame = (int)frame.getTime();
        this.subFrame = frame.getTime() % 1;
        this.remove = remove;
        this.keyframes = keyframes;
        this.type = type;

        value = frame;
        this.element = element;
    }

    @Override
    public void loadInspector(WidgetGroup parent, int offsetX, int offsetY, int sizeX, int sizeY) {

        /// FRAME FIELDS

        parent.addWidget(new LabelWidget(8, 12, "Frame:").setHoverTooltips("Frame in ticks.", "(timeline shows seconds, tick = 1/20 second)"));
        TextFieldWidget frameField = new TextFieldWidget(65, 10, 100, 15, null, null) {
            @Override
            protected void onTextChanged(String newTextString) {
                try {
                    value.setTime(Integer.parseInt(newTextString));
                } catch(NumberFormatException ignored) {
                }
            }
        };
        frameField.setCurrentString(value.getTime());
        parent.addWidget(frameField);


        parent.addWidget(new LabelWidget(8, 32, "Sub Frame:").setHoverTooltips("Sub frame, from 0 to 1", "(if you need frames smaller than 1 tick, this the part of the frame)"));
        TextFieldWidget subFrameField = new TextFieldWidget(65, 30, 100, 15, null, null) {
            @Override
            protected void onTextChanged(String newTextString) {
                try {
                    subFrame = Math.max(0, Math.min(Float.parseFloat(newTextString), 1));
                } catch(NumberFormatException ignored) {
                }
            }
        };
        subFrameField.setCurrentString(subFrame);
        parent.addWidget(subFrameField);


        List<String> colorOptions = new ArrayList<>();
        colorOptions.add("DEFAULT");
        colorOptions.addAll(Arrays.stream(DyeColor.values()).map(DyeColor::getName).toList());

        parent.addWidget(new LabelWidget(8, 52, "Color:").setHoverTooltips("Display color on the timeline.", "(default is white on odd and black on even frames)"));
        SelectorWidget colorField = new SelectorWidget(65, 50, 100, 15, colorOptions, GuiDrawer.text_dark.getRGB());
        colorField.setOnChanged(newColor -> color = DyeColor.valueOf(newColor.toUpperCase()));
        colorField.setButtonBackground(ColorPattern.T_GRAY.rectTexture());
        colorField.setValue(color == null ? "DEFAULT" : color.getName());
        parent.addWidget(colorField);


        parent.addWidget(new LabelWidget(8, 72, type + " X:"));
        TextFieldWidget xField = getTextFieldWidgetFloat(70, (v) -> value.getValue().x = v, value.getValue().x);
        parent.addWidget(xField);

        parent.addWidget(new LabelWidget(8, 92, type + " Y:"));
        TextFieldWidget yField = getTextFieldWidgetFloat(90, (v) -> value.getValue().y = v, value.getValue().y);
        parent.addWidget(yField);

        parent.addWidget(new LabelWidget(8, 112, type + " Z:"));
        TextFieldWidget zField = getTextFieldWidgetFloat(110, (v) -> value.getValue().z = v, value.getValue().z);
        parent.addWidget(zField);

        ButtonWidget deleteButton = new ButtonWidget(65, 130, 60, 15, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("DELETE")), clickData -> {
            remove.accept(clickData, this);
            keyframes.remove(value);
        });
        parent.addWidget(deleteButton);

        /// ELEMENT FIELDS

        parent.addWidget(new LabelWidget(8, 150, "Element Options"));
        parent.addWidget(new ImageWidget(5, 162, sizeX - 10, 1, new ColorRectTexture(GuiDrawer.text_dark.getRGB())));

        parent.addWidget(new LabelWidget(8, 172, "Anim. Len."));
        TextFieldWidget animLenField = getTextFieldWidgetInt(170, (v) -> element.animationLength = v, element.animationLength);
        parent.addWidget(animLenField);

        List<String> modeOptions = new ArrayList<>(Arrays.stream(ProgressMode.values()).map(ProgressMode::getSerializedName).toList());

        parent.addWidget(new LabelWidget(8, 192, "Mode:"));
        SelectorWidget modeField = new SelectorWidget(65, 190, 100, 15, modeOptions, GuiDrawer.text_dark.getRGB());
        modeField.setOnChanged(newMode -> element.progressMode = ProgressMode.valueOf(newMode.toUpperCase()));
        modeField.setButtonBackground(ColorPattern.T_GRAY.rectTexture());
        modeField.setValue(element.progressMode == null ? "ALWAYS" : element.progressMode.getSerializedName());
        parent.addWidget(modeField);

        if(value instanceof MachineAnimation.OriginFrame) {
            parent.addWidget(new LabelWidget(8, 72, type + " X:"));
            TextFieldWidget xOriginField = getTextFieldWidgetFloat(130, (v) -> ((MachineAnimation.OriginFrame) value).getOrigin().x = v, ((MachineAnimation.OriginFrame) value).getOrigin().x);
            parent.addWidget(xOriginField);

            parent.addWidget(new LabelWidget(8, 92, type + " Y:"));
            TextFieldWidget yOriginField = getTextFieldWidgetFloat(150, (v) -> ((MachineAnimation.OriginFrame) value).getOrigin().y = v, ((MachineAnimation.OriginFrame) value).getOrigin().y);
            parent.addWidget(yOriginField);

            parent.addWidget(new LabelWidget(8, 112, type + " Z:"));
            TextFieldWidget zOriginField = getTextFieldWidgetFloat(170, (v) -> ((MachineAnimation.OriginFrame) value).getOrigin().z = v, ((MachineAnimation.OriginFrame) value).getOrigin().z);
            parent.addWidget(zOriginField);
        }
    }

    private @NotNull TextFieldWidget getTextFieldWidgetFloat(int yPosition, Consumer<Float> setValue, float value1) {
        TextFieldWidget xOriginField = new TextFieldWidget(65, yPosition, 100, 15, null, null) {
            @Override
            protected void onTextChanged(String newTextString) {
                try {
                    setValue.accept(Float.parseFloat(newTextString));
                } catch(NumberFormatException ignored) {
                }
            }
        };
        xOriginField.setCurrentString(value1);
        return xOriginField;
    }
    private @NotNull TextFieldWidget getTextFieldWidgetInt(int yPosition, Consumer<Integer> setValue, int value1) {
        TextFieldWidget xOriginField = new TextFieldWidget(65, yPosition, 100, 15, null, null) {
            @Override
            protected void onTextChanged(String newTextString) {
                try {
                    setValue.accept(Integer.parseInt(newTextString));
                } catch(NumberFormatException ignored) {
                }
            }
        };
        xOriginField.setCurrentString(value1);
        return xOriginField;
    }
}
