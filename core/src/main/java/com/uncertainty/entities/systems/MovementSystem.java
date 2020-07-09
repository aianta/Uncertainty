package com.uncertainty.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.uncertainty.UncertaintyGame;
import com.uncertainty.components.PositionComponent;
import com.uncertainty.components.VelocityComponent;

public class MovementSystem extends EntitySystem {
    public static float MAX_VELOCITY = 0.5f;
    public static float FRICTION = 0.99f;
    private ImmutableArray<Entity> entities;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    public void addedToEngine(Engine engine){
        entities = engine.getEntitiesFor(Family.all(
                PositionComponent.class,
                VelocityComponent.class)
                .get()
        );
    }

    public void update (float deltaTime){
        for(Entity entity: entities){
            var position = pm.get(entity);
            var velocity = vm.get(entity);

            //Apply friction
            velocity.x *= FRICTION;
            velocity.y *= FRICTION;

            //Check to see if we're about to hit a wall and bounce off it if so.
            var nextX = position.x + velocity.x * deltaTime;
            var nextY = position.y + velocity.y * deltaTime;

            if(nextX > UncertaintyGame.MAP_WIDTH-2 || nextX < 1){
                velocity.x = -velocity.x;
            }

            if(nextY > UncertaintyGame.MAP_HEIGHT-2 || nextY < 1){
                velocity.y = -velocity.y;
            }

            position.x = Math.min(position.x + velocity.x * deltaTime, position.x + MAX_VELOCITY);
            position.y = Math.min(position.y + velocity.y * deltaTime, position.y + MAX_VELOCITY);


        }
    }
}
