package components;

import coal.Component;
import coal.Transform;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;

public class SpriteRenderer extends Component{

    private Vector4f colour = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean dirty = true;

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(Float deltaTime) {
        if (!lastTransform.equals(gameObject.transform)) {
            // Transform moved
            gameObject.transform.copy(lastTransform);
            dirty = true;
        }
    }

    @Override
    public void imgui(){
        float[] imColour = {colour.x, colour.y, colour.z, colour.w};
        if (ImGui.colorPicker4("Colour Picker: ", imColour)){
            colour.set(imColour[0], imColour[1], imColour[2], imColour[3]);
            dirty = true;
        }

    }

    public Vector4f getColour() {
        return colour;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTextureCoords();
    }

    public void setColour(Vector4f colour) {
        if (!this.colour.equals(colour)) {
            // Colours aren't equal
            this.colour.set(colour);
            dirty = true;
        }
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clean(){
        dirty = false;
    }
}
