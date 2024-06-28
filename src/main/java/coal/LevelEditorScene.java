package coal;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        camera = new Camera(new Vector2f());

        Spritesheet sprites = AssetPool.getSpritesheet("assets/textures/characters.png");

        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 1", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        obj2.addComponent(new SpriteRenderer(sprites.getSprite(1)));
        addGameObjectToScene(obj2);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/textures/characters.png",
                new Spritesheet(AssetPool.getTexture("assets/textures/characters.png"),
                        16, 16, 10, 0));
    }

    @Override
    public void update(float deltaTime) {
        for (GameObject g : gameObjects){
            g.update(deltaTime);
        }

        renderer.render();
    }
}
