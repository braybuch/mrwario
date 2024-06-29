package coal;

import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;

public class GameObjectTypeAdapter implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.get("components").getAsJsonArray();
        Transform transform = context.deserialize(jsonObject.get("transform"), Transform.class);
        int zIndex = context.deserialize(jsonObject.get("zIndex"), int.class);

        GameObject g = new GameObject(name, transform, zIndex);
        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);
            g.addComponent(c);
        }
        return g;
    }
}
