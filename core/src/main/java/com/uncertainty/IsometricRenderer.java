package com.uncertainty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.uncertainty.world.BlockType;
import com.uncertainty.world.World;

import java.util.Random;

public class IsometricRenderer {

    public static final int TEXTURE_WIDTH = 32;
    public static final int TEXTURE_HEIGHT = 32;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 16;

    public static final int MAP_LENGTH = 100;

    private static int [] gridSelection = new int[2];

    private Texture dirtBlock;
    private Texture grassTile;
    private Texture selectedTile;
    private Texture isoHelper;

    private Random random = new Random();
    private int [] [] topLayer = new int[16][MAP_LENGTH+1];


    Vector3 vOrigin = new Vector3(World.LENGTH ,0,0);

    public IsometricRenderer(){
        dirtBlock = new Texture(Gdx.files.internal("dirt-cube.png"));
        grassTile = new Texture(Gdx.files.internal("grass-tile.png"));
        selectedTile = new Texture(Gdx.files.internal("selected-tile.png"));
        isoHelper = new Texture(Gdx.files.internal("iso-helper.png"));

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

    public int [] getGridSelection(){
        return gridSelection;
    }

    public void drawWorld(SpriteBatch batch, World w, int currDepth){


        //Prevent invalid depths
        if(currDepth > World.DEPTH-1){
            currDepth = World.DEPTH-1;
        }

        if(currDepth < 0){
            currDepth = 0;
        }

        for (int depth = 0; depth <= currDepth; depth++){
//            for(int col = 0; col < World.LENGTH; col++){
//            for(int row = 0; row < World.WIDTH; row++){

            for(int row = World.WIDTH-1; row >= 0; row--){
                for(int col = World.LENGTH-1; col >= 0; col--){
                    BlockType blockType = w.get(depth, row, col);

                    Vector3 xy = new Vector3(row, col,0);

                    Vector3 iso = xy;
                    float x = (vOrigin.x * TILE_WIDTH) +(iso.x - iso.y)*(TILE_WIDTH/2) ;
                    float y = (vOrigin.y * TILE_HEIGHT)+(iso.x + iso.y)*(TILE_HEIGHT/2) + (TILE_HEIGHT*depth);



                    switch (blockType){
                        case EMPTY: break;
                        case DIRT: batch.draw(dirtBlock, x,y,TEXTURE_WIDTH,TEXTURE_HEIGHT);
                    }

                }
            }
        }
    }


    public Vector3 drawSelection(SpriteBatch batch, Vector3 selection, Vector3 offset){
        Color color = getColorAtPixel((int)offset.x,(int)offset.y, isoHelper);
        //System.out.println("Color: (r:"+ color.r + " g:" + color.g + " b:" + color.b +")");
        Vector3 selectedXY = new Vector3(selection.x, selection.y, 0);
        if(color.equals(Color.RED)){
            selectedXY.add(new Vector3(-1,0,0));
        }
        if(color.equals(Color.GREEN)){
            selectedXY.add(new Vector3(0,1,0));
        }
        if(color.equals(Color.BLUE)){
            selectedXY.add(new Vector3(1,0,0));
        }
        if(color.equals(Color.YELLOW)){
            selectedXY.add(new Vector3(0,-1,0));
        }
        float x = (vOrigin.x * TILE_WIDTH) +(selectedXY.x - selectedXY.y)*(TILE_WIDTH/2) ;
        float y = (vOrigin.y * TILE_HEIGHT)+(selectedXY.x + selectedXY.y)*(TILE_HEIGHT/2);
        batch.draw(selectedTile, x,y+16, 32, 16);
        return selectedXY;
    }

    private Color getColorAtPixel(int x, int y, Texture texture){
        if(!texture.getTextureData().isPrepared()){
            texture.getTextureData().prepare();
        }
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        return new Color(pixmap.getPixel(x,y));
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
                float y = (col + row) * (TILE_HEIGHT/4f);

                batch.draw(dirtBlock, x,y,TEXTURE_WIDTH,TEXTURE_HEIGHT);
            }
        }
    }

}
