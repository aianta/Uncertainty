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
import com.uncertainty.components.PositionComponent;
import com.uncertainty.components.SizeComponent;

public class RenderingSystem extends IteratingSystem {
    private static final Vector3 INVALID_POSITION = new Vector3(-1,-1,-1);

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SizeComponent> sm = ComponentMapper.getFor(SizeComponent.class);

    private SpriteBatch batch;
    private Texture truckTexture;

    private IsometricRenderer renderer;

    public RenderingSystem (SpriteBatch batch, IsometricRenderer renderer){
        super(Family.all(
                PositionComponent.class,
                SizeComponent.class
        ).get());

        this.batch = batch;
        this.truckTexture = new Texture(Gdx.files.internal("dirt-cube.png"));
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
        renderer.drawTruck(batch, truckTexture, pos,  sizeComponent.width, sizeComponent.height);
}
}
