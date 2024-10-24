package components;

import pixel_pioneer.Camera;
import pixel_pioneer.GameObject;
import pixel_pioneer.Window;
import org.joml.Vector4f;

public class GameCamera extends Components {
    private transient GameObject player;
    private transient Camera gameCamera;
    private transient float highestX = Float.MIN_VALUE;
    private transient float undergrounYLevel = 0.0f;
    private transient float cameraBuffer = 1.5f;
    private transient float playerBuffer = 0.25f;
    private transient boolean safePlace = false;

    private Vector4f skyColor = new Vector4f(92.0f / 255.0f, 148.0f / 255.0f, 252.0f / 255.0f, 1.0f);
    private Vector4f undergroundColor = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    public GameCamera(Camera camera) {
        this.gameCamera = camera;
    }

    @Override
    public void start() {
        this.player = Window.getScene().getGameObjectWidth(PlayerController.class);
        this.gameCamera.clearColor.set(skyColor);
        this.undergrounYLevel = this.gameCamera.position.y - this.gameCamera.getProjectionSize().y - this.cameraBuffer;
    }

    @Override
    public void update(float dt) {
        if(player != null && !player.getComponent(PlayerController.class).hasWon()){
            gameCamera.position.x = Math.max(player.transform.position.x - 2.5f, highestX);
            highestX = Math.max(highestX, gameCamera.position.x);

            if(player.transform.position.y < -playerBuffer) {
                this.gameCamera.position.y = undergrounYLevel;
                this.gameCamera.clearColor.set(undergroundColor);
                safePlace = true;
            } else if (player.transform.position.y >= 0) {
                this.gameCamera.position.y = 0.0f;
                this.gameCamera.clearColor.set(skyColor);
            }
        }
    }

    public boolean isSafePlace() {
        return safePlace;
    }

}
