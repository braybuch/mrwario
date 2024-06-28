package coal;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        camera = new Camera(new Vector2f());

        int xOffset = 10, yOffset = 10;
        float totalWidth = (float)(600.0f - xOffset * 2.0f);
        float totalHeight = (float)(300.0f - yOffset * 2.0f);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;

        for (int x = 0; x < 100; x++){
            for (int y = 0; y < 100; y++){
                float xPos = xOffset + (x * sizeX);
                float yPos = yOffset + (y * sizeY);

                GameObject g = new GameObject("obj" + x + "" + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                g.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
                addGameObjectToScene(g);
            }
        }

        loadResources();
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

    }

    @Override
    public void update(float deltaTime) {
        for (GameObject g : gameObjects){
            g.update(deltaTime);
        }

        renderer.render();
    }
}
