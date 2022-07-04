package com.uncertainty.world;

import java.util.ArrayList;
import java.util.List;

public class Polygon {

    int centerX = -1,centerY = -1;
    public List<Pixel> pixels = new ArrayList<>();

    public int getCenterX() {
        if(centerX == -1){
            this.centerX = (int)Math.floor(pixels.stream().mapToInt(pixel-> pixel.x).average().getAsDouble());
        }
        return centerX;
    }

    public int getCenterY(){
        if(centerY == -1){
            this.centerY = (int)Math.floor(pixels.stream().mapToInt(pixel-> pixel.y).average().getAsDouble());
        }
        return centerY;
    }

    public String toString(){
        return "Polygon " + pixels.get(0).n + " (" + pixels.size() + ")";
    }
}
