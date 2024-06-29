package scenes;

import coal.Camera;
import coal.GameObject;
import coal.GameObjectTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentTypeAdapter;
import imgui.ImGui;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a scene object.
 */
public abstract class Scene {
    /** the renderer to use for the scene */
    protected Renderer renderer = new Renderer();
    /** the camera to use for the scene */
    protected Camera camera;
    /** represents if the application has begun updating yet */
    private boolean isRunning = false;
    /** the list of game objects in the scene */
    protected List<GameObject> gameObjects = new ArrayList<>();
    /** if the level has been loaded yet */
    protected boolean loadedLevel = false;
    /** the game object you've selected in the inspector */
    protected GameObject activeGameObject = null;

    /**
     * Optional default constructor.
     */
    public Scene() {}

    /**
     * Get the camera.
     *
     * @return the camera
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Optional method.
     */
    public void init() {}

    /**
     * Begin rendering and running the scene.
     */
    public void start() {
        for (GameObject g : gameObjects) {
            g.start();
            renderer.add(g);
        }
        isRunning = true;
    }

    /**
     * Put new object in the scene.
     *
     * @param gameObject the new object to be added
     */
    public void addGameObjectToScene(GameObject gameObject) {
        if (!isRunning) {
            gameObjects.add(gameObject);
        } else {
            gameObjects.add(gameObject);
            gameObject.start();
            renderer.add(gameObject);
        }
    }

    /**
     * To be called every frame
     *
     * @param deltaTime time elapsed since last frame
     */
    public abstract void update(float deltaTime);

    /**
     *
     */
    // TODO what
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

    /**
     * Serialize objects
     */
    public void saveExit(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
                .create();

        try {
            final String JUNK_FILE = "junk-level.txt";
            final String FILE = "level.txt";
            FileWriter writer = new FileWriter(FILE);
            writer.write(gson.toJson(gameObjects));
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Deserialize objects
     */
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
            int maxObjectID = -1;
            int maxComponentID = -1;
            GameObject[] g = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < g.length; i++) {
                addGameObjectToScene(g[i]);

                // Find max IDs
                for (Component c : g[i].getComponents()){
                    if (c.getUid() > maxComponentID){
                        maxComponentID = c.getUid();
                    }
                }
                if (g[i].getUid() > maxObjectID){
                    maxObjectID = g[i].getUid();
                }

            }
            //Init objects and components with one higher than current maxID
            GameObject.init(++maxObjectID);
            Component.init(++maxComponentID);
            loadedLevel = true;
        }

    }
}
