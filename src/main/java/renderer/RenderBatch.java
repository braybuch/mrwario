package renderer;

import coal.Window;
import components.SpriteRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {
    // Vertex format
    // - - -
    // Position           Colour                            Texture coords      Texture ID
    // float, float,      float, float, float, float,       float, float,       float

    private final int   POS_SIZE = 2,
                        COLOUR_SIZE = 4,
                        TEX_COORDS_SIZE = 2,
                        TEX_ID_SIZE = 1,
                        POS_OFFSET = 0,
                        COLOUR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES,
                        TEX_COORDS_OFFSET = COLOUR_OFFSET + COLOUR_SIZE * Float.BYTES,
                        TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES,
                        VERTEX_SIZE = 9,
                        VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoomForSprites;

    private float[] vertices;

    private int[] texSlots = { 0, 1, 2, 3, 4, 5, 6, 7 };
    private List<Texture> textures;

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;
    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex) {
        this.zIndex = zIndex;

        // Set batch size and init shader and sprites
        shader = AssetPool.getShader("assets/shaders/default.glsl");
        sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // Init vertices (4 vertex quads)
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        // Init companion variables for array
        numSprites = 0;
        hasRoomForSprites = true;
        textures = new ArrayList<>();
    }

    public void start(){
        // Generate and bind a vertex array object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload the indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOUR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOUR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void addSprite(SpriteRenderer sprite){
        // Get index and add sprite
        int index = numSprites;
        sprites[index] = sprite;
        numSprites++;

        // If sprite has a texture, and it's not on the list, add it
        if (sprite.getTexture() != null){
            if (!textures.contains(sprite.getTexture())){
                textures.add(sprite.getTexture());
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (numSprites >= maxBatchSize){
            hasRoomForSprites = false;
        }
    }

    public void render(){
        // Check if sprites need to be rebuffered
        boolean rebufferData = false;
        for (int i = 0; i < numSprites; i++){
            SpriteRenderer sprite = sprites[i];
            if (sprite.isDirty()) {
                loadVertexProperties(i);
                sprite.clean();
                rebufferData = true;
            }
        }

        if (rebufferData){
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Use the shader
        shader.use();
        shader.uploadMatrix4("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMatrix4("uView", Window.getScene().camera().getViewMatrix());

        // Bind textures list to graphics card slots
        for (int i = 0; i < textures.size(); i++){
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw
        glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0);

        // Detach
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++){
            textures.get(i).unbind();
        }
        shader.detach();
    }

    private void loadVertexProperties(int index){
        SpriteRenderer sprite = sprites[index];

        // Find offset within array (4 vertices per sprite
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f colour = sprite.getColour();
        Vector2f[] texCoords = sprite.getTexCoords();

        int texID = 0;
        // [0, tex, tex, tex]
        // Zero index is reserved for painting colour
        if (sprite.getTexture() != null){
            for (int i = 0; i < textures.size(); i++){
                if (textures.get(i) == sprite.getTexture()){
                    texID = i + 1;
                    break;
                }
            }
        }

        // Add vertices with the appropriate properties
        float xAdd = 1.0f, yAdd = 1.0f;
        for (int i = 0; i < 4; i++){
            if (i == 1){
                yAdd = 0.0f;
            } else if (i == 2){
                xAdd = 0.0f;
            } else if (i == 3){
                yAdd = 1.0f;
            }

            // Load position
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            // Load colour
            vertices[offset + 2] = colour.x;
            vertices[offset + 3] = colour.y;
            vertices[offset + 4] = colour.z;
            vertices[offset + 5] = colour.w;

            // Load texture coordinates
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            // Load texture ID
            vertices[offset + 8] = texID;

            // Move to next vertex
            offset += VERTEX_SIZE;
        }


    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offSetArrayIndex = 6 * index;
        int offset = 4 * index;

        // Create triangle 1
        elements[offSetArrayIndex] = offset + 3;
        elements[offSetArrayIndex + 1] = offset + 2;
        elements[offSetArrayIndex + 2] = offset + 0;

        // Create triangle 2
        elements[offSetArrayIndex + 3] = offset + 0;
        elements[offSetArrayIndex + 4] = offset + 2;
        elements[offSetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoomForSprites(){
        return hasRoomForSprites;
    }

    public boolean hasRoomForTextures(){
        final int MAX_GPU_TEXTURE_SLOTS = 8;
        return textures.size() < MAX_GPU_TEXTURE_SLOTS;
    }

    public boolean hasTexture(Texture texture){
        return textures.contains(texture);
    }

    public int zIndex(){
        return zIndex;
    }

    @Override
    public int compareTo(@NotNull RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }
}
