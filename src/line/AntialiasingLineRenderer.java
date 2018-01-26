package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.math.BigDecimal;

/**
 * Created by Leo on 9/20/2017.
 */
public class AntialiasingLineRenderer implements LineRenderer{
    private static final double RADIUS = 0.5;
    private static final double DIAMETER = 1;
    private static final double PI = Math.PI;
    private static final double AREA = PI * RADIUS * RADIUS;

    private AntialiasingLineRenderer() {}

    @Override
    public void drawLine(Vertex3D p1, Vertex3D p2, Drawable panel){
        for(int y = p1.getIntY() - 2; y < p2.getIntY() + 2; y++) {
            for(int x = p1.getIntX(); x < p2.getIntX(); x++) {
                Vertex3D pixel = new Vertex3D(x, y, 0.0, Color.BLACK);
                double d = getD(pixel, p1, p2);
                double coverage = getPercentage(d);
                Color oldColor = Color.fromARGB(panel.getPixel(x, y));
                Color newColor = p1.getColor().blendInto(coverage, oldColor);
                if(oldColor.asARGB() < newColor.asARGB()){
                    panel.setPixel(x, y, 0.0, newColor.asARGB());
                }
            }
        }
    }

    private double getPercentage(double d){
        double fraction;
        if(d >= DIAMETER){
            fraction = 0.0;
        }else if(d == 0){
            fraction = 1.0;
        }else if(d == RADIUS) {
            fraction = 0.5;
        }else if(d > RADIUS && d < DIAMETER) {
            d = d - 0.5;
            double angle = Math.acos(d / RADIUS);
            fraction = 1 - ((((1 - (angle / PI)) * AREA) + (d * Math.sqrt(Math.pow(RADIUS, 2) - Math.pow(d, 2)))) / AREA);
        }else if(d < RADIUS && d > 0){
            d = 0.5 - d;
            double angle = Math.acos(d / RADIUS);
            fraction = (((1 - (angle / PI)) * AREA) + (d * Math.sqrt(Math.pow(RADIUS, 2) - Math.pow(d, 2)))) / AREA;
        }else if(d == -0.5){
            fraction = 0.5;
        }else if(d > -0.5 && d < 0){
            d = 0.5 + d; // d negative here
            double angle = Math.acos(d / RADIUS);
            fraction = (((1 - (angle / PI)) * AREA) + (d * Math.sqrt(Math.pow(RADIUS, 2) - Math.pow(d, 2)))) / AREA;
        }else if(d < -0.5 && d > 1){
            d = Math.abs(d) - 0.5; // d negative here
            double angle = Math.acos(d / RADIUS);
            fraction = 1-(((1 - (angle / PI)) * AREA)+ (d * Math.sqrt(Math.pow(RADIUS, 2) - Math.pow(d, 2)))) / AREA;
        }else{
            fraction = 0;
        }
        return fraction;
    }

    private double getD(Vertex3D pixel, Vertex3D p1, Vertex3D p2){
        double pixel_x = pixel.getIntX();
        int p1_x = p1.getIntX();
        int p2_x = p2.getIntX();
        int p1_y = p1.getIntY();
        int p2_y = p2.getIntY();
        double delta_X = p2_x - p1_x;
        double delta_Y = p2_y - p1_y;
        double slope = delta_Y / delta_X;
        double angle = Math.atan(delta_Y / delta_X);
//        BigDecimal temp = new BigDecimal(angle);
//        int intAngle = temp.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        //System.out.println(Math.toDegrees(intAngle));
        double y = p1.getIntY();
        double x = p1_x;
        while(x <= pixel_x){
            x++;
            y += slope;
        }
        //double yDiff = pixel.getY() - y;
        double vertical_Y = Math.abs(pixel.getY() - y);
        return (Math.cos(angle) * vertical_Y);
    }

    public static LineRenderer make(){
        return new AnyOctantLineRenderer(new AntialiasingLineRenderer());
    }
}
