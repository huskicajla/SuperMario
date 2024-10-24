package components;

import editor.PPImGui;
import pixel_pioneer.GameObject;
import imgui.ImGui;
import imgui.type.ImInt;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Components {
    private static int ID_Counter = 0;
    private int u_id = -1;

    public transient GameObject gameObject = null;

    public void start() {

    }

    public void update(float dt){

    }

    public void editorUpdate(float dt) {

    }

    public void beginCollision(GameObject collidiongGameObject, Contact contact, Vector2f hitNormal) {

    }

    public void endCollision(GameObject collidiongGameObject, Contact contact, Vector2f hitNormal) {

    }

    public void preSolve(GameObject collidiongGameObject, Contact contact, Vector2f hitNormal) {

    }

    public void postSolve(GameObject collidiongGameObject, Contact contact, Vector2f hitNormal) {

    }



    public void imgui(){
        try{
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if(isTransient){
                    continue;
                }

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if(isPrivate){
                    field.setAccessible(true);
                }
                Class type = field.getType();
                Object value = field.get(this);
                String fieldName = field.getName();

                if(type == int.class){
                    int val = (int)value;
                    field.set(this, PPImGui.dragInt(fieldName + ":", val));
                } else if(type == float.class){
                    float val = (float)value;
                    field.set(this, PPImGui.dragFloat(fieldName + ":", val));
                } else if(type == boolean.class){
                    boolean val = (boolean)value;
                    if(ImGui.checkbox(fieldName + ": ", val)){
                        field.set(this, !val);
                    }
                } else if (type == Vector2f.class){
                    Vector2f val = (Vector2f)value;
                    PPImGui.drawVec2Ctrl(fieldName + ": ", val);
                }else if(type == Vector3f.class){
                    Vector3f val = (Vector3f)value;
                    float[] imVec = {val.x, val.y, val.z};
                    if(ImGui.dragFloat3(fieldName + ": ", imVec)){
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if(type == Vector4f.class){
                    Vector4f val = (Vector4f)value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if(ImGui.dragFloat4(fieldName + ": ", imVec)){
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                } else if(type.isEnum()) {
                    String[] enumVlues = getEnumValues(type);
                    String enumType = ((Enum)value).name();
                    ImInt index = new ImInt(indexOf(enumType, enumVlues));
                    if(ImGui.combo(field.getName(), index, enumVlues, enumVlues.length)){
                        field.set(this, type.getEnumConstants()[index.get()]);
                    }
                } else if(type == String.class){
                    field.set(this, PPImGui.inputText(field.getName() + ": ", (String)value));
                }


                if(isPrivate){
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public void generate_id(){
        if(this.u_id == -1){
            this.u_id = ID_Counter++;
        }
    }

    private  <T extends Enum<T>> String[] getEnumValues(Class<T> enumType){
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for(T value : enumType.getEnumConstants()){
            enumValues[i++] = value.name();
        }
        return enumValues;
    }

    private int indexOf(String s, String[] array){
        for(int i = 0; i < array.length; i++){
            if(s.equals(array[i])){
                return i;
            }
        }
        return -1;
    }

    public void destroy() {

    }

    public int getU_id(){
        return this.u_id;
    }

    public static void init(int maxId){
        ID_Counter = maxId;
    }

}
