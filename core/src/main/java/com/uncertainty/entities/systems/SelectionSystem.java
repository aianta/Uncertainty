package com.uncertainty.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.uncertainty.UncertaintyGame;
import com.uncertainty.components.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SelectionSystem extends IteratingSystem{

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<SelectableComponent> sm = ComponentMapper.getFor(SelectableComponent.class);
    private ComponentMapper<TypeComponent> tm = ComponentMapper.getFor(TypeComponent.class);
    private ComponentMapper<SelectComponent> selectMapper = ComponentMapper.getFor(SelectComponent.class);

    public SelectionSystem() {
        super(Family.all(SelectComponent.class).get());
    }




    @Override
    protected void processEntity(Entity selectEntity, float deltaTime) {
        //Get all selectable entities
        ImmutableArray<Entity> selectableEntities = getEngine().getEntitiesFor(Family.all(
                PositionComponent.class,
                VelocityComponent.class,
                SelectableComponent.class,
                TypeComponent.class
        ).get());

        /**
         * For this select entity, go through all selectable entities
         * and see if it selects one. If it doesn't, remove it.
         */
        for (Entity selectable: selectableEntities){
            PositionComponent positionComponent = pm.get(selectable);
            SelectableComponent selectableComponent = sm.get(selectable);
            System.out.println("Selectable position:" + positionComponent.position + " select position: "+ pm.get(selectEntity).position + " " + positionComponent.position.epsilonEquals(pm.get(selectEntity).position));
            if(positionComponent.position.epsilonEquals(pm.get(selectEntity).position)){
                TypeComponent typeComponent = tm.get(selectable);
                selectableComponent.isSelected = true;
                selectable.add(new SelectedComponent());
                System.out.println("Selected " + typeComponent.type + "@(" + positionComponent.position.x + "," + positionComponent.position.y + "," +positionComponent.position.z+ ")");


                getEngine().removeEntity(selectEntity);
                return;
            }
        }

        //If we get here the select entity couldn't select any selectable entity.
        getEngine().removeEntity(selectEntity);

        //De-select any previously selected entities
        ImmutableArray<Entity> selectedEntities = getEngine().getEntitiesFor(Family.all(SelectedComponent.class).get());
        for (Entity selected: selectedEntities){
            selected.remove(SelectedComponent.class);
        }
    }



}
