package client.testPages;

import geometry.Vertex;
import geometry.Vertex3D;
import line.LineRenderer;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.Random;

/**
 * Created by Leo on 2017-09-23.
 */
public class RandomPolygonTest {
    private final PolygonRenderer renderer;
    private final Drawable panel;
    private static Vertex3D[] p1 = new Vertex3D[20];
    private static Vertex3D[] p2 = new Vertex3D[20];
    private static Vertex3D[] p3 = new Vertex3D[20];
    private static int counter = 0;

    public RandomPolygonTest(Drawable panel, PolygonRenderer renderer) {
        this.panel = panel;
        this.renderer = renderer;

        render();
    }

    private void render() {
        if(counter % 2 == 0){
            generatePoints();
        }
        for(int count = 0; count < 20; count++) {
            Polygon tri = Polygon.makeEnsuringClockwise(p1[count], p2[count], p3[count]);
            renderer.drawPolygon(tri, panel);
        }
        counter++;
    }

    private void generatePoints() {
        for(int count = 0; count < 20; count++){
            int p1_x = generate_random();
            int p1_y = generate_random();
            int p2_x = generate_random();
            int p2_y = generate_random();
            int p3_x = generate_random();
            int p3_y = generate_random();
            Color c = generate_random_color();
            p1[count] = new Vertex3D(p1_x, p1_y, 0.0, c);
            p2[count] = new Vertex3D(p2_x, p2_y, 0.0, c);
            p3[count] = new Vertex3D(p3_x, p3_y, 0.0, c);
        }
    }

    private int generate_random() {
        Random rand = new Random();
        return rand.nextInt(299);
    }

    private Color generate_random_color() {
        //Random rand = new Math.Random();
        double r = Math.random();
        double g = Math.random();
        double b = Math.random();
        return new Color(r,g,b);
    }
}
