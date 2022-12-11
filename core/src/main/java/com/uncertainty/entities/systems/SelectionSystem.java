package com.uncertainty.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.uncertainty.UncertaintyGame;
import com.uncertainty.components.PositionComponent;
import com.uncertainty.components.SelectableComponent;
import com.uncertainty.components.TypeComponent;
import com.uncertainty.components.VelocityComponent;

public class SelectionSystem extends IteratingSystem {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<SelectableComponent> sm = ComponentMapper.getFor(SelectableComponent.class);
    private ComponentMapper<TypeComponent> tm = ComponentMapper.getFor(TypeComponent.class);

    public SelectionSystem() {
        super(Family.all(
                PositionComponent.class,
                VelocityComponent.class,
                SelectableComponent.class,
                TypeComponent.class
        ).get());
    }



    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        PositionComponent positionComponent = pm.get(entity);
        SelectableComponent selectableComponent = sm.get(entity);

        if(positionComponent.position.equals(UncertaintyGame.selectedCoordinates)){
            TypeComponent typeComponent = tm.get(entity);
            selectableComponent.isSelected = true;
            System.out.println("Selected " + typeComponent.type + "@(" + positionComponent.position.x + "," + positionComponent.position.y + ")");
        }

    }


}
