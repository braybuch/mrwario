package renderer;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.*;

/**
 * This class buffers a frame
 */
public class FrameBuffer {
    /** the frame buffer id */
    private int fboID;
    /** the texture */
    private Texture texture = null;

    /**
     * Construct a new frame buffer
     *
     * @param width the width of the buffer
     * @param height the height of the buffer
     */
    public FrameBuffer(int width, int height) {
        // Generate frame buffer
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        // Create the texture to render the data to, and attach it to our frame buffer
        texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getID(), 0);

        // Create render buffer for the depth
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);

        // Attach the render buffer to the frame buffer
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        // Check for success
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false : "Error: Frame buffer is not complete";
        }

        // Unbind the frame buffer and put everything back on the window
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Get the frame buffer object's ID
     *
     * @return the fboID
     */
    public int getFboID() {
        return fboID;
    }

    /**
     * Get the texture
     *
     * @return the texture
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Bind the frame buffer
     */
    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }

    /**
     * Unbind the frame buffer
     */
    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
