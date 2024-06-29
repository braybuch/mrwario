package renderer;

import coal.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;
import util.JMath;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * This class will draw lines added to it
 */
public class DebugDraw {
    /** The maximum number of lines that can be drawn */
    private static int MAX_LINES = 500;
    /** the list of lines */
    private static List<Line2D> lines = new ArrayList<>();
    /** the array of vertices, where there's 6 floats per vertex and 2 vertices per line */
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    /** The straight line shader */
    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");
    /** The vertex array ID */
    private static int vaoID;
    /** The vertex buffer ID */
    private static int vboID;
    /** Whether this has started yet */
    private static boolean started = false;

    /**
     * Bind to the gpu
     */
    public static void start() {
        // Generate the vertex array object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create the vertex buffer array and allocate some memory
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Enable the vertex array attributes for position
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Enable the vertex array attributes for colour
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Increase line width
        glLineWidth(2.0f);
    }

    /**
     * Check if each line should be rendered this frame
     */
    public static void beginFrame() {
        // Check and see if we need to remove any lines
        if (!started) {
            start();
            started = true;
        }

        // Remove dead-lines
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }

    /**
     * Draw lines
     */
    public static void draw() {
        // Draw the lines
        if (lines.size() <= 0) return;

        int index = 0;
        for (Line2D line : lines) {
            for (int i = 0; i < 2; i++) {
                Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                Vector3f colour = line.getColour();

                // Load position
                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = -10.0f;

                // Load colour
                vertexArray[index + 3] = colour.x;
                vertexArray[index + 4] = colour.y;
                vertexArray[index + 5] = colour.z;

                index += 6;
            }
        }

        // Bind vertices to gpu
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.clone(), GL_DYNAMIC_DRAW);

        // Use the shader
        shader.use();
        shader.uploadMatrix4("uProjection", Window.get().getScene().getCamera().getProjectionMatrix());
        shader.uploadMatrix4("uView", Window.get().getScene().getCamera().getViewMatrix());

        // Bind the vertex array object to the gpu
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the batch
        glDrawArrays(GL_LINES, 0, lines.size());

        // Free
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();
    }

    /**
     * Add a line to draw
     *
     * @param from the position to start
     * @param to the position to end
     */
    public static void addLine2D(Vector2f from, Vector2f to) {
        // Draw in blue by default
        addLine2D(from, to, new Vector3f(0, 1, 0), 1);
    }

    /**
     * Add a line to draw
     *
     * @param from the position to start
     * @param to the position to end
     * @param colour the colour to draw
     */
    public static void addLine2D(Vector2f from, Vector2f to, Vector3f colour) {
        addLine2D(from, to, colour, 1);
    }

    /**
     * Add a line to draw
     *
     * @param from the position to start
     * @param to the position to end
     * @param colour the colour to draw
     * @param lifetime the... in frame-seconds to draw the line
     */
    public static void addLine2D(Vector2f from, Vector2f to, Vector3f colour, int lifetime){
        if (lines.size() >= MAX_LINES) return;
        DebugDraw.lines.add(new Line2D(from, to, colour, lifetime));
    }

    /**
     * Add box to draw
     *
     * @param center the middle
     * @param dimensions the width and height
     * @param rotation the amount of rotation in degrees
     */
    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation) {
        // Draw in blue by default
        addBox2D(center, dimensions, rotation, new Vector3f(0, 1, 0), 200);
    }

    /**
     * Add box to draw
     *
     * @param center the middle
     * @param dimensions the width and height
     * @param rotation the amount of rotation in degrees
     * @param colour the colour
     */
    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f colour) {
        addBox2D(center, dimensions, rotation, colour, 200);
    }

    /**
     * Add box to draw
     *
     * @param center the middle
     * @param dimensions the width and height
     * @param rotation the amount of rotation in degrees
     * @param colour the colour
     * @param lifetime the lifetime in frame-seconds
     */
    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f colour, int lifetime) {
        // Get bottom left corner
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).mul(0.5f));
        // Get top right corner
        Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).mul(0.5f));

        // Get vertices
        Vector2f[] vertices = {
            new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
            new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        // Check if they need to be rotated
        if (rotation != 0.0f){
            for (Vector2f v : vertices){
                JMath.rotate(v, rotation, center);
            }
        }

        // Draw lines of box
        addLine2D(vertices[0], vertices[1], colour, lifetime);
        addLine2D(vertices[0], vertices[3], colour, lifetime);
        addLine2D(vertices[1], vertices[2], colour, lifetime);
        addLine2D(vertices[2], vertices[3], colour, lifetime);
    }

    /**
     * Add circle to draw
     *
     * @param center the middle
     * @param radius the radius
     */
    public static void addCircle2D(Vector2f center, float radius) {
        // Draw in blue by default
        addCircle2D(center, radius, new Vector3f(0, 1, 0), 200);
    }

    /**
     * Add circle to draw
     *
     * @param center the middle
     * @param radius the radius
     * @param colour the colour
     */
    public static void addCircle2D(Vector2f center, float radius, Vector3f colour) {
        addCircle2D(center, radius, colour, 200);
    }

    /**
     * Add circle to draw
     *
     * @param center the middle
     * @param radius the radius
     * @param colour the colour
     * @param lifetime the lifetime in frame-seconds
     */
    public static void addCircle2D(Vector2f center, float radius, Vector3f colour, int lifetime) {
        /*
            Using the power of lines we will draw a circle
         */

        // Get number of segments
        Vector2f[] points = new Vector2f[8];
        int increment = 360 / points.length;
        int currentAngle = 0;

        // For each point
        for (int i = 0; i < points.length; i++) {
            // get Segment
            Vector2f tmp = new Vector2f(radius, 0);
            JMath.rotate(tmp, currentAngle, new Vector2f());

            // Draw line from this point to last point
            if (i > 0){
                addLine2D(points[i - 1], points[i], colour, lifetime);
            }
            // Increment angle for next iteration
            currentAngle += increment;
        }

        // Add final point
        addLine2D(points[points.length - 1], points[0], colour, lifetime);

        // Congratulations you have drawn what some philosophers would call a circle
    }

}
