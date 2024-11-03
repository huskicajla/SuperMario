package components.game_objects;

import components.Components;
import components.PlayerController;
import pixel_pioneer.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import core.assets.AssetPool;
import scenes.LevelSceneInitializer;

public class Coin extends Components {
    private Vector2f topY;
    private float coinSpeed = 1.4f;
    private transient boolean playAnim = false;

    @Override
    public void start() {
        topY = new Vector2f(this.gameObject.transform.position.y).add(0, 0.5f);
    }

    @Override
    public void update(float dt) {
        if(playAnim) {
            if(this.gameObject.transform.position.y < topY.y) {
                this.gameObject.transform.position.y += dt * coinSpeed;
                this.gameObject.transform.scale.x -= (0.5f * dt) % -1.0f;
            } else {
                this.gameObject.destroy();
                LevelSceneInitializer.addScore(100);
            }
        }
    }

    @Override
    public void preSolve(GameObject gameObject, Contact contact, Vector2f contactNormal){
        if(gameObject.getComponent(PlayerController.class) != null) {
            AssetPool.getSound("textures/sounds/coin.ogg").play();
            playAnim = true;
            contact.setEnabled(false);
        }
    }

}
