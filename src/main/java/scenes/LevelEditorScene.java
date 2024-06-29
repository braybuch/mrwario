package scenes;

import coal.Camera;
import coal.GameObject;
import coal.Prefabs;
import coal.Transform;
import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import util.AssetPool;

public class LevelEditorScene extends Scene {
    private GameObject obj1;
    private Spritesheet sprites;
    private GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(new Vector2f()), 0);

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        loadResources();
        camera = new Camera(new Vector2f());
        camera.adjustProjection();
        SpriteRenderer obj1Sprite;
        sprites = AssetPool.getSpritesheet("assets/textures/sheet.png");

        if (loadedLevel){
            activeGameObject = gameObjects.get(0);
            return;
        }

    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/textures/sheet.png", new Spritesheet(AssetPool.getTexture("assets/textures/sheet.png"), 16, 16, 70, 0));
    }

    @Override
    public void update(float deltaTime) {
        levelEditorStuff.update(deltaTime);

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
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            int textureID = sprite.getTextureID();
            Vector2f[] textureCoords = sprite.getTextureCoords();

            ImGui.pushID(i);
            if(ImGui.imageButton(textureID, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
                // Generate a game object and attach to the mouse cursor
                GameObject object = Prefabs.generateSpriteObject(sprite, 32, 32);
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
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
