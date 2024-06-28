package util;

import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();

    /**
     * Return the shader or create and return the new reference
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
     * @param resource full filepath to the resource
     * @return the texture
     */
    public static Texture getTexture(String resource){
        File file = new File(resource);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture(resource);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }
}
