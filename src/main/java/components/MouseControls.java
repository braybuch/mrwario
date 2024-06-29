package components;

import coal.GameObject;
import coal.MouseListener;
import coal.Window;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component{
    GameObject holdingObject = null;

    public void pickupObject(GameObject object){
        holdingObject = object;
        Window.get().getScene().addGameObjectToScene(object);
    }

    public void place(){
        holdingObject = null;
    }

    @Override
    public void update(float deltaTime){
        final int MOUSE_OFFSET = 16;
        if (holdingObject != null){
            holdingObject.transform.position.x = MouseListener.getOrthoX() - MOUSE_OFFSET;
            holdingObject.transform.position.y = MouseListener.getOrthoY() - MOUSE_OFFSET;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
            }
        }
    }
}
