package com.uncertainty.components;

import com.badlogic.ashley.core.Component;
import com.uncertainty.model.Orientation;

public class OrientationComponent implements Component {
    public Orientation orientation = Orientation.UP_RIGHT;

    public OrientationComponent(Orientation orientation){
        this.orientation = orientation;
    }
}
