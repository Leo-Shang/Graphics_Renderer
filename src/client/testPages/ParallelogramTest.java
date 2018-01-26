package client.testPages;

import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

/**
 * Created by Leo on 9/20/2017.
 */
public class ParallelogramTest {

    private final LineRenderer renderer;
    private final Drawable panel;


    public ParallelogramTest(Drawable panel, LineRenderer renderer) {
        this.panel = panel;
        this.renderer = renderer;

        render();
    }

    private void render() {
        for(int p = 0; p <= 50; p++){
            Vertex3D ini_point_1 = new Vertex3D(20, 80+p, 0.0, Color.WHITE);
            Vertex3D end_point_1 = new Vertex3D(150, 150 + p, 0.0, Color.WHITE);
            Vertex3D ini_point_2 = new Vertex3D(160 + p, 270, 0.0, Color.WHITE);
            Vertex3D end_point_2 = new Vertex3D(240 + p, 40, 0.0, Color.WHITE);
            renderer.drawLine(ini_point_1, end_point_1, panel);
            renderer.drawLine(ini_point_2, end_point_2, panel);
        }
    }

}
