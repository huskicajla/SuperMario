package visualizer;

import pixel_pioneer.GameObject;
import pixel_pioneer.Window;
import components.ui.SpriteRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VisualBatch  implements Comparable<VisualBatch> {

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEXTURE_COORDS_SIZE = 2;
    private final int TEXTURE_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEXTURE_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXTURE_ID_OFFSET = TEXTURE_COORDS_OFFSET + TEXTURE_COORDS_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEXTURE_ID_OFFSET + TEXTURE_ID_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numberOfSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private int zIndex;

    private Visualizer visualizer;

    public VisualBatch(int maxBatchSize, int zIndex, Visualizer visualizer) {
        this.zIndex = zIndex;
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        this.visualizer = visualizer;

        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numberOfSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void start(){
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        int eboID = glGenBuffers();
        int[] indicies = generateIndicies();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STATIC_DRAW);

        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEXTURE_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }

    public void addSprite(SpriteRenderer sprite){
        int index = this.numberOfSprites;
        this.sprites[index] = sprite;
        this.numberOfSprites++;

        if(sprite.getTexture() != null){
            if(!textures.contains(sprite.getTexture())){
                this.textures.add(sprite.getTexture());
            }
        }

        loadVertexProperties(index);

        if(numberOfSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }
    }

    public void render(){

        boolean rebufferData = false;
        for (int i = 0; i < numberOfSprites; i++){
            SpriteRenderer spriteRenderer = sprites[i];
            if(spriteRenderer.isDirty()){
                if(!hasTexture(spriteRenderer.getTexture())){
                    this.visualizer.destroyGameObject(spriteRenderer.gameObject);
                    this.visualizer.add(spriteRenderer.gameObject);
                } else {
                    loadVertexProperties(i);
                    spriteRenderer.setClean();
                    rebufferData = true;
                }
            }

            if(spriteRenderer.gameObject.transform.zIndex != this.zIndex) {
                destroyIfExists(spriteRenderer.gameObject);
                visualizer.add(spriteRenderer.gameObject);
                i--;
            }
        }
        if(rebufferData){
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        Shader shader = Visualizer.getBoundShader();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());
        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", textureSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numberOfSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++){
            textures.get(i).unbind();
        }

        shader.detach();
    }

    public boolean destroyIfExists(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        for (int i = 0; i < numberOfSprites; i++){
            if(sprites[i] == spriteRenderer) {
                for(int j = i; j < numberOfSprites - 1; j++){
                    sprites[j] = sprites[j + 1];
                    sprites[j].setDirty();
                }
                numberOfSprites--;
                return true;
            }
        }

        return false;
    }

    public void loadVertexProperties(int index){
        SpriteRenderer sprite = this.sprites[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] textureCoords = sprite.getTextureCoords();

        int texId = 0;
        if(sprite.getTexture() != null){
            for (int i = 0; i < textures.size(); i++){
                if(textures.get(i).equals(sprite.getTexture())){
                    texId = i + 1;
                    break;
                }
            }
        }

        boolean isRotated = sprite.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if(isRotated){
            transformMatrix.translate(sprite.gameObject.transform.position.x,
                                        sprite.gameObject.transform.position.y,
                                        0);
            transformMatrix.rotate((float)Math.toRadians(sprite.gameObject.transform.rotation),
                                        0, 0, 1);
            transformMatrix.scale(sprite.gameObject.transform.scale.x,
                                    sprite.gameObject.transform.scale.y, 1);
        }

        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for(int i = 0; i < 4; i++){
            if(i == 1){
                yAdd = -0.50f;
            } else if(i == 2){
                xAdd = -0.50f;
            } else if(i == 3){
                yAdd = 0.50f;
            }

            Vector4f currentPosition = new Vector4f(
                    sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x),
                    sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y),
                    0, 1);

            if(isRotated){
                currentPosition = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }

            //Load position
            vertices[offset] = currentPosition.x;
            vertices[offset + 1] = currentPosition.y;

            //Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            //Load texture coordinates
            vertices[offset + 6] = textureCoords[i].x;
            vertices[offset + 7] = textureCoords[i].y;

            //Load texture id
            vertices[offset + 8] = texId;

            //Load entity id
            vertices[offset + 9] = sprite.gameObject.getUid() + 1;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndicies(){
        int[] indicies = new int[maxBatchSize * 6];
        for(int i = 0; i < maxBatchSize; i++){
            loadElementIndicies(indicies, i);
        }

        return indicies;
    }

    private void loadElementIndicies(int[] indicies, int index){
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        indicies[offsetArrayIndex] = offset + 3;
        indicies[offsetArrayIndex + 1] = offset + 2;
        indicies[offsetArrayIndex + 2] = offset;

        indicies[offsetArrayIndex + 3] = offset;
        indicies[offsetArrayIndex + 4] = offset + 2;
        indicies[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTextureRoom() {
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture texture) {
        return this.textures.contains(texture);
    }

    public int getzIndex() {
        return this.zIndex;
    }

    @Override
    public int compareTo(@NotNull VisualBatch o) {
        return Integer.compare(this.zIndex, o.getzIndex());
    }
}
