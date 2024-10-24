package components;

import pixel_pioneer.Camera;
import pixel_pioneer.KeyboardListener;
import pixel_pioneer.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Components {

    private float dragDebounce = 0.032f;

    private Camera levelEditorCamera;
    private Vector2f clickOrigin;

    private float lerpTime = 0.0f;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;
    private float moveSpeed;

    private boolean reset = false;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt) {
        // Dragging functionality remains the same
        if ((MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0)) {
            this.clickOrigin = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
            dragDebounce -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePosition = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
            Vector2f delta = new Vector2f(mousePosition).sub(this.clickOrigin);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePosition, dt);
        }
        if (dragDebounce <= 0 && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDebounce = 0.01f;
        }

        // Zoom using mouse scroll
        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
                    1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

        // Zoom in with 'CTRL + Z' key combo
        if (KeyboardListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyboardListener.isKeyPressed(GLFW_KEY_Z)) {
            levelEditorCamera.addZoom(-scrollSensitivity); // Zoom in
        }

        // Zoom out with 'CTRL + X' key combo
        if (KeyboardListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyboardListener.isKeyPressed(GLFW_KEY_X)) {
            levelEditorCamera.addZoom(scrollSensitivity); // Zoom out
        }

        // Reset camera view
        if (KeyboardListener.isKeyPressed(GLFW_KEY_KP_DECIMAL)) {
            reset = true;
        }

        // Reset functionality
        if (reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * dt;
            if (Math.abs(levelEditorCamera.position.x) <= 5.0f &&
                    Math.abs(levelEditorCamera.position.y) <= 5.0f) {
                this.lerpTime = 0.0f;
                levelEditorCamera.position.set(0f, 0f);
                this.levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }

        // Move camera with arrow keys while holding CTRL
        if (KeyboardListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            moveSpeed = 3.0f;
            if (KeyboardListener.isKeyPressed(GLFW_KEY_UP)) {
                levelEditorCamera.position.y += moveSpeed * dt;
            }
            if (KeyboardListener.isKeyPressed(GLFW_KEY_DOWN)) {
                levelEditorCamera.position.y -= moveSpeed * dt;
            }
            if (KeyboardListener.isKeyPressed(GLFW_KEY_RIGHT)) {
                levelEditorCamera.position.x += moveSpeed * dt;
            }
            if (KeyboardListener.isKeyPressed(GLFW_KEY_LEFT)) {
                levelEditorCamera.position.x -= moveSpeed * dt;
            }
        }
    }
}
