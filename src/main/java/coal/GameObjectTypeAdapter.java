package coal;

import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;

/**
 * This class helps deserialize game objects
 */
public class GameObjectTypeAdapter implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        // Convert the json element to a json object
        JsonObject jsonObject = json.getAsJsonObject();
        // Extract information from json object
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.get("components").getAsJsonArray();
        Transform transform = context.deserialize(jsonObject.get("transform"), Transform.class);
        int zIndex = context.deserialize(jsonObject.get("zIndex"), int.class);
        // Create new game object with extracted fields
        GameObject g = new GameObject(name, transform, zIndex);
        // Deserialize each component and add to the game object
        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);
            g.addComponent(c);
        }
        // return the deserialized game object
        return g;
    }
}
