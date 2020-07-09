package com.uncertainty.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chunk {

    public static final Random RANDOM = new Random();
    private double undergroundChance = 0.8;
    private double abovegroundChance = 0.0;

    int width;
    int height;
    int depth;

    List<BlockType [][]> chunk;
    int groundLevel;

    public Chunk(int width, int height, int depth){
        //Init chunk
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.chunk = new ArrayList<>(depth);

        //Pick a ground level for this chunk
        groundLevel = pickGroundLevel();

        //Build the layers of the chunk
        for(int i = 0; i < depth; i++){
            chunk.add( createLayer(i));
        }
    }

    private int pickGroundLevel(){
        return RANDOM.ints(1,0,depth).sum();
    }

    private BlockType [][] createLayer(int depth){
        BlockType [][] layer = new BlockType [height][width];
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                layer[y][x] = BlockType.EMPTY; //All empty by default;


                //If we're underground and we roll less than the underground chance
                if(depth < groundLevel && RANDOM.nextDouble() < undergroundChance){
                    layer[y][x] = BlockType.DIRT; //Place  a block
                    continue;
                }

                if(depth == groundLevel){
                    layer[y][x] = BlockType.GRASS;
                }

                //If we're aboveground and we roll less than the aboveground chance
                if(depth > groundLevel && RANDOM.nextDouble() < abovegroundChance){
                    layer[y][x] = BlockType.DIRT; //Place a block
                    continue;
                }
            }
        }
        return layer;
    }

    public BlockType [][] getLayer(int depth){
        return chunk.get(depth);
    }
}
