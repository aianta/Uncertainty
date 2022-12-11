package com.uncertainty.components;

import com.badlogic.ashley.core.Component;

public class SizeComponent implements Component {
    public int width;
    public int height;

    public SizeComponent(int width, int height){
        this.width = width;
        this.height = height;
    }
}
