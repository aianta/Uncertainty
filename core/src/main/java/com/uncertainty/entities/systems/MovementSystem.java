package com.uncertainty.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.uncertainty.UncertaintyGame;
import com.uncertainty.components.OrdersComponent;
import com.uncertainty.components.OrientationComponent;
import com.uncertainty.components.PositionComponent;
import com.uncertainty.components.VelocityComponent;
import com.uncertainty.model.MoveCommand;
import com.uncertainty.model.Orientation;
import com.uncertainty.model.UnitCommand;

public class MovementSystem extends IteratingSystem {
    public static float MAX_VELOCITY = 0.5f;
    public static float FRICTION = 0.99f;
    private ImmutableArray<Entity> entities;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<OrdersComponent> om = ComponentMapper.getFor(OrdersComponent.class);
    private ComponentMapper<OrientationComponent> orientationMapper = ComponentMapper.getFor(OrientationComponent.class);
    public MovementSystem(){
        super(Family.all(OrdersComponent.class, OrientationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        OrdersComponent orders = om.get(entity);

        UnitCommand command = orders.commandQueue.peek();

        if(command instanceof MoveCommand){
            MoveCommand moveCommand = (MoveCommand) command;
            Vector3 position = pm.get(entity).position;

            if(Math.abs(moveCommand.targetPosition.x - position.x) <= deltaTime && Math.abs(moveCommand.targetPosition.y - position.y) <= deltaTime){
                orders.commandQueue.poll(); //Pop the command off the queue.
            }else{
                OrientationComponent orientation = orientationMapper.get(entity);
                VelocityComponent velocity = vm.get(entity);
                if((moveCommand.targetPosition.x - position.x) < 0 && Math.abs(moveCommand.targetPosition.x - position.x) > deltaTime && moveCommand.targetPosition.y > position.y){
                    orientation.orientation = Orientation.UP_LEFT;
                    position.x -= velocity.x * deltaTime;
                }

                if((moveCommand.targetPosition.x - position.x) > 0 && Math.abs(moveCommand.targetPosition.x - position.x) > deltaTime && moveCommand.targetPosition.y > position.y){
                    orientation.orientation = Orientation.UP_RIGHT;
                    position.x += velocity.x * deltaTime;
                }

                if((moveCommand.targetPosition.x - position.x) < 0 && Math.abs(moveCommand.targetPosition.x - position.x) > deltaTime && moveCommand.targetPosition.y < position.y){
                    orientation.orientation = Orientation.DOWN_LEFT;
                    position.x -= velocity.x * deltaTime;
                }

                if((moveCommand.targetPosition.x - position.x) > 0 && Math.abs(moveCommand.targetPosition.x - position.x) > deltaTime && moveCommand.targetPosition.y < position.y){
                    orientation.orientation = Orientation.DOWN_RIGHT;
                    position.x += velocity.x * deltaTime;
                }

                if (Math.abs(moveCommand.targetPosition.x - position.x) <= deltaTime && Math.abs(moveCommand.targetPosition.y - position.y) > deltaTime){
                    if(moveCommand.targetPosition.y > position.y){
                        orientation.orientation = Orientation.UP_LEFT;
                        position.y += velocity.y * deltaTime;
                    }
                    if(moveCommand.targetPosition.y < position.y){
                        orientation.orientation = Orientation.DOWN_RIGHT;
                        position.y -= velocity.y * deltaTime;
                    }

                }

            }

        }
    }
}
