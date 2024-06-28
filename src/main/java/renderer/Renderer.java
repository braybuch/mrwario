package renderer;

import coal.GameObject;
import components.SpriteRenderer;

import java.util.ArrayList;
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
            if (b.hasRoom()){
                b.addSprite(sprite);
                added = true;
                break;
            }
        }

        if (!added){// Create a new render batch
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
        }
    }

    public void render(){
        for (RenderBatch b : batches){
            b.render();
        }
    }
}
