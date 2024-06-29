package coal;

import org.lwjgl.Version;
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

public class Window {
    private int width, height;
    private final String title;
    private long windowPointer;
    private ImGuiLayer imguiLayer;
    public float r, g, b, a;
    private final boolean fadeToBlack = false;

    private static Window window = null;

    private static Scene currentScene = null;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Mwrario";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static void changeScene(int newScene){
        switch(newScene){
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

    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();
        }

        return Window.window;
    }

    public void run(){
        System.out.println("Hello LWJGL v" + Version.getVersion());
        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(windowPointer);
        glfwDestroyWindow(windowPointer);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    private void init(){
        // Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // Create the window
        windowPointer = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (windowPointer == NULL){
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

        Window.changeScene(0);
    }


    private void loop(){

        // Init timing variables
        float startTime = (float)glfwGetTime();
        float endTime;
        float deltaTime = -1.0f;



        while(!glfwWindowShouldClose(windowPointer)){// Until close window event
            // Poll events
            glfwPollEvents();

            DebugDraw.beginFrame();

            // Paint window white
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            // Update scene
            if (deltaTime >= 0.0f){
                DebugDraw.draw();
                currentScene.update(deltaTime);
            }

            imguiLayer.update(deltaTime, currentScene);

            glfwSwapBuffers(windowPointer);

            // Establish delta time
            endTime = (float)glfwGetTime();
            deltaTime = endTime - startTime;
            startTime = endTime;
        }

        currentScene.saveExit();
    }

    public Scene getScene(){
        return currentScene;
    }

    public int height(){
        return get().height;
    }

    public int width(){
        return get().width;
    }

    public void setHeight(int height){
        Window.get().height = height;
    }

    public void setWidth(int width){
        Window.get().width = width;
    }
}
