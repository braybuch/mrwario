package util;

import org.joml.Vector4f;

public class Settings {
    public static int GRID_WIDTH = 32;
    public static int GRID_HEIGHT = 32;

    /**
     * Colours
     */
    public static Vector4f RED_4F = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
    public static Vector4f BLUE_4F = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
    public static Vector4f NULL_4F = new Vector4f(0, 0, 0, 0);
    /**
     * Assets
     */
    public static String TRANSLATE_TEXTURE = "assets/textures/translate-arrow.png";
}
