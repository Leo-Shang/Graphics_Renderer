package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

/**
 * Created by Leo on 10/8/2017.
 */
public class ColoredDDA implements LineRenderer {
    private ColoredDDA() {}

    @Override
    public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable){
        double deltaX = p2.getIntX() - p1.getIntX();
        double deltaY = p2.getIntY() - p1.getIntY();
        double deltaR = p2.getColor().getIntR() - p1.getColor().getIntR();
        double deltaG = p2.getColor().getIntG() - p1.getColor().getIntG();
        double deltaB = p2.getColor().getIntB() - p1.getColor().getIntB();

        double slope = deltaY / deltaX;
        double R_slope = deltaR / deltaX;
        double G_slope = deltaG / deltaX;
        double B_slope = deltaB / deltaX;

        double y = p1.getIntY();
        int x = p1.getIntX();
        double r = p1.getColor().getIntR();
        double g = p1.getColor().getIntG();
        double b = p1.getColor().getIntB();

        while(x <= p2.getIntX()){
            drawable.setPixel(x, (int)Math.round(y), 0.0, Color.makeARGB((int)Math.round(r),(int)Math.round(g),(int)Math.round(b)));
            x++;
            y += slope;
            r += R_slope;
            g += G_slope;
            b += B_slope;
        }
    }

    public static LineRenderer make() {
        return new AnyOctantLineRenderer(new ColoredDDA());
    }
}
