package components;

import coal.Component;
import org.joml.Vector4f;

public class SpriteRenderer extends Component{

    private Vector4f colour;

    public SpriteRenderer(Vector4f colour) {
        this.colour = colour;
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
}
