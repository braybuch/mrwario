package components;

import coal.GameObject;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * This class represents a component that can be added to a game object
 */
public abstract class Component {
    /**
     * the number of UIDs handed out across all components
     */
    private static int ID_COUNTER = 0;
    /**
     * this objects unique id
     */
    private int uid = -1;
    /**
     * the game object that contains this component
     */
    public transient GameObject gameObject = null;

    /**
     * Get the UID
     *
     * @return the UID
     */
    public int getUid() {
        return uid;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public void start() {

    }

    public void update(float deltaTime) {

    }

    public void imgui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field f : fields) {
                // Check if field is transient, and if so, skip
                boolean isTransient = Modifier.isTransient(f.getModifiers());
                if (isTransient) {
                    continue;
                }

                // Check if field is private and if so, expose
                boolean isPrivate = Modifier.isPrivate(f.getModifiers());
                if (isPrivate) {
                    f.setAccessible(true);
                }

                Class type = f.getType();
                Object value = f.get(this);
                String name = f.getName();

                if (type == int.class) {
                    int val = (int) value;
                    int[] imInt = {val};
                    if (ImGui.dragInt(name + ": ", imInt)) {
                        f.set(this, imInt[0]);
                    }
                } else if (type == float.class) {
                    float val = (float) value;
                    float[] imFloat = {val};
                    if (ImGui.dragFloat(name + ": ", imFloat)) {
                        f.set(this, imFloat[0]);
                    }
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    boolean[] imBoolean = {val};
                    if (ImGui.checkbox(name + ": ", val)) {
                        f.set(this, !val);
                    }
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    float[] imVec = {val.x, val.y};
                    if (ImGui.dragFloat2(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1]);
                    }
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat4(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                }

                if (isPrivate) {
                    f.setAccessible(false);
                }

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Make a unique identifier for this object
     */
    public void generateID() {
        if (uid == -1) {
            uid = ID_COUNTER++;
        }
    }


}
