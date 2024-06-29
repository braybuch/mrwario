package renderer;

import coal.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static int MAX_LINES = 500;

    private static List<Line2D> lines = new ArrayList<>();

    // 6 floats per vertex, 2 vertices per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

    private static int vaoID, vboID;

    private static boolean started = false;

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
        glLineWidth(4.0f);
    }


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
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

        // Use the shader
        shader.use();
        shader.uploadMatrix4("uProjection", Window.get().getScene().camera().getProjectionMatrix());
        shader.uploadMatrix4("uView", Window.get().getScene().camera().getViewMatrix());

        // Bind the vertex array object to the gpu
        glBindVertexArray(vboID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the batch
        glDrawArrays(GL_LINES, 0, lines.size() * 6 * 2);

        // Free
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();
    }

    public static void addLine2D(Vector2f from, Vector2f to) {
        addLine2D(from, to, new Vector3f(0, 1, 0), 1);
    }
    public static void addLine2D(Vector2f from, Vector2f to, Vector3f colour) {
        addLine2D(from, to, colour, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f colour, int lifetime){
        if (lines.size() >= MAX_LINES) return;
        DebugDraw.lines.add(new Line2D(from, to, colour, lifetime));

    }
}
