package visualizer;

import pixel_pioneer.GameObject;
import components.ui.SpriteRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Visualizer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<VisualBatch> batchList;
    private static Shader currentShader;

    public Visualizer() {
        this.batchList = new ArrayList<>();
    }

    public void add(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null) {
            add(spriteRenderer);
        }
    }

    public void add(SpriteRenderer spriteRenderer) {
        boolean added = false;
        for(VisualBatch batch : batchList) {
            if(batch.hasRoom() && batch.getzIndex() == spriteRenderer.gameObject.transform.getZIndex()){
                Texture texture = spriteRenderer.getTexture();
                if(texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())) {
                    batch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }
        if(!added) {
            VisualBatch newVisualbatch = new VisualBatch(MAX_BATCH_SIZE,
                    spriteRenderer.gameObject.transform.getZIndex(),
                    this);
            newVisualbatch.start();
            batchList.add(newVisualbatch);
            newVisualbatch.addSprite(spriteRenderer);
            Collections.sort(batchList);
        }
    }

    public void destroyGameObject(GameObject gameObject) {
        if(gameObject.getComponent(SpriteRenderer.class) == null) return;
        for(VisualBatch batch : batchList) {
            if(batch.destroyIfExists(gameObject)){
                return;
            }
        }
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getBoundShader() {
        return currentShader;
    }

    public void render() {
        currentShader.use();
        for(int i = 0; i < batchList.size(); i++) {
            VisualBatch batch = batchList.get(i);
            batch.render();
        }
    }
}
