package coal;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class KeyListener {
    private static KeyListener instance;
    private boolean keyPressed[] = new boolean[350];

    private KeyListener() {

    }

    private static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }
        return instance;
    }

    public static void keyCallback(long window, int key, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
        } else if (action == GLFW.GLFW_RELEASE) {
            get().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode){
        return get().keyPressed[keyCode];
    }
}