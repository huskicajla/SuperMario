package core.physics.components;

import components.Components;
import org.joml.Vector2f;
import visualizer.DebugDraw;

public class CircleCollider extends Components {
    private float radius = 1f;
    private transient boolean resetFixtureNextFrame = false;
    protected Vector2f offset = new Vector2f();

    public float getRadius(){
        return radius;
    }

    public Vector2f getOffset() {
        return this.offset;
    }

    public void setRadius(float radius){
        this.radius = radius;
    }

    public void setOffset(Vector2f offset){
        this.offset = offset;
    }

    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
        DebugDraw.addCircle2D(center, this.radius);
    }
}
