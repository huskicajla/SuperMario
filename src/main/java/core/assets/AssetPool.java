package core.assets;

import pixel_pioneer.Sound;
import components.ui.Spritesheet;
import visualizer.Shader;
import visualizer.Texture;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spriteSheets = new HashMap<>();
    private static Map<String, Sound> sounds = new HashMap<>();

    public static Shader getShader(String name) {
        File file = new File(name);
        if(AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            return AssetPool.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(name);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String name) {
        File file = new File(name);
        if(AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(name);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpriteSheet(String name, Spritesheet spriteSheet) {
        File file = new File(name);
        if(!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spriteSheets.put(file.getAbsolutePath(), spriteSheet);
        }
    }

    public static Spritesheet getSpriteSheet(String name) {
        File file = new File(name);
        if(!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            assert false : "Error: Tried to access sprite sheet " + name + " but there is no sprite sheet";
        }
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }

    public static Collection<Sound> getSounds() {
        return sounds.values();
    }

    public static Sound getSound(String name) {
        File file = new File(name);
        if(sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            assert false : "Sound not found";
        }

        return null;
    }

    public static Sound addSound(String name, boolean loops) {
        File file = new File(name);
        if(sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            Sound sound = new Sound(file.getAbsolutePath(), loops);
            AssetPool.sounds.put(file.getAbsolutePath(), sound);
            return sound;
        }
    }
}
