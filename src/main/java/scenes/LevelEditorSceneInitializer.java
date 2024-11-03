package scenes;

import components.game_logic.controls.KeyboardControls;
import components.game_logic.controls.MouseControls;
import components.game_logic.Ground;
import components.gizmo.GizmoSystem;
import components.ui.Sprite;
import components.ui.SpriteRenderer;
import components.ui.Spritesheet;
import components.ui.StateMachine;
import core.enums.Direction;
import components.*;
import components.game_objects.BreakableBrick;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import core.physics.components.Box2DCollider;
import core.physics.components.Rigidbody2D;
import core.enums.BodyType;
import core.assets.AssetPool;
import pixel_pioneer.GameObject;
import pixel_pioneer.KeyCharacteristics;
import pixel_pioneer.Sound;

import java.io.File;
import java.util.Collection;

public class LevelEditorSceneInitializer extends SceneInitializer {

    private Spritesheet spriteSheet;

    private GameObject levelEditorStuff;

    public LevelEditorSceneInitializer(){

    }

    @Override
    public void init(Scene scene) {
        spriteSheet = AssetPool.getSpriteSheet("textures/images/spritesheets/decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpriteSheet("textures/images/gizmos.png");

        levelEditorStuff = scene.createGameObject("LevelEditorStuff");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new KeyboardControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));

        scene.addGameObject(levelEditorStuff);
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

        AssetPool.getSound("textures/sounds/main-theme-overworld.ogg").stop();

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

        ImGui.begin("Objects");

