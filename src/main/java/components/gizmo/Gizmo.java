package components.gizmo;

import editor.PropertiesWindow;
import components.Components;
import components.ui.Sprite;
import components.ui.SpriteRenderer;
import components.game_logic.NonPickable;
import pixel_pioneer.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import pixel_pioneer.GameObject;
import pixel_pioneer.KeyCharacteristics;
import pixel_pioneer.MouseListener;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Components {
    private Vector4f xAxisColor = new Vector4f(1f, 0.3f, 0.3f, 1f);
    private Vector4f xAxisColorHover = new Vector4f(1f, 0f, 0f, 1f);
    private Vector4f yAxisColor = new Vector4f(0.3f, 1f, 0.3f, 1f);
    private Vector4f yAxisColorHover = new Vector4f(0f, 1f, 0f, 1f);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    protected GameObject activeGameObject = null;

    private SpriteRenderer xAxisSpriteRenderer;
    private SpriteRenderer yAxisSpriteRenderer;

    private Vector2f xAxisOffset = new Vector2f(24f / 80f, -6 / 80f);
    private Vector2f yAxisOffset = new Vector2f(-7f / 80f, 21f / 80f);

    protected float gizmoWidth = 16f / 80f;
    protected float gizmoHeight = 48f / 80f;

    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;
    private boolean using = false;

    private PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = KeyCharacteristics.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = KeyCharacteristics.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.xAxisSpriteRenderer = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSpriteRenderer = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;

        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        Window.getScene().addGameObject(this.xAxisObject);
        Window.getScene().addGameObject(this.yAxisObject);
    }

    @Override
    public void start() {
        this.xAxisObject.transform.rotation = 90;
        this.yAxisObject.transform.rotation = 180;
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();


    }

    @Override
    public void update(float dt) {
        if (using) {
            this.setInactive();
        }
        this.xAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
        this.yAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
    }

    @Override
    public void editorUpdate(float dt){
        if(!using) return;
        this.activeGameObject=this.propertiesWindow.getActiveGameObject();

        if(this.activeGameObject!=null){
            this.setActive();
        } else {
            this.setInactive();
            return;
        }

        boolean xAxiosHot = checkXHoverState();
        boolean yAxiosHot = checkYHoverState();

        if((xAxiosHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            xAxisActive = true;
            yAxisActive = false;
        } else if((yAxiosHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            yAxisActive = true;
            xAxisActive = false;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if(this.activeGameObject != null){
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.add(this.yAxisOffset);
        }
    }

    private void setActive(){
        this.xAxisSpriteRenderer.setColor(xAxisColor);
        this.yAxisSpriteRenderer.setColor(yAxisColor);
    }

    private void setInactive () {
        this.activeGameObject = null;
        this.xAxisSpriteRenderer.setColor(new Vector4f(0, 0, 0, 0));
        this.yAxisSpriteRenderer.setColor(new Vector4f(0, 0, 0, 0));
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
        if(mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f) &&
                mousePos.x >= xAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (gizmoHeight / 2.0f) &&
                mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2.0f)){

            xAxisSpriteRenderer.setColor(xAxisColorHover);
            return true;
        }
        xAxisSpriteRenderer.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
        if(mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2.0f) &&
                mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f) &&
                mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2.0f)){

            yAxisSpriteRenderer.setColor(yAxisColorHover);
            return true;
        }
        yAxisSpriteRenderer.setColor(yAxisColor);
        return false;
    }

    public void setUsing(){
        this.using = true;
    }

    public void setNotUsing(){
        this.using = false;
        this.setInactive();
    }
}
