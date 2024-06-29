package coal;

import imgui.ImGui;
import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    /**
     * The game object you're inspecting in a window
     */
    protected GameObject activeGameObject = null;

    public Scene() {}

    public void init() {}

    public void start() {
        for (GameObject g : gameObjects) {
            g.start();
            renderer.add(g);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject g) {
        if (!isRunning) {
            gameObjects.add(g);
        } else {
            gameObjects.add(g);
            g.start();
            renderer.add(g);
        }
    }

    public abstract void update(float deltaTime);

    public Camera camera() {
        return camera;
    }

    public void sceneImgui(){
        if (activeGameObject != null) {
            ImGui.begin("Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }

        imgui();
    }

    public void imgui(){

    }
}
