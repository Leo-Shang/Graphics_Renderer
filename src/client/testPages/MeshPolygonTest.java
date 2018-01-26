package client.testPages;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import javax.swing.*;
import java.util.Random;

/**
 * Created by Leo on 2017-09-23.
 */
public class MeshPolygonTest {
    private static final int MARGIN = 55;
    private static final int NUM_DOTS = 100;

    public static final int NO_PERTURBATION = 0;
    public static final int USE_PERTURBATION = 1;

    private static Vertex3D[] points_shifted = new Vertex3D[NUM_DOTS];
    private static Vertex3D[] points = new Vertex3D[NUM_DOTS];
    private static int counter = 0;

    public MeshPolygonTest(Drawable panel, PolygonRenderer renderer, int n){
        int width = computeWidth();
        if(n == 0){
            generatePoints(width);
            for(int i = 0; i <= 88; i++){
                if ((i + 1) % 10 != 0) {
                    Polygon tri = Polygon.make(points[i], points[i + 1], points[i + 11]); // lower triangle
                    renderer.drawPolygon(tri, panel);
                }
            }
            for(int i = 10; i <= 98; i++) {
                if ((i + 1) % 10 != 0) {
                    Polygon tri = Polygon.make(points[i], points[i + 1], points[i - 10]); // upper triangle
                    renderer.drawPolygon(tri, panel);
                }
            }
        }
        else if(n == 1){
            if(counter % 2 == 0) {
                genereatePointWithShift(width);
//                for(int i = 0; i < NUM_DOTS; i++){
//                    System.out.println("PRINTING:  "+ points_shifted[i]);
//                }
            }
            for(int i = 0; i <= 88; i++){
                if ((i + 1) % 10 != 0) {
                    Polygon tri = Polygon.make(points_shifted[i], points_shifted[i + 1], points_shifted[i + 11]); // lower triangle
                    renderer.drawPolygon(tri, panel);
                }
            }
            for(int i = 11; i <= 99; i++) {
                if (i % 10 != 0) {
                    Polygon tri = Polygon.make(points_shifted[i], points_shifted[i - 1], points_shifted[i - 11]); // upper triangle
                    renderer.drawPolygon(tri, panel);
                }
            }
            counter++;
        }
    }

    private void genereatePointWithShift(int width) { // generate point for 3rd panel
        int x = MARGIN;
        int y = MARGIN;
        points_shifted[0] = new Vertex3D(MARGIN, MARGIN, 0.0, Color.random());
        for(int count = 1; count < NUM_DOTS; count++){
            if(count % 10 == 0){
                y += width;
                x = MARGIN;
                points_shifted[count] = new Vertex3D(x + shifted(), y + shifted(), 0.0, Color.random());
            }
            else{
                x += width;
                points_shifted[count] = new Vertex3D(x + shifted(), y + shifted(), 0.0, Color.random());
            }
        }
    }


    private void generatePoints(int width) { // generate points for 2nd panel
        int x = MARGIN;
        int y = MARGIN;
        points[0] = new Vertex3D(MARGIN, MARGIN, 0.0, Color.random());
        for(int count = 1; count < NUM_DOTS; count++){
            if(count % 10 == 0){
                y += width;
                x = MARGIN;
                points[count] = new Vertex3D(x, y, 0.0, Color.random());
            }
            else{
                x += width;
                points[count] = new Vertex3D(x, y, 0.0, Color.random());
            }
        }
    }

    private int computeWidth(){
        return (650 - 2*MARGIN) / (NUM_DOTS / 10 - 1);
    }

    private int shifted() {
        Random rand = new Random();
        return rand.nextInt(25) - 12;
    }
}