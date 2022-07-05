package com.uncertainty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class IsometricRenderer {

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final int MAP_LENGTH = 100;

    private Texture dirtBlock;
    private Random random = new Random();
    private int [] [] topLayer = new int[16][MAP_LENGTH+1];

    public IsometricRenderer(){
        dirtBlock = new Texture(Gdx.files.internal("dirt-cube.png"));

        for (int i =  0; i <  16; i++){
            for(int j = 0; j < MAP_LENGTH+1; j++){
                if(random.nextBoolean()){
                    topLayer[i][j] = 1;
                }else{
                    topLayer[i][j] = 0;
                }
            }
        }
    }

    public void drawLayer(SpriteBatch batch, int layer){
        if(layer == 0){
            drawGround(batch);
            return;
        }

        for (int row = 15; row >= 0; row--){
            for(int col = MAP_LENGTH; col >= 0; col--){
                float x = (col - row) * (TILE_WIDTH/2f);
                float y = (col + row) * (TILE_HEIGHT/4f)+(TILE_HEIGHT*layer/2f);

                if (topLayer[row][col] == 1){
                    batch.draw(dirtBlock, x,y,TILE_WIDTH,TILE_HEIGHT);
                }
            }
        }
    }

    public void drawGround(SpriteBatch batch){
        for (int row = 15; row >= 0; row--){
            for(int col = MAP_LENGTH; col >= 0; col--){
                float x = (col - row) * (TILE_WIDTH/2f);
                float y = (col + row) * (TILE_HEIGHT/ 4f);

                batch.draw(dirtBlock, x,y,TILE_WIDTH,TILE_HEIGHT);
            }
        }
    }

}
