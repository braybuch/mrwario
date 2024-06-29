package components;

import org.joml.Vector2f;
import renderer.Texture;

import java.util.ArrayList;
import java.util.List;

/**
 * This component provides a texture atlas/spritesheet
 */
public class Spritesheet extends Component {
    /** the list of sprites */
    private final List<Sprite> sprites;

    /**
     * Construct spritesheet with all fields
     *
     * @param texture the texture to use as an atlas
     * @param spriteWidth the width of a single sprite
     * @param spriteHeight the height of a single sprite
     * @param numSprites the number of sprites in the atlas
     * @param spacing the padding between sprites
     */
    public Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing){
        sprites = new ArrayList<Sprite>();

        // Get bottom left corner of top left sprite
        int     currentX = 0,
                currentY = texture.getHeight() - spriteHeight;

        // For every sprite
        for (int i = 0; i < numSprites; i++){
            // Get normalized sprite coordinates
            float   topY    = (currentY + spriteHeight) / (float)texture.getHeight(),
                    rightX  = (currentX + spriteWidth) / (float)texture.getWidth(),
                    leftX   = currentX / (float)texture.getWidth(),
                    bottomY = currentY / (float)texture.getHeight();

            // Create texture coordinates
            Vector2f[] textureCoords = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY),
            };

            // Create new sprite using the given texture atlas and the calculated coords and add to list
            Sprite sprite = new Sprite();
            sprite.setTexture(texture);
            sprite.setTextureCoords(textureCoords);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);
            sprites.add(sprite);

            // Increment x
            currentX += spriteWidth + spacing;
            if (currentX >= texture.getWidth()){// End of the line
                // Increment y down to next texture
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    /**
     * Get the sprite at the specified index
     *
     * @param index the position of the sprite in the atlas
     * @return the sprite fund at the atlas
     */
    public Sprite getSprite(int index){
        return sprites.get(index);
    }

    /**
     * Get the size of the spritesheet
     *
     * @return the size of the spritesheet
     */
    public int size() {
        return sprites.size();
    }
}
