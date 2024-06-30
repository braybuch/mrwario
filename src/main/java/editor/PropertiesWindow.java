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
    private float debounceTime = 0.2f;

    public PropertiesWindow (PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
    }


    public void update(float deltaTime, Scene currentScene){
        debounceTime -= deltaTime;
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounceTime < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectID = pickingTexture.readPixel(x, y);
            activeGameObject = currentScene.getGameObject(gameObjectID);
            System.out.println(pickingTexture.readPixel(x, y));
            debounceTime = 0.2f;
        }
    }

    public void imgui(){
        if (activeGameObject != null) {
            ImGui.begin("Properties");
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }
}
