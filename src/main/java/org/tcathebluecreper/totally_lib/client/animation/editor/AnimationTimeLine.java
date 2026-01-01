package org.tcathebluecreper.totally_lib.client.animation.editor;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.tcathebluecreper.totally_lib.lib.GuiDrawer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

class AnimationTimeLine extends Widget {
    private final AnimationEditor animationEditor;
    int cursorPos = 0;
    float cursorSubPos = 0;
    int scroll = 0;
    boolean isDragging = false;
    int animationLength = 100;
    boolean playing = false;
    long lastTick = 0;


    public List<AnimationKeyframeData> posKeyframes = new ArrayList<>();
    public List<AnimationKeyframeData> rotKeyframes = new ArrayList<>();
    public List<AnimationKeyframeData> sizeKeyframes = new ArrayList<>();

    public AnimationTimeLine(AnimationEditor animationEditor) {
        super(5, 5, 100, 90);
        this.animationEditor = animationEditor;
    }

    @Override
    public void updateScreen() {
        this.setSize(parent.getSizeWidth() - 10, 90);
        if(playing && lastTick != Minecraft.getInstance().level.getGameTime()) {
            lastTick = Minecraft.getInstance().level.getGameTime();
            cursorPos++;
            cursorSubPos = cursorPos;
            if(cursorPos > animationLength) cursorPos = 0;
        }
        animationEditor.partsList.setSize(animationEditor.inspector.getSizeWidth() - 8, animationEditor.inspector.getSizeHeight() - 10);
    }

    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        GuiDrawer g = new GuiDrawer(graphics, this);

        g.drawRect(10, 18 * 2, getSizeWidth() - 10, 1, GuiDrawer.text_dark);
        g.drawRect(10, 18 * 3, getSizeWidth() - 10, 1, GuiDrawer.text_dark);
        g.drawRect(10, 18 * 4, getSizeWidth() - 10, 1, GuiDrawer.text_dark);

        g.drawRect(32, 24, 1, 62, GuiDrawer.text_dark);

        g.drawString("TIMELINE", 0, 0, GuiDrawer.text_light);
        g.drawString(String.valueOf(cursorPos), 0, 20, GuiDrawer.text_light);
        g.drawString("POS", 0, 6 + 18 * 2, GuiDrawer.text_light);
        g.drawString("ROT", 0, 6 + 18 * 3, GuiDrawer.text_light);
        g.drawString("SIZE", 0, 6 + 18 * 4, GuiDrawer.text_light);

        g.drawRect(23, 6 + 18 * 2, 8, 8, GuiDrawer.dark);
        g.drawRect(24, 7 + 18 * 2, 6, 6, GuiDrawer.mid);
        g.drawRect(24, 9 + 18 * 2, 6, 2, GuiDrawer.text_dark);
        g.drawRect(26, 7 + 18 * 2, 2, 6, GuiDrawer.text_dark);

        g.drawRect(23, 6 + 18 * 3, 8, 8, GuiDrawer.dark);
        g.drawRect(24, 7 + 18 * 3, 6, 6, GuiDrawer.mid);
        g.drawRect(24, 9 + 18 * 3, 6, 2, GuiDrawer.text_dark);
        g.drawRect(26, 7 + 18 * 3, 2, 6, GuiDrawer.text_dark);

        g.drawRect(23, 6 + 18 * 4, 8, 8, GuiDrawer.dark);
        g.drawRect(24, 7 + 18 * 4, 6, 6, GuiDrawer.mid);
        g.drawRect(24, 9 + 18 * 4, 6, 2, GuiDrawer.text_dark);
        g.drawRect(26, 7 + 18 * 4, 2, 6, GuiDrawer.text_dark);

        g.drawRect(50, 0, 10, 10, GuiDrawer.dark);
        g.drawRect(51, 1, 8, 8, GuiDrawer.mid);
        g.drawRect(52, 2, 2, 6, GuiDrawer.text_dark);
        g.drawRect(54, 3, 2, 4, GuiDrawer.text_dark);
        g.drawRect(56, 4, 2, 2, GuiDrawer.text_dark);

