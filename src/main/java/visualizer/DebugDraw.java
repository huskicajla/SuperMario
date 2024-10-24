package visualizer;

import pixel_pioneer.Camera;
import pixel_pioneer.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import core.assets.AssetPool;
import core.math.JMath;

import java.util.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static int MAX_LINES = 5000;

    private static List<Line2D> lines = new ArrayList<>();

    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("textures/shaders/debugLine2D.glsl");

    private static int vaoID;
    private static int vboID;

    private static boolean started = false;

    public static void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(0.1f);

    }

    public static void beginFrame() {
        if(!started){
            start();
            started = true;
        }

        for(int i = 0; i < lines.size(); i++){
            if(lines.get(i).beginFrame() < 0){
                lines.remove(i);
                i--;
            }
        }
    }

    public static void draw() {
        if(lines.size() <= 0) return;

        int index = 0;
        for(Line2D line : lines){
            for(int i = 0; i < 2; i++){
                Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                //Load position
                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = -10.0f;

                //Load color
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                index += 6;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, lines.size());

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }

    public static void addLine2D(Vector2f from, Vector2f to){
        addLine2D(from, to, new Vector3f(0, 1, 0), 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color){
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime){
        Camera camera = Window.getScene().getCamera();
        Vector2f cameraLeft = new Vector2f(camera.position).add(new Vector2f(-2.0f, -2.0f));
        Vector2f cameraRight = new Vector2f(camera.position)
                .add(new Vector2f(camera.getProjectionSize()).mul(camera.getZoom()))
                .add(new Vector2f(4.0f, 4.0f));
        boolean lineInView = ((from.x >= cameraLeft.x && from.x <= cameraRight.x)
                        && (from.y >= cameraLeft.y && from.y <= cameraRight.y))
                        || ((to.x >= cameraLeft.x && to.x <= cameraRight.x)
                        && (to.y >= cameraLeft.y && to.y <= cameraRight.y));
        if(lines.size() >= MAX_LINES || !lineInView) {
            return;
        }
        DebugDraw.lines.add(new Line2D(from, to, color, lifetime));
    }

    public static void addBox2D(Vector2f center, Vector2f dimension, float rotation){
        addBox2D(center, dimension, rotation, new Vector3f(0, 1, 0), 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimension, float rotation, Vector3f color){
        addBox2D(center, dimension, rotation, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimension, float rotation, Vector3f color, int lifetime){
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimension).mul(0.5f));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimension).mul(0.5f));

        Vector2f[] verticies = {
                new Vector2f(min.x, min.y),   new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y),   new Vector2f(max.x, min.y)
        };

        if(rotation != 0.0f){
            for(Vector2f v : verticies){
                JMath.rotate(v, rotation, center);
            }
        }

        addLine2D(verticies[0], verticies[1], color, lifetime);
        addLine2D(verticies[1], verticies[2], color, lifetime);
        addLine2D(verticies[2], verticies[3], color, lifetime);
        addLine2D(verticies[3], verticies[0], color, lifetime);
    }

    public static void addCircle2D(Vector2f center, float radius){
        addCircle2D(center, radius, new Vector3f(0, 1, 0), 1);
    }

    public static void addCircle2D(Vector2f center, float radius, Vector3f color){
        addCircle2D(center, radius, color, 1);
    }

    public static void addCircle2D(Vector2f center, float radius, Vector3f color, int lifetime){
        Vector2f[] points = new Vector2f[20];
        int increment = 360 / points.length;
        int currentAngle = 0;

        for(int i = 0; i < points.length; i++){
            Vector2f temp = new Vector2f(radius, 0);
            JMath.rotate(temp, currentAngle, new Vector2f());
            points[i] = new Vector2f(temp).add(center);

            if(i > 0){
                addLine2D(points[i-1], points[i], color, lifetime);
            }
            currentAngle += increment;
        }
        addLine2D(points[points.length - 1], points[0], color, lifetime);
    }
}
