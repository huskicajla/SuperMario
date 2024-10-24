package components.game_logic.controls;

import editor.PropertiesWindow;
import components.Components;
import components.ui.StateMachine;
import pixel_pioneer.GameObject;
import pixel_pioneer.KeyboardListener;
import pixel_pioneer.Window;
import utils.Settings;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardControls extends Components {
    private float debounceTime = 0.2f;
    private float debounce = 0.0f;
    @Override
    public void editorUpdate(float dt) {
        PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
        float multiplier = KeyboardListener.isKeyPressed(GLFW_KEY_SPACE) ? 0.1f : 1.0f;
        debounce -= dt;

        if (KeyboardListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyboardListener.keyBeginPress(GLFW_KEY_D) && activeGameObject != null) {
            GameObject newObj = activeGameObject.copy();
            Window.getScene().addGameObject(newObj);
            newObj.transform.position.add(Settings.GRID_WIDTH, 0.0f);
            propertiesWindow.setActiveGameObject(newObj);
            if(newObj.getComponent(StateMachine.class) != null) {
                newObj.getComponent(StateMachine.class).refreshTextures();
            }
        } else if (KeyboardListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyboardListener.keyBeginPress(GLFW_KEY_D) && activeGameObjects.size() > 1) {
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addGameObject(copy);
                propertiesWindow.addActiveGameObject(copy);
                if(copy.getComponent(StateMachine.class) != null) {
                    copy.getComponent(StateMachine.class).refreshTextures();
                }
            }
        } else if (KeyboardListener.keyBeginPress(GLFW_KEY_DELETE)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        } else if (KeyboardListener.keyBeginPress(GLFW_KEY_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.position.y -= Settings.GRID_HEIGHT * multiplier;
            }
        } else if (KeyboardListener.keyBeginPress(GLFW_KEY_UP) && debounce < 0) {
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.position.y += Settings.GRID_HEIGHT * multiplier;
            }
        } else if (KeyboardListener.keyBeginPress(GLFW_KEY_LEFT) && debounce < 0) {
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.position.x -= Settings.GRID_WIDTH * multiplier;
            }
        } else if (KeyboardListener.keyBeginPress(GLFW_KEY_RIGHT) && debounce < 0) {
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.position.x += Settings.GRID_WIDTH * multiplier;
            }
        } else if (KeyboardListener.keyBeginPress(GLFW_KEY_PAGE_UP) && debounce < 0) {
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.zIndex++;
            }
        } else if (KeyboardListener.keyBeginPress(GLFW_KEY_PAGE_DOWN) && debounce < 0){
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.zIndex--;
            }
        }
    }
}
