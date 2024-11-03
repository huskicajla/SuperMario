package scenes;


import components.*;

import components.ui.SpriteRenderer;
import components.ui.Spritesheet;
import components.ui.StateMachine;
import core.assets.AssetPool;
import core.enums.EventType;
import core.event_system.Event;
import core.event_system.EventSystem;
import editor.GameViewWindow;
import imgui.ImFont;
import imgui.ImGui;
import pixel_pioneer.GameObject;

public class LevelSceneInitializer extends SceneInitializer {
    private static int lives = 3;
    private static int score = 0;

    public LevelSceneInitializer(){

    }

    @Override
    public void init(Scene scene) {
        Spritesheet spriteSheet = AssetPool.getSpriteSheet("textures/images/spritesheets/decorationsAndBlocks.png");

        GameObject cameraObject = scene.createGameObject("GameCamera");
        cameraObject.addComponent(new GameCamera(scene.getCamera()));
        cameraObject.start();
        scene.addGameObject(cameraObject);
    }

    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("textures/shaders/default.glsl");

        AssetPool.addSpriteSheet("textures/images/character_and_enemies.png",
                new Spritesheet(AssetPool.getTexture("textures/images/character_and_enemies.png"),
                        16, 16, 26, 0));

        AssetPool.addSpriteSheet("textures/images/spritesheets/items.png",
                new Spritesheet(AssetPool.getTexture("textures/images/spritesheets/items.png"),
                        16,16,43,0));

        AssetPool.addSpriteSheet("textures/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("textures/images/spritesheets/decorationsAndBlocks.png"),
                        16,16,81,0));

        AssetPool.addSpriteSheet("textures/images/gizmos.png",
                new Spritesheet(AssetPool.getTexture("textures/images/gizmos.png"),
                        24, 48, 3 ,0));
        AssetPool.addSpriteSheet("textures/images/turtle.png",
                new Spritesheet(AssetPool.getTexture("textures/images/turtle.png"),
                        16,24,4,0));
        AssetPool.addSpriteSheet("textures/images/bigSpritesheet.png",
                new Spritesheet(AssetPool.getTexture("textures/images/bigSpritesheet.png"),
                        16,32,42,0));
        AssetPool.addSpriteSheet("textures/images/pipes.png",
                new Spritesheet(AssetPool.getTexture("textures/images/pipes.png"),
                        32,32,42,0));

        AssetPool.getTexture("textures/images/blend2.png");

        //add sounds
        AssetPool.addSound("textures/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("textures/sounds/flagpole.ogg", false);
        AssetPool.addSound("textures/sounds/break_block.ogg", false);
        AssetPool.addSound("textures/sounds/bump.ogg", false);
        AssetPool.addSound("textures/sounds/coin.ogg", false);
        AssetPool.addSound("textures/sounds/gameover.ogg", false);
        AssetPool.addSound("textures/sounds/jump-small.ogg", false);
        AssetPool.addSound("textures/sounds/mario_die.ogg", false);
        AssetPool.addSound("textures/sounds/pipe.ogg", false);
        AssetPool.addSound("textures/sounds/powerup.ogg", false);
        AssetPool.addSound("textures/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("textures/sounds/stage_clear.ogg", false);
        AssetPool.addSound("textures/sounds/stomp.ogg", false);
        AssetPool.addSound("textures/sounds/kick.ogg", false);
        AssetPool.addSound("textures/sounds/invincible.ogg", false);

        AssetPool.getSound("textures/sounds/main-theme-overworld.ogg").play();

        for (GameObject gameObject : scene.getGameObjects()){
            if(gameObject.getComponent(SpriteRenderer.class) != null){
                SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
                if(spriteRenderer != null){
                    spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilepath()));
                }
            }

            if(gameObject.getComponent(StateMachine.class) != null){
                StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }
    }

    @Override
    public void imgui(){

    }

    public static void resetOnLifeLoss(){
        if(lives > 1) {
            lives--;
        } else {
            lives = 3;
            score = 0;
            GameViewWindow.isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }
    }

    public static void addScore(int points) {
        score += points;
    }


    public static int getLives() {
        return lives;
    }

    public static int getScore() {
        return score;
    }
}
