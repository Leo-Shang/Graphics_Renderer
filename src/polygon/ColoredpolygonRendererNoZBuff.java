package polygon;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

/**
 * Created by Leo on 10/8/2017.
 */
public class ColoredpolygonRendererNoZBuff implements PolygonRenderer {
    private ColoredpolygonRendererNoZBuff() {}
    public static PolygonRenderer make() {return new ColoredpolygonRendererNoZBuff();}

    double delta_r_top;// = p_right.getColor().getIntR() - p_left.getColor().getIntR();
    double delta_g_top;// = p_right.getColor().getIntG() - p_left.getColor().getIntG();
    double delta_b_top;// = p_right.getColor().getIntB() - p_left.getColor().getIntB();
    double R_slope_top = 0 ;//= delta_r_top / (p_right.getIntY() - p_left.getIntY());
    double G_slope_top = 0;//= delta_g_top / (p_right.getIntY() - p_left.getIntY());
    double B_slope_top = 0;// = delta_b_top / (p_right.getIntY() - p_left.getIntY());

    double delta_r_left ;// = p_left.getColor().getIntR() - p_bottom.getColor().getIntR();
    double delta_g_left ;// = p_left.getColor().getIntG() - p_bottom.getColor().getIntG();
    double delta_b_left;// = p_left.getColor().getIntB() - p_bottom.getColor().getIntB();
    double R_slope_left = 0 ;//= delta_r_left / (p_left.getIntY() - p_bottom.getIntY());
    double G_slope_left = 0;// = delta_g_left / (p_left.getIntY() - p_bottom.getIntY());
    double B_slope_left = 0;// = delta_b_left / (p_left.getIntY() - p_bottom.getIntY());

    double delta_r_right;// = p_right.getColor().getIntR() - p_bottom.getColor().getIntR();
    double delta_g_right;// = p_right.getColor().getIntG() - p_bottom.getColor().getIntG();
    double delta_b_right;// = p_right.getColor().getIntB() - p_bottom.getColor().getIntB();
    double R_slope_right = 0;// = delta_r_right / (p_right.getIntY() - p_bottom.getIntY());
    double G_slope_right = 0;// = delta_g_right / (p_right.getIntY() - p_bottom.getIntY());
    double B_slope_right = 0;//= delta_b_right / (p_right.getIntY() - p_bottom.getIntY());

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

        delta_r_top = p_left.getColor().getIntR() - p_right.getColor().getIntR();
        delta_g_top = p_left.getColor().getIntG() - p_right.getColor().getIntG();
        delta_b_top = p_left.getColor().getIntB() - p_right.getColor().getIntB();
        R_slope_top = delta_r_top / (p_right.getIntY() - p_left.getIntY());
        G_slope_top = delta_g_top / (p_right.getIntY() - p_left.getIntY());
        B_slope_top = delta_b_top / (p_right.getIntY() - p_left.getIntY());

        delta_r_left = p_bottom.getColor().getIntR() - p_left.getColor().getIntR();
        delta_g_left = p_bottom.getColor().getIntG() - p_left.getColor().getIntG();
        delta_b_left = p_bottom.getColor().getIntB() - p_left.getColor().getIntB();
        R_slope_left = delta_r_left / (p_left.getIntY() - p_bottom.getIntY());
        G_slope_left = delta_g_left / (p_left.getIntY() - p_bottom.getIntY());
        B_slope_left = delta_b_left / (p_left.getIntY() - p_bottom.getIntY());

        delta_r_right = p_bottom.getColor().getIntR() - p_right.getColor().getIntR();
        delta_g_right = p_bottom.getColor().getIntG() - p_right.getColor().getIntG();
        delta_b_right = p_bottom.getColor().getIntB() - p_right.getColor().getIntB();
        R_slope_right = delta_r_right / (p_right.getIntY() - p_bottom.getIntY());
        G_slope_right = delta_g_right / (p_right.getIntY() - p_bottom.getIntY());
        B_slope_right = delta_b_right / (p_right.getIntY() - p_bottom.getIntY());

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
        double r_L = p_left.getColor().getIntR();
        double g_L = p_left.getColor().getIntG();
        double b_L = p_left.getColor().getIntB();
        double r_R = p_right.getColor().getIntR();
        double g_R = p_right.getColor().getIntG();
        double b_R = p_right.getColor().getIntB();

