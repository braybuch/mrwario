package util;

import components.Spritesheet;
import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    /** The map of shaders */
    private static final Map<String, Shader> shaders = new HashMap<>();
    /** The map of textures */
    private static final Map<String, Texture> textures = new HashMap<>();
    /** The map of sprites */
    private static final Map<String, Spritesheet> spritesheets = new HashMap<>();

    /**
     * Return the shader or create and return the new reference
     *
     * @param resource full filepath to the resource
     * @return the shader
     */
    public static Shader getShader(String resource) {
        File file = new File(resource);
        if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            return AssetPool.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(resource);
            shader.compileAndLink();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    /**
     * Returns the texture or create and return the new reference
     *
     * @param resource full filepath to the resource
     * @return the texture
     */
    public static Texture getTexture(String resource){
        File file = new File(resource);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(resource);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    /**
     * Add a spritesheet to the resource pool
     *
     * @param resource full filepath to the resource
     * @param spritesheet the spritesheet
     */
    public static void addSpritesheet(String resource, Spritesheet spritesheet){
        File file = new File(resource);
        if(!AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {// Don't have this spritesheet yet
            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    /**
     * Get the spritesheet
     *
     * @param resource the full filepath to the resource
     * @return the spritesheet found, or null
     */
    public static Spritesheet getSpritesheet(String resource){
        File file = new File(resource);
        assert AssetPool.spritesheets.containsKey(file.getAbsolutePath()) : "Error, tried to acccess spritesheet not been added to the asset pool";
        return AssetPool.spritesheets.getOrDefault(file.getAbsolutePath(), null);
    }
}
