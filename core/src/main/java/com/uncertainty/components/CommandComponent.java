package com.uncertainty.components;

import com.badlogic.ashley.core.Component;
import com.uncertainty.model.UnitCommand;

public class CommandComponent implements Component {
    public UnitCommand command;

    public CommandComponent(UnitCommand command){
        this.command = command;
    }
}
