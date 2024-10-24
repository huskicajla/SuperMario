package pixel_pioneer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.ComponentSerializerAndDeserializer;
import components.Components;
import components.ui.SpriteRenderer;
import imgui.ImGui;
import core.assets.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private static int ID_COUNTER = 0;
    private int uid;

    public String name;
    private List<Components> components;
    public transient Transform transform;
    private boolean doSerialization = true;
    private boolean isDead = false;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();


        this.uid = ID_COUNTER++;
    }


    public <T extends Components> T getComponent(Class<T> componentClass) {
        for (Components component : components) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                try{
                    return componentClass.cast(component);
                } catch (ClassCastException e){
                    e.printStackTrace();
                    assert false: "Error: Casting components failed";
                }
            }
        }
        return null;
    }

    public <T extends Components> void removeComponent(Class<T> componentClass) {
        for(int i = 0; i < components.size(); i++){
            Components comp = components.get(i);
            if(componentClass.isAssignableFrom(comp.getClass())){
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Components component) {
        component.generate_id();
        this.components.add(component);
        component.gameObject = this;
    }

    public void update(float dt){
        for(int i = 0; i < components.size(); i++){
            components.get(i).update(dt);
        }
    }

    public void editorUpdate(float dt){
        for(int i = 0; i < components.size(); i++){
            components.get(i).editorUpdate(dt);
        }
    }

    public void start(){
        for(int i = 0; i < components.size(); i++){
            components.get(i).start();
        }
    }

    public void imgui() {
        for(Components component : components){
            if(ImGui.collapsingHeader(component.getClass().getSimpleName())) {
                component.imgui();
            }
        }
    }

    public void destroy() {
        this.isDead = true;
        for(int i = 0; i < components.size(); i++){
            components.get(i).destroy();
        }
    }

    public GameObject copy(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Components.class, new ComponentSerializerAndDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        String objJson = gson.toJson(this);
        GameObject copy = gson.fromJson(objJson, GameObject.class);

        copy.generateUID();
        for(Components component : copy.getAllComponents()){
            component.generate_id();
        }

        SpriteRenderer spriteRenderer = copy.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null && spriteRenderer.getTexture() != null){
            spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilepath()));
        }
        return copy;
    }


    public boolean isDead(){
        return isDead;
    }

    public static void init(int maxId){
        ID_COUNTER = maxId;
    }

    public int getUid() {
        return this.uid;
    }

    public List<Components> getAllComponents(){
        return this.components;
    }

    public void setNoSerialize(){
        this.doSerialization = false;
    }

    public boolean doSerialization() {
        return this.doSerialization;
    }

    public void generateUID() {
        this.uid = ID_COUNTER++;
    }
}
