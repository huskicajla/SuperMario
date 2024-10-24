package components.game_objects;

import components.PlayerController;
import core.assets.AssetPool;

public class BreakableBrick extends Block {

    @Override
    void playerHit(PlayerController playerController) {
        if(!playerController.isSmall()) {
            AssetPool.getSound("textures/sounds/break_block.ogg").play();
            gameObject.destroy();
        }
    }
}
