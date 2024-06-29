package renderer;

import coal.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;

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
}
