package geometry;

import client.interpreter.SimpInterpreter;
import polygon.Polygon;
import windowing.graphics.Color;

public class ApplyPerspective {
    private static double[][] perspective_matrix = new double[4][4];
    private static double[] z_value;

    public static Polygon multiplyPerspectiveMatrix(Polygon vertices){
        perspective_matrix = SimpInterpreter.getPerspectiveMatrix();
        Vertex3D[] result = new Vertex3D[vertices.length()];
        z_value = new double[vertices.length()];
        for(int index = 0; index < vertices.length(); index++){
            Vertex3D current = vertices.get(index);
            z_value[index] = current.getZ();
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
                    lineTotal += perspective_matrix[i][j] * current_point[j];
                }
                temp[i] = lineTotal;
            }
            current_point = temp;
            if(current_point[3] == 0){current_point[3] = 1; }
            Point3DH temp_point = new Point3DH(current_point[0], current_point[1], current_point[2], current_point[3]).euclidean(); // possible error with z coordinate
            result[index] = new Vertex3D(temp_point, color);
        }

       return Polygon.make(result);
    }

    public static double[] getZ_value(){
        return z_value;
    }
}
