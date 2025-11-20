package org.tcathebluecreper.totally_lib.client.animation.editor;

import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import org.tcathebluecreper.totally_lib.multiblock.MachineAnimation;

import java.util.ArrayList;
import java.util.List;

public abstract class AnimationElement extends WidgetGroup {
    List<AnimationKeyframeData> posKeyframes = new ArrayList<>();
    List<AnimationKeyframeData> rotKeyframes = new ArrayList<>();
    List<AnimationKeyframeData> sizeKeyframes = new ArrayList<>();

    public AnimationElement() {
    }

    public AnimationElement(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public AnimationElement(Position position) {
        super(position);
    }

    public AnimationElement(Position position, Size size) {
        super(position, size);
    }

    public abstract MachineAnimation.AnimatedModelElement getAnimationElement();


    public void unselect() {
        this.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);
    }
}
