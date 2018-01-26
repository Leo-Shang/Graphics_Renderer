package line;

/**
 * Created by Leo on 11/07/2017.
 */
public class Interpolate {
    private double current, change;

    public Interpolate(double x1, double x2, double current, double y1, double y2){
        this.change = (y2 - y1)/(x2 - x1);
        this.current = y1 + ((current - x1) * this.change);
    }

    public Interpolate(double x1, double x2, double current, double y1, double y2, boolean sign){
        this.change = (y2 - y1)/(x2 - x1);
        this.current = y1 + ((current - x1) * this.change);
        if (sign){this.change = - this.change;}
    }

    public Interpolate(double start, double change){
        this.current = start;
        this.change = change;
    }

    public double getValue(){ return current; }
    public void add(){ current += change; }

}
