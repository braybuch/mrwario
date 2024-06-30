package components;

import coal.Camera;
import coal.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.Settings;

/**
 * This component gives a game object grid lines
 */
public class GridLines extends Component {
    @Override
    public void update(float deltaTime){
        // Get position and projection of camera
        Camera camera = Window.get().getScene().getCamera();
        Vector2f cameraPos = camera.getPosition();
        Vector2f projectionSize = camera.getProjectionSize();

        // Snap to corners
        float firstX = ((int)(cameraPos.x / Settings.GRID_WIDTH) - 1) * Settings.GRID_HEIGHT;
        float firstY = ((int)(cameraPos.y / Settings.GRID_HEIGHT) - 1) * Settings.GRID_HEIGHT;

        // Check how many vertical lines fit
        final int MAGIC_NUMBER_OF_LINES = 64;
        int numVerticalLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + MAGIC_NUMBER_OF_LINES;

        // Check how many horizontal lines fit
        int numHorizontalLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + MAGIC_NUMBER_OF_LINES;

        // Get integer value of projection size
        int width = (int)(projectionSize.x * camera.getZoom()) + Settings.GRID_WIDTH * 2;
        int height =(int)(projectionSize.y * camera.getZoom()) + Settings.GRID_HEIGHT * 2;

        // Draw lines
        int maxLines = Math.max(numVerticalLines, numHorizontalLines);
        Vector3f colour = new Vector3f(.2f, .2f, .2f);
        for (int i = 0; i < maxLines; i++) {
            // Calculate where x and y will be for each line
            float x = firstX + (Settings.GRID_WIDTH * i);
            float y = firstY + (Settings.GRID_HEIGHT * i);

            // Add lines to debug draw pass
            if (i < numVerticalLines) {
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), colour);
            }
            if (i < numHorizontalLines) {
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), colour);
            }
        }
    }
}
