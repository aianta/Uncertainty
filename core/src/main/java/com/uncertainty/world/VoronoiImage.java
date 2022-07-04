package com.uncertainty.world;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static com.uncertainty.world.VoronoiGenerator.distance;

public class VoronoiImage extends JFrame {
    static int MAX_ITERATIONS = 20;

    BufferedImage image;
    int px[],py[],color[], cells = 100, size = 1000;
    Polygon [] polygons = new Polygon[cells];

    public VoronoiImage(Polygon [] inputPolygons, int iteration, int [] color){
        super("Voronoi Relaxation " + iteration);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(0,0,size,size);

        int n = 0;
        image = new BufferedImage(size,size, BufferedImage.TYPE_INT_RGB);
        px = new int[inputPolygons.length];
        py = new int[inputPolygons.length];
        this.color = color;

        //Pick cells from polygon centers
        for(int i = 0; i < inputPolygons.length; i++){
            px[i] = inputPolygons[i].getCenterX();
            py[i] = inputPolygons[i].getCenterY();
        }

        //For every point in the image
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                n = 0;

                //Find the cell nearest to this point by going through the cells
                for (int i = 0; i < cells; i++){
                    //If the distance to cell i is less than the distance to cell n
                    if(distance(px[i], x, py[i],y) < distance(px[n], x, py[n], y)){
                        //make cell n = to cell i
                        n = i;

                    }
                }

                //Set the color of the point to cell n's color
                image.setRGB(x,y,color[n]);
                Pixel pixel = new Pixel();
                pixel.x = x;
                pixel.y = y;
                pixel.n = n;
                if(this.polygons[n] == null){
                    this.polygons[n] = new Polygon();
                }
                this.polygons[n].pixels.add(pixel);
            }
        }

        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        for (int i = 0; i < cells; i++){
            g.fill(new Ellipse2D.Double(px[i]-2.5,py[i] - 2.5,5,5));
        }
        try{
            ImageIO.write(image, "png", new File("voronoi-relaxation-"+iteration+".png"));
        }catch (IOException e){

        }

        printPolygons(polygons);
    }

    public VoronoiImage(){
        super("VoronoiTest");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(0,0,size,size);

        int n = 0;
        Random rand = new Random();
        image = new BufferedImage(size,size, BufferedImage.TYPE_INT_RGB);
        px = new int[cells];
        py = new int[cells];
        color = new int[cells];

        //Pick random x,y and color values for the cells
        for (int i = 0 ; i < cells; i++){
            px[i] = rand.nextInt(size);
            py[i] = rand.nextInt(size);
            VCell.allCells.add(new VCell(px[i],py[i]));
            color[i] = rand.nextInt(16777215);
        }

        //For every point in the image
        for (int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                n = 0;

                //Find the cell nearest to this point by going through the cells
                for (int i = 0; i < cells; i++){
                    //If the distance to cell i is less than the distance to cell n
                    if(distance(px[i], x, py[i],y) < distance(px[n], x, py[n], y)){
                        //make cell n = to cell i
                        n = i;

                    }
                }
                //Set the color of the point to cell n's color
                image.setRGB(x,y,color[n]);
                Pixel pixel = new Pixel();
                pixel.x = x;
                pixel.y = y;
                pixel.n = n;
                if(polygons[n] == null){
                    polygons[n] = new Polygon();
                }
                polygons[n].pixels.add(pixel);
            }
        }

        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        for (int i = 0; i < cells; i++){
            g.fill(new Ellipse2D.Double(px[i]-2.5,py[i] - 2.5,5,5));
        }
        try{
            ImageIO.write(image, "png", new File("voronoi-test.png"));
        }catch (IOException e){

        }

        printPolygons(polygons);

    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image,0,0,this);
    }

    public static void main(String args[]){
        //Generate the initial image
        VoronoiImage initialImage = new VoronoiImage();
        initialImage.setVisible(true);

        //Compute iterations of Lloyd's relaxations
        int iteration = 1;

        Polygon[] polygons = initialImage.getPolygons();
        int [] colors = initialImage.getColors();
        while (iteration < MAX_ITERATIONS + 1){
            VoronoiImage relaxation = new VoronoiImage(
                    polygons,
                    iteration,
                    colors
            );
            if (iteration % 3 == 0){
                relaxation.setVisible(true);
            }
            polygons = relaxation.getPolygons();
            iteration++;
        }

    }

    void printPolygons(Polygon[] polygons){
        for(Polygon polygon:polygons){
            System.out.println("Polygon " + polygon.pixels.get(0).n + " (" + polygon.pixels.size() + ")" );
            StringBuilder sb = new StringBuilder();
            Iterator<Pixel> it = polygon.pixels.iterator();
            while (it.hasNext()){
                Pixel pixel = it.next();
                sb.append("(" + pixel.x + "," + pixel.y + ")");
                if(it.hasNext()){
                    sb.append(",");
                }
            }
            //System.out.println(sb.toString());
        }
    }

    Polygon[] getPolygons(){
        return polygons;
    }

    int [] getColors(){
        return color;
    }
}
