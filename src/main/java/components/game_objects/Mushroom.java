package components.game_objects;

import components.Components;
import components.game_logic.Ground;
import components.PlayerController;
import pixel_pioneer.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import core.physics.components.Rigidbody2D;
import core.assets.AssetPool;

public class Mushroom extends Components {
    private transient boolean goingRight = true;
    private transient Rigidbody2D rigidBody;
    private transient Vector2f speed = new Vector2f(1.0f, 0.0f);
    private transient float maxSpeed = 0.8f;
    private transient boolean hitPlayer = false;


    @Override
    public void start(){
        this.rigidBody = gameObject.getComponent(Rigidbody2D.class);
        AssetPool.getSound("textures/sounds/powerup_appears.ogg").play();

    }

    @Override
    public void update(float dt){
        if(goingRight && Math.abs(rigidBody.getVelocity().x) < maxSpeed){
            rigidBody.addVelocity(speed);
        } else if(!goingRight && Math.abs(rigidBody.getVelocity().x) < maxSpeed){
            rigidBody.addVelocity(new Vector2f(-speed.x, speed.y));
        }
    }

    @Override
    public void preSolve(GameObject gameObject, Contact contact, Vector2f contactNormal) {
       PlayerController playerController = gameObject.getComponent(PlayerController.class);
        if(playerController != null){
            contact.setEnabled(false);
            if(!hitPlayer){
                if(playerController.isSmall()){
                    playerController.powerUp();
                } else {
                    AssetPool.getSound("textures/sounds/coin.ogg").play();
                }
                this.gameObject.destroy();
                hitPlayer = true;
            }
        } else if (gameObject.getComponent(Ground.class) == null){
            contact.setEnabled(false);
            return;
        }

        if(Math.abs(contactNormal.y) < 0.1f){
            goingRight = contactNormal.x < 0;
        }



    }


}
