package com.uncertainty.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VCell {

    static List<VCell> allCells = new ArrayList<>();

    int x,y;
    List<VCell> neighbors = new ArrayList<>();
    Map<VCell,Double> distances = new HashMap<>();

    public VCell(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean equals(Object o){
        return o instanceof VCell && ((VCell)o).x == this.x && ((VCell)o).y == this.y;
    }

    public static void computeNeighbors(List<VCell> allCells){

        for (int i = 0; i < allCells.size(); i++){

            VCell curr = allCells.get(i);
            for (int j = 0; j < allCells.size(); j++){
                VCell candidate = allCells.get(j);

                //Skip yourself, you're not your own neighbor
                if (curr.equals(candidate)){
                    continue;
                }

                double distanceToCandidate = VoronoiGenerator.distance(curr, candidate);
                curr.distances.put(candidate, distanceToCandidate);
            }


        }

    }
}
