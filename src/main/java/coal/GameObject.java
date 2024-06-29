package coal;

import components.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a game object
 */
public class GameObject {
    /** how many IDs have been distributed across all game objects */
    private static int ID_COUNTER = 0;
    /** the unique ID */
    private int uid = -1;
    /** the name */
    private String name;
    /** the list of components */
    private List<Component> components;
    /** the location */
    public Transform transform;
    /** the layer order */
    private int zIndex;

    /**
     * Default constructor makes empty game object
     */
    public GameObject(){
        init("", new Transform(), 0);
    }

    /**
     * Constructor with default location.
     *
     * @param name the name of the object
     * @param zIndex the layer order of the object
     */
    public GameObject(String name, int zIndex) {
        init(name, new Transform(), zIndex);
    }

    /**
     * Constructor with all specified parameters
     *
     * @param name the name of the object
     * @param transform the location of the object
     * @param zIndex the layer order of the object
     */
    public GameObject(String name, Transform transform, int zIndex) {
        init(name, transform, zIndex);
    }

    /**
     * Get the layer order
     *
     * @return the layer order
     */
    public int getZIndex(){
        return zIndex;
    }

    /**
     * Get the unique id
     *
     * @return the unique id
     */
    public int getUid(){
        return uid;
    }

    /**
     * Get the list of components
     *
     * @return the list of components
     */
    public List<Component> getComponents(){
        return components;
    }

    /**
     * This method returns a human-readable string representation of the contents of this object
     *
     * @return the string representing this object
     */
    public String toString(){
        return String.format("UID %d: %s. Z-Index; %d, location (%.2f, %.2f), with %s", uid, name, zIndex, transform.position.x, transform.position.y, Arrays.toString(components.toArray()));
    }

    /**
     * This method initializes all fields of this object
     *
     * @param name the name of the object
     * @param transform the location of the object
     * @param zIndex the layer order of the object
     */
    private void init(String name, Transform transform, int zIndex){
        this.name = name;
        this.transform = transform;
        this.zIndex = zIndex;
        this.components = new ArrayList<>();
        uid = ID_COUNTER++;
    }

    /**
     * Get the specified component
     *
     * @param componentClass the class of component you want to receive
     * @return the component if found, or null
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {// Should be impossible but makes java happy
                    e.printStackTrace();
                    assert false : "Error casting component";
                }
            }
        }

        return null;
    }

    /**
     * This method tries to remove the component from this object
     *
     * @param componentClass the class of component you want to recieve
     */
    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())){
                components.remove(i);
                return;
            }
        }
    }

    /**
     * Add the passed component to this object
     *
     * @param component the component you want to add
     */
    public void addComponent(Component component) {
        component.generateID();
        components.add(component);
        component.gameObject = this;
    }

    /**
     * To be called to initialize this object
     *
     * @param maxId TODO what is this
     */
    public static void init(int maxId){
        ID_COUNTER = maxId;
    }

    /**
     * To be called when this object is initialized
     */
    public void start(){
        // For every component, start it
        for (int i = 0; i < components.size(); i++){
            components.get(i).start();
        }
    }

    /**
     * To be called on every frame
     *
     * @param deltaTime the time passed since last update
     */
    public void update(float deltaTime){
        // For each component, update it
        for (int i = 0; i < components.size(); i++){
            components.get(i).update(deltaTime);
        }
    }

    /**
     * To be called every frame by the UI
     */
    public void imgui(){
        for (Component c : components){
            c.imgui();
        }
    }

}
