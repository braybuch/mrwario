package editor;

import coal.MouseListener;
import coal.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

/**
 * This window contains the game view
 */
public class GameViewWindow {
    private static float leftX;
    private static float rightX;
    private static float topY;
    private static float bottomY;

    public void imgui(){
        // Create game viewport
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        // Normalize mouse to viewport
        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;

        // Get texture id for frame buffer
        int textureID = Window.get().getFrameBuffer().getTexture().getID();
        ImGui.image(textureID, windowSize.x, windowSize.y, 0, 1, 1, 0);

        // Give mouse listener viewport normalized position
        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        // end
        ImGui.end();
    }

    /**
     * Calculate the centered position for the viewport
     *
     * @param aspectSize the aspect ratio of the viewport
     * @return the centered position for the viewport
     */
    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        // Get window size containing the viewport
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        // Make sure not to count region outside scroll
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        // Find centered x and y position
        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());

    }

    /**
     * Calculate the largest size the viewport can occupy in the containing window
     *
     * @return the ImGui vector 2 containing size
     */
    private ImVec2 getLargestSizeForViewport() {
        // Get window size that contains the viewport
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        // Make sure not to count region outside scroll
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        // Check aspect ratio
        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.get().getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // Switch to pillar box mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.get().getTargetAspectRatio();

        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    public boolean getWantCaptureMouse() {
        return (MouseListener.getX() >= leftX && MouseListener.getX() <= rightX && MouseListener.getY() >= bottomY && MouseListener.getY() >= topY);
    }
}
