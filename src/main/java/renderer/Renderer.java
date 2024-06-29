package renderer;

import coal.GameObject;
import components.SpriteRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class renders with the assistance of batch renderering
 */
public class Renderer {
    /** Max number to batch */
    private final int MAX_BATCH_SIZE = 1000;
    /** The list of batches to render */
    private final List<RenderBatch> batches;

    /**
     * Constructor initializes default fields
     */
    public Renderer() {
        batches = new ArrayList<>();
    }

    /**
     * Add a game object to the renderer
     *
     * @param gameObject the game object to render
     */
    public void add(GameObject gameObject){
        // If game object has a sprite renderer, add it to the batch
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null) {
            add(spriteRenderer);
        }
    }

    /**
     * Add a sprite to a render batch
     *
     * @param spriteRenderer the spriteRenderer to add
     */
    private void add(SpriteRenderer spriteRenderer) {
        // Check if there's a render batch with room and on the same zIndex as this spriteRenderer
        boolean added = false;
        for (RenderBatch b : batches){
            if (b.hasRoomForSprites() && b.getZIndex() == spriteRenderer.gameObject.getZIndex()){
                // Render batch has room and is on the same zIndex
                Texture texture = spriteRenderer.getTexture();
                if (texture == null || (b.hasTexture(texture) || b.hasRoomForTextures())) {
                    // SpriteRenderer has no texture, or the batch has the texture, or it has room
                    b.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added){
            // Create a new render batch for the spriteRenderer
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, spriteRenderer.gameObject.getZIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(spriteRenderer);
            Collections.sort(batches);// Make sure zIndices are drawn in correct order
        }
    }

    /**
     * Render each batch
     */
    public void render() {
        for (RenderBatch b : batches){
            b.render();
        }
    }
}
