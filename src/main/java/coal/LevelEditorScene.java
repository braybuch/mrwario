package coal;

import components.FontRenderer;
import components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL20.*;

public class LevelEditorScene extends Scene {

    private String vertexShaderSource = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColour;\n" +
            "\n" +
            "out vec4 fColour;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColour = aColour;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSource = "#version 330 core\n" +
            "\n" +
            "in vec4 fColour;\n" +
            "\n" +
            "out vec4 colour;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    colour = fColour;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertices = {
            // position             // Colour                   // UV Coordinates
             100f,   0f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f,      1, 1, // Bottom right
               0f, 100f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,      0, 0, // Top left
             100f, 100f, 0.0f,     0.0f, 0.0f, 1.0f, 1.0f,      1, 0, // Top right
               0f,   0f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f,      0, 1  // Bottom left
    };

    // Counterclockwise from the bottom right
    private int[] elements = {
        2, 1, 0, // Top right triangle
        0, 1, 3 // Bottom left triangle
    };

    private int vaoID, vboID, eboID;

    private Shader defaultShader;

    private Texture testTexture;

    GameObject testObj;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        System.out.println("Creating test object");
        this.testObj = new GameObject("Test Object");
        this.testObj.addComponent(new SpriteRenderer());
        this.testObj.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testObj);

        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compileAndLink();
        this.testTexture = new Texture("assets/textures/mario-like.png");

        /*
            Generate VAO, VBO, EBO buffer objects and send to GPU
         */

        // Bind vertex array
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        // Create VBO and upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elements.length);
        elementBuffer.put(elements).flip();

        // Create EBO and upload the element buffer
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colourSize = 4;
        int uvSize = 2;
        int vertexSizeInBytes = (positionsSize + colourSize + uvSize) * Float.BYTES;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeInBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colourSize, GL_FLOAT, false, vertexSizeInBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeInBytes, (positionsSize + colourSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float deltaTime) {
        camera.position.x -= deltaTime * 50.0f;
        camera.position.y -= deltaTime * 35.0f;

        defaultShader.use();

        // Upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMatrix4("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMatrix4("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        // Bind the VAO
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw elements
        glDrawElements(GL_TRIANGLES, elements.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        defaultShader.detach();

        for (GameObject g : gameObjects){
            g.update(deltaTime);
        }
    }
}
