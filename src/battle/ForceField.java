package battle;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import math.Vector2d;
import math.Util;
import utilities.Gfx;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by colormotor on 15/06/15.
 */

/// TODO: optimize attenuation for dist^2?

class ForceObj{
    public ForceObj()
    {

    }

    public Vector2d getForceAt( Vector2d pos ) { return new Vector2d(0,0); }
    public void draw( Graphics2D g ) {}

    static public Color attractColor = Color.green;
    static public Color repulseColor = Color.red;
    static public boolean normalize = true;
    static public double multiplier = 200.0;
}



class RadialRepulsion extends ForceObj{
    Vector2d center;
    double radius;
    double weight;

    public RadialRepulsion( Vector2d center, double radius, double weight )
    {
        this.center = center;
        this.radius = radius;
        this.weight = weight;
    }

    @Override
    public Vector2d getForceAt( Vector2d pos )
    {
        Vector2d force = new Vector2d(0,0);
        Vector2d d = Vector2d.subtract(pos, center);

        double dist = d.mag();

        if(normalize)
        {
            if(dist == 0.0)
            {
                // hack ..
                return new Vector2d(radius, radius);
            }

            d.divide(dist);
        }

        d.multiply(Util.attenuation(dist, radius) * weight * multiplier);
        return d;
    }

    @Override
    public void draw( Graphics2D g )
    {
        g.setColor(ForceObj.repulseColor);
        Gfx.drawCircle(g, center, radius);
        Gfx.drawCircle(g, center, radius*weight);
    }
}

class PointAttraction extends ForceObj{
    public Vector2d center;
    public double weight;
    public double max;

    public PointAttraction( Vector2d center, double max, double weight )
    {
        this.center = center;
        this.weight = weight;
        this.max = max;
    }

    @Override
    public Vector2d getForceAt( Vector2d pos )
    {
        Vector2d zero = new Vector2d(0,0);

        Vector2d d = Vector2d.subtract(center, pos);
        double dist = d.mag();

        if(dist == 0.0)
            return zero;

        if(dist > max)
        {
            d.multiply(max / dist);
        }

        if(normalize)
            d.divide(dist);

        d.multiply(weight * multiplier);
        return d;
    }

    @Override
    public void draw( Graphics2D g )
    {
        g.setColor(ForceObj.attractColor);
        Gfx.drawCircle(g, center, 5.0);
    }
}

class RadialAttraction extends ForceObj{
    public Vector2d center;
    public double radius;
    public double weight;

    public RadialAttraction( Vector2d center, double radius, double weight )
    {
        this.center = center;
        this.radius = radius;
        this.weight = weight;
    }

    @Override
    public Vector2d getForceAt( Vector2d pos )
    {
        Vector2d zero = new Vector2d(0,0);

        Vector2d d = Vector2d.subtract(pos, center);
        double dist = d.mag();

        if(dist == 0.0)
            return zero;

        if(normalize)
            d.divide(dist);

        d.multiply((1.0 - Util.attenuation(dist, radius)) * weight  * multiplier);
        return d;
    }

    @Override
    public void draw( Graphics2D g )
    {
        g.setColor(ForceObj.attractColor);
        Gfx.drawCircle(g, center, radius);
        Gfx.drawCircle(g, center, radius*weight);
    }
}

class SegmentAttraction extends ForceObj
{
    public Vector2d a;
    public Vector2d b;
    public double width;

    public double weight;

    public boolean follow; // this flags indicates to follow the segment
    public Vector2d forward; // in the direction of the vector a-b

    public SegmentAttraction( Vector2d a, Vector2d b, double width, double weight )
    {
        this(a, b, width, weight, false);
    }

    public SegmentAttraction( Vector2d a, Vector2d b, double width, double weight, boolean follow )
    {
        this.a = a;
        this.b = b;
        this.width = width;
        this.weight = weight;
        this.follow = follow;
        this.forward = Vector2d.subtract(a, b);
        this.forward.normalise();
    }

    @Override
    public Vector2d getForceAt( Vector2d pos )
    {
        Vector2d force = new Vector2d(0,0);

        Vector2d segp = Util.closestPointOnSegment(pos, a, b);

        double dist = segp.dist(pos);

        if( follow && dist < width )
        {

            Vector2d target = Vector2d.add(segp,forward);
            Vector2d d = Vector2d.subtract(target, pos);
            if( normalize )
                d.normalise();
            d.multiply(weight);
            return d;
        }

        Vector2d d = Vector2d.subtract(segp, pos);
        if(normalize)
            d.normalise();;

        d.multiply(Util.attenuation(dist, width) * weight * multiplier );
        return d;
    }

