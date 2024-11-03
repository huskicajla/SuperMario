package components.game_objects;

import components.Components;
import components.PlayerController;
import pixel_pioneer.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import core.assets.AssetPool;

// Abstract class that represents a block component in the game,
public abstract class Block extends Components {
    private transient boolean bopGoingUp = true;
    private transient boolean doBopAnimation = true;
    private transient Vector2f bopStart;
    private transient Vector2f topBopLocation;
    private transient boolean active = true;

    public float bopSpeed = 0.4f;

    @Override
    public void start() {
        this.bopStart = new Vector2f(this.gameObject.transform.position);
        this.topBopLocation = new Vector2f(bopStart).add(0.0f, 0.02f);
    }

    // Updates the block's position for the bop animation over time
    @Override
    public void update(float dt) {
        if(doBopAnimation) {
            if(bopGoingUp) {
                if(this.gameObject.transform.position.y < topBopLocation.y) {
                    this.gameObject.transform.position.y += bopSpeed * dt;
                } else {
                    bopGoingUp = false;
                }
            } else {
                if(this.gameObject.transform.position.y > bopStart.y) {
                    this.gameObject.transform.position.y -= bopSpeed * dt;
                } else {
                    this.gameObject.transform.position.y = this.bopStart.y;
                    bopGoingUp = true;
                    doBopAnimation = false;
                }
            }
        }
    }

    // Called when the block begins to collide with another game object
    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if(active && playerController != null && contactNormal.y < -0.8f) {
            doBopAnimation = true;
            AssetPool.getSound("textures/sounds/bump.ogg").play();
            playerHit(playerController);
        }
    }

    // Deactivates the block, preventing further interactions with the player
    public void setInactive() {
        this.active = false;
    }

    // Abstract method that must be implemented by subclasses to define player interaction behavior
    abstract void playerHit(PlayerController playerController);
}
