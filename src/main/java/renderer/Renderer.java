package renderer;

import coal.GameObject;
import components.SpriteRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer() {
        batches = new ArrayList<>();
    }

    public void add(GameObject gameObject){
        SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
        if (sprite != null) {
            add(sprite);
        }
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch b : batches){
            if (b.hasRoomForSprites() && b.zIndex() == sprite.gameObject.zIndex()){
                Texture texture = sprite.getTexture();
                if (texture == null || (b.hasTexture(texture) || b.hasRoomForTextures())) {
                    b.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        if (!added){// Create a new render batch
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.zIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            // Make sure zIndices are drawn in correct order
            Collections.sort(batches);
        }
    }

    public void render(){
        for (RenderBatch b : batches){
            b.render();
        }
    }
}
