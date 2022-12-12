package com.uncertainty.entities.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.uncertainty.components.CommandComponent;
import com.uncertainty.components.OrdersComponent;
import com.uncertainty.components.SelectedComponent;
import com.uncertainty.model.Orientation;

public class CommandSystem extends IteratingSystem {

    private ComponentMapper<CommandComponent> cm = ComponentMapper.getFor(CommandComponent.class);
    private ComponentMapper<OrdersComponent> om = ComponentMapper.getFor(OrdersComponent.class);

    public CommandSystem(){
        super(Family.all(CommandComponent.class).get());
    }

    @Override
    protected void processEntity(Entity command, float v) {
        //Get entities for this command
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(
                OrdersComponent.class,
                SelectedComponent.class
                ).get());

        //Enqueue command for entities
        for(Entity entity: entities){
            OrdersComponent orders = om.get(entity);
            orders.commandQueue.add(cm.get(command).command);
        }

        //Consume command when done
        getEngine().removeEntity(command);
    }
}
