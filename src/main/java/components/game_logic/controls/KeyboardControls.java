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

        //Sets movement speed; movement is slower if the SPACE key is pressed.
        float multiplier = KeyboardListener.isKeyPressed(GLFW_KEY_SPACE) ? 0.1f : 1.0f;
        debounce -= dt;
        //Duplicates the active object when CTRL + D is pressed, and moves the copy by one grid unit.
        if (KeyboardListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyboardListener.keyBeginPress(GLFW_KEY_D) && activeGameObject != null) {
            GameObject newObj = activeGameObject.copy();
            Window.getScene().addGameObject(newObj);
            newObj.transform.position.add(Settings.GRID_WIDTH, 0.0f);
            propertiesWindow.setActiveGameObject(newObj);
            if(newObj.getComponent(StateMachine.class) != null) {
                newObj.getComponent(StateMachine.class).refreshTextures();
            }
        }
        // Duplicates all selected objects if more than one is selected.
        else if (KeyboardListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
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
        }
        // Deletes all selected objects when the DELETE key is pressed.
        else if (KeyboardListener.keyBeginPress(GLFW_KEY_DELETE)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        }
        // Moves all selected objects down when the DOWN arrow key is pressed.
        else if (KeyboardListener.keyBeginPress(GLFW_KEY_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.position.y -= Settings.GRID_HEIGHT * multiplier;
            }
        }
        // Moves all selected objects down when the DOWN arrow key is pressed.
        else if (KeyboardListener.keyBeginPress(GLFW_KEY_UP) && debounce < 0) {
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.position.y += Settings.GRID_HEIGHT * multiplier;
            }
        }
        // Moves objects left when the LEFT arrow key is pressed.
        else if (KeyboardListener.keyBeginPress(GLFW_KEY_LEFT) && debounce < 0) {
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.position.x -= Settings.GRID_WIDTH * multiplier;
            }
        }
        // Moves objects right when the RIGHT arrow key is pressed.
        else if (KeyboardListener.keyBeginPress(GLFW_KEY_RIGHT) && debounce < 0) {
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.position.x += Settings.GRID_WIDTH * multiplier;
            }
        }
        // Increases z-index of objects to bring them to the front when PAGE_UP is pressed.
        else if (KeyboardListener.keyBeginPress(GLFW_KEY_PAGE_UP) && debounce < 0) {
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.zIndex++;
            }
        }
        // Decreases z-index of objects to send them to the back when PAGE_DOWN is pressed.
        else if (KeyboardListener.keyBeginPress(GLFW_KEY_PAGE_DOWN) && debounce < 0){
            debounce = debounceTime;
            for(GameObject gameObject : activeGameObjects) {
                gameObject.transform.zIndex--;
            }
        }
    }
}
