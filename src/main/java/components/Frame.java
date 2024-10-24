package components;

import components.ui.Sprite;

public class Frame {
    public Sprite sprite;
    public float frameTime;

    public Frame(){

    }

    public Frame(Sprite sprite, float frameTime){
        this.sprite = sprite;
        this.frameTime = frameTime;
    }
}
