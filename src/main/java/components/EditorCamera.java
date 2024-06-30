package components;

import coal.Camera;
import coal.KeyListener;
import coal.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component {

    private float dragDebounce = 0.1f;
    private float dragSpeed = 30.0f;

    private float scrollSpeed = 0.1f;

    private float lerpTime = 0.0f;

    private Camera levelEditorCamera;
    private Vector2f clickOrigin;

    private boolean reset = false;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        clickOrigin = new Vector2f();
    }

    @Override
    public void update(float deltaTime) {
        // Camera drag
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT) && dragDebounce > 0) {
            // Get mouse position
            clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= deltaTime;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            Vector2f delta = new Vector2f(mousePos).sub(clickOrigin);
            levelEditorCamera.getPosition().sub(delta.mul(deltaTime).mul(dragSpeed));
            clickOrigin.lerp(mousePos, deltaTime);
        }

        if (dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            dragDebounce = 0.1f;
        }

        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * scrollSpeed), 1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.adjustZoom(addValue);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_BACKSPACE)){
            reset = true;
        }

        if (reset){
            // Linear interpolate the camera
            levelEditorCamera.getPosition().lerp(new Vector2f(), lerpTime);
            // Lerp the zoom
            levelEditorCamera.setZoom(levelEditorCamera.getZoom() + (1.0f - levelEditorCamera.getZoom()) * lerpTime);
            // Lerp the camera position
            lerpTime += 0.03f * deltaTime;
            if (Math.abs(levelEditorCamera.getPosition().x) <= 1.0f && Math.abs(levelEditorCamera.getPosition().y) <= 1.0f) {
                lerpTime = 0.0f;
                levelEditorCamera.getPosition().set(0f, 0f);
                levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }
    }

}
