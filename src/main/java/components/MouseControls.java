package components;

import coal.GameObject;
import coal.MouseListener;
import coal.Window;
import util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

/**
 * This component provides mouse controls
 */
public class MouseControls extends Component{
    /** the object held by the mouse */
    GameObject holdingObject = null;

    /**
     * Attach the object to the mouse
     *
     * @param object the object to attach to the mouse
     */
    public void pickupObject(GameObject object){
        holdingObject = object;
        Window.get().getScene().addGameObjectToScene(object);
    }

    /**
     * Put the object down
     */
    public void place(){
        // For now the object is printed, then destroyed
        System.out.println(holdingObject.toString());
        holdingObject = null;
    }

    @Override
    public void update(float deltaTime){
        final int   MOUSE_OFFSET_X = 0,
                    MOUSE_OFFSET_Y = 0;
        if (holdingObject != null){
            holdingObject.transform.position.x = MouseListener.getOrthoX() - MOUSE_OFFSET_X;
            holdingObject.transform.position.y = MouseListener.getOrthoY() - MOUSE_OFFSET_Y;

            // Snap position to grid
            holdingObject.transform.position.x = (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            holdingObject.transform.position.y = (int)(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;


            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
            }
        }
    }
}
