package client.testPages;

import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import java.util.Random;

/**
 * Created by Leo on 9/20/2017.
 */
public class RandomLineTest {
    private final LineRenderer renderer;
    private final Drawable panel;
    private static int number = 0;

    private static double[] ini_x_values = new double [30];
    private static double[] ini_y_values = new double [30];
    private static double[] final_x_values = new double [30];
    private static double[] final_y_values = new double [30];
    private static Color[] random_colors = new Color[30];

    public RandomLineTest(Drawable panel, LineRenderer renderer) {
        this.panel = panel;
        this.renderer = renderer;
        if(number % 4 == 0){
            generate();
        }
        number++;
        render();
    }

    private void generate() {
        double[] x1_values = new double [30];
        double[] y1_values = new double [30];
        double[] x2_values = new double [30];
        double[] y2_values = new double [30];
        Color[] rand_colors = new Color[30];
        for(int count = 0; count < 30; count++){
            x1_values[count] = generate_random();
            y1_values[count] = generate_random();
            x2_values[count] = generate_random();
            y2_values[count] = generate_random();
            rand_colors[count] = generate_random_color();
        }
        for(int count = 0; count < 30; count++){
            ini_x_values[count] = x1_values[count];
            ini_y_values[count] = y1_values[count];
            final_x_values[count] = x2_values[count];
            final_y_values[count] = y2_values[count];
            random_colors[count] = rand_colors[count];
        }
    }

    private void render() {
        for(int count = 0; count < 30; count++) {
            Vertex3D p1 = new Vertex3D(ini_x_values[count], ini_y_values[count], 0.0, random_colors[count]);
            Vertex3D p2 = new Vertex3D(final_x_values[count], final_y_values[count], 0.0, random_colors[count]);
            renderer.drawLine(p1, p2, panel);
        }
    }

    private double generate_random() {
        Random rand = new Random();
        return rand.nextInt(299);
    }

    private Color generate_random_color() {
        double r = Math.random();
        double g = Math.random();
        double b = Math.random();
        return new Color(r,g,b);
    }

}
