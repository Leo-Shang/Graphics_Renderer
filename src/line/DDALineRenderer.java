package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;

//import static java.lang.Math.round;

/**
 * Created by Leo on 9/20/2017.
 */
public class DDALineRenderer implements LineRenderer {
    private DDALineRenderer() {}

    public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable){
        double deltaX = p2.getIntX() - p1.getIntX();
        double deltaY = p2.getIntY() - p1.getIntY();

        //double slope = (p2.getIntY() - p1.getIntY()) / (p2.getIntX() - p1.getIntX());
        double slope = deltaY / deltaX;
        int argbColor = p1.getColor().asARGB();
        double y = p1.getIntY();
        int x = p1.getIntX();
        while(x <= p2.getIntX()){
            drawable.setPixel(x, (int)Math.round(y), 0.0, argbColor);
            x++;
            y += slope;
        }
    }

    public static LineRenderer make() {
        return new AnyOctantLineRenderer(new DDALineRenderer());
    }
}
