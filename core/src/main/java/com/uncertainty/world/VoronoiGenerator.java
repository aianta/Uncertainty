package com.uncertainty.world;

import java.awt.image.BufferedImage;
import java.util.Optional;

//https://rosettacode.org/wiki/Voronoi_diagram#Java
public class VoronoiGenerator {




    public VoronoiGenerator(){

    }

    static double distance(int x1, int x2, int y1, int y2){
        double d;
        d = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2) * (y1-y2)); //Euclidian
        return d;
    }

    static double distance(VCell c1, VCell c2){
        return distance(c1.x, c2.x, c1.y, c2.y);
    }



}
