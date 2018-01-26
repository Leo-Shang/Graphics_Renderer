package windowing.drawable;

/**
 * Created by Leo on 10/11/2017.
 */
public class Z_buffering {
    private static double[][] z_buffer = new double[650][650];
    private final int X = 650;
    private final int Y = 650;
    public Z_buffering() {
        set_entry();
    }

    private void set_entry(){
        for(int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++)
                z_buffer[i][j] = -200;
        }
    }

    public static boolean isValid(int x, int y, double z){
        boolean isvalid = false;
        if(x >= 0 && x < 650 && y >= 0 && y < 650){
            if(z_buffer[x][y] > z ){
                isvalid = true;
            }
        }
        return isvalid;
    }

    public double getValue(int x, int y) {
        return z_buffer[x][y];
    }

    public void changeValue(int x, int y, double value){
        z_buffer[x][y] = value;
    }

}
