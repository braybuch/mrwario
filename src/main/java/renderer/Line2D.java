package renderer;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * This class is a 2d line object
 */
public class Line2D {

    /** The position to start from */
    private Vector2f from;
    /** The position to end */
    private Vector2f to;
    /** The colour to draw */
    private Vector3f colour;
    /** The lifetime to draw for */
    private int lifetime;

    /**
     * Constructor to initialize all fields
     * @param from the position to start from
     * @param to the position to end
     * @param colour the colour to draw
     * @param lifetime the lifetime to draw for
     */
    public Line2D(Vector2f from, Vector2f to, Vector3f colour, int lifetime) {
        this.colour = colour;
        this.from = from;
        this.lifetime = lifetime;
        this.to = to;
    }

    /**
     * Get the colour
     *
     * @return the colour
     */
    public Vector3f getColour() {
        return colour;
    }

    /**
     * Get from
     *
     * @return from
     */
    public Vector2f getFrom() {
        return from;
    }

    /**
     * Get to
     *
     * @return to
     */
    public Vector2f getTo() {
        return to;
    }

    /**
     * Begin frame and update lifetime
     *
     * @return the amount of time left to keep rendering for
     */
    int beginFrame() {
        // Lifetime is... *sigh*, bound directly to the framerate
        return --lifetime;
    }

}
