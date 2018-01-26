package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;

/**
 * Created by Leo on 9/20/2017.
 */
public class BresenhamLineRenderer implements LineRenderer {
    private BresenhamLineRenderer() {}

    public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable){
        int x0=p1.getIntX();
        int y0=p1.getIntY();
        int x1=p2.getIntX();
        int y1=p2.getIntY();
        int argbColor = p1.getColor().asARGB();

        int m_num = 2*(y1 - y0);
        int x = p1.getIntX();
        int y = p1.getIntY();
        int err = 2*(y1 - y0) - (x1 - x0);
        int k=2*(y1 - y0) - 2*(x1 - x0);

        while(x <= p2.getIntX()){
            drawable.setPixel(x, y, 0.0, argbColor);
            x++;
            if(err >= 0){
                err += k;
                y++;
            }
            else
                err += m_num;
        }
    }

    public static LineRenderer make() {
        return new AnyOctantLineRenderer(new BresenhamLineRenderer());
    }
}
