package components.game_objects;

import components.PlayerController;
import components.ui.StateMachine;
import pixel_pioneer.GameObject;
import pixel_pioneer.KeyCharacteristics;
import pixel_pioneer.Window;

public class QuestionBlock extends Block {
    private enum BlockType{
        Coin,
        PowerUp,
        Invincibility
    }

    public BlockType blockType = BlockType.Coin;

    @Override
    void playerHit(PlayerController playerController) {
        switch (blockType){
            case Coin: doCoin(playerController); break;
            case PowerUp: doPowerUp(playerController); break;
            case Invincibility: doInvincibility(playerController); break;
        }
        StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
        if(stateMachine != null){
            stateMachine.trigger("setInactive");
            this.setInactive();
        }
    }

    private void doCoin(PlayerController playerController){
        GameObject coin = KeyCharacteristics.generateBlockCoin();
        coin.transform.position.set(this.gameObject.transform.position);
        coin.transform.position.y += 0.25f;
        Window.getScene().addGameObject(coin);
    }

    private void doPowerUp(PlayerController playerController){
        if(playerController.isSmall()) {
            spawnMushroom();
        } else {
            spawnFlower();
        }
    }

    private void doInvincibility(PlayerController playerController){

    }

    private void spawnMushroom(){
        GameObject mushroom = KeyCharacteristics.generateMushroom();
        mushroom.transform.position.set(this.gameObject.transform.position);
        mushroom.transform.position.y += 0.25f;
        Window.getScene().addGameObject(mushroom);
    }

    private void spawnFlower(){
        GameObject flower = KeyCharacteristics.generateFlower();
        flower.transform.position.set(this.gameObject.transform.position);
        flower.transform.position.y += 0.25f;
        Window.getScene().addGameObject(flower);
    }
}
