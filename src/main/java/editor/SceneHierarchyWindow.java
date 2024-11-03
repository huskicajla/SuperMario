package editor;

import pixel_pioneer.GameObject;
import pixel_pioneer.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneHierarchyWindow {

    private static String playloadType = "SceneHierarchy";

    public void imgui() {
        if (GameViewWindow.isPlaying) {
            return;
        }

        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index = 0;
        for (GameObject gameObject : gameObjects) {
            if(!gameObject.doSerialization()){
                continue;
            }

            boolean treeNodeOpen = doTreeNode(gameObject, index);

            if(treeNodeOpen){
                ImGui.treePop();
            }
            index++;
        }

        ImGui.end();
    }

    private boolean doTreeNode(GameObject gameObject, int index) {
        ImGui.pushID(index);
        boolean treeNodeOpen = ImGui.treeNodeEx(
                gameObject.name,
                ImGuiTreeNodeFlags.DefaultOpen |
                        ImGuiTreeNodeFlags.FramePadding |
                        ImGuiTreeNodeFlags.OpenOnArrow |
                        ImGuiTreeNodeFlags.SpanAvailWidth,
                gameObject.name
        );
        ImGui.popID();

        if(ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayloadObject(playloadType, gameObject);
            ImGui.text(gameObject.name);
            ImGui.endDragDropSource();
        }

        if(ImGui.beginDragDropTarget()) {
            Object payload = ImGui.acceptDragDropPayloadObject(playloadType);
            if(payload != null){
                //payload instanceof GameObject
                if(payload.getClass().isAssignableFrom(GameObject.class)){
                    GameObject player = (GameObject) payload;

                }
            }
            ImGui.endDragDropTarget();
        }

        return treeNodeOpen;
    }

}
