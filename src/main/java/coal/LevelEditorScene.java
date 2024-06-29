package coal;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import org.joml.Vector2f;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    private Spritesheet sprites;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        camera = new Camera(new Vector2f());

        Spritesheet sprites = AssetPool.getSpritesheet("assets/textures/characters.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)), 4);
        obj1.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/textures/blendImage2.png"))));
        addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 2);
        obj2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/textures/blendImage1.png"))));
        addGameObjectToScene(obj2);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/textures/characters.png", new Spritesheet(AssetPool.getTexture("assets/textures/characters.png"), 16, 16, 4, 0));
    }

    @Override
    public void update(float deltaTime) {
        obj1.transform.position.x += 10 * deltaTime;
        for (GameObject g : gameObjects) {
            g.update(deltaTime);
        }

        renderer.render();
    }

    @Override
    public void imgui(){
        ImGui.begin("Level Editor");
        ImGui.text("Inspecting ");
        ImGui.end();
    }
}
