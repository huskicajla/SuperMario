package pixel_pioneer;

import components.game_logic.Ground;
import components.game_objects.*;
import components.*;
import components.ui.Sprite;
import components.ui.SpriteRenderer;
import components.ui.Spritesheet;
import components.ui.StateMachine;
import core.enums.Direction;
import org.joml.Vector2f;
import core.physics.components.Box2DCollider;
import core.physics.components.CircleCollider;
import core.physics.components.PillboxCollider;
import core.physics.components.Rigidbody2D;
import core.enums.BodyType;
import core.assets.AssetPool;

public class KeyCharacteristics {

    public static Spritesheet items = AssetPool.getSpriteSheet("textures/images/spritesheets/items.png");
    public static Spritesheet characterAndEnemies = AssetPool.getSpriteSheet("textures/images/character_and_enemies.png");
    public static Spritesheet pipes = AssetPool.getSpriteSheet("textures/images/pipes.png");
    public static Spritesheet turtleSprite = AssetPool.getSpriteSheet("textures/images/turtle.png");

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY){
        GameObject block = Window.getScene().createGameObject("Sprite_Block_Generated");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        spriteRenderer.setSprite(sprite);
        block.addComponent(spriteRenderer);

        return block;
    }

    public static GameObject generateMario(){
        Spritesheet bigPlayerSprites = AssetPool.getSpriteSheet("textures/images/bigSpritesheet.png");
        GameObject mario = generateSpriteObject(characterAndEnemies.getSprite(0), 0.24f, 0.24f );

        //Little MARIO
        Animation run = new Animation();
        run.title = "Run";
        float defaultFrameTime = 0.2f;
        run.addFrame(characterAndEnemies.getSprite(0), defaultFrameTime);
        run.addFrame(characterAndEnemies.getSprite(2), defaultFrameTime);
        run.addFrame(characterAndEnemies.getSprite(3), defaultFrameTime);
        run.addFrame(characterAndEnemies.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        Animation switchDirection = new Animation();
        switchDirection.title = "Switch Direction";
        switchDirection.addFrame(characterAndEnemies.getSprite(4), 0.1f);
        switchDirection.setLoop(false);

        Animation idle = new Animation();
        idle.title = "Idle";
        idle.addFrame(characterAndEnemies.getSprite(0), 0.1f);
        idle.setLoop(false);

        Animation jump = new Animation();
        jump.title = "Jump";
        jump.addFrame(characterAndEnemies.getSprite(5), 0.1f);
        jump.setLoop(false);

        // Big Mario animations
        Animation bigRun = new Animation();
        bigRun.title = "BigRun";
        bigRun.addFrame(bigPlayerSprites.getSprite(0), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(3), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.setLoop(true);

        Animation bigSwitchDirection = new Animation();
        bigSwitchDirection.title = "Big Switch Direction";
        bigSwitchDirection.addFrame(bigPlayerSprites.getSprite(4), 0.1f);
        bigSwitchDirection.setLoop(false);

        Animation bigIdle = new Animation();
        bigIdle.title = "BigIdle";
        bigIdle.addFrame(bigPlayerSprites.getSprite(0), 0.1f);
        bigIdle.setLoop(false);
        Animation bigJump = new Animation();
        bigJump.title = "BigJump";
        bigJump.addFrame(bigPlayerSprites.getSprite(5), 0.1f);
        bigJump.setLoop(false);

        // Fire mario animations
        int fireOffset = 21;
        Animation fireRun = new Animation();
        fireRun.title = "FireRun";
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 3), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.setLoop(true);

        Animation fireSwitchDirection = new Animation();
        fireSwitchDirection.title = "Fire Switch Direction";
        fireSwitchDirection.addFrame(bigPlayerSprites.getSprite(fireOffset + 4), 0.1f);
        fireSwitchDirection.setLoop(false);

        Animation fireIdle = new Animation();
        fireIdle.title = "FireIdle";
        fireIdle.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), 0.1f);
        fireIdle.setLoop(false);

        Animation fireJump = new Animation();
        fireJump.title = "FireJump";
        fireJump.addFrame(bigPlayerSprites.getSprite(fireOffset + 5), 0.1f);
        fireJump.setLoop(false);

        Animation die = new Animation();
        die.title = "Die";
        die.addFrame(characterAndEnemies.getSprite(6), 0.1f);
        die.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addAnimation(run);
        stateMachine.addAnimation(idle);
        stateMachine.addAnimation(switchDirection);
        stateMachine.addAnimation(jump);
        stateMachine.addAnimation(die);

        stateMachine.addAnimation(bigRun);
        stateMachine.addAnimation(bigIdle);
        stateMachine.addAnimation(bigSwitchDirection);
        stateMachine.addAnimation(bigJump);

        stateMachine.addAnimation(fireRun);
        stateMachine.addAnimation(fireIdle);
        stateMachine.addAnimation(fireSwitchDirection);
        stateMachine.addAnimation(fireJump);

        stateMachine.setDefaultAnimationTitle(idle.title);
        stateMachine.addStateTrigger(run.title, switchDirection.title, "switchDirection");
        stateMachine.addStateTrigger(run.title, idle.title, "stopRunning");
        stateMachine.addStateTrigger(run.title, jump.title, "jump");
        stateMachine.addStateTrigger(switchDirection.title, idle.title, "stopRunning");
        stateMachine.addStateTrigger(switchDirection.title, run.title, "startRunning");
        stateMachine.addStateTrigger(switchDirection.title, jump.title, "jump");
        stateMachine.addStateTrigger(idle.title, run.title, "startRunning");
        stateMachine.addStateTrigger(idle.title, jump.title, "jump");
        stateMachine.addStateTrigger(jump.title, idle.title, "stopJumping");
        stateMachine.addStateTrigger(bigRun.title, bigSwitchDirection.title, "switchDirection");
        stateMachine.addStateTrigger(bigRun.title, bigIdle.title, "stopRunning");
        stateMachine.addStateTrigger(bigRun.title, bigJump.title, "jump");
        stateMachine.addStateTrigger(bigSwitchDirection.title, bigIdle.title, "stopRunning");
        stateMachine.addStateTrigger(bigSwitchDirection.title, bigRun.title, "startRunning");
        stateMachine.addStateTrigger(bigSwitchDirection.title, bigJump.title, "jump");
        stateMachine.addStateTrigger(bigIdle.title, bigRun.title, "startRunning");
        stateMachine.addStateTrigger(bigIdle.title, bigJump.title, "jump");
        stateMachine.addStateTrigger(bigJump.title, bigIdle.title, "stopJumping");

        stateMachine.addStateTrigger(fireRun.title, fireSwitchDirection.title, "switchDirection");
        stateMachine.addStateTrigger(fireRun.title, fireIdle.title, "stopRunning");
        stateMachine.addStateTrigger(fireRun.title, fireJump.title, "jump");
        stateMachine.addStateTrigger(fireSwitchDirection.title, fireIdle.title, "stopRunning");
        stateMachine.addStateTrigger(fireSwitchDirection.title, fireRun.title, "startRunning");
        stateMachine.addStateTrigger(fireSwitchDirection.title, fireJump.title, "jump");
        stateMachine.addStateTrigger(fireIdle.title, fireRun.title, "startRunning");
        stateMachine.addStateTrigger(fireIdle.title, fireJump.title, "jump");
        stateMachine.addStateTrigger(fireJump.title, fireIdle.title, "stopJumping");

        stateMachine.addStateTrigger(run.title, bigRun.title, "powerup");
        stateMachine.addStateTrigger(idle.title, bigIdle.title, "powerup");
        stateMachine.addStateTrigger(switchDirection.title, bigSwitchDirection.title, "powerup");
        stateMachine.addStateTrigger(jump.title, bigJump.title, "powerup");
        stateMachine.addStateTrigger(bigRun.title, fireRun.title, "powerup");
        stateMachine.addStateTrigger(bigIdle.title, fireIdle.title, "powerup");
        stateMachine.addStateTrigger(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
        stateMachine.addStateTrigger(bigJump.title, fireJump.title, "powerup");

        stateMachine.addStateTrigger(bigRun.title, run.title, "damage");
        stateMachine.addStateTrigger(bigIdle.title, idle.title, "damage");
        stateMachine.addStateTrigger(bigSwitchDirection.title, switchDirection.title, "damage");
        stateMachine.addStateTrigger(bigJump.title, jump.title, "damage");
        stateMachine.addStateTrigger(fireRun.title, bigRun.title, "damage");
        stateMachine.addStateTrigger(fireIdle.title, bigIdle.title, "damage");
        stateMachine.addStateTrigger(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
        stateMachine.addStateTrigger(fireJump.title, bigJump.title, "damage");

        stateMachine.addStateTrigger(run.title, die.title, "die");
        stateMachine.addStateTrigger(switchDirection.title, die.title, "die");
        stateMachine.addStateTrigger(idle.title, die.title, "die");
        stateMachine.addStateTrigger(jump.title, die.title, "die");
        stateMachine.addStateTrigger(bigRun.title, run.title, "die");
        stateMachine.addStateTrigger(bigSwitchDirection.title, switchDirection.title, "die");
        stateMachine.addStateTrigger(bigIdle.title, idle.title, "die");
        stateMachine.addStateTrigger(bigJump.title, jump.title, "die");
        stateMachine.addStateTrigger(fireRun.title, bigRun.title, "die");
        stateMachine.addStateTrigger(fireSwitchDirection.title, bigSwitchDirection.title, "die");
        stateMachine.addStateTrigger(fireIdle.title, bigIdle.title, "die");
        stateMachine.addStateTrigger(fireJump.title, bigJump.title, "die");

        mario.addComponent(stateMachine);

        PillboxCollider pillboxCollider = new PillboxCollider();
        pillboxCollider.width = 0.39f;
        pillboxCollider.height = 0.31f;

        Rigidbody2D rigidbody = new Rigidbody2D();
        rigidbody.setBodyType(BodyType.Dynamic);
        rigidbody.setContinuousCollision(false);
        rigidbody.setFixedRotation(true);
        rigidbody.setMass(25.0f);

        mario.addComponent(rigidbody);
        mario.addComponent(pillboxCollider);
        mario.addComponent(new PlayerController());

        mario.transform.zIndex = 10;

        return mario;
    }

    public static GameObject generateQuestionBlock(){
        GameObject questionBlock = generateSpriteObject(items.getSprite(0), 0.25f, 0.25f );

        Animation flicker = new Animation();
        flicker.title = "Flicker";
        float defaultFrameTime = 0.23f;
        flicker.addFrame(items.getSprite(0), 0.57f);
        flicker.addFrame(items.getSprite(1), defaultFrameTime);
        flicker.addFrame(items.getSprite(2), defaultFrameTime);
        flicker.setLoop(true);

        Animation inactive = new Animation();
        inactive.title = "Inactive";
        inactive.addFrame(items.getSprite(3), 0.1f);
        inactive.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addAnimation(flicker);
        stateMachine.addAnimation(inactive);
        stateMachine.setDefaultAnimationTitle(flicker.title);
        stateMachine.addStateTrigger(flicker.title, inactive.title, "setInactive");
        questionBlock.addComponent(stateMachine);
        questionBlock.addComponent(new QuestionBlock());

        Rigidbody2D rigidbody = new Rigidbody2D();
        rigidbody.setBodyType(BodyType.Static);
        questionBlock.addComponent(rigidbody);
        Box2DCollider boxCollider = new Box2DCollider();
        boxCollider.setHalfSize(new Vector2f(0.25f, 0.25f));
        questionBlock.addComponent(boxCollider);
        questionBlock.addComponent(new Ground());

        return questionBlock;
    }

    public static GameObject generateBlockCoin() {
        GameObject coin = generateSpriteObject(items.getSprite(7), 0.25f, 0.25f );

        Animation coinFlip = new Animation();
        coinFlip.title = "CoinFlip";
        float defaultFrameTime = 0.23f;
        coinFlip.addFrame(items.getSprite(7), 0.57f);
        coinFlip.addFrame(items.getSprite(8), defaultFrameTime);
        coinFlip.addFrame(items.getSprite(9), defaultFrameTime);
        coinFlip.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addAnimation(coinFlip);
        stateMachine.setDefaultAnimationTitle(coinFlip.title);
        coin.addComponent(stateMachine);
        coin.addComponent(new QuestionBlock());

        coin.addComponent(new BlockCoin());

        return coin;
    }

    public static GameObject generateMushroom() {
        GameObject mushroom = generateSpriteObject(items.getSprite(10), 0.25f, 0.25f );

        Rigidbody2D rigidbody = new Rigidbody2D();
        rigidbody.setBodyType(BodyType.Dynamic);
        rigidbody.setFixedRotation(true);
        rigidbody.setContinuousCollision(false);
        mushroom.addComponent(rigidbody);

        CircleCollider collider = new CircleCollider();
        collider.setRadius(0.14f);
        mushroom.addComponent(collider);
        mushroom.addComponent(new Mushroom());

        return mushroom;
    }

    public static GameObject generateFlower() {
        GameObject flower = generateSpriteObject(items.getSprite(20), 0.25f, 0.25f );

        Rigidbody2D rigidbody = new Rigidbody2D();
        rigidbody.setBodyType(BodyType.Static);
        rigidbody.setFixedRotation(true);
        rigidbody.setContinuousCollision(false);
        flower.addComponent(rigidbody);

        CircleCollider collider = new CircleCollider();
        collider.setRadius(0.14f);
        flower.addComponent(collider);
        flower.addComponent(new Flower());

        return flower;
    }

    public static GameObject generateGoomba() {
        GameObject goomba = generateSpriteObject(characterAndEnemies.getSprite(14), 0.25f, 0.25f );

        Animation walk = new Animation();
        walk.title = "Walk";
        float defaultFrameTime = 0.23f;
        walk.addFrame(characterAndEnemies.getSprite(14), defaultFrameTime);
        walk.addFrame(characterAndEnemies.getSprite(15), defaultFrameTime);
        walk.setLoop(true);

        Animation squashed = new Animation();
        walk.title = "squashed";
        squashed.addFrame(characterAndEnemies.getSprite(16), 0.1f);
        squashed.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addAnimation(walk);
        stateMachine.addAnimation(squashed);
        stateMachine.setDefaultAnimationTitle(walk.title);
        stateMachine.addStateTrigger(walk.title, squashed.title, "squashMe");
        goomba.addComponent(stateMachine);

        Rigidbody2D rigidbody = new Rigidbody2D();
        rigidbody.setBodyType(BodyType.Dynamic);
        rigidbody.setMass(0.1f);
        rigidbody.setFixedRotation(true);
        goomba.addComponent(rigidbody);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.12f);
        goomba.addComponent(circleCollider);

        goomba.addComponent(new Goomba());

        return goomba;
    }

    public static GameObject generatePipe(Direction direction) {
        int index = direction == Direction.Down ? 0
                : direction == Direction.Up ? 1
                : direction == Direction.Right ? 2
                : direction == Direction.Left ? 3 : -1;
        assert index != -1 : "Invalid pipe direction";
        GameObject pipe = generateSpriteObject(pipes.getSprite(index), 0.5f, 0.5f );

        Rigidbody2D rigidbody = new Rigidbody2D();
        rigidbody.setBodyType(BodyType.Static);
        rigidbody.setFixedRotation(true);
        rigidbody.setContinuousCollision(false);
        pipe.addComponent(rigidbody);

        Box2DCollider box2DCollider = new Box2DCollider();
        box2DCollider.setHalfSize(new Vector2f(0.5f, 0.5f));
        pipe.addComponent(box2DCollider);
        pipe.addComponent(new Pipe(direction));
        pipe.addComponent(new Ground());

        return pipe;
    }

    public static GameObject generateTurtle() {
        GameObject turtle = generateSpriteObject(turtleSprite.getSprite(0), 0.25f, 0.35f);

        Animation walk = new Animation();
        walk.title = "Walk";
        float defaultFrameTime = 0.23f;
        walk.addFrame(turtleSprite.getSprite(0), defaultFrameTime);
        walk.addFrame(turtleSprite.getSprite(1), defaultFrameTime);
        walk.setLoop(true);

        Animation turtleShellSpin = new Animation();
        turtleShellSpin.title = "TurtleShellSpin";
        turtleShellSpin.addFrame(turtleSprite.getSprite(2), 0.3f);
        turtleShellSpin.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addAnimation(walk);
        stateMachine.addAnimation(turtleShellSpin);
        stateMachine.setDefaultAnimationTitle(walk.title);
        stateMachine.addStateTrigger(walk.title, turtleShellSpin.title, "squashMe");
        turtle.addComponent(stateMachine);

        Rigidbody2D rigidbody = new Rigidbody2D();
        rigidbody.setBodyType(BodyType.Dynamic);
        rigidbody.setMass(0.1f);
        rigidbody.setFixedRotation(true);
        turtle.addComponent(rigidbody);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.13f);
        circleCollider.setOffset(new Vector2f(0.0f, -0.05f));
        turtle.addComponent(circleCollider);

        turtle.addComponent(new Turtle());

        return turtle;
    }

    public static GameObject generateFlagtop() {
        GameObject flagtop = generateSpriteObject(items.getSprite(6), 0.25f, 0.25f );

        Rigidbody2D rigidbody = new Rigidbody2D();
        rigidbody.setBodyType(BodyType.Dynamic);
        rigidbody.setFixedRotation(true);
        rigidbody.setContinuousCollision(false);
        flagtop.addComponent(rigidbody);

        Box2DCollider boxCollider = new Box2DCollider();
        boxCollider.setHalfSize(new Vector2f(0.1f, 0.25f));
        boxCollider.setOffset(new Vector2f(-0.075f, 0.0f));
        flagtop.addComponent(boxCollider);
        flagtop.addComponent(new Flagpole(true));

        return flagtop;
    }

    public static GameObject generateFlagPole() {
        GameObject flagpole = generateSpriteObject(items.getSprite(33), 0.25f, 0.25f );

        Rigidbody2D rigidbody = new Rigidbody2D();
        rigidbody.setBodyType(BodyType.Dynamic);
        rigidbody.setFixedRotation(true);
        rigidbody.setContinuousCollision(false);
        flagpole.addComponent(rigidbody);

        Box2DCollider boxCollider = new Box2DCollider();
        boxCollider.setHalfSize(new Vector2f(0.1f, 0.25f));
        boxCollider.setOffset(new Vector2f(-0.075f, 0.0f));
        flagpole.addComponent(boxCollider);
        flagpole.addComponent(new Flagpole(false));

        return flagpole;
    }

    public static GameObject generateFireball(Vector2f position) {
        GameObject fireball = generateSpriteObject(items.getSprite(32), 0.18f, 0.18f );
        fireball.transform.position = position;

        Rigidbody2D rigidbody = new Rigidbody2D();
        rigidbody.setBodyType(BodyType.Dynamic);
        rigidbody.setFixedRotation(true);
        rigidbody.setContinuousCollision(false);
        fireball.addComponent(rigidbody);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.08f);
        fireball.addComponent(circleCollider);
        fireball.addComponent(new Fireball());

        return fireball;
    }

    public static GameObject generateCoin() {
        GameObject coin = generateSpriteObject(items.getSprite(7), 0.25f, 0.25f );

        Animation coinFlip = new Animation();
        coinFlip.title = "CoinFlip";
        float defaultTime = 0.23f;
        coinFlip.addFrame(items.getSprite(7), 0.57f);
        coinFlip.addFrame(items.getSprite(8), defaultTime);
        coinFlip.addFrame(items.getSprite(9), defaultTime);
        coinFlip.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addAnimation(coinFlip);
        stateMachine.setDefaultAnimationTitle(coinFlip.title);
        coin.addComponent(stateMachine);
        coin.addComponent(new Coin());

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.12f);
        coin.addComponent(circleCollider);
        Rigidbody2D rigidbody = new Rigidbody2D();
        rigidbody.setBodyType(BodyType.Static);
        coin.addComponent(rigidbody);

        return coin;
    }

}
