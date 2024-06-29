package coal;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * This class listens for key presses
 */
public class KeyListener {
    /** the singleton key listener */
    private static KeyListener instance;
    /** the list of keys */
    private final boolean[] keyPressed = new boolean[350];

    /**
     * Get the singleton instance
     *
     * @return the singleton
     */
    private static KeyListener get() {
        // If not yet instanced, instance
        if (instance == null) {
            instance = new KeyListener();
        }
        // then return
        return instance;
    }

    /**
     * Callback method to handle key events.
     * This method is registered with GLFW to handle key presses and releases.
     *
     * @param window The window that received the event.
     * @param key The keyboard key that was pressed or released.
     * @param scanCode The platform-specific scancode of the key.
     * @param action The action (press or release) that occurred.
     * @param mods Bitfield describing which modifier keys were held down.
     */
    public static void keyCallback(long window, int key, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
        } else if (action == GLFW.GLFW_RELEASE) {
            get().keyPressed[key] = false;
        }
    }

    /**
     * Check if a key is being pressed
     *
     * @param keyCode the code for the key
     * @return if the key is being pressed
     */
    public static boolean isKeyPressed(int keyCode){
        return get().keyPressed[keyCode];
    }
}