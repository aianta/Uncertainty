package com.uncertainty.model;

import com.badlogic.gdx.math.Vector3;

public class MoveCommand extends UnitCommand{

    public Vector3 targetPosition;

    public MoveCommand(Vector3 position){
        this.targetPosition = position;
    }

}
