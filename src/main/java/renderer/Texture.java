package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * This class represents a texture object
 */
public class Texture {
    /** the filepath to the image */
    private String filepath;
    /** the id of the texture */
    private int textureID;
    /** the size of the texture */
    private int width, height;

    /**
     * Get the width
     *
     * @return the width
     */
    public int getWidth(){
        return width;
    }

    /**
     * Get the height
     *
     * @return the height
     */
    public int getHeight(){
        return height;
    }

    /**
     * Get the ID
     *
     * @return the ID
     */
    public int getID() {
        return textureID;
    }

    /**
     * Initialize the texture
     *
     * @param filepath the relative path from the project root to the image file
     */
    public void init(String filepath){
        this.filepath = filepath;

        // Generate texture on GPU
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        /*
            Set texture parameters
         */

        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // When stretching image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        /*
            Load image
         */

        // Create buffers
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if (image != null) {
            // Store size of image from buffer
            this.width = width.get(0);
            this.height = height.get(0);

            // Check if image has alpha
            if (channels.get(0) == 3) {// RGB
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) { // RGBA
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Unknown image type";
            }
        } else {
            assert false : "Error: (Texture) Could not load image";
        }

        stbi_image_free(image);
    }

    /**
     * Bind a texture to the gpu
     */
    public void bind(){
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    /**
     * Unbind a texture from the gpu
     */
    public void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }

}
