package pixel_pioneer;

import editor.PPImGui;
import components.Components;
import org.joml.Vector2f;

public class Transform extends Components {

    public Vector2f position;
    public Vector2f scale;
    public float rotation = 0.0f;
    public int zIndex;

    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }

    public void init(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
        this.zIndex = 0;
    }

    public Transform copy() {
        return new Transform(new Vector2f(position), new Vector2f(scale));
    }

    @Override
    public void imgui() {
        gameObject.name = PPImGui.inputText("Name: ", gameObject.name);
        PPImGui.drawVec2Ctrl("Position: ", this.position);
        PPImGui.drawVec2Ctrl("Scale: ", this.scale, 32.0f);
        this.rotation = PPImGui.dragFloat("Rotation: ", this.rotation);
        this.zIndex = PPImGui.dragInt("ZIndex: ", this.zIndex);
    }

    public void copy(Transform transform) {
        transform.position.set(this.position);
        transform.scale.set(this.scale);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Transform)) return false;

        Transform other = (Transform) obj;
        return other.position.equals(this.position) && other.scale.equals(this.scale) &&
                other.rotation == this.rotation && other.zIndex == this.zIndex;
    }

    public int getZIndex(){
        return this.zIndex;
    }
}
