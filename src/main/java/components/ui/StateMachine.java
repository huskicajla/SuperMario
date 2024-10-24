package components.ui;

import components.Animation;
import components.Components;
import components.Frame;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StateMachine extends Components {
    private class StateTrigger {
        public String state;
        public String trigger;

        public StateTrigger() {}
        public StateTrigger(String state, String trigger) {
            this.state = state;
            this.trigger = trigger;
        }

        @Override
        public boolean equals(Object o) {
            if(o.getClass() != StateTrigger.class) return false;
            StateTrigger stateTrigger = (StateTrigger) o;
            return stateTrigger.trigger.equals(this.trigger)
                    && stateTrigger.state.equals(this.state);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, trigger);
        }
    }

    public HashMap<StateTrigger, String> states = new HashMap<>();
    private List<Animation> animations = new ArrayList<>();
    private transient Animation currentAnimation = null;
    private String defaultStateTitle = "default";

    public void addStateTrigger(String state, String trigger, String onTrigger) {
        this.states.put(new StateTrigger(state, onTrigger), trigger);
    }

    public void refreshTextures() {
        for (Animation animation : animations) {
            animation.refreshTextures();
        }
    }

    public void addAnimation(Animation animation) {
        this.animations.add(animation);
    }

    public void setDefaultAnimationTitle(String defaultAnimationTitle) {
        for(Animation animation : animations) {
            if(animation.title.equals(defaultAnimationTitle)) {
                defaultStateTitle = defaultAnimationTitle;
                if(currentAnimation == null) {
                    currentAnimation = animation;
                    return;
                }
            }
        }
    }

    public void trigger(String trigger) {
        for(StateTrigger stateTrigger : states.keySet()) {
            if(stateTrigger.state.equals(currentAnimation.title)
                    && stateTrigger.trigger.equals(trigger)) {
                if(states.get(stateTrigger) != null) {
                    int newStateTrigger = -1;
                    int index = 0;
                    for(Animation animation : animations) {
                        if(animation.title.equals(states.get(stateTrigger))) {
                            newStateTrigger = index;
                            break;
                        }
                        index++;
                    }
                    if(newStateTrigger > -1) {
                        currentAnimation = animations.get(newStateTrigger);
                    }
                }
                return;
            }
        }
    }

    @Override
    public void start() {
        for(Animation animation : animations) {
            if(animation.title.equals(defaultStateTitle)) {
                currentAnimation = animation;
                break;
            }
        }
    }

    @Override
    public void update(float dt) {
        if(currentAnimation != null) {
            currentAnimation.update(dt);
            SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
            if(spriteRenderer != null) {
                spriteRenderer.setSprite(currentAnimation.getCurrentSprite());
            }
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if(currentAnimation != null) {
            currentAnimation.update(dt);
            SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
            if(spriteRenderer != null) {
                spriteRenderer.setSprite(currentAnimation.getCurrentSprite());
            }
        }
    }

    @Override
    public void imgui(){
        int index = 0;
        for (Animation animation : animations) {
            ImString title = new ImString(animation.title);
            ImGui.inputText("State: ", title);
            animation.title = title.get();

            ImBoolean doesLoop = new ImBoolean(animation.doesLoop);
            ImGui.checkbox("Does loop?", doesLoop);
            animation.setLoop(doesLoop.get());
            for(Frame frame : animation.animationFrames) {
                float[] temp = new float[1];
                temp[0] = frame.frameTime;
                ImGui.dragFloat("Frame(" + index + ") Time:", temp, 0.01f);
                frame.frameTime = temp[0];
                index++;
            }
        }
    }
}