        if(ImGui.beginTabBar("WindowTabBar")){
            if(ImGui.beginTabItem("Solid Blocks")) {
                ImVec2 windowPosition = new ImVec2();
                ImGui.getWindowPos(windowPosition);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPosition.x + windowSize.x;
                for(int i = 0; i < spriteSheet.size(); i++){
                    if(i == 34) continue;
                    if(i >= 38 && i < 61) continue;
                    Sprite sprite = spriteSheet.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 2;
                    float spriteHeight = sprite.getHeight() * 2;
                    int id = sprite.getTextureID();
                    Vector2f[] textureCoordinations = sprite.getTextureCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                        GameObject object = KeyCharacteristics.generateSpriteObject(sprite, 0.25f, 0.25f);
                        Rigidbody2D rigidbody = new Rigidbody2D();
                        rigidbody.setBodyType(BodyType.Static);
                        object.addComponent(rigidbody);
                        Box2DCollider boxCollider = new Box2DCollider();
                        boxCollider.setHalfSize(new Vector2f(0.25f, 0.25f));
                        object.addComponent(boxCollider);
                        object.addComponent(new Ground());
                        if(i == 12){
                            object.addComponent(new BreakableBrick());
                        }
                        levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPosition = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPosition);

                    float lastButtonX2 = lastButtonPosition.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if(i + 1 < spriteSheet.size() && nextButtonX2 < windowX2){
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            if(ImGui.beginTabItem("Decoration Blocks")){
                ImVec2 windowPosition = new ImVec2();
                ImGui.getWindowPos(windowPosition);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPosition.x + windowSize.x;
                for(int i = 34; i < 61; i++){
                    if(i >= 35 && i < 38) continue;
                    if(i >= 42 && i < 45) continue;
                    Sprite sprite = spriteSheet.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 2;
                    float spriteHeight = sprite.getHeight() * 2;
                    int id = sprite.getTextureID();
                    Vector2f[] textureCoordinations = sprite.getTextureCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                        GameObject object = KeyCharacteristics.generateSpriteObject(sprite, 0.25f, 0.25f);
                        levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPosition = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPosition);

                    float lastButtonX2 = lastButtonPosition.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if(i + 1 < spriteSheet.size() && nextButtonX2 < windowX2){
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }

            if(ImGui.beginTabItem("Prefabs")){
                int uid = 0;
                Spritesheet playerSprites = AssetPool.getSpriteSheet("textures/images/character_and_enemies.png");
                Sprite sprite = playerSprites.getSprite(0);
                float spriteWidth = sprite.getWidth() * 2;
                float spriteHeight = sprite.getHeight() * 2;
                int id = sprite.getTextureID();
                Vector2f[] textureCoordinations = sprite.getTextureCoords();

                ImGui.pushID(uid++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                    GameObject object = KeyCharacteristics.generateMario();
                    levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                Spritesheet items = AssetPool.getSpriteSheet("textures/images/spritesheets/items.png");
                sprite = items.getSprite(0);
                id = sprite.getTextureID();
                textureCoordinations = sprite.getTextureCoords();
                ImGui.pushID(uid++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                    GameObject object = KeyCharacteristics.generateQuestionBlock();
                    levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                sprite = items.getSprite(7);
                id = sprite.getTextureID();
                textureCoordinations = sprite.getTextureCoords();
                ImGui.pushID(uid++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                    GameObject object = KeyCharacteristics.generateCoin();
                    levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                sprite = playerSprites.getSprite(14);
                id = sprite.getTextureID();
                textureCoordinations = sprite.getTextureCoords();
                ImGui.pushID(uid++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                    GameObject object = KeyCharacteristics.generateGoomba();
                    levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                Spritesheet turtle = AssetPool.getSpriteSheet("textures/images/turtle.png");
                sprite = turtle.getSprite(0);
                id = sprite.getTextureID();
                textureCoordinations = sprite.getTextureCoords();
                ImGui.pushID(uid++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                    GameObject object = KeyCharacteristics.generateTurtle();
                    levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                sprite = items.getSprite(6);
                id = sprite.getTextureID();
                textureCoordinations = sprite.getTextureCoords();
                ImGui.pushID(uid++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                    GameObject object = KeyCharacteristics.generateFlagtop();
                    levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                sprite = items.getSprite(33);
                id = sprite.getTextureID();
                textureCoordinations = sprite.getTextureCoords();
                ImGui.pushID(uid++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                    GameObject object = KeyCharacteristics.generateFlagPole();
                    levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                Spritesheet pipes = AssetPool.getSpriteSheet("textures/images/pipes.png");
                sprite = pipes.getSprite(0);
                id = sprite.getTextureID();
                textureCoordinations = sprite.getTextureCoords();
                ImGui.pushID(uid++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                    GameObject object = KeyCharacteristics.generatePipe(Direction.Down);
                    levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                sprite = pipes.getSprite(1);
                id = sprite.getTextureID();
                textureCoordinations = sprite.getTextureCoords();
                ImGui.pushID(uid++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                    GameObject object = KeyCharacteristics.generatePipe(Direction.Up);
                    levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                sprite = pipes.getSprite(2);
                id = sprite.getTextureID();
                textureCoordinations = sprite.getTextureCoords();
                ImGui.pushID(uid++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                    GameObject object = KeyCharacteristics.generatePipe(Direction.Right);
                    levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                sprite = pipes.getSprite(3);
                id = sprite.getTextureID();
                textureCoordinations = sprite.getTextureCoords();
                ImGui.pushID(uid++);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinations[2].x, textureCoordinations[0].y, textureCoordinations[0].x, textureCoordinations[2].y)){
                    GameObject object = KeyCharacteristics.generatePipe(Direction.Left);
                    levelEditorStuff.getComponent(MouseControls.class).pickUpObject(object);
                }
                ImGui.popID();

                ImGui.endTabItem();
            }

            if(ImGui.beginTabItem("Sounds")){
                Collection<Sound> sounds = AssetPool.getSounds();
                for(Sound sound : sounds){
                    File temp = new File(sound.getFilepath());
                    if(ImGui.button(temp.getName())){
                        if(!sound.isPlaying()){
                            sound.play();
                        } else {
                            sound.stop();
                        }
                    }

                    if(ImGui.getContentRegionAvailX() > 100){
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }

        ImGui.end();
    }
}
