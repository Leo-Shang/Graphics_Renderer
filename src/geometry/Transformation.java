package geometry;

/**
 * Created by Leo on 10/11/2017.
 */
public class Transformation {
    private static double[][] matrix;

    public Transformation() {
        matrix = new double[4][4];
    }

    public static Transformation identity(){
        Transformation trans = new Transformation();
        for(int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(i == j)
                    matrix[i][j] = 1;
                else
                    matrix[i][j] = 0;
            }
        }
        return trans;
    }

    public static void change_value(int row, int col, double value) {
        matrix[row][col] = value;
    }

    public double getValue(int row, int col) {
        return matrix[row][col];
    }

    public int getWidth(){return 4;}
    public int getHeight() {return 4;}

    public double[][] getMatrix(){return matrix;}
}
