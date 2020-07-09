package com.uncertainty;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.uncertainty.components.PositionComponent;
import com.uncertainty.components.SizeComponent;
import com.uncertainty.components.VelocityComponent;
import com.uncertainty.entities.systems.MovementSystem;
import com.uncertainty.world.BlockType;
import com.uncertainty.world.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class UncertaintyGame extends ApplicationAdapter {

    public static final int MAP_HEIGHT = 27;
    public static final int MAP_WIDTH = 27;
    public static final int CHUNK_HEIGHT = MAP_HEIGHT/3;
    public static final int CHUNK_WIDTH = MAP_WIDTH/3;

    public static final int MAX_DEPTH = 10;
    public static final int MIN_DEPTH = 0;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private MovementSystem movementSystem;
    private Engine engine;

    private Entity player;
    private ComponentMapper<VelocityComponent> velocity = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SizeComponent> size = ComponentMapper.getFor(SizeComponent.class);

    private Matrix4 matrix = new Matrix4();

    private BitmapFont font;
    private SpriteBatch batch;

    private Chunk [][] world;
    public static int currentDepth = 0;

    public void create(){

        //Setup isometric view
        camera = new OrthographicCamera(MAP_WIDTH/2,(MAP_HEIGHT/2) * (Gdx.graphics.getHeight()/(float)Gdx.graphics.getWidth()));
        camera.position.set(MAP_WIDTH/2,MAP_HEIGHT/2,MAP_HEIGHT/2);
        camera.direction.set(-1,-1,-1);
        camera.near = -200;
        camera.far = 200;
        camera.viewportHeight = 34;
        camera.viewportWidth = 37;
        matrix.setToRotation(new Vector3(1,0,0), 90);


        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();

        //Create world
        world = new Chunk[MAP_HEIGHT/CHUNK_HEIGHT][MAP_WIDTH/CHUNK_WIDTH];
        for(int i = 0; i < MAP_HEIGHT/CHUNK_HEIGHT; i++){
            for(int j = 0; j < MAP_WIDTH/CHUNK_WIDTH; j++){
                world[j][i] = new Chunk(CHUNK_WIDTH,CHUNK_HEIGHT, MAX_DEPTH);
            }
        }

        //Initalize entity system
        engine = new Engine();

        //Register the movement system
        movementSystem = new MovementSystem();
        engine.addSystem(movementSystem);

        //Create a player
        player = new Entity();
        var startingPositon = new PositionComponent();
        startingPositon.x = MAP_WIDTH/2;
        startingPositon.y = MAP_HEIGHT/2;

        player.add(startingPositon);
        player.add(new VelocityComponent());

        var playerSize = new SizeComponent();
        playerSize.width = 1;
        playerSize.height = 1;
        player.add(playerSize);
        engine.addEntity(player);

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
        camera.update();

        //Update entities
        engine.update(Gdx.graphics.getDeltaTime());

        //Process Controls
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) velocity.get(player).x -= MovementSystem.MAX_VELOCITY/8;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) velocity.get(player).x += MovementSystem.MAX_VELOCITY/8;
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) velocity.get(player).y -= MovementSystem.MAX_VELOCITY/8;
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) velocity.get(player).y += MovementSystem.MAX_VELOCITY/8;

        //Render
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //Depth based render
        int counter = 0;
        for(int i = 0; i <= currentDepth; i++){

            BlockType[][] levelMap = getLayer(i);
            var levelMatrix = matrix.cpy();
            levelMatrix.translate(-i,-i,-i);
            shapeRenderer.setTransformMatrix(levelMatrix);

            for(int z = 0; z < MAP_HEIGHT; z++){
                for(int x = 0; x < MAP_WIDTH; x++){

                    BlockType block = levelMap[z][x];
                    float light = 0.25f + i*0.1f;

                    switch (block){
                        case DIRT:
                            shapeRenderer.setColor(Color.BROWN.cpy().mul(light));
                            shapeRenderer.rect(x,z, 1,1);
                            break;
                        case EMPTY:
                            continue;
                        case GRASS:
                            shapeRenderer.setColor(Color.FOREST.cpy().mul(light));
                            shapeRenderer.rect(x,z,1,1);
                    }
                }
            }


            if(counter == currentDepth){
                //Draw player
                shapeRenderer.setTransformMatrix(levelMatrix);
                shapeRenderer.setColor(Color.FIREBRICK);
                shapeRenderer.rect(
                        position.get(player).x,
                        position.get(player).y,
                        size.get(player).width,
                        size.get(player).height
                );
            }
            counter++;

        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //Depth based render
        counter = 0;
        for(int i = 0; i <= currentDepth; i++){

            BlockType[][] levelMap = getLayer(i);
            var levelMatrix = matrix.cpy();
            levelMatrix.translate(-i,-i,-i);
            shapeRenderer.setTransformMatrix(levelMatrix);

            for(int z = 0; z < MAP_HEIGHT; z++){
                for(int x = 0; x < MAP_WIDTH; x++){

                    BlockType block = levelMap[z][x];
                    float light = 0.25f + i*0.1f;

                    switch (block){
                        case DIRT:
                            shapeRenderer.setColor(Color.BLACK.cpy().mul(light));
                            shapeRenderer.rect(x,z, 1,1);
                            break;
                        case EMPTY:
                            continue;
                        case GRASS:
                            shapeRenderer.setColor(Color.BLACK.cpy().mul(light));
                            shapeRenderer.rect(x,z,1,1);
                    }
                }
            }


            if(counter == currentDepth){
                //Draw player
                shapeRenderer.setTransformMatrix(levelMatrix);
                shapeRenderer.setColor(Color.FIREBRICK);
                shapeRenderer.rect(
                        position.get(player).x,
                        position.get(player).y,
                        size.get(player).width,
                        size.get(player).height
                );
            }
            counter++;

        }

        shapeRenderer.end();

        //Render FPS
        batch.begin();
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10,20);
        font.draw(batch, "Depth: " + UncertaintyGame.currentDepth, 10, 40);
        font.draw(batch, "Viewport (height: " + camera.viewportHeight + ", width: " + camera.viewportWidth + ")", 10, 60);
        batch.end();
    }

    public BlockType [][] getLayer(int depth){
        BlockType [][] layer = new BlockType[MAP_HEIGHT][MAP_WIDTH];

        // Iterate through the world chunks
        for(int i = 0; i < MAP_HEIGHT/CHUNK_HEIGHT; i++){
            for(int j = 0; j < MAP_WIDTH/CHUNK_WIDTH; j++){
                Chunk chunk = world[i][j];
                BlockType [][] chunkLayer = world[i][j].getLayer(depth);

                //Iterate through the individual chunk
                for(int chunkI = 0; chunkI < chunk.height; chunkI++){
                    for(int chunkJ = 0; chunkJ < chunk.width; chunkJ++){

                        // Convert local chunk co-ordonates to world co-ordonates
                        layer[i*CHUNK_HEIGHT + chunkI][j*CHUNK_WIDTH + chunkJ] = chunkLayer[chunkI][chunkJ];
                    }
                }
            }
        }

        return layer;

    }

}