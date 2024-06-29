package coal;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import renderer.DebugDraw;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * This class provides singleton access to the window object, and thus, the gpu.
 */
public class Window {
    /**
     * size of the window
     */
    private int width, height;
    /**
     * name of the window
     */
    private final String title;
    /**
     * the variable holding the C style pointer to the window in memory
     */
    private long windowPointer;
    /**
     * the interface with the gui
     */
    private ImGuiLayer imguiLayer;
    /**
     * colour value
     */
    public float r, g, b, a;
    /**
     * window singleton
     */
    private static Window window = null;
    /**
     * currently running scene
     */
    private static Scene currentScene = null;


    /**
     * Default constructor sets up window with standard settings.
     */
    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Mwrario";
        // Colour normalized 0-1
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    /**
     * This method returns a singleton of the class.
     *
     * @return the private window instance
     */
    public static Window get() {
        // If singleton does not yet exist, make a new one
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    /**
     * Get the width of the window.
     *
     * @return the width of the window
     */
    public int getWidth() {
        return Window.get().width;
    }

    /**
     * Set the width of the window.
     *
     * @param width the new width of the window
     */
    public void setWidth(int width) {
        Window.get().width = width;
    }

    /**
     * Get the height of the window.
     *
     * @return the height of the window
     */
    public int getHeight() {
        return Window.get().height;
    }

    /**
     * Set the height of the window.
     *
     * @param height the new height of the window
     */
    public void setHeight(int height) {
        Window.get().height = height;
    }

    /**
     * Get the current scene.
     *
     * @return the current scene
     */
    public Scene getScene() {
        return currentScene;
    }

    /**
     * Load passed scene.
     *
     * @param newScene index of the new scene
     */
    public static void setScene(int newScene) {
        // TODO Pass the whole scene, not just an index
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene ";
                break;
        }

        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    /**
     * This method acts as the public interface to begin running this window
     */
    public void run() {
        // Initialize and loop
        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(windowPointer);
        glfwDestroyWindow(windowPointer);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    /**
     * This method configures the window and loads it
     */
    private void init() {
        // Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // Create the window
        windowPointer = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (windowPointer == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        // Create callbacks
        glfwSetCursorPosCallback(windowPointer, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(windowPointer, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(windowPointer, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(windowPointer, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(windowPointer, (w, newWidth, newHeight) -> {
            Window.get().setWidth(newWidth);
            Window.get().setHeight(newHeight);
        });

        // Make OpenGL context current
        glfwMakeContextCurrent(windowPointer);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowPointer);

        // " This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use. "
        //
        // source: https://www.lwjgl.org/guide
        GL.createCapabilities();

        // Setup alpha channel
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // Assign imgui layer
        imguiLayer = new ImGuiLayer(windowPointer);
        imguiLayer.initImGui();

        Window.setScene(0);
    }

    /**
     * This method provides an update loop with non-framerate dependant time
     */
    private void loop() {

        // Init timing variables
        float startTime = (float) glfwGetTime();
        float endTime;
        float deltaTime = -1.0f;

        // Do until close window event
        while (!glfwWindowShouldClose(windowPointer)) {
            // Poll events
            glfwPollEvents();

            // Draw debug line
            DebugDraw.beginFrame();

            // Paint window white
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            // Update scene
            if (deltaTime >= 0.0f) {
                DebugDraw.draw();
                currentScene.update(deltaTime);
            }

            // Update gui
            imguiLayer.update(deltaTime, currentScene);

            // Swap shown frame buffer with last rendered buffer
            glfwSwapBuffers(windowPointer);

            // Establish delta time
            endTime = (float) glfwGetTime();
            deltaTime = endTime - startTime;
            startTime = endTime;
        }

        // Serialize objects
        currentScene.saveExit();
    }
}
