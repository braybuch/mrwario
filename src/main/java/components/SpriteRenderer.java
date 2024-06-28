package components;

import coal.Component;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;

public class SpriteRenderer extends Component{

    private Vector4f colour;
    private Vector2f[] texCoords;
    // (0, 1)
    // (0, 0)
    // (1, 1)
    // (1, 0)
    private Texture texture;

    public SpriteRenderer(Vector4f colour) {
        this.colour = colour;
        this.texture = null;
    }

    public SpriteRenderer(Texture texture) {
        this.texture = texture;
        this.colour = new Vector4f(1, 1, 1, 1);
    }

    @Override
    public void start() {

    }

    @Override
    public void update(Float deltaTime) {

    }

    public Vector4f getColour() {
        return colour;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTexCoords() {
        Vector2f[] texCoords = {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1),
        };
        return texCoords;
    }

}
