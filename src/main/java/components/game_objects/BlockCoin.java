package components.game_objects;

import components.Components;
import org.joml.Vector2f;
import core.assets.AssetPool;
import scenes.LevelSceneInitializer;

public class BlockCoin extends Components {
    private Vector2f topY;
    private float coinSpeed = 1.4f;

    @Override
    public void start(){
        topY = new Vector2f(this.gameObject.transform.position.y).add(0, 0.5f);
        AssetPool.getSound("textures/sounds/coin.ogg").play();
    }

    @Override
    public void update(float dt){
        if(this.gameObject.transform.position.y < topY.y){
            this.gameObject.transform.position.y += dt * coinSpeed;
            this.gameObject.transform.position.x -= (0.05f * dt) % -1.0f;
        } else {
            gameObject.destroy();
            LevelSceneInitializer.addScore(100);
        }
    }
}
