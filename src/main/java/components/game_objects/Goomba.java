package components.game_objects;

import components.Components;
import components.PlayerController;
import components.ui.StateMachine;
import pixel_pioneer.Camera;
import pixel_pioneer.GameObject;
import pixel_pioneer.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import core.physics.Physics2D;
import core.physics.components.Rigidbody2D;
import core.assets.AssetPool;

public class Goomba extends Components {

    private transient boolean goingRight = false;
    private transient Rigidbody2D rigidbody;
    private transient float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f();
    private transient boolean onGround = false;
    private transient boolean isDead = false;
    private transient float timeToKill = 0.5f;
    private transient StateMachine stateMachine;

    @Override
    public void start(){
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rigidbody = gameObject.getComponent(Rigidbody2D.class);
        this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
    }

    @Override
    public void update(float dt){
        Camera camera = Window.getScene().getCamera();
        float viewportWidth = Window.getWidth() / camera.getZoom() * 2;
        if (this.gameObject.transform.position.x > camera.position.x * viewportWidth) {
            return;
        }

        if(isDead){
            timeToKill -= dt;
            if(timeToKill <= 0){
                this.gameObject.destroy();
            }
            this.rigidbody.setVelocity(new Vector2f());
            return;
        }

        if(goingRight) {
            velocity.x = walkSpeed;
        } else {
            velocity.x = -walkSpeed;
        }

        checkOnGround();
        if(onGround) {
            this.acceleration.y = 0;
            this.velocity.y = 0;
        } else {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }

        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
        this.rigidbody.setVelocity(velocity);
        if(this.gameObject.transform.
                position.x < Window.getScene().getCamera().position.x - 0.5f){
            this.gameObject.destroy();
        }
    }

    public void checkOnGround() {
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.14f;
        onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    @Override
    public void preSolve(GameObject gameObject, Contact contact, Vector2f contactNormal) {
        if (isDead) {
            return;
        }
        PlayerController playerController = gameObject.getComponent(PlayerController.class);
        if (playerController != null) {
            if (!playerController.isDead()
                    && !playerController.isHurtInvincible()
                    && contactNormal.y > 0.58f) {
                playerController.enemyBounce();
                stomp();
            } else if (!playerController.isDead() && !playerController.isInvincible()) {
                playerController.die();
                if(!playerController.isDead()){
                    contact.setEnabled(false);
                }
            } else if (!playerController.isDead() && playerController.isInvincible()) {
                contact.setEnabled(false);
            }
        } else if (Math.abs(contactNormal.y) < 0.1f) {
            goingRight = contactNormal.x < 0;
        }

        if(gameObject.getComponent(Fireball.class) != null) {
            stomp();
            gameObject.getComponent(Fireball.class).disappear();
        }

    }
        public void stomp() {
            stomp(true);
        }

    public void stomp(boolean playSound) {
        this.isDead = true;
        this.velocity.zero();
        this.rigidbody.setVelocity(new Vector2f());
        this.rigidbody.setAngularVelocity(0.0f);
        this.rigidbody.setGravityScale(0.0f);
        this.stateMachine.trigger("squashMe");
        this.rigidbody.setIsSensor();
        if (playSound) {
            AssetPool.getSound("textures/sounds/bump.ogg").play();
        }
    }
}
