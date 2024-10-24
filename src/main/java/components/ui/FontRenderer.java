package components.ui;

import components.Components;

public class FontRenderer extends Components {

    public FontRenderer() {
        gameObject = null;
    }

    @Override
    public void start(){
        if(gameObject.getComponent(SpriteRenderer.class) != null){
            System.out.println("Found Font Renderer");
        }
    }

    @Override
    public void update(float dt) {

    }
}
