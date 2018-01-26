package geometry;

public class Vector {

    private double x, y, z;

    public Vector(Point3D p1, Point3D p2){
        this.x = p2.getX() - p1.getX();
        this.y = p2.getY() - p1.getY();
        this.z = p2.getZ() - p1.getZ();
    }

    public Vector(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX(){ return x;}
    public double getY(){ return y;}
    public double getZ(){ return z;}

    public double dotProduct(Vector v){
        return this.getX() * v.getX() + this.getY() * v.getY() + this.getZ() * v.getZ();
    }

    public Vector getNormVector(){
        double norm = Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2) + Math.pow(getZ(), 2));
        if (norm == 0){return new Vector(x, y, z);}
        else
            return new Vector(x / norm, y / norm, z / norm);
    }

    public Vector crossProduct(Vector v){
        double temp_X = this.getY() * v.getZ() - this.getZ() * v.getY();
        double temp_Y = -this.getX() * v.getZ() + this.getZ() * v.getX();
        double temp_Z = this.getX() * v.getY() - this.getY() * v.getX();
        return new Vector(temp_X, temp_Y, temp_Z);
    }

    public Vector scale(double k){
        double temp_X = getX() * k;
        double temp_Y = getY() * k;
        double temp_Z = getZ() * k;
        return new Vector(temp_X, temp_Y, temp_Z);
    }

    public Vector add(Vector v) { return new Vector(v.getX() + getX(), v.getY() + getY(), v.getZ() + getZ()); }
}
