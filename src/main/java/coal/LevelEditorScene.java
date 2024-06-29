package coal;

import components.RigidBody;
import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
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

        if (loadedLevel){
            activeGameObject = gameObjects.get(0);
            return;
        }

        Spritesheet sprites = AssetPool.getSpritesheet("assets/textures/characters.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)), 4);
        SpriteRenderer obj1Sprite = new SpriteRenderer();
        obj1Sprite.setColour(new Vector4f(1, 0, 0, 1));
        obj1.addComponent(obj1Sprite);
        obj1.addComponent(new RigidBody());
        addGameObjectToScene(obj1);
        activeGameObject = obj1;

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(800, 100), new Vector2f(256, 256)), 2);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(AssetPool.getTexture("assets/textures/blendImage2.png"));
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        addGameObjectToScene(obj2);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/textures/characters.png", new Spritesheet(AssetPool.getTexture("assets/textures/characters.png"), 16, 16, 4, 0));
    }

    @Override
    public void update(float deltaTime) {
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
