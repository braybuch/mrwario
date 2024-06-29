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

/**
 * This class renders by batch for improved frame rates
 */
public class RenderBatch implements Comparable<RenderBatch> {
    // Vertex format
    // - - -
    // Position           Colour                            Texture coords      Texture ID
    // float, float,      float, float, float, float,       float, float,       float

    /** Size of the position attribute (2 floats) */
    private final int POS_SIZE = 2;
    /** Size of the colour attribute (4 floats) */
    private final int COLOUR_SIZE = 4;
    /** Size of the texture coordinates attribute (2 floats) */
    private final int TEX_COORDS_SIZE = 2;
    /** Size of the texture ID attribute (1 float) */
    private final int TEX_ID_SIZE = 1;
    /** Offset of the position attribute (0 bytes) */
    private final int POS_OFFSET = 0;
    /** Offset of the colour attribute */
    private final int COLOUR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    /** Offset of the texture coordinates attribute */
    private final int TEX_COORDS_OFFSET = COLOUR_OFFSET + COLOUR_SIZE * Float.BYTES;
    /** Offset of the texture ID attribute */
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    /** Total size of a vertex (9 floats) */
    private final int VERTEX_SIZE = 9;
    /** Total size of a vertex in bytes */
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
    /** The default shader to use */
    private final String DEFAULT_SHADER = "assets/shaders/default.glsl";
    /** Array to store sprites in the batch */
    private final SpriteRenderer[] spriteRenderers;
    /** Number of sprites currently in the batch */
    private int numSprites;
    /** Flag indicating if there is room for more sprites in the batch */
    private boolean hasRoomForSprites;
    /** Array to store vertex data */
    private final float[] vertices;
    /** Texture slots for the batch */
    private final int[] texSlots = { 0, 1, 2, 3, 4, 5, 6, 7 };
    /** List to store textures used in the batch */
    private final List<Texture> textures;
    /** Vertex Array Object ID */
    private int vaoID;
    /** Vertex Buffer Object ID */
    private int vboID;
    /** Maximum number of sprites in the batch */
    private final int maxBatchSize;
    /** Shader used for rendering */
    private final Shader shader;
    /** z-index for sorting render batches */
    private final int zIndex;

    /**
     * Constructor declaring batch size and z-index for the batch
     *
     * @param maxBatchSize the max size of the batch
     * @param zIndex the layer order to render the batch
     */
    public RenderBatch(int maxBatchSize, int zIndex) {
        // Set batch size, zIndex, and init shader and sprites
        this.maxBatchSize = maxBatchSize;
        this.zIndex = zIndex;
        shader = AssetPool.getShader(DEFAULT_SHADER);
        spriteRenderers = new SpriteRenderer[this.maxBatchSize];

        // Init vertices (4 vertex quads)
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        // Init companion variables for array
        numSprites = 0;
        hasRoomForSprites = true;
        textures = new ArrayList<>();
    }


    /**
     * Get if there's room for more sprites
     *
     * @return true if there's room for more sprites, or false
     */
    public boolean hasRoomForSprites(){
        return hasRoomForSprites;
    }

    /**
     * Get if there's room for more textures
     *
     * @return true if there's room for more textures, or false
     */
    public boolean hasRoomForTextures(){
        final int MAX_GPU_TEXTURE_SLOTS = 8;
        return textures.size() < MAX_GPU_TEXTURE_SLOTS;
    }

    /**
     * Get if the list already contains a given texture
     *
     * @param texture the texture to check
     * @return true if the list contains the texture, or false
     */
    public boolean hasTexture(Texture texture){
        return textures.contains(texture);
    }

    /**
     * Get the zIndex of the batch
     *
     * @return the zIndex of the batch
     */
    public int getZIndex(){
        return zIndex;
    }

    @Override
    public int compareTo(@NotNull RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }

    public void start(){
        // Generate and bind a vertex array object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

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

    /**
     * Add a spriteRenderer to the batch
     *
     * @param spriteRenderer the SpriteRenderer to add
     */
    public void addSprite(SpriteRenderer spriteRenderer){
        // Get index and add sprite
        int index = numSprites;
        spriteRenderers[index] = spriteRenderer;
        numSprites++;

        // If sprite has a texture, and it's not on the list, add it
        if (spriteRenderer.getTexture() != null){
            if (!textures.contains(spriteRenderer.getTexture())){
                textures.add(spriteRenderer.getTexture());
            }
        }

        // Add spriteRenderer's properties to local vertices array
        loadVertexProperties(index);

        // Check if there's room for more sprites
        if (numSprites >= maxBatchSize){
            hasRoomForSprites = false;
        }
    }

    /**
     * Draw the batch
     */
    public void render(){
        // Check if sprites need to be re-buffered
        boolean rebufferData = false;
        for (int i = 0; i < numSprites; i++){
            SpriteRenderer spriteRenderer = spriteRenderers[i];
            if (spriteRenderer.getIsDirty()) {
                loadVertexProperties(i);
                spriteRenderer.setIsDirty(false);
                rebufferData = true;
            }
        }
        if (rebufferData){
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Use the shader
        shader.use();
        shader.uploadMatrix4("uProjection", Window.get().getScene().getCamera().getProjectionMatrix());
        shader.uploadMatrix4("uView", Window.get().getScene().getCamera().getViewMatrix());

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

    /**
     * Load properties of given spriteRenderer into vertex array
     *
     * @param spriteRendererIndex the index of the spriteRenderer you want to pair to the vertex array
     */
    private void loadVertexProperties(int spriteRendererIndex){
        SpriteRenderer spriteRenderer = spriteRenderers[spriteRendererIndex];

        // Find offset within array (4 vertices per sprite
        int offset = spriteRendererIndex * 4 * VERTEX_SIZE;

        // Set colour and texture coordinates
        Vector4f colour = spriteRenderer.getColour();
        Vector2f[] texCoords = spriteRenderer.getTexCoords();

        // Set texture ID
        int texID = 0;
        // [0, tex, tex, tex]
        // Zero index is reserved for painting colour
        if (spriteRenderer.getTexture() != null){
            for (int i = 0; i < textures.size(); i++){
                if (textures.get(i).equals(spriteRenderer.getTexture())){
                    texID = i + 1;// Preserve zero index for painting colours
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
            vertices[offset] = spriteRenderer.gameObject.transform.position.x + (xAdd * spriteRenderer.gameObject.transform.scale.x);
            vertices[offset + 1] = spriteRenderer.gameObject.transform.position.y + (yAdd * spriteRenderer.gameObject.transform.scale.y);

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

    /**
     * Generates the indices for rendering quads.
     * Each quad is made up of 2 triangles, requiring 6 indices.
     *
     * @return An array of indices for the quads.
     */
    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        // Populate the elements array with indices for each quad
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    /**
     * Loads the element indices for a specific quad into the elements array.
     *
     * @param elements The array to load the indices into.
     * @param index The index of the quad.
     */
    private void loadElementIndices(int[] elements, int index) {
        int offSetArrayIndex = 6 * index;
        int offset = 4 * index;

        // Create triangle 1
        elements[offSetArrayIndex] = offset + 3;
        elements[offSetArrayIndex + 1] = offset + 2;
        elements[offSetArrayIndex + 2] = offset;

        // Create triangle 2
        elements[offSetArrayIndex + 3] = offset;
        elements[offSetArrayIndex + 4] = offset + 2;
        elements[offSetArrayIndex + 5] = offset + 1;
    }

}
