package com.uncertainty.components;

import com.badlogic.ashley.core.Component;
import com.uncertainty.model.UnitCommand;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The orders component stores information about UnitCommands given to an entity.
 *
 * IE: move orders
 */
public class OrdersComponent implements Component {

    public Queue<UnitCommand> commandQueue = new LinkedList<>();

}
