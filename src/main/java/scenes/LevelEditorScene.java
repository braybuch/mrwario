package scenes;

import coal.*;
import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import util.AssetPool;
import util.Settings;

public class LevelEditorScene extends Scene {
    private GameObject obj1;
    private Spritesheet sprites;
    private GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(new Vector2f()), 0);
    private GameObject gameObject;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        sprites = AssetPool.getSpritesheet("assets/textures/sheet.png");
        Spritesheet gizmos = AssetPool.getSpritesheet(Settings.TRANSLATE_TEXTURE);
        camera = new Camera(new Vector2f());
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(camera));
        levelEditorStuff.addComponent((new TranslateGizmo(gizmos.getSprite(0), Window.get().getImguiLayer().getPropertiesWindow())));
        camera.adjustProjection();

    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/textures/sheet.png", new Spritesheet(AssetPool.getTexture("assets/textures/sheet.png"), 16, 16, 70, 0));
        AssetPool.addSpritesheet(Settings.TRANSLATE_TEXTURE, new Spritesheet(AssetPool.getTexture(Settings.TRANSLATE_TEXTURE), 16, 16, 1, 0));

        // Clear extra textures
        for (GameObject g : gameObjects){
            if (g.getComponent(SpriteRenderer.class) != null){
                // Component has a sprite renderer
                SpriteRenderer spriteRenderer = g.getComponent(SpriteRenderer.class);
                if (spriteRenderer.getTexture() != null){
                    // Set sprite's texture to the copy in the asset pool.
                    // Java garbage collection will throw away the old copies
                    spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilepath()));
                }
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        levelEditorStuff.update(deltaTime);
        camera.adjustProjection();

        for (GameObject g : gameObjects) {
            g.update(deltaTime);
        }

    }

    @Override
    public void render() {
        renderer.render();
    }

    @Override
    public void imgui(){
        ImGui.begin("Level Editor Stuff)");
        levelEditorStuff.imgui();
        ImGui.end();

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
