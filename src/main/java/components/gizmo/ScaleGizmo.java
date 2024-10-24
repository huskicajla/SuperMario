package components.gizmo;

import editor.PropertiesWindow;
import components.ui.Sprite;
import pixel_pioneer.MouseListener;

public class ScaleGizmo extends Gizmo {

    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if(activeGameObject != null) {
            if(xAxisActive && !yAxisActive) {
                activeGameObject.transform.scale.x -= MouseListener.getWorldDx();
            } else if(yAxisActive && !xAxisActive) {
                activeGameObject.transform.scale.y -= MouseListener.getWorldDy();
            }
        }
        super.editorUpdate(dt);
    }

}
