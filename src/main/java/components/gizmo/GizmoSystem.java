package components.gizmo;

import components.Components;
import components.ui.Spritesheet;
import pixel_pioneer.KeyboardListener;
import pixel_pioneer.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class GizmoSystem extends Components {
    private Spritesheet gizmos;
    private int usingGizmo = 0;

    public GizmoSystem(Spritesheet spritesheet) {
        gizmos = spritesheet;
    }

    @Override
    public void  start(){
        gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(0),
                Window.getImGuiLayer().getPropertiesWindow()));
        gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1),
                Window.getImGuiLayer().getPropertiesWindow()));
    }

    @Override
    public void editorUpdate(float dt){
        if (usingGizmo == 0) {
            gameObject.getComponent(TranslateGizmo.class).setUsing();
            gameObject.getComponent(ScaleGizmo.class).setNotUsing();
        } else if(usingGizmo == 1) {
            gameObject.getComponent(TranslateGizmo.class).setNotUsing();
            gameObject.getComponent(ScaleGizmo.class).setUsing();
        }
        if(KeyboardListener.isKeyPressed(GLFW_KEY_E)){
            usingGizmo = 0;
        } else if(KeyboardListener.isKeyPressed(GLFW_KEY_R)){
            usingGizmo = 1;
        }
    }
}
