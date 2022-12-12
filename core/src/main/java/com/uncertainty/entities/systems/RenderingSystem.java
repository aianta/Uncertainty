package com.uncertainty.entities.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.uncertainty.IsometricRenderer;
import com.uncertainty.components.OrientationComponent;
import com.uncertainty.components.PositionComponent;
import com.uncertainty.components.SizeComponent;

public class RenderingSystem extends IteratingSystem {
    private static final Vector3 INVALID_POSITION = new Vector3(-1,-1,-1);

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SizeComponent> sm = ComponentMapper.getFor(SizeComponent.class);
    private ComponentMapper<OrientationComponent> om = ComponentMapper.getFor(OrientationComponent.class);
    private SpriteBatch batch;
    private Texture truckDownRight;
    private Texture truckDownLeft;
    private Texture truckUpLeft;
    private Texture truckUpRight;

    private IsometricRenderer renderer;

    public RenderingSystem (SpriteBatch batch, IsometricRenderer renderer){
        super(Family.all(
                PositionComponent.class,
                SizeComponent.class,
                OrientationComponent.class
        ).get());

        this.batch = batch;
        this.truckDownRight = new Texture(Gdx.files.internal("truck_right_down.png"));
        this.truckDownLeft = new Texture(Gdx.files.internal("truck_left_down.png"));
        this.truckUpLeft = new Texture(Gdx.files.internal("truck_left_up.png"));
        this.truckUpRight = new Texture(Gdx.files.internal("truck_right_up.png"));
        this.renderer = renderer;
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        PositionComponent positionComponent = pm.get(entity);
        Vector3 pos = positionComponent.position;

        if(pos.equals(INVALID_POSITION)){
            //Don't render entities with invalid positions
            return;
        }

        SizeComponent sizeComponent = sm.get(entity);
        OrientationComponent orientationComponent = om.get(entity);
        Texture texture = switch (orientationComponent.orientation){

            case DOWN_RIGHT -> truckDownRight;
            case DOWN_LEFT -> truckDownLeft;
            case UP_RIGHT -> truckUpRight;
            case UP_LEFT -> truckUpLeft;
        };
        renderer.drawTruck(batch, texture, pos,  sizeComponent.width, sizeComponent.height);
}
}
