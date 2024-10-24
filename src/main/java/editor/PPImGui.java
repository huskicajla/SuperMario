package editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class PPImGui {

    private static float defaultColumnWidth = 220.0f;

    public static void drawVec2Ctrl(String label, Vector2f values){
        drawVec2Ctrl(label, values, 0.0f, defaultColumnWidth);
    }

    public static void drawVec2Ctrl(String label, Vector2f values, float resetVal){
        drawVec2Ctrl(label, values, resetVal, defaultColumnWidth);
    }

    public static void drawVec2Ctrl(String label, Vector2f values, float resetVal, float columnWidth){
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f;

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.16f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 1.0f, 0.3f, 0.17f, 1.0f);
        if(ImGui.button("X", buttonSize.x, buttonSize.y)){
            values.x = resetVal;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesX = {values.x};
        ImGui.dragFloat("##x", vecValuesX, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.4f, 0.9f, 0.4f, 1.0f);
        if(ImGui.button("Y", buttonSize.x, buttonSize.y)){
            values.y = resetVal;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesY = {values.y};
        ImGui.dragFloat("##y", vecValuesY, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.nextColumn();

        values.x = vecValuesX[0];
        values.y = vecValuesY[0];

        ImGui.columns(1);
        ImGui.popStyleVar();
        ImGui.popID();
    }


    public static float dragFloat(String label, float value){
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] valuesArray = {value};
        ImGui.dragFloat("##dragFloat", valuesArray, 0.1f);

        ImGui.columns(1);
        ImGui.popID();

        return valuesArray[0];
    }

    public static int dragInt(String label, int value){
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        int[] valuesArray = {value};
        ImGui.dragInt("##dragFloat", valuesArray, 0.1f);

        ImGui.columns(1);
        ImGui.popID();

        return valuesArray[0];
    }

    public static boolean colorPicker4(String label, Vector4f color){
        boolean result = false;
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] imColor = {color.x, color.y, color.z, color.w};
        if(ImGui.colorEdit4("##colorPicker", imColor)){
            color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
            result = true;
        }

        ImGui.columns(1);
        ImGui.popID();

        return result;
    }

    public static String inputText(String label, String value){
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        ImString imString = new ImString(value, 256);
        if(ImGui.inputText("##" + label, imString)){
            ImGui.columns(1);
            ImGui.popID();

            return imString.get();
        }

        ImGui.columns(1);
        ImGui.popID();

        return value;
    }
}
