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

class ForceObj{
    public ForceObj()
    {

    }

    public Vector2d getForceAt( Vector2d pos ) { return new Vector2d(0,0); }
    public void draw( Graphics2D g ) {}

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

        return force;
    }

    @Override
    public void draw( Graphics2D g )
    {
        g.setColor(Color.red);
        Gfx.drawCircle(g, center, radius);
        g.setColor(Color.red);
        Gfx.drawCircle(g, center, radius*weight);
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
        Vector2d force = new Vector2d(0,0);

        return force;
    }

    @Override
    public void draw( Graphics2D g )
    {
        g.setColor(Color.green);
        Gfx.drawCircle(g, center, radius);
        g.setColor(Color.green);
        Gfx.drawCircle(g, center, radius*weight);
    }
}

class SegmentAttraction extends ForceObj
{
    public Vector2d a;
    public Vector2d b;
    public double width;
    public double weight;


    SegmentAttraction( Vector2d a, Vector2d b, double width, double weight )
    {
        this.a = a;
        this.b = b;
        this.width = width;
        this.weight = weight;
    }

    @Override
    public Vector2d getForceAt( Vector2d pos )
    {
        Vector2d force = new Vector2d(0,0);

        return force;
    }

    @Override
    public void draw( Graphics2D g )
    {

    }
}


public class ForceField {

    public ArrayList<ForceObj> forceObjs;

    public ForceField()
    {
    }

    void radialRepulsion( Vector2d center, double radius, double weight )
    {   forceObjs.add( new RadialRepulsion(center, radius, weight)); }

    void radialAttraction( Vector2d center, double radius, double weight )
    {   forceObjs.add( new RadialAttraction(center, radius, weight)); }

    void segmentAttraction( Vector2d a, Vector2d b, double width, double weight )
    {   forceObjs.add( new SegmentAttraction(a, b, width, weight)); }

    void clear()
    {
        forceObjs.clear();
    }

    Vector2d getForceAt( Vector2d pos )
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

        for( int y = 0; y < width; y+=step )
        {
            for( int x = 0; x < height; x+=step )
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