        for(double y = y_top; y > y_bottom; y--) {
            Vertex3D p1 = new Vertex3D(x_L, y, 0, Color.fromARGB(Color.makeARGB((int)Math.round(r_L),(int)Math.round(g_L),(int)Math.round(b_L))));
            Vertex3D p2 = new Vertex3D(x_R, y, 0, Color.fromARGB(Color.makeARGB((int)Math.round(r_R),(int)Math.round(g_R),(int)Math.round(b_R))));
            render_bet_points(p1, p2, panel);
            x_L += slope_left;
            x_R += slope_right;
            r_L += R_slope_left;
            g_L += G_slope_left;
            b_L += B_slope_left;
            r_R += R_slope_right;
            g_R += G_slope_right;
            b_R += B_slope_right;
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
            // =======================================================================
            delta_r_top = p_middle.getColor().getIntR() - p_top.getColor().getIntR(); // changed order <=============
            delta_g_top = p_middle.getColor().getIntG() - p_top.getColor().getIntG();
            delta_b_top = p_middle.getColor().getIntB() - p_top.getColor().getIntB();
            R_slope_top = delta_r_top / (p_top.getIntY() - p_middle.getIntY());
            G_slope_top = delta_g_top / (p_top.getIntY() - p_middle.getIntY());
            B_slope_top = delta_b_top / (p_top.getIntY() - p_middle.getIntY());

            delta_r_left = p_bottom.getColor().getIntR() - p_top.getColor().getIntR();
            delta_g_left = p_bottom.getColor().getIntG() - p_top.getColor().getIntG();
            delta_b_left = p_bottom.getColor().getIntB() - p_top.getColor().getIntB();
            R_slope_left = delta_r_left / (p_top.getIntY() - p_bottom.getIntY());
            G_slope_left = delta_g_left / (p_top.getIntY() - p_bottom.getIntY());
            B_slope_left = delta_b_left / (p_top.getIntY() - p_bottom.getIntY());

            delta_r_right = p_bottom.getColor().getIntR() - p_middle.getColor().getIntR();
            delta_g_right = p_bottom.getColor().getIntG() - p_middle.getColor().getIntG();
            delta_b_right = p_bottom.getColor().getIntB() - p_middle.getColor().getIntB();
            R_slope_right = delta_r_right / (p_middle.getIntY() - p_bottom.getIntY());
            G_slope_right = delta_g_right / (p_middle.getIntY() - p_bottom.getIntY());
            B_slope_right = delta_b_right / (p_middle.getIntY() - p_bottom.getIntY());

            render1stPolygon(x_top, y_top, y_middle, y_bottom, left_slope,
                    first_right_slope, second_right_slope, R_slope_top, G_slope_top, B_slope_top,
                    R_slope_left, G_slope_left, B_slope_left, R_slope_right, G_slope_right, B_slope_right,
                    p_top, p_middle, p_bottom, panel);
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

            delta_r_top = p_middle.getColor().getIntR() - p_top.getColor().getIntR(); // changed order <<=====================
            delta_g_top = p_middle.getColor().getIntG() - p_top.getColor().getIntG();
            delta_b_top = p_middle.getColor().getIntB() - p_top.getColor().getIntB();
            R_slope_top = delta_r_top / (p_top.getIntY() - p_middle.getIntY());
            G_slope_top = delta_g_top / (p_top.getIntY() - p_middle.getIntY());
            B_slope_top = delta_b_top / (p_top.getIntY() - p_middle.getIntY());

            delta_r_left = p_bottom.getColor().getIntR() - p_middle.getColor().getIntR();
            delta_g_left = p_bottom.getColor().getIntG() - p_middle.getColor().getIntG();
            delta_b_left = p_bottom.getColor().getIntB() - p_middle.getColor().getIntB();
            R_slope_left = delta_r_left / (p_middle.getIntY() - p_bottom.getIntY());
            G_slope_left = delta_g_left / (p_middle.getIntY() - p_bottom.getIntY());
            B_slope_left = delta_b_left / (p_middle.getIntY() - p_bottom.getIntY());

            delta_r_right = p_bottom.getColor().getIntR() - p_top.getColor().getIntR();
            delta_g_right = p_bottom.getColor().getIntG() - p_top.getColor().getIntG();
            delta_b_right = p_bottom.getColor().getIntB() - p_top.getColor().getIntB();
            R_slope_right = delta_r_right / (p_top.getIntY() - p_bottom.getIntY());
            G_slope_right = delta_g_right / (p_top.getIntY() - p_bottom.getIntY());
            B_slope_right = delta_b_right / (p_top.getIntY() - p_bottom.getIntY());

            render2ndPolygon(x_top, y_top, y_middle, y_bottom, right_slope,
                    first_left_slope, second_left_slope, R_slope_top, G_slope_top, B_slope_top,
                    R_slope_left, G_slope_left, B_slope_left, R_slope_right, G_slope_right, B_slope_right,p_top, p_middle, p_bottom, panel);
        }

    }

    private void render1stPolygon(int x_top, int y_top, int y_middle, int y_bottom, double left_slope,
                                  double upper_right_slope, double lower_right_slope, double R_slope_top,double G_slope_top,
                                  double B_slope_top,double R_slope_left,double G_slope_left,double B_slope_left,
                                  double R_slope_right,double G_slope_right,double B_slope_right, Vertex3D p_top,Vertex3D p_middle,
                                  Vertex3D p_bottom, Drawable panel) {
        double x = x_top;
        double x_left = x_top;
        double x_right = x_top;
        double y = y_top;

        //=====================================
        double r_L = p_top.getColor().getIntR();
        double g_L = p_top.getColor().getIntG();
        double b_L = p_top.getColor().getIntB();
        double r_R_u = p_top.getColor().getIntR();
        double g_R_u = p_top.getColor().getIntG();
        double b_R_u = p_top.getColor().getIntB();
        double r_R_l = p_middle.getColor().getIntR();
        double g_R_l = p_middle.getColor().getIntG();
        double b_R_l = p_middle.getColor().getIntB();

        for(; y > y_middle; y--) {
            Vertex3D p1 = new Vertex3D(x, y, 0, Color.fromARGB(Color.makeARGB((int)Math.round(r_L),(int)Math.round(g_L),
                    (int)Math.round(b_L))));
            Vertex3D p2 = new Vertex3D(x_right, y, 0, Color.fromARGB(Color.makeARGB((int)Math.round(r_R_u),(int)Math.round(g_R_u),
                    (int)Math.round(b_R_u))));
            render_bet_points(p1, p2, panel);
            x_left += left_slope;
            x_right += upper_right_slope;
            x = x_left;
            r_L += R_slope_left;
            r_R_u += R_slope_top;
            g_L += G_slope_left;
            g_R_u += G_slope_top;
            b_L += B_slope_left;
            b_R_u += B_slope_top;
        }
        for(; y > y_bottom; y--) {
            Vertex3D p1 = new Vertex3D(x, y, 0, Color.fromARGB(Color.makeARGB((int)Math.round(r_L),(int)Math.round(g_L),
                    (int)Math.round(b_L))));
            Vertex3D p2 = new Vertex3D(x_right, y, 0, Color.fromARGB(Color.makeARGB((int)Math.round(r_R_l),(int)Math.round(g_R_l),
                    (int)Math.round(b_R_l))));
            render_bet_points(p1, p2, panel);
            x_left += left_slope;
            x_right += lower_right_slope;
            x = x_left;
            r_L += R_slope_left;
            g_L += G_slope_left;
            b_L += B_slope_left;
            r_R_l += R_slope_right;
            g_R_l += G_slope_right;
            b_R_l += B_slope_right;
        }
    }

    private void render2ndPolygon(int x_top, int y_top, int y_middle, int y_bottom, double right_slope,
                                  double upper_left_slope, double lower_left_slope, double R_slope_top, double G_slope_top, double B_slope_top,
                                  double R_slope_left, double G_slope_left, double B_slope_left, double R_slope_right, double G_slope_right, double B_slope_right,
                                  Vertex3D p_top, Vertex3D p_middle, Vertex3D p_bottom ,Drawable panel) {
        double x = x_top;
        double y = y_top;
        double x_left = x_top;
        double x_right = x_top;

        double r_L_u = p_top.getColor().getIntR();
        double g_L_u = p_top.getColor().getIntG();
        double b_L_u = p_top.getColor().getIntB();
        double r_R = p_top.getColor().getIntR();
        double g_R = p_top.getColor().getIntG();
        double b_R = p_top.getColor().getIntB();
        double r_L_l = p_middle.getColor().getIntR();
        double g_L_l = p_middle.getColor().getIntG();
        double b_L_l = p_middle.getColor().getIntB();

        for(; y > y_middle; y--) {
            Vertex3D p1 = new Vertex3D(x, y, 0, Color.fromARGB(Color.makeARGB((int)Math.round(r_L_u),(int)Math.round(g_L_u),
                    (int)Math.round(b_L_u))));
            Vertex3D p2 = new Vertex3D(x_right, y, 0, Color.fromARGB(Color.makeARGB((int)Math.round(r_R),(int)Math.round(g_R),
                    (int)Math.round(b_R))));
            render_bet_points(p1, p2, panel);
            x_left += upper_left_slope;
            x_right += right_slope;
            x = x_left;
            r_L_u += R_slope_top;
            g_L_u += G_slope_top;
            b_L_u += B_slope_top;
            r_R += R_slope_right;
            g_R += G_slope_right;
            b_R += B_slope_right;
        }
        for(; y > y_bottom; y--) {
            Vertex3D p1 = new Vertex3D(x, y, 0, Color.fromARGB(Color.makeARGB((int)Math.round(r_L_l),(int)Math.round(g_L_l),
                    (int)Math.round(b_L_l))));
            Vertex3D p2 = new Vertex3D(x_right, y, 0, Color.fromARGB(Color.makeARGB((int)Math.round(r_R),(int)Math.round(g_R),
                    (int)Math.round(b_R))));
            render_bet_points(p1, p2, panel);
            x_left += lower_left_slope;
            x_right += right_slope;
            x = x_left;
            r_L_l += R_slope_left;
            g_L_l += G_slope_left;
            b_L_l += B_slope_left;
            r_R += R_slope_right;
            g_R += G_slope_right;
            b_R += B_slope_right;
        }
    }


    private void render_bet_points(Vertex3D p1, Vertex3D p2, Drawable drawable){
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
}