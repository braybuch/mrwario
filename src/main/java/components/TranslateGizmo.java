package components;

import coal.GameObject;
import coal.Prefabs;
import coal.Window;
import editor.PropertiesWindow;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.Settings;

public class TranslateGizmo extends Component {
    private Vector4f xAxisColour = Settings.RED_4F;
    private Vector4f xAxisColourHove = new Vector4f();
    private Vector4f yAxisColour = Settings.BLUE_4F;
    private Vector4f yAxisColourHover = new Vector4f();

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSpriteRenderer;
    private SpriteRenderer yAxisSpriteRenderer;
    private GameObject activeGameObject = null;
    private PropertiesWindow propertiesWindow;

    private Vector2f xAxisOffset = new Vector2f(36, 9);
    private Vector2f yAxisOffset = new Vector2f(23, 37);

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        // Create translation arrows
        xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 16);
        yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 16);
        xAxisSpriteRenderer = xAxisObject.getComponent(SpriteRenderer.class);
        yAxisSpriteRenderer = yAxisObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;

        // Add them to the scene
        Window.get().getScene().addGameObjectToScene(xAxisObject);
        Window.get().getScene().addGameObjectToScene(yAxisObject);
    }

    @Override
    public void start(){
        xAxisObject.transform.rotation = 90;
        yAxisObject.transform.rotation = 180;
        xAxisObject.setDoSerialize(false);
        yAxisObject.setDoSerialize(false);

    }

    @Override
    public void update(float deltaTime) {
        if (activeGameObject != null) {
            xAxisObject.transform.position.set(activeGameObject.transform.position);
            yAxisObject.transform.position.set(activeGameObject.transform.position);
            xAxisObject.transform.position.add(xAxisOffset);
            yAxisObject.transform.position.add(yAxisOffset);
        }

        activeGameObject = propertiesWindow.getActiveGameObject();
        if (activeGameObject != null) {
            setActive();
        } else {
            setInactive();
        }
    }

        private void setActive() {
            xAxisSpriteRenderer.setColour(xAxisColour);
            yAxisSpriteRenderer.setColour(yAxisColour);
        }

        private void setInactive() {
            activeGameObject = null;
            xAxisSpriteRenderer.setColour(Settings.NULL_4F);
            yAxisSpriteRenderer.setColour(Settings.NULL_4F);
        }
}
