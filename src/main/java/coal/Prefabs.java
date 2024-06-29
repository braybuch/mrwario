package coal;

import components.Sprite;
import components.SpriteRenderer;
import org.joml.Vector2f;

/**
 * This class represents an object that can be instanced on the fly containing a game object and a renderer
 */
public class Prefabs {

    /**
     * Construct the new prefab and initialize fields
     *
     * @param sprite the sprite to use for the prefab
     * @param sizeX the width
     * @param sizeY the height
     * @return this newly instanced prefab
     */
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY){
        GameObject block = new GameObject("Sprite_Object_Gen", new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)), 0);
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }
}
