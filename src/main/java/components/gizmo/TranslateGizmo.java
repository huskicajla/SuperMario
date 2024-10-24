package components.gizmo;

import editor.PropertiesWindow;
import components.ui.Sprite;
import pixel_pioneer.MouseListener;

public class TranslateGizmo extends Gizmo {
    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if(activeGameObject != null) {
            if(xAxisActive && !yAxisActive) {
                activeGameObject.transform.position.x -= MouseListener.getWorldDx();
            } else if(yAxisActive && !xAxisActive) {
                activeGameObject.transform.position.y -= MouseListener.getWorldDy();
            }
        }
        super.editorUpdate(dt);
    }

}