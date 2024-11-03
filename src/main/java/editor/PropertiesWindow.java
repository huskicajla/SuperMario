package editor;

import pixel_pioneer.GameObject;

import components.ui.SpriteRenderer;
import imgui.ImGui;
import org.joml.Vector4f;
import core.physics.components.Box2DCollider;
import core.physics.components.CircleCollider;
import core.physics.components.Rigidbody2D;
import visualizer.PickingTexture;

import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow {
    private List<GameObject> activeGameObjects;
    private List<Vector4f> activeGameObjectOriginalColor;
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.activeGameObjectOriginalColor = new ArrayList<>();
    }

    public void imgui() {
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties Window");


            if(ImGui.beginPopupContextWindow("ComponentsAdder")) {
                if(ImGui.menuItem("Add Rigidbody")) {
                    if(activeGameObject.getComponent(Rigidbody2D.class) == null){
                        activeGameObject.addComponent(new Rigidbody2D());
                    }
                }
                if(ImGui.menuItem("Add Box Collider")) {
                    if(activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(CircleCollider.class) == null){
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }
                if(ImGui.menuItem("Add Circle Collider")) {
                    if(activeGameObject.getComponent(CircleCollider.class) == null &&
                            activeGameObject.getComponent(Box2DCollider.class) == null){
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }
                if (ImGui.menuItem("Delete object")){
                    activeGameObject.destroy();
                    setActiveGameObject(null);
                }
                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return activeGameObjects.size() == 1 ? this.activeGameObjects.get(0) : null;
    }

    public List<GameObject> getActiveGameObjects() {
        return this.activeGameObjects;
    }

    public void clearSelected() {
        if(!activeGameObjectOriginalColor.isEmpty()){
            int i = 0;
            for(GameObject gameObject : activeGameObjects){
                SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
                if(spriteRenderer != null){
                    spriteRenderer.setColor(activeGameObjectOriginalColor.get(i));
                }
                i++;
            }
        }
        this.activeGameObjects.clear();
        this.activeGameObjectOriginalColor.clear();
    }

    public void setActiveGameObject(GameObject activeGameObject) {
        if(activeGameObject != null) {
            clearSelected();
            this.activeGameObjects.add(activeGameObject);
        }
    }

    public void addActiveGameObject(GameObject activeGameObject) {
        SpriteRenderer spriteRenderer = activeGameObject.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null) {
            this.activeGameObjectOriginalColor.add(new Vector4f(spriteRenderer.getColor()));
            spriteRenderer.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            this.activeGameObjectOriginalColor.add(new Vector4f());
        }
        this.activeGameObjects.add(activeGameObject);

    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }
}
