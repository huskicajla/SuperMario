package pixel_pioneer;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardListener {
    private static KeyboardListener instance;
    private boolean[] keyPressed = new boolean[GLFW_KEY_LAST + 1];
    private boolean[] keyBeginPress = new boolean[GLFW_KEY_LAST + 1];

    private KeyboardListener() {

    }

    public static void endFrame() {
        Arrays.fill(getInstance().keyBeginPress, false);
    }

    public static KeyboardListener getInstance() {
        if (instance == null) {
            instance = new KeyboardListener();
        }
        return KeyboardListener.instance;
    }

    public static void keyboardCallBack (long window, int key, int scancode, int action, int mods) {
       if(key <= GLFW_KEY_LAST && key >= 0){
           if (action == GLFW_PRESS) {
               getInstance().keyPressed[key] = true;
               getInstance().keyBeginPress[key] = true;
           } else if (action == GLFW_RELEASE) {
               getInstance().keyPressed[key] = false;
               getInstance().keyBeginPress[key] = false;
           }
       }
    }

    public static boolean isKeyPressed (int key) {
        if(key <= GLFW_KEY_LAST && key >= 0){
            return getInstance().keyPressed[key];
        }
        return false;
    }

    public static boolean keyBeginPress (int key) {
        if(key <= GLFW_KEY_LAST && key >= 0){
            return getInstance().keyBeginPress[key];
        }
        return false;
    }
}
