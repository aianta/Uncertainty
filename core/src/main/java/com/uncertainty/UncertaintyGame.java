package com.uncertainty;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.uncertainty.components.PositionComponent;
import com.uncertainty.components.SizeComponent;
import com.uncertainty.components.VelocityComponent;
import com.uncertainty.entities.systems.MovementSystem;
import com.uncertainty.world.BlockType;

import java.util.ArrayList;
import java.util.List;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class UncertaintyGame extends ApplicationAdapter {

    public static final int VIEWPORT_WIDTH = 320 * 4;
    public static final int VIEWPORT_HEIGHT = 180 * 4;

    public static final int MAP_HEIGHT = 27;
    public static final int MAP_WIDTH = 27;
    public static final int CHUNK_HEIGHT = MAP_HEIGHT/3;
    public static final int CHUNK_WIDTH = MAP_WIDTH/3;

    public static final int MAX_DEPTH = 20;
    public static final int MIN_DEPTH = 0;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private IsometricRenderer renderer;
    private MovementSystem movementSystem;
    private Engine engine;

    private Entity player;
    private ComponentMapper<VelocityComponent> velocity = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SizeComponent> size = ComponentMapper.getFor(SizeComponent.class);

    private Matrix4 matrix = new Matrix4();

    private BitmapFont font;
    private SpriteBatch batch;
    private SpriteBatch textOverlay;

    public static int currentDepth = 0;
    public static boolean showGrid = true;
    float axisX = -1f;
    float axisY = -1f;
    float axisZ = -1f;

    public int currAngle = 0;

    Texture img;
    Sprite sprite;
    List<Sprite> sprites = new ArrayList<>();
//    TiledMap tiledMap;
//    IsometricTiledMapRenderer tiledMapRenderer;

    public void create(){



        //Setup isometric view
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH,VIEWPORT_HEIGHT);
        camera.position.set(VIEWPORT_WIDTH/2 - 500, VIEWPORT_HEIGHT/2, 10);


        batch = new SpriteBatch();
        textOverlay = new SpriteBatch();

        font = new BitmapFont();

        renderer = new IsometricRenderer();

//        //Create world
//        world = generateWorld();
//
//        //Initalize entity system
//        engine = new Engine();
//
//        //Register the movement system
//        movementSystem = new MovementSystem();
//        engine.addSystem(movementSystem);

//        //Create a player
//        player = new Entity();
//        var startingPositon = new PositionComponent();
//        startingPositon.x = MAP_WIDTH/2;
//        startingPositon.y = MAP_HEIGHT/2;

//        player.add(startingPositon);
//        player.add(new VelocityComponent());

//        var playerSize = new SizeComponent();
//        playerSize.width = 1;
//        playerSize.height = 1;
//        player.add(playerSize);
//        engine.addEntity(player);

        Gdx.input.setInputProcessor(new CameraController(camera));
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

    }

    @Override
    public void dispose() {
        engine.removeAllEntities();
        engine.removeSystem(movementSystem);
    }

    public void render(){
        Gdx.gl.glClearColor(0,0,0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setProjectionMatrix(camera.combined);


        batch.begin();
        int layerIndex = 0;
        while (layerIndex <= currentDepth){
            renderer.drawLayer(batch, layerIndex);
            layerIndex++;
        }
        batch.end();


        //Update entities
        //engine.update(Gdx.graphics.getDeltaTime());

        //Process Controls
        if(Gdx.input.isKeyPressed(Input.Keys.W)) camera.translate(0,1,0);
        if(Gdx.input.isKeyPressed(Input.Keys.A)) camera.translate(-1, 0,0);
        if(Gdx.input.isKeyPressed(Input.Keys.S)) camera.translate(0,-1,0);
        if(Gdx.input.isKeyPressed(Input.Keys.D)) camera.translate(1,0,0);
//        if(Gdx.input.isKeyPressed(Input.Keys.L))
//        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) velocity.get(player).x -= MovementSystem.MAX_VELOCITY/8;
//        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) velocity.get(player).x += MovementSystem.MAX_VELOCITY/8;
//        if(Gdx.input.isKeyPressed(Input.Keys.UP)) velocity.get(player).y -= MovementSystem.MAX_VELOCITY/8;
//        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) velocity.get(player).y += MovementSystem.MAX_VELOCITY/8;



        //Render FPS
        textOverlay.begin();
        font.draw(textOverlay, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10,20);
        font.draw(textOverlay, "Depth: " + UncertaintyGame.currentDepth, 10, 40);
        font.draw(textOverlay, "Viewport (height: " + camera.viewportHeight + ", width: " + camera.viewportWidth + ")", 10, 60);
        font.draw(textOverlay, "Camera Position (x: "+camera.position.x+" y: "+ camera.position.y+" z: "+camera.position.z+")", 10, 80);
        textOverlay.end();

        camera.update();
    }



}