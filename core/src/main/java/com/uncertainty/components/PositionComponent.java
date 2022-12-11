package com.uncertainty.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class PositionComponent implements Component {

    public Vector3 position = new Vector3(-1,-1,-1);

    public PositionComponent(Vector3 v){
        this.position = v;
    }
}
