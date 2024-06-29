package coal;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private String name;
    private List<Component> components;
    public Transform transform;
    private int zIndex;

    public String toString(){
        return String.format("%s is at %s", name, transform.toString());
    }

    public GameObject(){
        init("", new Transform(), 0);
    }

    public GameObject(String name, int zIndex) {
        init(name, new Transform(), zIndex);
    }

    public GameObject(String name, Transform transform, int zIndex) {
        init(name, transform, zIndex);
    }

    private void init(String name, Transform transform, int zIndex){
        this.name = name;
        this.transform = transform;
        this.zIndex = zIndex;
        this.components = new ArrayList<>();
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error casting component";
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())){
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component component) {
        components.add(component);
        component.gameObject = this;
    }

    public void update(float deltaTime){
        for (int i = 0; i < components.size(); i++){
            components.get(i).update(deltaTime);
        }
    }

    public void start(){
        for (int i = 0; i < components.size(); i++){
            components.get(i).start();
        }
    }

    public void imgui(){
        for (Component c : components){
            c.imgui();
        }
    }

    public int zIndex(){
        return zIndex;
    }
}
