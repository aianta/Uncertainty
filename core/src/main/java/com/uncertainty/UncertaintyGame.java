package com.uncertainty;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.uncertainty.components.PositionComponent;
import com.uncertainty.components.SizeComponent;
import com.uncertainty.components.VelocityComponent;
import com.uncertainty.entities.systems.MovementSystem;

import java.util.ArrayList;
import java.util.List;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class UncertaintyGame extends ApplicationAdapter {

    public static final int MAP_HEIGHT = 25;
    public static final int MAP_WIDTH = 25;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private ModelBuilder modelBuilder;
    private ModelBatch modelBatch;

    private MovementSystem movementSystem;
    private Engine engine;

    private Entity player;
    private ComponentMapper<VelocityComponent> velocity = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SizeComponent> size = ComponentMapper.getFor(SizeComponent.class);

    private Matrix4 matrix = new Matrix4();
    private int [][] map;
    private BitmapFont font;
    private SpriteBatch batch;
    private Model playerCube;
    private Model wallCube;
    private ModelInstance playerInstance;
    private List<ModelInstance> walls = new ArrayList<>();
    private Environment environment;


    public void create(){

        //Setup isometric view
        camera = new OrthographicCamera(MAP_WIDTH/2,(MAP_HEIGHT/2) * (Gdx.graphics.getHeight()/(float)Gdx.graphics.getWidth()));
        camera.position.set(MAP_WIDTH/2,MAP_HEIGHT/2,MAP_HEIGHT/2);
        camera.direction.set(-1,-1,-1);
        camera.near = 1;
        camera.far = 200;
        matrix.setToRotation(new Vector3(1,0,0), 90);

        //Setup Rendering Classes
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        modelBuilder = new ModelBuilder();
        modelBatch = new ModelBatch();

        //Generate the map
        map = generateMap(MAP_WIDTH,MAP_HEIGHT);

        //Create Wall Model
        wallCube = modelBuilder.createBox(
                1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        //Create Walls using Wall Model
        for (int z = 0; z < MAP_HEIGHT; z++){
            for(int x = 0; x < MAP_WIDTH; x++){
                if(map[z][x] == 1){
                    var wall = new ModelInstance(wallCube);
                    wall.transform.set(
                            new Vector3(1,0,0),
                            new Vector3(0,1,0),
                            new Vector3(0,0,1),
                            new Vector3(x,0f,z)
                    );
                    walls.add(wall);
                }
            }
        }

        //Create Player Model & Instance
        playerCube = modelBuilder.createBox(
                1f,1f,1f,
                new Material(ColorAttribute.createDiffuse(Color.TEAL)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        playerInstance = new ModelInstance(playerCube);


        //Setup environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.createAmbient(0.4f,0.4f,0.4f,1f)));
        environment.add(new DirectionalLight().set(0.8f,0.8f,0.8f, -1f,-1f,1f));

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


        playerInstance.transform.set(
                new Vector3(1,0,0),
                new Vector3(0,1,0),
                new Vector3(0,0,1),
                new Vector3(startingPositon.x,0f,startingPositon.y)
                );

        player.add(startingPositon);
        player.add(new VelocityComponent());

        var playerSize = new SizeComponent();
        playerSize.width = 1;
        playerSize.height = 1;
        playerSize.depth = 1;

        player.add(playerSize);
        engine.addEntity(player);

        //Set up input processors
        //For the camera
        InputProcessor cameraController = new CameraController(camera);
        //Send input events to both processors via InputMultiplexer
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(cameraController);
        //Set the multiplexer as the input processor
        Gdx.input.setInputProcessor(inputMultiplexer);
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

        camera.update();

        //Render
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setTransformMatrix(matrix);

        //Process player controls
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) velocity.get(player).x -= MovementSystem.MAX_VELOCITY/8;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) velocity.get(player).x += MovementSystem.MAX_VELOCITY / 8;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) velocity.get(player).y -= MovementSystem.MAX_VELOCITY/8;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))velocity.get(player).y += MovementSystem.MAX_VELOCITY/8;


        //Update entities
        engine.update(Gdx.graphics.getDeltaTime());

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(int z = 0; z < MAP_HEIGHT; z++){
            for(int x = 0; x < MAP_WIDTH; x++){
                if(map[z][x] == 1){
                    shapeRenderer.setColor(Color.GRAY);
                }else{
                    shapeRenderer.setColor(Color.FOREST);
                }
                shapeRenderer.rect(x,z, 1,1);
            }
        }

        shapeRenderer.setColor(Color.FIREBRICK);
        shapeRenderer.rect(
                position.get(player).x,
                position.get(player).y,
                size.get(player).width,
                size.get(player).height
        );

        shapeRenderer.end();



        modelBatch.begin(camera);
        playerInstance.transform.set(
                new Vector3(1,0,0),
                new Vector3(0,1,0),
                new Vector3(0,0,1),
                new Vector3(position.get(player).x,0f,position.get(player).y)
        );
        modelBatch.render(playerInstance, environment);
        modelBatch.render(walls, environment);
        modelBatch.end();


        //Render FPS
        batch.begin();
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10,20);
        batch.end();
    }

    public int [][] generateMap(int width, int height){
        int [][] map = new int[height][width];
        for(int y = 0; y < height; y++){
            //If this is the first or last row of the map
            if(y == 0 || y == height - 1){
                //Make every tile a wall
                for(int x = 0; x < width; x++){
                    map[y][x] = 1;
                }
            }else{
                //Otherwise, have walls only on the edges
                map[y][0] = 1;
                map[y][width - 1] = 1;
            }
        }
        return map;
    }

}