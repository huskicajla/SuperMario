package pixel_pioneer;

import pixel_pioneer.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX, worldX, worldY, lastWorldX, lastWorldY;
    private boolean mouseButtonPressed[] = new boolean[9];
    private boolean isDragging;

    private int mouseButtonDown = 0;

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();


    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static void endFrame() {
        getInstance().scrollX = 0.0;
        getInstance().scrollY = 0.0;
    }

    public static void clear() {
        getInstance().scrollX = 0.0;
        getInstance().scrollY = 0.0;
        getInstance().xPos = 0.0;
        getInstance().yPos = 0.0;
        getInstance().lastX = 0.0;
        getInstance().lastY = 0.0;
        getInstance().lastWorldX = 0.0;
        getInstance().lastWorldY = 0.0;
        getInstance().mouseButtonDown = 0;
        getInstance().isDragging = false;
        Arrays.fill(getInstance().mouseButtonPressed, false);
    }

    public static MouseListener getInstance() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    public static void mousePositionCallback(long window, double xpos, double ypos) {
        if(!Window.getImGuiLayer().getGameViewWindow().getWantCaptureMouse()){
            clear();
        }
        if(getInstance().mouseButtonDown > 0){
            getInstance().isDragging = true;
        }

        getInstance().lastX = getInstance().xPos;
        getInstance().lastY = getInstance().yPos;
        getInstance().lastWorldX = getInstance().worldX;
        getInstance().lastWorldY = getInstance().worldY;
        getInstance().xPos = xpos;
        getInstance().yPos = ypos;
        calcWorldX();
        calcWorldY();
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if(action == GLFW_PRESS){
            getInstance().mouseButtonDown++;

            if(button < getInstance().mouseButtonPressed.length){
                getInstance().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE){
            getInstance().mouseButtonDown--;

            if(button < getInstance().mouseButtonPressed.length){
                getInstance().mouseButtonPressed[button] = false;
                getInstance().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        getInstance().scrollX = xOffset;
        getInstance().scrollY = yOffset;
    }

    public static float getDx() {
        return (float)(getInstance().lastX - getInstance().xPos);
    }

    public static float getWorldDx() {
        return (float)(getInstance().lastWorldX - getInstance().worldX);
    }

    public static float getDy() {
        return (float)(getInstance().lastY - getInstance().yPos);
    }

    public static float getWorldDy() {
        return (float)(getInstance().lastWorldY - getInstance().worldY);
    }

    public static float getMouseX() {
        return (float)getInstance().xPos;
    }

    public static float getMouseY() {
        return (float)getInstance().yPos;
    }

    public static float getScrollX() {
        return (float)getInstance().scrollX;
    }

    public static float getScrollY() {
        return (float)getInstance().scrollY;
    }

    public static boolean isDragging() {
        return getInstance().isDragging;
    }

    public  static boolean mouseButtonDown(int button){
        if(button < getInstance().mouseButtonPressed.length){
            return getInstance().mouseButtonPressed[button];
        }
        return false;
    }

    public static float getScreenX(){
        return getScreen().x;
    }

    public static float getScreenY(){
        return getScreen().y;
    }

    public static Vector2f getScreen(){
        float currentX = getMouseX() - getInstance().gameViewportPos.x;
        currentX = (currentX / getInstance().gameViewportSize.x) * 1040.0f;

        float currentY = getMouseY() - getInstance().gameViewportPos.y;
        currentY = 639.0f - ((currentY / getInstance().gameViewportSize.y) * 639.0f);

        return new Vector2f(currentX, currentY);
    }

    public static float getWorldX(){
        return (float)getInstance().worldX;
    }

    public static float getWorldY(){
        return (float)getInstance().worldY;
    }

    public static void setGameViewportPos(Vector2f pos) {
        getInstance().gameViewportPos.set(pos);
    }

    public static void setGameViewportSize(Vector2f size) {
        getInstance().gameViewportSize.set(size);
    }

    private static void calcWorldX() {
        float currentX = getMouseX() - getInstance().gameViewportPos.x;
        currentX = (currentX / getInstance().gameViewportSize.x) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);

        Camera camera = Window.getScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);

        getInstance().worldX = tmp.x;
    }

    private static void calcWorldY() {
        float currentY = getMouseY() - getInstance().gameViewportPos.y;
        currentY = -((currentY / getInstance().gameViewportSize.y) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(0, currentY, 0, 1);

        Camera camera = Window.getScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);

        getInstance().worldY = tmp.y;
    }

    public static Vector2f screenToWorld(Vector2f screenCoords) {
        Vector2f normalizedScreenCords = new Vector2f(
                screenCoords.x / 1040,
                screenCoords.y / 639
        );
        normalizedScreenCords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        Camera camera = Window.getScene().getCamera();
        Vector4f temp = new Vector4f(normalizedScreenCords.x, normalizedScreenCords.y, 0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseViewMatrix());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjectionMatrix());

        temp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(temp.x, temp.y);
    }

    public static Vector2f worldToScreen (Vector2f worldCoords) {
        Camera camera = Window.getScene().getCamera();
        Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePos.mul(projection.mul(view));
        Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(1040, 639));
        return windowSpace;
    }

}

