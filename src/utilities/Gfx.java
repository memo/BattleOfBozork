package utilities;
import java.awt.*;
import java.util.ArrayList;
import math.Vector2d;
import java.awt.geom.AffineTransform;


/**
 * Created by colormotor on 15/06/15.
 */
public class Gfx {
    public static void drawCircle( Graphics2D g, Vector2d center, double r )
    {
        g.drawOval((int)(center.x - r), (int)(center.y - r), (int)(r*2), (int)(r*2));
    }

    public static void drawLine( Graphics2D g, Vector2d a, Vector2d b )
    {
        g.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
    }

    public static void drawArrow( Graphics2D g, Vector2d a, Vector2d b, double size )
    {
        drawLine(g, a,b);
        Vector2d d = Vector2d.subtract(b, a);
        d.normalise();
        Vector2d perp = new Vector2d(-d.y*size,d.x*size);
        d.multiply(size);
        d = Vector2d.subtract(b, d);

        drawLine( g, Vector2d.subtract(d,perp), b);
        drawLine(g, Vector2d.add(d, perp), b);
    }

    public static void drawCapsule( Graphics2D g, Vector2d a, Vector2d b, double width )
    {
        drawLine(g, a, b);
        drawCircle(g, a, width);
        drawCircle(g, b, width);
        // TODO connect
    }

}