        g.drawRect(65, 0, 10, 10, GuiDrawer.dark);
        g.drawRect(66, 1, 8, 8, GuiDrawer.mid);
        g.drawRect(67, 2, 2, 6, GuiDrawer.text_dark);
        g.drawRect(71, 2, 2, 6, GuiDrawer.text_dark);

        for(int i = scroll / 20; i < (getSizeWidth() - 20 + scroll) / 20; i++) {
            if(20 * i + 35 - scroll >= 35) g.drawRect(20 * i + 35 - scroll, 22, 1, 12, GuiDrawer.light);
            g.drawString(String.valueOf(i), 20 * i + 33 - scroll, 12, GuiDrawer.light);
        }

        for(int i = 0; i < posKeyframes.size(); i++) {
            AnimationKeyframeData frame = posKeyframes.get(i);
            if(!(((int) frame.value.getTime()) + 35 - scroll >= 35)) continue;
            Color color = frame.color == null ? ((int) frame.value.getTime()) % 2 == 0 ? GuiDrawer.text_dark : GuiDrawer.text_light : new Color(frame.color.getTextColor());
            g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 42, 1, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 43, 3, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 33 - scroll, 44, 5, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 45, 3, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 46, 1, 1, color);

            if(animationEditor.inspecting == frame) {
                g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 42, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 43, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 36 - scroll, 43, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 33 - scroll, 44, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 37 - scroll, 44, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 45, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 36 - scroll, 45, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 46, 1, 1, GuiDrawer.mid);
            }
        }

        for(int i = 0; i < rotKeyframes.size(); i++) {
            AnimationKeyframeData frame = rotKeyframes.get(i);
            if(!(((int) frame.value.getTime()) + 35 - scroll >= 35)) continue;
            Color color = frame.color == null ? ((int) frame.value.getTime()) % 2 == 0 ? GuiDrawer.text_dark : GuiDrawer.text_light : new Color(frame.color.getTextColor());
            g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 62, 1, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 63, 3, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 33 - scroll, 64, 5, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 65, 3, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 66, 1, 1, color);

            if(animationEditor.inspecting == frame) {
                g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 62, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 63, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 36 - scroll, 63, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 33 - scroll, 64, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 37 - scroll, 64, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 65, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 36 - scroll, 65, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 66, 1, 1, GuiDrawer.mid);
            }
        }

        for(int i = 0; i < sizeKeyframes.size(); i++) {
            AnimationKeyframeData frame = sizeKeyframes.get(i);
            if(!(((int) frame.value.getTime()) + 35 - scroll >= 35)) continue;
            Color color = frame.color == null ? ((int) frame.value.getTime()) % 2 == 0 ? GuiDrawer.text_dark : GuiDrawer.text_light : new Color(frame.color.getTextColor());
            g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 82, 1, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 83, 3, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 33 - scroll, 84, 5, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 85, 3, 1, color);
            g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 86, 1, 1, color);

            if(animationEditor.inspecting == frame) {
                g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 82, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 83, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 36 - scroll, 83, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 33 - scroll, 84, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 37 - scroll, 84, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 34 - scroll, 85, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 36 - scroll, 85, 1, 1, GuiDrawer.mid);
                g.drawRect(((int) frame.value.getTime()) + 35 - scroll, 86, 1, 1, GuiDrawer.mid);
            }
        }

        g.drawRect(Math.max(Math.min(cursorPos + 34 - scroll, getSizeWidth() - 1), 32), 30, 3, 1, GuiDrawer.light);
        g.drawRect(Math.max(Math.min(cursorPos + 35 - scroll, getSizeWidth()), 33), 30, 1, 60, GuiDrawer.light);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        //((button == 0 && mouseY - dragY <= getPositionY() + 24 && mouseY - dragY >= getPositionY() + 19) || isDragging) && mouseX - dragX <= getPositionX() + cursorSubPos + 35 && mouseX - dragX >= getPositionX() + cursorSubPos + 30
        if((button == 0 && animationEditor.mouseInside(this, mouseX - dragX, mouseY - dragY, Math.max(Math.min(cursorPos + 34 - scroll, getSizeWidth() - 1), 29), -1, 6, -1)) && (animationEditor.mouseInside(this, mouseX - dragX, mouseY - dragY, -1, 27, -1, 6) || isDragging)) {
            cursorSubPos += (float) dragX;
            cursorPos = (int) Math.max(mouseX - 40 + scroll, 0);
//                if(cursorPos < 0) cursorPos = 0;
            isDragging = true;
            return false;
        } else isDragging = false;
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX >= 21 + getPositionX() && mouseX <= 29 + getPositionX()) {
            if(animationEditor.selectedAnimationPart != null) {
                if(mouseY >= 6 + 18 * 2 + getPositionY() && mouseY <= 14 + 18 * 2 + getPositionY()) {
                    posKeyframes.add(new AnimationKeyframeData(cursorPos, "Pos", 0, (clickData, frame) -> posKeyframes.remove(frame), animationEditor.selectedAnimationPart.getAnimationElement().positionFrames, animationEditor.selectedAnimationPart));
                    posKeyframes.sort(Comparator.comparingInt(a -> a.frame));
                }
                if(mouseY >= 6 + 18 * 3 + getPositionY() && mouseY <= 14 + 18 * 3 + getPositionY()) {
                    rotKeyframes.add(new AnimationKeyframeData(cursorPos, "Rot", 0, (clickData, frame) -> rotKeyframes.remove(frame), animationEditor.selectedAnimationPart.getAnimationElement().rotationFrames, new Vector3f(0, 0, 0), animationEditor.selectedAnimationPart));
                    rotKeyframes.sort(Comparator.comparingInt(a -> a.frame));
                }
                if(mouseY >= 6 + 18 * 4 + getPositionY() && mouseY <= 14 + 18 * 4 + getPositionY()) {
                    sizeKeyframes.add(new AnimationKeyframeData(cursorPos, "Size", 1, (clickData, frame) -> sizeKeyframes.remove(frame), animationEditor.selectedAnimationPart.getAnimationElement().scaleFrames, new Vector3f(0, 0, 0), animationEditor.selectedAnimationPart));
                    sizeKeyframes.sort(Comparator.comparingInt(a -> a.frame));
                }
            }
        }

        if(animationEditor.mouseInside(this, mouseX, mouseY, 35, 36, -1, 16)) {
            Optional<AnimationKeyframeData> frame = posKeyframes.stream().filter(k -> k.frame == (int) (mouseX - getPositionX() - 35 + scroll)).findFirst();
            if(frame.isPresent()) {
                animationEditor.inspecting = frame.get();
                animationEditor.reloadInspector();
            }
            return true;
        }
        if(animationEditor.mouseInside(this, mouseX, mouseY, 35, 56, -1, 16)) {
            Optional<AnimationKeyframeData> frame = rotKeyframes.stream().filter(k -> k.frame == (int) (mouseX - getPositionX() - 35 + scroll)).findFirst();
            if(frame.isPresent()) {
                animationEditor.inspecting = frame.get();
                animationEditor.reloadInspector();
            }
            return true;
        }
        if(animationEditor.mouseInside(this, mouseX, mouseY, 35, 76, -1, 16)) { // mouseX >= getPositionX() + 31 && mouseY <= getPositionY() + 85 && mouseY >= getPositionY() + 69
            Optional<AnimationKeyframeData> frame = sizeKeyframes.stream().filter(k -> k.frame == (int) (mouseX - getPositionX() - 35 + scroll)).findFirst();
            if(frame.isPresent()) {
                animationEditor.inspecting = frame.get();
                animationEditor.reloadInspector();
            }
            return true;
        }
        if(animationEditor.mouseInside(this, mouseX, mouseY, 50, 0, 10, 10)) {
            playing = true;
        }
        if(animationEditor.mouseInside(this, mouseX, mouseY, 65, 0, 10, 10)) {
            playing = false;
        }
        return false;
    }

    @Override
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        if(animationEditor.mouseInside(this, mouseX, mouseY, 35, 0, getSizeHeight(), -1)) {
            scroll += (int) (wheelDelta * (Screen.hasShiftDown() ? 5 : 1) * (Screen.hasControlDown() ? 5 : 1));
            if(scroll < 0) scroll = 0;

            return true;
        }
        return false;
    }
}
