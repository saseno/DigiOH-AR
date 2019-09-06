package dev.saseno.jakarta.digioh.wavefront;

public class Coords {
	
    private double x = 0;
    private double y = 0;
    private double z = 0;

    public Coords(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coords(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Coords scale(Coords c, double r) {
        return new Coords(
                c.x * r,
                c.y * r,
                c.z * r
        );
    }

    public static Coords add(Coords c1, Coords c2) {
        return new Coords(
                c1.x + c2.x,
                c1.y + c2.y,
                c1.z + c2.z
        );
    }

    public static Coords cross(Coords c1, Coords c2) {
        return new Coords(
                c1.y * c2.z - c1.z * c2.y,
                c1.z * c2.x - c1.x * c2.z,
                c1.x * c2.y - c1.y * c2.x
        );
    }

    public Coords normalize() {
        double d = Math.sqrt(x * x + y * y + z * z);

        if (d == 0) {
            return new Coords(0, 0, 0);
        } else {
            return new Coords(x / d, y / d, z / d);
        }
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f, %.3f)", x, y, z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getU() {
        // u = x
        return x;
    }

    public double getV() {
        // v = y
        return y;
    }
}
