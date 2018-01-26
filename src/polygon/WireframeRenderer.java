package polygon;

import geometry.Vertex3D;
import line.ColoredDDA;
import line.LineRenderer;
import windowing.drawable.Drawable;

/**
 * Created by Leo on 10/8/2017.
 */
public class WireframeRenderer implements PolygonRenderer {
    private LineRenderer renderer = ColoredDDA.make();

    private WireframeRenderer() {}
    public static PolygonRenderer make() {return new WireframeRenderer();}

    @Override
    public void drawPolygon(Polygon polygon, Drawable panel, Shader vertexShader) {
        Vertex3D p1 = polygon.get(0);
        Vertex3D p2 = polygon.get(1);
        Vertex3D p3 = polygon.get(2);

        renderer.drawLine(p1, p2, panel);
        renderer.drawLine(p2, p3, panel);
        renderer.drawLine(p3, p1, panel);
    }
}
