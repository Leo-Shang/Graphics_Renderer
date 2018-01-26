package geometry;

public class Plane {

    private double A, B, C, D;
    private Point3D pointInPlane;

    public Plane(Point3D p, Point3D q, Point3D r){
        Vector pq = new Vector(p, q);
        Vector pr = new Vector(p, r);
        Vector pqXpr = pq.crossProduct(pr);

        this.A = pqXpr.getX();
        this.B = pqXpr.getY();
        this.C = pqXpr.getZ();
        this.D = -(this.A * p.getX() + this.B * p.getY() + this.C * p.getZ());
        this.pointInPlane = p;
    }

    public Plane(Point3D p, Vector normal){
        this.A = normal.getX();
        this.D = normal.getY();
        this.C = normal.getZ();
        this.D = -(this.A * p.getX() + this.B * p.getY() + this.C * p.getZ());
        this.pointInPlane = p;
    }

    public boolean onPositiveSide(Point3D p){
        Vector normal = getNormalVector().getNormVector();
        Vector planeToPoint = new Vector(pointInPlane, p).getNormVector();
        return normal.dotProduct(planeToPoint) >= 0;
    }

    public boolean onPositiveSide(Vertex3D p) {return onPositiveSide(p.getPoint3D());}

    public Vector getNormalVector(){return new Vector(this.A, this.B, this.C);}

    public Point3D getPointInPlane(){return pointInPlane;}
}
