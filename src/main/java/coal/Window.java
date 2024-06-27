package coal;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private final int width, height;
    private final String title;
    private long windowPointer;
    private static Window window = null;
    private float r, g, b, a;
    private boolean fadeToBlack = false;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Mwrario";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();
        }

        return Window.window;
    }

    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion());
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
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

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

        // Make OpenGL context current
        glfwMakeContextCurrent(windowPointer);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowPointer);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
    }


    private void loop(){
        while(!glfwWindowShouldClose(windowPointer)){
            // Poll events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
                fadeToBlack = true;
            }

            if (fadeToBlack){
                r = Math.max(r - 0.01f, 0);
                g = Math.max(g - 0.01f, 0);
                b = Math.max(b - 0.01f, 0);
                a = Math.max(a - 0.01f, 0);
            }

            glfwSwapBuffers(windowPointer);
        }
    }



}
