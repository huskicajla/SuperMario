package components;

import pixel_pioneer.Camera;
import pixel_pioneer.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import utils.Settings;
import visualizer.DebugDraw;

public class GridLines extends Components {

    @Override
    public void editorUpdate(float dt){
        Camera camera = Window.getScene().getCamera();
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        float firstX = ((int)Math.floor(cameraPos.x / Settings.GRID_WIDTH)) * Settings.GRID_WIDTH;
        float firstY = ((int)Math.floor(cameraPos.y / Settings.GRID_HEIGHT)) * Settings.GRID_HEIGHT;

        int numberOfVerticalLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
        int numberOfHorizontalLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

        float height = (int)(projectionSize.y * camera.getZoom()) + (Settings.GRID_HEIGHT * 5);
        float width = (int)(projectionSize.x  * camera.getZoom()) + (Settings.GRID_WIDTH * 5);

        int maxLines = Math.max(numberOfVerticalLines, numberOfHorizontalLines);
        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);
        for (int i = 0; i < maxLines; i++) {
            float x = firstX + (Settings.GRID_WIDTH * i);
            float y = firstY + (Settings.GRID_HEIGHT * i);

            if(i < numberOfVerticalLines){
                DebugDraw.addLine2D(new Vector2f(x,firstY), new Vector2f(x, firstY + height), color);
            }

            if(i < numberOfHorizontalLines){
                DebugDraw.addLine2D(new Vector2f(firstX,y), new Vector2f(firstX + width, y), color);
            }
        }
    }
}
