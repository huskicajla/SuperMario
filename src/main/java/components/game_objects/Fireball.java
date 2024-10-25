package components.game_objects;

import components.Components;
import components.PlayerController;
import pixel_pioneer.GameObject;
import pixel_pioneer.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import core.physics.Physics2D;
import core.physics.components.Rigidbody2D;

public class Fireball extends Components {
    public transient boolean goingRight = false;
    private transient Rigidbody2D rigidbody;
    private transient float fireallSpeed = 1.7f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    private transient boolean onGround = false;
    private transient float lifeTime = 4.0f;


    private static int fireballCount = 0;

    public static boolean canSpawn(){
        return fireballCount < 4;
    }

    @Override
    public void start(){
        this.rigidbody = this.gameObject.getComponent(Rigidbody2D.class);
        this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        fireballCount++;
    }

    @Override
    public void update(float dt){
        lifeTime -= dt;

        if(lifeTime <= 0.0f){
            disappear();
            return;
        }

        if(goingRight) {
            velocity.x = fireallSpeed;
        } else {
            velocity.x = -fireallSpeed;
        }

        checkOnGround();
        if(onGround) {
            this.acceleration.y = 1.5f;
            this.velocity.y = 2.5f;
        } else {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }

        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
        this.rigidbody.setVelocity(velocity);
    }

    public void checkOnGround() {
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.09f;
        onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2f contactNormal) {
        if (Math.abs(contactNormal.x) > 0.8f) {
            this.goingRight = contactNormal.x < 0;
        }
    }

    public void dissapear() {
        fireballCount--;
        this.gameObject.destroy();
    }

    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
        if (obj.getComponent(PlayerController.class) != null ||
                obj.getComponent(Fireball.class) != null) {
            contact.setEnabled(false);
        }
    }
    public void disappear() {
        fireballCount--;
        this.gameObject.destroy();
    }
}
