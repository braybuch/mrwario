package editor;

import coal.GameObject;
import coal.MouseListener;
import imgui.ImGui;
import renderer.PickingTexture;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {

    /** the game object you've selected in the inspector */
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow (PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
    }

    public void update(float deltaTime, Scene currentScene){
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectID = pickingTexture.readPixel(x, y);
            activeGameObject = currentScene.getGameObject(gameObjectID);
            System.out.println(pickingTexture.readPixel(x, y));
        }
    }

    public void imgui(){
        if (activeGameObject != null) {
            ImGui.begin("Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }
    }
}
