package com.uncertainty.components;

import com.badlogic.ashley.core.Component;

public class TypeComponent implements Component {
    public String type;

    public TypeComponent(String name){
        this.type = name;
    }
}
