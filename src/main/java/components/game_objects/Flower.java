package components.game_objects;

import components.Components;
import components.PlayerController;
import pixel_pioneer.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import core.physics.components.Rigidbody2D;
import core.assets.AssetPool;

public class Flower extends Components {
    private transient Rigidbody2D rigidbody;

    @Override
    public void start(){
        this.rigidbody = gameObject.getComponent(Rigidbody2D.class);
        AssetPool.getSound("textures/sounds/powerup_appears.ogg").play();
        this.rigidbody.setIsSensor();
    }

    @Override
    public void beginCollision(GameObject gameObject, Contact contact, Vector2f cxontactNormal){
        PlayerController player = gameObject.getComponent(PlayerController.class);
        if(player != null){
            player.powerUp();
            this.gameObject.destroy();
        }
    }
}