    @Override
    public void draw( Graphics2D g )
    {
        g.setColor(ForceObj.attractColor);
        Gfx.drawCapsule(g, a, b, width );
        Gfx.drawCapsule(g, a, b, width*weight );

        if(follow)
        {
            Gfx.drawArrow(g, b, a, 3);
        }
    }
}

class SegmentRepulsion extends ForceObj
{
    public Vector2d a;
    public Vector2d b;
    public double width;
    public double weight;

    public Vector2d forward;

    SegmentRepulsion( Vector2d a, Vector2d b, double width, double weight )
    {
        this.a = a;
        this.b = b;
        this.width = width;
        this.weight = weight;

        this.forward = Vector2d.subtract(a, b);
        this.forward.normalise();
    }

    @Override
    public Vector2d getForceAt( Vector2d pos )
    {
        Vector2d force = new Vector2d(0,0);

        Vector2d segp = Util.closestPointOnSegment(pos, a, b);

        double dist = segp.dist(pos);

        Vector2d d = Vector2d.subtract(pos, segp);

        if(normalize)
        {
            if(dist == 0.0)
            {
                // hack ..
                Vector2d dp = Util.perpendicular(forward);
                dp.multiply(width);
                return dp;
            }

            d.divide(dist);
        }


        d.multiply(Util.attenuation(dist, width) * weight  * multiplier);
        return d;
    }


    @Override
    public void draw( Graphics2D g )
    {
        g.setColor(ForceObj.repulseColor);
        Gfx.drawCapsule(g, a, b, width );
        Gfx.drawCapsule(g, a, b, width*weight );

    }
}



public class ForceField {

    public ArrayList<ForceObj> forceObjs;

    public ForceField()
    {
        forceObjs = new ArrayList<ForceObj>();
    }

    public void pointAttraction( Vector2d center, double max, double weight )
    {   forceObjs.add( new PointAttraction(center, max, weight)); }

    public void radialRepulsion( Vector2d center, double radius, double weight )
    {   forceObjs.add( new RadialRepulsion(center, radius, weight)); }

    public void radialAttraction( Vector2d center, double radius, double weight )
    {   forceObjs.add( new RadialAttraction(center, radius, weight)); }

    public void segmentAttraction( Vector2d a, Vector2d b, double width, double weight )
    {   forceObjs.add( new SegmentAttraction(a, b, width, weight)); }

    public void segmentAttractionFollow( Vector2d a, Vector2d b, double width, double weight )
    {   forceObjs.add( new SegmentAttraction(a, b, width, weight, true)); }

    public void segmentRepulsion( Vector2d a, Vector2d b, double width, double weight )
    {   forceObjs.add( new SegmentRepulsion(a, b, width, weight)); }

    public void clear()
    {
        forceObjs.clear();
    }

    public Vector2d getForceAt( Vector2d pos )
    {
        Vector2d f = new Vector2d(0, 0, true);
        // accumulate forces.
        for( int i = 0; i < forceObjs.size(); i++ )
            f.add(forceObjs.get(i).getForceAt(pos));

        return f;
    }

    public Vector2d rotThrustAt( Vector2d pos )
    {
        Vector2d rt = new Vector2d(0,0);

        Vector2d f = getForceAt(pos);

        double mag = f.mag();

        if( mag > 0.0 )
            f = Vector2d.divide(f, mag);

        return new Vector2d(Util.heading(f), mag);
    }

    public void draw( Graphics2D g, int width, int height, int step )
    {
        g.setColor(Color.gray);

        for( int y = 0; y < height; y+=step )
        {
            for( int x = 0; x < width; x+=step )
            {
                Vector2d pos = new Vector2d((double)x, (double)y);
                Vector2d to = Vector2d.add(pos, getForceAt(pos) );
                Gfx.drawArrow(g, pos, to, 3);
            }
        }

        // draw objects
        for( int i = 0; i < forceObjs.size(); i++ )
            forceObjs.get(i).draw(g);
    }
}

