package geometry;

public class Point3D implements Point {
	private double x;
	private double y;
	private double z;
	private double w;
	
	public Point3D(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	public Point3D(double x, double y, double z) {
		this(x, y, z, 1.0);
	}
	public Point3D(double[] coords) {
		this(coords[0], coords[1], coords[2], coords[3]);
	}
	
	public double getX() {
		return x / w;
	}
	public double getY() {
		return y / w;
	}
	public double getZ() { return z / w; }
	public double getW() {
		return w;
	}
	public int getIntX() {
		return (int) Math.round(getX());
	}
	public int getIntY() {
		return (int) Math.round(getY());
	}
	public int getIntZ() {
		return (int) Math.round(getZ());
	}
	public Point3D round() {
		double newX = Math.round(x);
		double newY = Math.round(y);
		double newZ = Math.round(z);
		return new Point3D(newX, newY, newZ);
	}
	public Point3D add(Point point) {
		Point3D other = (Point3D)point;
		double newX = getX() + other.getX();
		double newY = getY() + other.getY();
		double newZ = getZ() + other.getZ();
		return new Point3D(newX, newY, newZ);
	}
	public Point3D subtract(Point point) {
		Point3D other = (Point3D)point;
		double newX = getX() - other.getX();
		double newY = getY() - other.getY();
		double newZ = getZ() - other.getZ();
		return new Point3D(newX, newY, newZ);
	}
	public Point3D scale(double scalar) {
		double newX = getX() * scalar;
		double newY = getY() * scalar;
		double newZ = getZ() * scalar;
		return new Point3D(newX, newY, newZ);
	}
	public String toString() {
		return "[" + x + " " + y + " " + z + " " + w + "]t";
	}
	public Point3D euclidean() {
		if(w == 0) {
			w = .000000001;
			throw new UnsupportedOperationException("attempt to get euclidean equivalent of point at infinity " + this);
		}
		double newX = x / w;
		double newY = y / w;
		double newZ = z / w;
		return new Point3D(newX, newY, newZ);
	}
	public Point3D addVector(Vector v){
		double newX = getX() + v.getX();
		double newY = getY() + v.getY();
		double newZ = getZ() + v.getZ();
		return new Point3D(newX, newY, newZ);
	}
}
