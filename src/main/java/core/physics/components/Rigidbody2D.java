package core.physics.components;

import pixel_pioneer.Window;
import components.Components;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;
import core.enums.BodyType;

public class Rigidbody2D extends Components {
    private Vector2f velocity = new Vector2f();
    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private float mass = 0;
    private BodyType bodyType = BodyType.Dynamic;
    private float friction = 0.1f;
    public float angularVelocity = 0.0f;
    public float gravityScale = 1.0f;
    private boolean isSensor = false;

    private boolean fixedRotation = false;
    private boolean continuousCollision = true;

    private transient Body rawBody = null;

    @Override
    public void update(float dt) {
        if(rawBody !=null) {
            if (this.bodyType == BodyType.Dynamic || this.bodyType == BodyType.Kinematic) {
                this.gameObject.transform.position.set(
                        rawBody.getPosition().x, rawBody.getPosition().y
                );
                this.gameObject.transform.rotation = (float) Math.toDegrees(rawBody.getAngle());
                Vec2 val = rawBody.getLinearVelocity();
                this.velocity.set(val.x, val.y);
            } else if (this.bodyType == BodyType.Static) {
                this.rawBody.setTransform(
                        new Vec2(this.gameObject.transform.position.x, this.gameObject.transform.position.y),
                        this.gameObject.transform.rotation
                );
            }
        }
    }

    public void addVelocity(Vector2f forceToAdd) {
        if (rawBody != null) {
            rawBody.applyForceToCenter(new Vec2(forceToAdd.x, forceToAdd.y));
        }
    }
    public void addImpulse(Vector2f impulse) {
        if (rawBody != null) {
            rawBody.applyLinearImpulse(new Vec2(velocity.x, velocity.y), rawBody.getWorldCenter());
        }
    }

    public void setPosition(Vector2f newPosition) {
        if(rawBody != null) {
            rawBody.setTransform(new Vec2(newPosition.x, newPosition.y), 0.0f);
        }
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public float getMass() {
        return mass;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    public Body getRawBody() {
        return rawBody;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if (rawBody != null) {
            this.rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
        }
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
        if (rawBody != null) {
            this.rawBody.setAngularVelocity(angularVelocity);
        }
    }
    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (rawBody != null) {
            this.rawBody.setGravityScale(gravityScale);
        }
    }

    public void setIsSensor() {
        isSensor = true;
        if (rawBody != null) {
            Window.getPhysics().setIsSensor(this);
        }
    }
    public void setNotSensor() {
        isSensor = false;
        if (rawBody != null) {
            Window.getPhysics().setNotSensor(this);
        }
    }
    public float getFriction() {
        return this.friction;
    }

    public boolean isSensor() {
        return this.isSensor;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }
}
