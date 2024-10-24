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

public class Turtle extends Components {
    private transient boolean goingRight = false;
    private transient Rigidbody2D rigidbody;
    private transient float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    private transient boolean onGround = false;
    private transient boolean isDead = false;
    private transient boolean isMoving = false;
    private transient StateMachine stateMachine;
    private float movingDebounce = 0.32f;

    @Override
    public void start(){
        this.stateMachine = this.gameObject.getComponent(StateMachine.class);
        this.rigidbody = this.gameObject.getComponent(Rigidbody2D.class);
        this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
    }

    @Override
    public void update(float dt){
        movingDebounce -= dt;
        Camera camera = Window.getScene().getCamera();
        float viewportWidth = Window.getWidth() / camera.getZoom() * 2;
        if (this.gameObject.transform.position.x > camera.position.x * viewportWidth) {
            return;
        }


        if (!isDead || isMoving) {
            if (goingRight) {
                gameObject.transform.scale.x = -0.25f;
                velocity.x = walkSpeed;
                acceleration.x = 0;
            } else {
                gameObject.transform.scale.x = 0.25f;
                velocity.x = -walkSpeed;
                acceleration.x = 0;
            }
        } else {
            velocity.x = 0;
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

        if(this.gameObject.transform.position.x < Window.getScene().getCamera().position.x - 0.5f){
            this.gameObject.destroy();
        }
    }

    public void checkOnGround() {
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.2f;
        onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    public void stomp() {
        this.isDead = true;
        this.isMoving = false;
        this.velocity.zero();
        this.rigidbody.setVelocity(this.velocity);
        this.rigidbody.setAngularVelocity(0.0f);
        this.rigidbody.setGravityScale(0.0f);
        this.stateMachine.trigger("squashMe");
        AssetPool.getSound("textures/sounds/bump.ogg").play();
    }

    @Override
    public void preSolve(GameObject gameObject, Contact contact, Vector2f contactNormal) {
        Goomba goomaba = gameObject.getComponent(Goomba.class);
        if(isDead && isMoving && goomaba!= null) {
            goomaba.stomp();
            contact.setEnabled(false);
            AssetPool.getSound("textures/sounds/kick.ogg").play();
        }

        PlayerController playerController = gameObject.getComponent(PlayerController.class);
        if (playerController != null) {
            if (!isDead && !playerController.isDead() &&
                    !playerController.isHurtInvincible() &&
                    contactNormal.y > 0.58f) {
                playerController.enemyBounce();
                stomp();
                walkSpeed *= 3.0f;
            } else if (movingDebounce < 0 && !playerController.isDead() &&
                    !playerController.isHurtInvincible() &&
                    (isMoving || !isDead) && contactNormal.y < 0.58f) {
                playerController.die();
                if(!playerController.isDead()) {
                    contact.setEnabled(false);
                }
            } else if (!playerController.isDead() && !playerController.isHurtInvincible()) {
                if (isDead && contactNormal.y > 0.58f) {
                    playerController.enemyBounce();
                    isMoving = !isMoving;
                    goingRight = contactNormal.x < 0;
                } else if (isDead && !isMoving) {
                    isMoving = true;
                    goingRight = contactNormal.x < 0;
                    movingDebounce = 0.32f;
                }
            } else if(!playerController.isDead() && playerController.isHurtInvincible()) {
                contact.setEnabled(false);
            }
        } else if (Math.abs(contactNormal.y) < 0.1f && !gameObject.isDead()
                    && gameObject.getComponent(Mushroom.class) == null) {
            goingRight = contactNormal.x < 0;
            if (isMoving && isDead) {
                AssetPool.getSound("textures/sounds/bump.ogg").play();
            }
        }
        if(gameObject.getComponent(Fireball.class) != null) {
            if(!isDead) {
                walkSpeed *= 3.0f;
                stomp();
            } else {
                isMoving = !isMoving;
                goingRight = contactNormal.x < 0;
            }
            gameObject.getComponent(Fireball.class).disappear();
            contact.setEnabled(false);
        }
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2f contactNormal) {
        if(gameObject.getComponent(Fireball.class) != null ) {
            gameObject.getComponent(Fireball.class).disappear();
        }
    }

}
