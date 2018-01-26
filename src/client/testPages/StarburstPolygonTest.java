package client.testPages;

import geometry.Vertex;
import geometry.Vertex3D;
import line.LineRenderer;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

/**
 * Created by Leo on 9/22/2017.
 */
public class StarburstPolygonTest {
    private static final int NUM_RAYS = 90;
    private static final double FRACTION_OF_PANEL_FOR_DRAWING = 0.9;

    private final PolygonRenderer renderer;
    private final Drawable panel;
    Vertex3D center;

    public StarburstPolygonTest(Drawable panel, PolygonRenderer renderer) {
        this.panel = panel;
        this.renderer = renderer;

        makeCenter();
        render();
    }

    private void render() {
//        //TESTING
//        System.out.println("TESTING");
//            Vertex3D p1 = new Vertex3D(150.0, 150.0, 0.0, Color.random());
//            Vertex3D p2 = new Vertex3D(150, 284, 0.0, Color.random());
//            Vertex3D p3 = new Vertex3D(135, 284, 0.0, Color.random());
//            Polygon tri = Polygon.make(p1, p2, p3);
//            renderer.drawPolygon(tri, panel);
//        System.out.println("TESTING ENDS");
//        //TESTING
        double radius = computeRadius();
        double angleDifference = (2.0 * Math.PI) / NUM_RAYS;
        double angle = 0.0;

        for(int ray = 0; ray < NUM_RAYS; ray++) {
            Vertex3D initialVertex = radialPoint(radius, angle);
            Vertex3D secondVertex = radialPoint(radius, angle + angleDifference);
            center.replaceColor(initialVertex.getColor());
            renderer.drawPolygon(Polygon.make(initialVertex, secondVertex, center), panel);
            angle = angle + angleDifference;
        }
    }

    private void makeCenter() {
        int centerX = panel.getWidth() / 2;
        int centerY = panel.getHeight() / 2;
        center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
    }

    private Vertex3D radialPoint(double radius, double angle) {
        double x = center.getX() + radius * Math.cos(angle);
        double y = center.getY() + radius * Math.sin(angle);
        return new Vertex3D(x, y, 0, Color.WHITE);
    }

    private double computeRadius() {
        int width = panel.getWidth();
        int height = panel.getHeight();

        int minDimension = width < height ? width : height;

        return (minDimension / 2.0) * FRACTION_OF_PANEL_FOR_DRAWING;
    }
}
