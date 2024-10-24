package pixel_pioneer;

import com.google.gson.*;
import components.Components;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        GameObject gameObject = new GameObject(name);
        for(JsonElement component : components) {
            Components componentsObject = jsonDeserializationContext.deserialize(component, Components.class);
            gameObject.addComponent(componentsObject);
        }
        gameObject.transform = gameObject.getComponent(Transform.class);
        return gameObject;
    }
}
