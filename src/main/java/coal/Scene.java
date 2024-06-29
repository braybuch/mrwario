package coal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected boolean loadedLevel = false;
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

    public void saveExit(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt");
            writer.write(gson.toJson(gameObjects));
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void load(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
                .create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (NoSuchFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!inFile.equals("")) {
            GameObject[] g = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < g.length; i++) {
                addGameObjectToScene(g[i]);
                loadedLevel = true;
            }
        }

    }
}
