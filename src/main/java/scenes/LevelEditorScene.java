package scenes;

import coal.Camera;
import coal.GameObject;
import coal.Prefabs;
import coal.Transform;
import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    private Spritesheet sprites;
    private MouseControls mouseControls = new MouseControls();

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        camera = new Camera(new Vector2f());
        sprites = AssetPool.getSpritesheet("assets/textures/sheet.png");

        if (loadedLevel){
            activeGameObject = gameObjects.get(0);
            return;
        }

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)), 4);
        SpriteRenderer obj1Sprite = new SpriteRenderer();
        obj1Sprite.setColour(new Vector4f(1, 0, 0, 1));
        obj1.addComponent(obj1Sprite);
        obj1.addComponent(new RigidBody());
        addGameObjectToScene(obj1);
        activeGameObject = obj1;

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 2);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = sprites.getSprite(68);
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        addGameObjectToScene(obj2);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/textures/sheet.png", new Spritesheet(AssetPool.getTexture("assets/textures/sheet.png"), 16, 16, 70, 0));
    }

    @Override
    public void update(float deltaTime) {
        mouseControls.update(deltaTime);

        for (GameObject g : gameObjects) {
            g.update(deltaTime);
        }

        renderer.render();
    }

    @Override
    public void imgui(){
        ImGui.begin("Level Editor");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);

        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.width() * 4;
            float spriteHeight = sprite.height() * 4;
            int textureID = sprite.getTextureID();
            Vector2f[] textureCoords = sprite.getTextureCoords();

            ImGui.pushID(i);
            if(ImGui.imageButton(textureID, spriteWidth, spriteHeight, textureCoords[0].x, textureCoords[0].y, textureCoords[2].x, textureCoords[2].y)) {
                // Generate a game object and attach to the mouse cursor
                GameObject object = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                mouseControls.pickupObject(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }
        ImGui.end();
    }
}
