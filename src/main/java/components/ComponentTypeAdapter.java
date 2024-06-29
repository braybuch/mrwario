package components;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ComponentTypeAdapter implements JsonDeserializer<Component>, JsonSerializer<Component> {

    @Override
    public Component deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String objType = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName(objType));
        } catch (ClassNotFoundException e){
            throw new JsonParseException("Unknown element type " + type, e);
        }
    }

    @Override
    public JsonElement serialize(Component component, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(component.getClass().getCanonicalName()));
        result.add("properties", context.serialize(component, component.getClass()));
        return result;
    }
}
