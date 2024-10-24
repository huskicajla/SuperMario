package components.game_logic.controls;

import editor.PropertiesWindow;
import components.Components;
import components.ui.StateMachine;
import components.game_logic.NonPickable;
import components.ui.SpriteRenderer;
import pixel_pioneer.GameObject;
import pixel_pioneer.KeyboardListener;
import pixel_pioneer.MouseListener;
import pixel_pioneer.Window;
import org.joml.Vector4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import scenes.Scene;
import utils.Settings;
import visualizer.DebugDraw;
import visualizer.PickingTexture;

import java.util.HashSet;
import java.util.Set;


import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Components {

    GameObject holdingObject = null;
    private float debounceTime = 0.2f;
    private float debounce = debounceTime;

    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();

    public void pickUpObject(GameObject gameObject){
        if(this.holdingObject != null){
            this.holdingObject.destroy();
        }
        this.holdingObject = gameObject;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        this.holdingObject.addComponent(new NonPickable());
        Window.getScene().addGameObject(gameObject);
    }

    public void place(){
        GameObject newGameObject = holdingObject.copy();
        if(newGameObject.getComponent(StateMachine.class) != null){
            newGameObject.getComponent(StateMachine.class).refreshTextures();
        }
        newGameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
        newGameObject.removeComponent(NonPickable.class);
        Window.getScene().addGameObject(newGameObject);
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;

        PickingTexture pickingTexture = Window.getImGuiLayer().getPropertiesWindow().getPickingTexture();
        Scene currentScene = Window.getScene();

        if(holdingObject != null){
            float x = MouseListener.getWorldX();
            float y = MouseListener.getWorldY();
            holdingObject.transform.position.x =
                    ((int)Math.floor(x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH)
                            + Settings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y =
                    ((int)Math.floor(y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT)
                            + Settings.GRID_HEIGHT / 2.0f;


            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                float halfWidth = Settings.GRID_WIDTH / 2.0f;
                float halfHeight = Settings.GRID_HEIGHT / 2.0f;
                boolean temporary = blockInSquare(holdingObject.transform.position.x - halfWidth,
                        holdingObject.transform.position.y - halfHeight);
                if((MouseListener.isDragging() && !temporary)){
                    place();
                } else if(!MouseListener.isDragging() && debounce < 0) {
                    place();
                    debounce = debounceTime;
                }
            }

            if(KeyboardListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                holdingObject.destroy();
                holdingObject = null;
            }
        } else if(!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0){
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();

            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject pickedGameObject = currentScene.getGameObject(gameObjectId);
            if(pickedGameObject != null && pickedGameObject.getComponent(NonPickable.class) == null){
                Window.getImGuiLayer().getPropertiesWindow().setActiveGameObject(pickedGameObject);
            } else if (pickedGameObject == null && !MouseListener.isDragging()) {
                Window.getImGuiLayer().getPropertiesWindow().clearSelected();
            }
            this.debounce = 0.2f;
        } else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            if(!boxSelectSet) {
                Window.getImGuiLayer().getPropertiesWindow().clearSelected();
                boxSelectStart = MouseListener.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = MouseListener.getScreen();
            Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize = (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.addBox2D((new Vector2f(boxSelectStartWorld)).add(halfSize), new Vector2f(halfSize).mul(2.0f), 0.0f);
        } else if(boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int)boxSelectStart.x;
            int screenStartY = (int)boxSelectStart.y;
            int screenEndX = (int)boxSelectEnd.x;
            int screenEndY = (int)boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if(screenEndX < screenStartX) {
                int temp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = temp;
            }

            if (screenEndY < screenStartY) {
                int temp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = temp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY)
            );

            Set<Integer> uniqueGameObjectIds = new HashSet<>();
            for(float gameObjectId : gameObjectIds){
                uniqueGameObjectIds.add((int) gameObjectId);
            }

            for(Integer gameObjectId : uniqueGameObjectIds){
                GameObject pickedGameObject = Window.getScene().getGameObject(gameObjectId);
                if(pickedGameObject != null && pickedGameObject.getComponent(NonPickable.class) == null){
                    Window.getImGuiLayer().getPropertiesWindow().setActiveGameObject(pickedGameObject);
                }
            }
        }
    }

    private boolean blockInSquare (float x, float y) {
        PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
        Vector2f startScreen = MouseListener.worldToScreen(start);
        Vector2f endScreen = MouseListener.worldToScreen(end);
        Vector2i startScreenInt = new Vector2i((int)startScreen.x + 2, (int)startScreen.y + 2);
        Vector2i endScreenInt = new Vector2i((int)endScreen.x - 2, (int)endScreen.y - 2);
        float[] gameObjectsIds = propertiesWindow.getPickingTexture().readPixels(startScreenInt, endScreenInt);

        for(int i = 0; i < gameObjectsIds.length; i++){
            if(gameObjectsIds[i] >= 0){
                GameObject pickedGameObject = Window.getScene().getGameObject((int)gameObjectsIds[i]);
                if(pickedGameObject.getComponent(NonPickable.class) == null){
                    return true;
                }
            }
        }
        return false;
    }
}
