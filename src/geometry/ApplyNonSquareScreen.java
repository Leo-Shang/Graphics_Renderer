package geometry;

import client.interpreter.SimpInterpreter;
import polygon.Polygon;
import windowing.graphics.Color;

public class ApplyNonSquareScreen {
    private static double[][] non_square_screen_matrix = new double[4][4];

    public static Polygon multiplyNonSquareScreenMatrix(Polygon vertices){
        double[] z_value = ApplyPerspective.getZ_value();
        non_square_screen_matrix = SimpInterpreter.get_non_square_matrix();
        Vertex3D[] result = new Vertex3D[vertices.length()];
        for(int index = 0; index < vertices.length(); index++){
            Vertex3D current = vertices.get(index);
            Color color = current.getColor();
            double[] temp = new double[4];
            double[] current_point = new double[4];
            current_point[0] = current.getX();
            current_point[1] = current.getY();
            current_point[2] = current.getZ();
            current_point[3] = current.getPoint3D().getW();
            for(int i = 0; i < 4; i++) {
                double lineTotal = 0;
                for(int j = 0; j < 4; j++) {
                    lineTotal += non_square_screen_matrix[i][j] * current_point[j];
                }
                temp[i] = lineTotal;
            }
            current_point = temp;
            if(current_point[3] == 0){current_point[3] = 1; }
            Point3DH temp_point = new Point3DH(current_point[0], current_point[1], current_point[2], current_point[3]).euclidean(); // possible error with z coordinate
            Vertex3D t = new Vertex3D(temp_point, color);
            result[index] = new Vertex3D(t.getX(), t.getY(), z_value[index], color);
        }
        return Polygon.make(result);
    }
}
