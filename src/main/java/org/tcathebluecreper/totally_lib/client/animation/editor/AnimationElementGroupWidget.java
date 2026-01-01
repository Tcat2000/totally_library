package org.tcathebluecreper.totally_lib.client.animation.editor;

import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import org.tcathebluecreper.totally_lib.client.animation.ProgressMode;
import org.tcathebluecreper.totally_lib.multiblock.MachineAnimation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnimationElementGroupWidget extends AnimationElement {
    private final AnimationEditor animationEditor;
    final List<AnimationElement> otherElements;
    IGuiTexture selectedTexture = new ColorBorderTexture(-1, Color.RED.getRGB());


    MachineAnimation.AnimatedModelGroup animationPart;

    public AnimationElementGroupWidget(AnimationEditor animationEditor, List<AnimationElement> otherElements) {
        super(0, 16, 192, 50);
        this.animationEditor = animationEditor;
        this.otherElements = otherElements;
        this.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);

        animationPart = new MachineAnimation.AnimatedModelGroup(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), ProgressMode.ALWAYS);
        animationEditor.machineAnimation.parts.add(animationPart);

        setDynamicSized(true);
        addWidget(new Widget(0, 16, 192, 1));
    }

    public AnimationElementGroupWidget(AnimationEditor animationEditor, List<AnimationElement> otherElements, MachineAnimation.AnimatedModelGroup modelPart) {
        this(animationEditor, otherElements);

        animationPart = modelPart;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOverElement(mouseX, mouseY) && !super.mouseClicked(mouseX, mouseY, button)) {
            this.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND, selectedTexture);
            animationEditor.selectedAnimationPart = this;
            animationEditor.timeLine.posKeyframes = posKeyframes;
            animationEditor.timeLine.rotKeyframes = rotKeyframes;
            animationEditor.timeLine.sizeKeyframes = sizeKeyframes;
            otherElements.forEach(element -> {
                if(element != this) element.unselect();
            });
            return true;
        }
        return false;
    }

    @Override
    public MachineAnimation.AnimatedModelElement getAnimationElement() {
        return animationPart;
    }

    public void unselect() {
        this.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);
    }
}
