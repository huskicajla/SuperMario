package scenes;

import pixel_pioneer.Camera;
import pixel_pioneer.GameObject;
import pixel_pioneer.GameObjectDeserializer;
import pixel_pioneer.Transform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.ComponentSerializerAndDeserializer;
import components.Components;
import org.joml.Vector2f;
import core.physics.Physics2D;
import visualizer.Visualizer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene {

    private Visualizer visualizer;
    private Camera camera;
    private boolean isRunning;
    private List<GameObject> gameObjects;
    private List<GameObject> pendingGameObjects;
    private Physics2D physics2D;

    private SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        this.physics2D = new Physics2D();
        this.visualizer = new Visualizer();
        this.gameObjects = new ArrayList<>();
        this.pendingGameObjects = new ArrayList<>();
        this.isRunning = false;
    }

    public Physics2D getPhysics() {
        return this.physics2D;
    }

    public void init(){
        this.camera = new Camera(new Vector2f(0, 0));
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }

    public void start(){
        for(int i = 0; i < gameObjects.size(); i++){
            GameObject gameObject = gameObjects.get(i);
            gameObject.start();
            this.visualizer.add(gameObject);
            this.physics2D.add(gameObject);
        }
        isRunning = true;
    }

    public void addGameObject(GameObject gameObject){
        if(!isRunning){
            gameObjects.add(gameObject);
        } else {
            pendingGameObjects.add(gameObject);
        }
    }

    public void destroy() {
        for(GameObject gameObject : gameObjects){
            gameObject.destroy();
        }
    }

    public <T extends Components> GameObject getGameObjectWidth(Class<T> componentClass){
        for(GameObject gameObject : gameObjects){
            if(gameObject.getComponent(componentClass) != null){
               return gameObject;
            }
        }
        return null;
    }

    public List<GameObject> getGameObjects(){
        return this.gameObjects;
    }

    public GameObject getGameObject(int gameObjectId){
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();

        return result.orElse(null);
    }

    public GameObject getGameObject(String gameObjectName){
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.name.equals(gameObjectName))
                .findFirst();

        return result.orElse(null);
    }

    public void editorUpdate(float dt){
        this.camera.adjustProjection();

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.editorUpdate(dt);

            if(gameObject.isDead()) {
                gameObjects.remove(i);
                this.visualizer.destroyGameObject(gameObject);
                this.physics2D.destroyGameObject(gameObject);
                i--;
            }
        }

        for(GameObject gameObject : pendingGameObjects){
            gameObjects.add(gameObject);
            gameObject.start();
            this.visualizer.add(gameObject);
            this.physics2D.add(gameObject);
        }
        pendingGameObjects.clear();
    }

    public void update (float dt){
        this.camera.adjustProjection();
        this.physics2D.update(dt);

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.update(dt);

            if(gameObject.isDead()) {
                gameObjects.remove(i);
                this.visualizer.destroyGameObject(gameObject);
                this.physics2D.destroyGameObject(gameObject);
                i--;
            }
        }

        for(GameObject gameObject : pendingGameObjects){
            gameObjects.add(gameObject);
            gameObject.start();
            this.visualizer.add(gameObject);
            this.physics2D.add(gameObject);
        }
        pendingGameObjects.clear();
    }

    public void render(){
        this.visualizer.render();

    }

    public Camera getCamera() {
        return this.camera;
    }

    public void imgui(){
        this.sceneInitializer.imgui();
    }

    public GameObject createGameObject(String name){
        GameObject gameObject = new GameObject(name);
        gameObject.addComponent(new Transform());
        gameObject.transform = gameObject.getComponent(Transform.class);

        return gameObject;
    }

    public void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Components.class, new ComponentSerializerAndDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        try{
            FileWriter writer = new FileWriter("level.txt");
            List<GameObject> objectsToSerialize = new ArrayList<>();
            for(GameObject gameObject : gameObjects){
                if(gameObject.doSerialization()){
                    objectsToSerialize.add(gameObject);
                }
            }
            writer.write(gson.toJson(objectsToSerialize));
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void load(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Components.class, new ComponentSerializerAndDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        String inFile = "";

        try{
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e){
            e.printStackTrace();
        }

        if(!inFile.equals("")){
            int maxGameObjectId = -1;
            int maxComponentId = -1;
            GameObject[] gameObjects = gson.fromJson(inFile, GameObject[].class);
            for(int i = 0; i < gameObjects.length; i++){
                addGameObject(gameObjects[i]);

                for (Components component : gameObjects[i].getAllComponents()){
                    if(component.getU_id() > maxComponentId){
                        maxComponentId = component.getU_id();
                    }
                }
                if(gameObjects[i].getUid() > maxGameObjectId){
                    maxGameObjectId = gameObjects[i].getUid();
                }
            }
            maxGameObjectId++;
            maxComponentId++;
            GameObject.init(maxGameObjectId);
            Components.init(maxComponentId);
        }
    }
}
