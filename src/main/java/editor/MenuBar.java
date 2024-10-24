package editor;

import imgui.ImGui;
import core.event_system.EventSystem;
import core.event_system.Event;
import core.enums.EventType;

public class MenuBar {
    public void imgui(){
        ImGui.beginMenuBar();

        if(ImGui.beginMenu("File")){
            if(ImGui.menuItem("Save", "Ctrl + S")){
                EventSystem.notify(null, new Event(EventType.SaveLevel));
            }

            if(ImGui.menuItem("Load", "Ctrl + O")){
                EventSystem.notify(null, new Event(EventType.LoadLevel));
            }

            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}
