package polygon;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

/**
 * Created by Leo on 9/23/2017.
 */
public class FilledPolygonRenderer implements PolygonRenderer{
    private FilledPolygonRenderer() {}

    public static PolygonRenderer make() {return new FilledPolygonRenderer();}

    @Override
    public void drawPolygon(Polygon polygon, Drawable panel, Shader vertexShader) {
        Chain left_chain = polygon.leftChain();
        if(left_chain.get(0).getIntY() != left_chain.get(1).getIntY()) {
            fillPolygon(polygon, panel);
        }else if(left_chain.get(0).getIntY() == left_chain.get(1).getIntY()) {
            x_same_triangle(polygon, panel);
        }
    }

    private void x_same_triangle(Polygon polygon, Drawable panel) {
        Chain left_chain = polygon.leftChain();
        Vertex3D p_left;
        Vertex3D p_right;
        Vertex3D p_bottom;

        if(polygon.get(0).getX() == polygon.get(1).getX() ||
                polygon.get(0).getX() == polygon.get(2).getX() ||
                polygon.get(1).getX() == polygon.get(2).getX()) {
            //System.out.println("RENDERING");
            p_left = left_chain.get(0);
            p_right = left_chain.get(1);
            p_bottom = left_chain.get(2);
        }
        else {
            //System.out.println("RENDERING");
            p_right = left_chain.get(0);
            p_left = left_chain.get(1);
            p_bottom = left_chain.get(2);
        }

        double x_left = p_left.getIntX();
        double x_right = p_right.getIntX();
        double x_bottom = p_bottom.getIntX();
        double y_top = p_left.getIntY();
        double y_bottom = p_bottom.getIntY();
        double delta_L = x_bottom - x_left;
        double delta_R = x_bottom - x_right;
        double delta_y = y_top - y_bottom;
        double slope_left = delta_L / delta_y;
        double slope_right = delta_R / delta_y;

        double x_L = x_left;
        double x_R = x_right;
        for(double y = y_top; y > y_bottom; y--) {
            for(double x = x_L; x < x_R; x++) {
                panel.setPixel((int)Math.round(x), (int)Math.round(y), 0, p_left.getColor().asARGB());
            }
            x_L += slope_left;
            x_R += slope_right;
        }
    }

    private void fillPolygon(Polygon polygon, Drawable panel) {
        Chain left_chain = polygon.leftChain();
        Chain right_chain = polygon.rightChain();
        int count_left = left_chain.length();
        if(count_left == 2) {
            Vertex3D p_top = left_chain.get(0);
            Vertex3D p_bottom = left_chain.get(1);
            Vertex3D p_middle = right_chain.get(1);

            int x_top = p_top.getIntX();
            int x_middle = p_middle.getIntX();
            int x_bottom = p_bottom.getIntX();
            int y_top = p_top.getIntY();
            int y_middle = p_middle.getIntY();
            int y_bottom = p_bottom.getIntY();
            double delta_L = p_bottom.getIntX() - p_top.getIntX();
            double delta_y = p_top.getIntY() - p_bottom.getIntY();
            double delta_x_ru = x_middle - x_top;
            double delta_y_ru = y_top - y_middle;
            double delta_x_rl = x_bottom - x_middle;
            double delta_y_rl = y_middle - y_bottom;
            double left_slope = delta_L / delta_y;
            double first_right_slope = delta_x_ru / delta_y_ru;
            double second_right_slope = delta_x_rl / delta_y_rl;
            render1stPolygon(x_top, y_top, y_middle, y_bottom, left_slope,
                    first_right_slope, second_right_slope, p_top, panel);
        }else if(count_left == 3){
            Vertex3D p_top = left_chain.get(0);
            Vertex3D p_middle = left_chain.get(1);
            Vertex3D p_bottom = right_chain.get(1);

            int x_top = p_top.getIntX();
            int x_middle = p_middle.getIntX();
            int x_bottom = p_bottom.getIntX();
            int y_top = p_top.getIntY();
            int y_middle = p_middle.getIntY();
            int y_bottom = p_bottom.getIntY();
            double delta_X = x_bottom - x_top;
            double delta_Y = y_top - y_bottom;
            double delta_x_lu = x_middle - x_top;
            double delta_y_lu = y_top - y_middle;
            double delta_x_ll = x_bottom - x_middle;
            double delta_y_ll = y_middle - y_bottom;
            double right_slope = delta_X / delta_Y;
            double first_left_slope = delta_x_lu / delta_y_lu;
            double second_left_slope = delta_x_ll / delta_y_ll;
            render2ndPolygon(x_top, y_top, y_middle, y_bottom, right_slope,
                    first_left_slope, second_left_slope, p_top, panel);
        }

    }

    private void render1stPolygon(int x_top, int y_top, int y_middle, int y_bottom, double left_slope,
                                   double upper_right_slope, double lower_right_slope, Vertex3D p_top, Drawable panel) {
        int color = Color.random().asARGB();
        double x = x_top;
        double x_left = x_top;
        double x_right = x_top;
        double y = y_top;

        for(; y > y_middle; y--) {
            for(; x < x_right ; x++) {
                panel.setPixel((int)Math.round(x), (int)Math.round(y), 0, p_top.getColor().asARGB());
            }
            x_left += left_slope;
            x_right += upper_right_slope;
            x = x_left;
        }
        for(; y > y_bottom; y--) {
            for(; x < x_right ; x++) {
                panel.setPixel((int)Math.round(x), (int)Math.round(y), 0, p_top.getColor().asARGB());
            }
            x_left += left_slope;
            x_right += lower_right_slope;
            x = x_left;
        }
    }

    private void render2ndPolygon(int x_top, int y_top, int y_middle, int y_bottom, double right_slope,
                                    double upper_left_slope, double lower_left_slope,Vertex3D p_top, Drawable panel) {
        int color = Color.random().asARGB();
        double x = x_top;
        double y = y_top;
        double x_left = x_top;
        double x_right = x_top;

        for(; y > y_middle; y--) {
            for(; x < x_right; x++) {
                panel.setPixel((int)Math.round(x), (int)Math.round(y), 0, p_top.getColor().asARGB());
            }
            x_left += upper_left_slope;
            x_right += right_slope;
            x = x_left;
        }
        for(; y > y_bottom; y--) {
            for(; x < x_right; x++) {
                panel.setPixel((int)Math.round(x), (int)Math.round(y), 0, p_top.getColor().asARGB());
            }
            x_left += lower_left_slope;
            x_right += right_slope;
            x = x_left;
        }
    }
}