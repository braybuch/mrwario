package components;

import coal.Transform;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;

/**
 * This component provides a game object with a sprite renderer
 */
public class SpriteRenderer extends Component{
    /** the colour of the sprite */
    private final Vector4f colour = new Vector4f(1, 1, 1, 1);
    /** the sprite object to render */
    private Sprite sprite = new Sprite();
    /** the last position of the sprite renderer */
    private transient Transform lastTransform;
    /** if the renderer needs to rebuffer */
    private transient boolean dirty = true;

    /**
     * Returns the current color.
     *
     * @return The current color
     */
    public Vector4f getColour() {
        return colour;
    }

    /**
     * Sets the color to the specified value.
     * If the new color is different from the current color, marks the object as dirty.
     *
     * @param colour The new color to set
     */
    public void setColour(Vector4f colour) {
        if (!this.colour.equals(colour)) {
            // Colours aren't equal
            this.colour.set(colour);
            dirty = true;
        }
    }

    /**
     * Sets the sprite to the specified value and marks the object as dirty.
     *
     * @param sprite The new sprite to set
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        dirty = true;
    }

    /**
     * Returns the texture associated with the current sprite.
     *
     * @return The texture of the current sprite
     */
    public Texture getTexture() {
        return sprite.getTexture();
    }

    /**
     * Returns the texture coordinates associated with the current sprite.
     *
     * @return An array of texture coordinates
     */
    public Vector2f[] getTexCoords() {
        return sprite.getTextureCoords();
    }

    /**
     * Returns whether the object is marked as dirty.
     *
     * @return True if the object is dirty, false otherwise
     */
    public boolean getIsDirty() {
        return dirty;
    }

    /**
     * Sets the dirty flag to the specified value.
     *
     * @param dirty The new value for the dirty flag
     */
    public void setIsDirty(boolean dirty){
        this.dirty = dirty;
    }

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float deltaTime) {
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

}
