package core.physics;

import components.Components;
import org.joml.*;

public class RigidBody extends Components {
    private int colliderType = 0;
    private float friction = 0.8f;
    public Vector3f velocity = new Vector3f(0, 0.5f, 0);
    public transient Vector4f temporary = new Vector4f(0, 0, 0, 0);

}
