package components.ui;

import org.joml.Vector2f;
import visualizer.Texture;

public class Sprite {

    private float width, height;

    private Texture texture = null;
    private Vector2f[] textureCoords = {
                new Vector2f(1.0f, 1.0f),
                new Vector2f(1.0f, 0.0f),
                new Vector2f(0.0f, 0.0f),
                new Vector2f(0.0f, 1.0f)
        };

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTextureCoords() {
        return this.textureCoords;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    public void setTextureCoords(Vector2f[] textureCoords) {
        this.textureCoords = textureCoords;
    }

    public float getWidth() {
        return this.width;
    }
    public float getHeight() {
        return this.height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getTextureID(){
        return texture == null ? -1 : texture.getTextureID();
    }
}
