package components;

import org.joml.Vector2f;
import renderer.Texture;

/**
 * This component provides a sprite for the game object
 */
public class Sprite extends Component{
    /** the size of the sprite */
    private float width, height;
    /** the image for the sprite */
    private Texture texture = null;
    /** The format of the texture coordinates */
    private Vector2f[] textureCoords = {new Vector2f(1, 1), new Vector2f(1, 0), new Vector2f(0, 0), new Vector2f(0, 1),};

    /**
     * Get the width
     *
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * Set the width
     *
     * @param width the new width
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Get the height
     *
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * Set the height
     *
     * @param height the new height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Get the texture
     *
     * @return the texture
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Set the texture
     *
     * @param texture the new texture
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /**
     * Get the texture coordinates
     *
     * @return the texture coordinates
     */
    public Vector2f[] getTextureCoords() {
        return textureCoords;
    }

    /**
     * Set the texture coordinates
     *
     * @param textureCoords the new texture coordinates
     */
    public void setTextureCoords(Vector2f[] textureCoords) {
        this.textureCoords = textureCoords;
    }

    /**
     * Get the texture ID
     *
     * @return the texture's id or -1 if it's null
     */
    public int getTextureID(){
        return texture == null ? -1 : texture.getID();
    }
}
