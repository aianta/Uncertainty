package com.uncertainty.world;

import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class World {

    public static final int DEPTH = 1;
    public static final int WIDTH = 16;
    public static final int LENGTH = 100;

    //Define a world with a depth of 20, 16 rows, and 100 columns
    BlockType [][][] space = new BlockType [DEPTH][WIDTH][LENGTH];

    Random random = new Random();

    public World(){
        //Top & bottom level should be flat ground
        for(int row = 0; row < WIDTH; row++){
            for(int col = 0; col < LENGTH; col++){
                space[DEPTH-1][row][col] = BlockType.DIRT;
                space[0][row][col] = BlockType.DIRT;
            }
        }

        //Lower levels can be random for now
        for(int depth = DEPTH-2; depth >= 1; depth--){
            for(int row = 0; row < WIDTH; row++){
                for(int col = 0; col < LENGTH; col++){
                    if(random.nextBoolean()){
                        space[depth][row][col] = BlockType.DIRT;
                    }else {
                        space[depth][row][col] = BlockType.EMPTY;
                    }
                }
            }
        }
    }

    public BlockType get(int depth, int row, int col){
        return space[depth][row][col];
    }

    /**
     * Returns true if coordinate is inside the world grid dimensions.
     * @param coordinate coordinate to test
     * @return true if valid
     */
    public boolean isValidWorldCoordinate(Vector3 coordinate){
        return (
            coordinate.x >= 0 && coordinate.x < WIDTH &&
            coordinate.y >= 0 && coordinate.y < LENGTH
        )?true:false;
    }

}
