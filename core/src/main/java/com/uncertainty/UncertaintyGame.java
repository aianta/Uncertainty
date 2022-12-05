package com.uncertainty;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.uncertainty.components.PositionComponent;
import com.uncertainty.components.SizeComponent;
import com.uncertainty.components.VelocityComponent;
import com.uncertainty.entities.systems.MovementSystem;
import com.uncertainty.world.BlockType;
import com.uncertainty.world.World;

import java.util.ArrayList;
import java.util.List;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class UncertaintyGame extends ApplicationAdapter {

    public static final int VIEWPORT_WIDTH = 100;
    public static final int VIEWPORT_HEIGHT = 100;

    public static final int MAP_HEIGHT = 27;
    public static final int MAP_WIDTH = 27;
    public static final int CHUNK_HEIGHT = MAP_HEIGHT/3;
    public static final int CHUNK_WIDTH = MAP_WIDTH/3;

    public static final int MAX_DEPTH = 20;
    public static final int MIN_DEPTH = 0;

    private OrthographicCamera camera;
    private CameraController cameraController;

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

    public static int currentDepth = World.DEPTH;
    public static boolean showGrid = true;
    float axisX = -1f;
    float axisY = -1f;
    float axisZ = -1f;

    public int currAngle = 0;

    World world;

    Texture img;
    Sprite sprite;
    List<Sprite> sprites = new ArrayList<>();
//    TiledMap tiledMap;
//    IsometricTiledMapRenderer tiledMapRenderer;

    public void create(){



        //Setup isometric view
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //camera.setToOrtho(false, VIEWPORT_WIDTH,VIEWPORT_HEIGHT);
        //camera.position.set(VIEWPORT_WIDTH/2 , VIEWPORT_HEIGHT/2, 10);


        batch = new SpriteBatch();
        textOverlay = new SpriteBatch();

        font = new BitmapFont();

        renderer = new IsometricRenderer();
        shapeRenderer = new ShapeRenderer();

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

        cameraController = new CameraController(camera);

        Gdx.input.setInputProcessor(cameraController);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        world = new World();

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


        Vector3 cursor = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(cursor);

        Vector3 cell = new Vector3(((int)cursor.x/32), (int)cursor.y/16, 0);
        Vector3 offset = new Vector3((int)cursor.x % 32, (int)cursor.y % 16, 0);
        Vector3 origin = new Vector3(10,1,0);


        Vector3 grid = new Vector3((cell.y-origin.y)+ (cell.x-origin.x), (cell.y-origin.y)-(cell.x-origin.x),0);


        batch.begin();

        renderer.drawWorld(batch, world, currentDepth);

//        int layerIndex = 0;
//        while (layerIndex <= currentDepth){
//            renderer.drawLayer(batch, layerIndex);
//            layerIndex++;
//        }
        Vector3 selectedXY = renderer.drawSelection(batch, grid,offset);
        batch.end();



        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //shapeRenderer.rect(cell.x*32, cell.y*32, 32f,32f, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE);
        shapeRenderer.rect((cell.x)*32, cell.y*16, 32f, 16f,Color.PINK, Color.PINK, Color.PINK, Color.PINK);
        //shapeRenderer.rect(cell.x, cell.y, 32f, 16f, Color.PINK, Color.PINK, Color.PINK, Color.PINK);
        shapeRenderer.end();


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
        font.draw(textOverlay, "Cursor (x: " +cursor.x + " y: " + cursor.y + ")", 10, 140 );
        font.draw(textOverlay, "Grid (x: " + grid.x + " y " + grid.y + ")", 10, 160);
        font.draw(textOverlay, "Offset( x: " + offset.x + " y: " + offset.y + ")", 10, 200);
        font.draw(textOverlay, "selectedXY (x: " + selectedXY.x + " y:" + selectedXY.y + ")", 10, 180);

        textOverlay.end();

        camera.update();
    }

    public static Vector3 isoToXY(Vector3 v){
        return new Vector3(0.5f*v.x - v.y, 0.5f*v.x + v.y, 0);
    }

    public static Vector3 XYtoIso(Vector3 v){
        return new Vector3(v.x + v.y, 0.5f * (v.y - v.x), 0);
    }



}