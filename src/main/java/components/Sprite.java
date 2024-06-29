package components;

import org.joml.Vector2f;
import renderer.Texture;

public class Sprite {

    private float width, height;
    private Texture texture = null;
    private Vector2f[] textureCoords = {new Vector2f(1, 1), new Vector2f(1, 0), new Vector2f(0, 0), new Vector2f(0, 1),};


    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTextureCoords() {
        return textureCoords;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setTextureCoords(Vector2f[] textureCoords) {
        this.textureCoords = textureCoords;
    }

    public float width() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float height() {
        return height;
    }

    public void setHeight(int spriteHeight) {
        this.height = spriteHeight;
    }

    public int getTextureID(){
        return texture == null ? -1 : texture.getID();
    }
}
