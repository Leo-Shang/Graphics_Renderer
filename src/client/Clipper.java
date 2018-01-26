package client;

import geometry.*;
import polygon.Polygon;

import java.util.LinkedList;

public class Clipper {

    public class Clipped<Type> {

        private final Type object;
        private boolean valid;

        Clipped(Type object){
            this.valid = true;
            this.object = object;
        }

        Clipped(Type object, boolean valid) {
            this.valid = valid;
            this.object = object;
        }

        public boolean objectIsValid(){return this.valid;}

        public Type getClippedPolygon(){return this.object;}
    }

    private Plane left, right, top, bottom, close, far;

    private enum SIDE{LEFT, RIGHT, TOP, BOTTOM, CLOSE, FAR}

    public Clipper(double leftX, double rightX, double bottomY, double topY, double closeZ, double farZ){
        Point3D origin = new Point3D(0, 0, 0);

        this.left = new Plane(origin, new Point3D(leftX, bottomY, closeZ), new Point3D(leftX, topY, closeZ));
        this.right = new Plane(origin, new Point3D(rightX, topY, closeZ), new Point3D(rightX, bottomY, closeZ));

        this.top = new Plane(origin, new Point3D(leftX, topY, closeZ), new Point3D(rightX, topY, closeZ));
        this.bottom = new Plane(origin, new Point3D(rightX, bottomY, closeZ), new Point3D(leftX, bottomY, closeZ));

        this.close = new Plane(new Point3D(0, 0, closeZ), new Vector(0, 0, -1));
        this.far = new Plane(new Point3D(0, 0, farZ), new Vector(0, 0, 1));
    }

    public Clipped<Polygon> clipPolygon(Polygon polygon){
        for (SIDE side: SIDE.values()){
            Clipped<Polygon> result = clipPolygonAgainstSide(polygon, side);
            if (result.objectIsValid()){polygon = result.getClippedPolygon();}
            else {return result;}
        }
        return new Clipped<>(polygon);
    }

    public Clipped<Line> clipLine(Line line){
        for (SIDE side: SIDE.values()){
            Clipped<Line> result = clipLineAgainstSide(line, side);
            if (result.objectIsValid()){line = result.getClippedPolygon();}
            else{return result;}
        }
        return new Clipped<>(line);
    }

    private Clipped<Line> clipLineAgainstSide(Line line, SIDE side) {
        Plane plane = getPlane(side);

        if (!plane.onPositiveSide(line.p1) && !plane.onPositiveSide(line.p2)){
            return new Clipped<>(line, false);
        }

        if (plane.onPositiveSide(line.p1) && plane.onPositiveSide(line.p2)){
            return new Clipped<>(line, true);
        }

        Vector ray = new Vector(line.p1.getPoint3D(), line.p2.getPoint3D());
        Vector normal = plane.getNormalVector();
        Vector planeToP1 = new Vector(plane.getPointInPlane(), line.p1.getPoint3D());

        double t = - (normal.dotProduct(planeToP1)) / (normal.dotProduct(ray));

        if (plane.onPositiveSide(line.p1)){line = new Line(line.p1, line.p1.addVector(ray.scale(t)));}

        else {line = new Line(line.p1.addVector(ray.scale(t)), line.p2);}

        return new Clipped<>(line, true);
    }

    private Clipped<Polygon> clipPolygonAgainstSide(Polygon polygon, SIDE side) {
        Plane plane = getPlane(side);
        boolean clippingNeeded = plane.onPositiveSide(polygon.get(0));
        LinkedList<Vertex3D> insideVertices = new LinkedList<>();

        for (int i = 1; i <= polygon.length(); i++){
            if (plane.onPositiveSide(polygon.get(i))){
                if (!clippingNeeded){
                    Line line = new Line(polygon.get(i-1), polygon.get(i));
                    Clipped<Line> result = clipLineAgainstSide(line, side);
                    line = result.getClippedPolygon();
                    insideVertices.add(line.p1);

                    clippingNeeded = true;
                }
                insideVertices.add(polygon.get(i));
            } else {
                if (clippingNeeded){
                    Line line = new Line(polygon.get(i-1), polygon.get(i));
                    Clipped<Line> result = clipLineAgainstSide(line, side);
                    line = result.getClippedPolygon();
                    insideVertices.add(line.p2);

                    clippingNeeded = false;
                }
            }
        }

        Vertex3D[] clippedPoints = new Vertex3D[insideVertices.size()];
        for (int i = 0; i < insideVertices.size(); i++){clippedPoints[i] = insideVertices.get(i);}

        if (insideVertices.isEmpty()){return new Clipped<>(polygon, false);}

        else {
            return new Clipped<>(Polygon.make(clippedPoints), true);
        }
    }

    private Plane getPlane(SIDE side){
        switch(side){
            default:
            case LEFT:
                return left;
            case RIGHT:
                return right;
            case BOTTOM:
                return bottom;
            case TOP:
                return top;
            case CLOSE:
                return close;
            case FAR:
                return far;
        }
    }
}