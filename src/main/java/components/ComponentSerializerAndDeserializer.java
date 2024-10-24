package components;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ComponentSerializerAndDeserializer implements JsonSerializer<Components>,
                                                JsonDeserializer<Components> {

    @Override
    public Components deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String type1 = jsonObject.get("type").getAsString();
        JsonElement jsonElement1 = jsonObject.get("properties");

        try {
            return jsonDeserializationContext.deserialize(jsonElement1, Class.forName(type1));

        } catch (ClassNotFoundException e){
            throw new JsonParseException("Unknown element type: " + type1,e);
        }
    }

    @Override
    public JsonElement serialize(Components components, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("type", new JsonPrimitive(components.getClass().getCanonicalName()));
        jsonObject.add("properties", jsonSerializationContext.serialize(components, components.getClass()));

        return jsonObject;
    }
}
