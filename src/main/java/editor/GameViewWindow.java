package editor;

import imgui.ImFont;
import imgui.flag.ImGuiCol;
import pixel_pioneer.MouseListener;
import pixel_pioneer.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import core.event_system.EventSystem;
import core.event_system.Event;
import core.enums.EventType;
import org.joml.Vector2f;
import scenes.LevelSceneInitializer;

public class GameViewWindow {

    private float leftX, rightX, topY, bottomY;
    public static boolean isPlaying = false;
    public static boolean showHUD = false;

    private int currentLevel = 1;
    private int score;
    private int lives;

    private ImFont marioFont;

    public GameViewWindow() {
        initFontRetroM();
    }

    public void initFontRetroM() {
        marioFont = ImGui.getIO().getFonts().addFontFromFileTTF("textures/fonts/RetroMario-Regular.otf", 45);
        ImGui.getIO().setFontGlobalScale(1.0f);
    }

    public void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar
                | ImGuiWindowFlags.NoScrollWithMouse
                | ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();
        if(ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            showHUD = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }
        if(ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            showHUD = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }
        ImGui.endMenuBar();

        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();
        leftX = topLeft.x;
        bottomY = topLeft.y - 25;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y - 25;

        int textureId = Window.getFramebuffer().getFboTextureId();
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        // Set game viewport position and size
        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y - 25));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        if(showHUD){
            ImGui.pushFont(marioFont);

            float margin = 20.0f;
            ImGui.setCursorPos(windowPos.x + margin, windowPos.y + margin);
            ImGui.text("Score: " + LevelSceneInitializer.getScore());

            ImGui.setCursorPos(windowPos.x + (windowSize.x / 2) - 60, windowPos.y + margin);
            ImGui.text("Level: " + currentLevel);

            ImGui.setCursorPos(windowPos.x + windowSize.x - 170, windowPos.y + margin);
            ImGui.text("Lives: " + LevelSceneInitializer.getLives());

            ImGui.popFont();
        }


        ImGui.end();
    }

    public boolean getWantCaptureMouse(){
        return  MouseListener.getMouseX() >= leftX
                && MouseListener.getMouseX() <= rightX
                && MouseListener.getMouseY() >= bottomY
                && MouseListener.getMouseY() <= topY;
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();

        if(aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportWidth = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportHeight = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportWidth + ImGui.getCursorPosX(),
                viewportHeight + ImGui.getCursorPosY());
    }
}
