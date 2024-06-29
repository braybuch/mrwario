package components;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * This class helps serialize and deserialize components
 */
public class ComponentTypeAdapter implements JsonDeserializer<Component>, JsonSerializer<Component> {

    @Override
    public Component deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        // Convert the json element to a json object
        JsonObject jsonObject = json.getAsJsonObject();
        // Extract information from json object
        String objType = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");
        // deserialize with extracted class
        try {
            return context.deserialize(element, Class.forName(objType));
        } catch (ClassNotFoundException e){
            throw new JsonParseException("Unknown element type " + type, e);
        }
    }

    @Override
    public JsonElement serialize(Component component, Type type, JsonSerializationContext context) {
        // Serialize type and fields
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(component.getClass().getCanonicalName()));
        result.add("properties", context.serialize(component, component.getClass()));
        return result;
    }
}
