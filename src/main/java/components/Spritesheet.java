package components;

import org.joml.Vector2f;
import renderer.Texture;

import java.util.ArrayList;
import java.util.List;

public class Spritesheet {

    private Texture texture;
    private List<Sprite> sprites;

    public Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing){
        sprites = new ArrayList<Sprite>();
        this.texture = texture;

        // Get bottom left corner of top left sprite
        int     currentX = 0,
                currentY = texture.height() - spriteHeight;
        for (int i = 0; i < numSprites; i++){
            // Get normalized sprite coordinates
            float   topY    = (currentY + spriteHeight) / (float)texture.height(),
                    rightX  = (currentX + spriteWidth) / (float)texture.width(),
                    leftX   = currentX / (float)texture.width(),
                    bottomY = currentY / (float)texture.height();

            // Create texture coordinates
            Vector2f[] textureCoords = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY),
            };

            // Create new sprite using the given texture atlas and the calculated coords
            Sprite sprite = new Sprite(this.texture, textureCoords);
            sprites.add(sprite);

            // Increment x
            currentX += spriteWidth + spacing;
            if (currentX >= texture.width()){// End of the line
                // Increment y down to next texture
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    public Sprite getSprite(int index){
        return sprites.get(index);
    }
}
