package coal;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * This class represents a camera object
 */
public class Camera {
    /** The matrix that defines the camera's projection transformation */
    private Matrix4f projectionMatrix;
    /** The matrix that defines the camera's view transformation */
    private Matrix4f viewMatrix;
    /** The inverse of the projection matrix, used for transforming coordinates from screen space back to world space */
    private Matrix4f inverseProjectionMatrix;
    /** The inverse of the view matrix, used for transforming coordinates from screen space back to world space */
    private Matrix4f inverseViewMatrix;
    /** the 2D position of the camera in world space */
    private Vector2f position;
    /** the size of the projection */
    private Vector2f projectionSize = new Vector2f(32.0f * 40.0f, 32.0f * 21.0f);

    private float zoom = 1.0f;

    /**
     * Constructor initializes to the given position then handles the rest
     *
     * @param position the 2d location to place the camera
     */
    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjectionMatrix = new Matrix4f();
        this.inverseViewMatrix = new Matrix4f();
        adjustProjection();
    }

    /**
     * Calculates and returns the view matrix for the camera.
     * The view matrix defines the transformation from world coordinates to camera coordinates.
     *
     * @return The view matrix.
     */
    public Matrix4f getViewMatrix() {
        // Define direction the camera is facing
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        // Define direction that is up from the camera
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        // Reset the view matrix to the identity matrix
        viewMatrix.identity();
        // Compute the view matrix
        viewMatrix = viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f), cameraFront.add(position.x, position.y, 0.0f), cameraUp);
        // Store inverse matrix
        viewMatrix.invert(inverseViewMatrix);
        // Return newly calculated viewMatrix
        return viewMatrix;
    }

    /**
     * Returns the projection matrix for the camera.
     * The projection matrix defines the transformation from camera coordinates to screen coordinates.
     *
     * @return The projection matrix.
     */
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    /**
     * Returns the inverse of the projection matrix.
     * The inverse projection matrix is used for transforming coordinates from screen space back to camera coordinates.
     *
     * @return The inverse projection matrix.
     */
    public Matrix4f getInverseProjectionMatrix() {
        return this.inverseProjectionMatrix;
    }

    /**
     * Returns the inverse of the view matrix.
     * The inverse view matrix is used for transforming coordinates from camera space back to world coordinates.
     *
     * @return The inverse view matrix.
     */
    public Matrix4f getInverseViewMatrix() {
        return this.inverseViewMatrix;
    }

    /**
     * Get the 2d position of the camera
     *
     * @return the position
     */
    public Vector2f getPosition() {
        return position;
    }

    /**
     * Get the projection size
     *
     * @return the projection size
     */
    public Vector2f getProjectionSize() {
        return projectionSize;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void adjustZoom(float value){
        zoom += value;
    }

    /**
     * Move projection
     */
    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, projectionSize.x * zoom, 0.0f, projectionSize.y * zoom, 0.0f, 100.0f);
        projectionMatrix.invert(inverseProjectionMatrix);
    }



}
