package renderer;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line2D {

    private Vector2f from;
    private Vector2f to;
    private Vector3f colour;
    private int lifetime;

    public Line2D(Vector2f from, Vector2f to, Vector3f colour, int lifetime) {
        this.colour = colour;
        this.from = from;
        this.lifetime = lifetime;
        this.to = to;
    }

    public Vector3f getColour() {
        return colour;
    }

    public Vector2f getFrom() {
        return from;
    }

    public Vector2f getTo() {
        return to;
    }

    int beginFrame() {
        // Lifetime is... *sigh*, bound directly to the framerate
        return --lifetime;
    }

}
