package math;

public class Util {
    public static double min(double[] x) {
        double min = x[0];
        for (int i=1; i<x.length; i++) {
            min = Math.min(min, x[i]);
        }
        return min;
    }

    public double angleBetween( Vector2d a, Vector2d b )
    {
        return Math.atan2(a.x * b.y - a.y * b.x, a.x * b.x + a.y * b.y);
    }

    double dot( Vector2d a, Vector2d b )
    {
        return a.x*b.x + a.y*b.y;
    }

    Vector2d  closestPointOnSegment( Vector2d p, Vector2d a, Vector2d b )
    {
        Vector2d v = Vector2d.subtract(b, a);
        Vector2d w = Vector2d.subtract(p, a);

        double d1 = dot(w,v);
        if( d1 <= 0.0 )
        {
            return a;
        }
        double d2 = dot(v,v);
        if( d2 <= d1 )
        {
            return b;
        }
        double t = d1/d2;
        return Vector2d.add(a, Vector2d.multiply(v, t));
    }

    double distanceToSegment( Vector2d p, Vector2d a, Vector2d b )
    {
        return p.dist( closestPointOnSegment(p,a,b) );
    }

}
