package client.testPages;

import geometry.Vertex3D;
import polygon.ColoredpolygonRenderer;
import polygon.ColoredpolygonRendererNoZBuff;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.Random;

/**
 * Created by Leo on 10/8/2017.
 */
public class CenteredTriangleTest {
    Vertex3D center;
    private final int RADIUS = 275;
    private final double[] v = {1, 0.85, 0.7, 0.55, 0.4, 0.25};
    private Drawable panel;
    private PolygonRenderer polygonRenderer;
    private Vertex3D[] points = new Vertex3D[3];
    private int v_counter = 0;

    public CenteredTriangleTest(Drawable panel, PolygonRenderer polygonRenderer){
        this.panel = panel;
        this.polygonRenderer = ColoredpolygonRendererNoZBuff.make();
        makeCenter();

        for(int i = 0 ; i < 6 ; i++){
            generatePoints();
            render();
        }
    }

    private void generatePoints() {
        double z = getRandomZ();
        Random rand = new Random();
        double angle = Math.toRadians(rand.nextInt(120));
        //System.out.println(angle);
        for(int i = 0 ; i < 3; i++){
            double x = center.getX() + RADIUS *  Math.cos(angle);
            double y = center.getY() + RADIUS *  Math.sin(angle);
            points[i] = new Vertex3D(x, y, z, Color.fromARGB(Color.makeARGB((int)Math.round(v[v_counter]*255),(int)Math.round(v[v_counter]*255),
                    (int)Math.round(v[v_counter]*255))));
            angle += Math.toRadians(120);
        }
//        System.out.println(points[0]);
//        System.out.println(points[1]);
//        System.out.println(points[2]);
        v_counter++;
    }

    private void render() {
        Polygon tri = Polygon.makeEnsuringClockwise(points[0], points[1], points[2]);
        polygonRenderer.drawPolygon(tri, panel);
    }

    private int getRandomZ(){
        Random rand = new Random();
        return rand.nextInt(199);
    }

    private void makeCenter() {
        int centerX = panel.getWidth() / 2;
        int centerY = panel.getHeight() / 2;
        center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
    }
}
