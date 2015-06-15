package battle.controllers.Dani;

import asteroids.Action;
import static asteroids.Constants.*;
import asteroids.Controller;
import asteroids.GameState;
import asteroids.GameObject;
import asteroids.Ship;
import battle.RenderableBattleController;
import battle.BattleMissile;
import battle.NeuroShip;
import battle.SimpleBattle;
import asteroids.Missile;

import java.awt.*;
import java.util.ArrayList;
import math.Vector2d;
import java.awt.geom.AffineTransform;

/**
 * Created by simonlucas on 30/05/15.
 */
public class DaniControllerEvo implements RenderableBattleController
{
    int myPlayerId = 0;

    public static final int THRUST_AMT = 0;
    public static final int ROT_AMT = 1;
    public static final int AVOID = 2;
    public static final int TAIL = 3;
    public static final int FOLLOW = 4;
    public static final int DIST = 5;
    
    public static final int N_FEATURES = 6;
    

    double [] features = new double[N_FEATURES];

    Action action;
    Vector2d targetPos;
    double viewRadius = 20.0;
    double thrustAmt = 1.5;
    double rotAmt = 2.5;
    int shotWait = 0;
    int shotDelay = 1;

    Vector2d meanMissilePos;
    Vector2d meanMissileDir;
    Vector2d missileLineA;
    Vector2d missileLineB;
    Vector2d segp;
    Vector2d enemyLineA;
    Vector2d enemyLineB;
    Vector2d enemyTailPt;

    boolean anyMissiles = false;

    Vector2d targetPosition;
    Vector2d targetDirection;

    ArrayList<Vector2d> grid;
    ArrayList<Vector2d> rtGrid;
    ArrayList<Vector2d> bulSegs;

    public DaniControllerEvo()

    {
        features[THRUST_AMT] = 1.5;
        features[ROT_AMT] = 1.5;
        features[AVOID] = 1.5;
        features[TAIL] = 1.0;
        features[FOLLOW] = 0.6;
        features[DIST] = 0.4;
        
        
        action = new Action();
        targetPos = new Vector2d(0,0);
        thrustAmt = 0.5 + Math.random();
    }

    public Action action(GameState game) {
        // action.thrust = 2.0;
        action.shoot = true;
        action.turn = 1;

        return action;
    }

    public double angleBetween( Vector2d a, Vector2d b )
    {
        return Math.atan2(a.x * b.y - a.y * b.x, a.x * b.x + a.y * b.y);
    }

    public boolean inView( NeuroShip ship, NeuroShip enemy )
    {
        Vector2d enemyPos = new Vector2d(enemy.s);
        Vector2d thisPos = new Vector2d(ship.s);
        double l = enemyPos.dist(thisPos);
        Vector2d tp = new Vector2d();
        // Now tell me how to write this in one line in Java with vectors xcxicwerwx
        // Porco dio porca madonna tutti gli angeli in colonna
        Vector2d d = new Vector2d(ship.d, true);
        d.normalise();;
        tp.x = thisPos.x + d.x * l;
        tp.y = thisPos.y + d.y * l;
        targetPos = tp;

        if( tp.dist(enemyPos) <= viewRadius)
            return true;
        return false;
    }

    ArrayList<Missile> getMissiles(SimpleBattle gstate)
    {
        ArrayList<GameObject> O = gstate.getObjects();
        ArrayList<Missile> M = new ArrayList<Missile>();

        for( GameObject go : O )
        {
            if( go instanceof Missile )
            {
                M.add((Missile)go);
            }
        }

        return M;
    }

    double dot( Vector2d a, Vector2d b )
    {
        return a.x*b.x + a.y*b.y;
    }

    Vector2d subtract( Vector2d a, Vector2d b )
    {
        return new Vector2d(a.x-b.x, a.y-b.y);
    }

    Vector2d  closestPointOnSegment( Vector2d p, Vector2d a, Vector2d b )
    {
        Vector2d v = Vector2d.subtract(b, a);
        Vector2d w = Vector2d.subtract(p, a);

        double d1 = dot(w,v);
        if( d1 <= 0.0 )
            return a;
        double d2 = dot(v,v);
        if( d2 <= d1 )
            return b;

        double t = d1/d2;
        return Vector2d.add(a, Vector2d.multiply(v, t));
    }

    double           distanceToSegment( Vector2d p, Vector2d a, Vector2d b )
    {
        return p.dist( closestPointOnSegment(p,a,b) );
    }

    Vector2d getPerp( Vector2d v )
    {
        return new Vector2d(-v.y, v.x);
    }

    Vector2d keepDistanceVector( SimpleBattle gstate, Vector2d shipPos, Vector2d enemyPos )
    {
        if( shipPos.dist(enemyPos) < 50 )
            return Vector2d.multiply( Vector2d.subtract(shipPos, enemyPos), 1.0 );

        return new Vector2d(0,0);
    }

    Vector2d avoidBulletsVector( SimpleBattle gstate, Vector2d shipPos )
    {
        Vector2d v = new Vector2d(0,0,true);
        bulSegs = new ArrayList<Vector2d>();
        ArrayList<Missile> M = getMissiles(gstate);
        for(Missile m : M)
        {
            Vector2d bp2 =  Vector2d.add(m.s, Vector2d.multiply(m.v, 10.0));
            bulSegs.add(m.s);
            bulSegs.add(bp2);

            Vector2d segp = closestPointOnSegment(shipPos, m.s, bp2);

            double l = segp.dist(shipPos);
            double limit = 100.0;
            if( l < limit ) {
                Vector2d d = Vector2d.subtract(shipPos, m.s);
                d.normalise();
                d = getPerp(d);
                double att = (1.0 - l/limit);
                att*=att;
                v.add( Vector2d.multiply(d, att*1000.0) );
            }
        }

        return v;
    }

    Vector2d tailVector( SimpleBattle gstate, Vector2d shipPos )
    {
        NeuroShip enemy = getEnemyShip(gstate);
        Vector2d va = Vector2d.subtract( enemy.s, Vector2d.multiply(enemy.d, 20.0) );
        Vector2d vb = Vector2d.subtract(va, Vector2d.multiply(enemy.d, 200.0) );
        Vector2d tailPos =  closestPointOnSegment(shipPos, va, vb);
        enemyTailPt = tailPos;
        enemyLineA = va;
        enemyLineB = vb;
        return Vector2d.multiply( Vector2d.subtract(tailPos, shipPos), 1.0 );
    }
    
    double heading( Vector2d v )
    {
        double theta = Math.atan2(v.y, v.x);
        if (theta < 0)
            theta += Math.PI * 2;
        return theta;
    }

    public Vector2d rotThrustAt( SimpleBattle gstate, Vector2d shipPos, Vector2d enemyPos )
    {
        Vector2d rt = new Vector2d(0,0);

        Vector2d v = new Vector2d(0,0,true);

        Vector2d vFollow = new Vector2d( enemyPos.x - shipPos.x, enemyPos.y - shipPos.y, true );
        Vector2d vAvoid = avoidBulletsVector(gstate, shipPos );
        Vector2d vDist = keepDistanceVector(gstate, shipPos, enemyPos);
        Vector2d vTail = tailVector(gstate, shipPos);

        //
        v.add( Vector2d.multiply(vAvoid, features[AVOID]) );
        v.add( Vector2d.multiply(vTail, features[TAIL]) );
        v.add( Vector2d.multiply(vFollow, features[FOLLOW]) ) ;
        v.add( Vector2d.multiply(vDist, features[DIST]) );

        double mag = v.mag();

        if( mag > 0.0 )
            v = Vector2d.divide(v, mag);

        return new Vector2d(heading(v),mag*0.1);
    }

    public NeuroShip getEnemyShip( SimpleBattle gstate )
    {
        return gstate.getShip((myPlayerId == 1)?0:1);
    }

    @Override
    public Action getAction(SimpleBattle gstate, int playerId)
    {
        myPlayerId = playerId;

        Action res = new Action(0,0,false);
        NeuroShip ship = gstate.getShip(playerId);
        NeuroShip enemy = gstate.getShip((playerId == 1)?0:1);

        Vector2d enemyPos = enemy.s;
        Vector2d shipPos = ship.s;

        Vector2d rt = rotThrustAt( gstate, shipPos, enemyPos );
        Vector2d d = new Vector2d( Math.cos(rt.x)*rt.y, Math.sin(rt.x)*rt.y );
        res.thrust = rt.y * features[THRUST_AMT];

        grid = new ArrayList<Vector2d>();
        rtGrid = new ArrayList<Vector2d>();

        for( int y = 0; y < size.height; y+=30 )
            for( int x = 0; x < size.width; x+=30 )
            {
                Vector2d p = new Vector2d(x,y);

                Vector2d rotThrust = rotThrustAt(gstate, new Vector2d(x,y), enemyPos );
                grid.add(p);
                rtGrid.add(rotThrust);
            }

        double rot = angleBetween(ship.d, d)*features[ROT_AMT];
        //double rot = rt.x - heading(ship.d);

        if(inView(ship, enemy) && shotWait <= 0)
        {
            res.shoot = true;
            shotWait = shotDelay;
        }
        else {
            res.shoot = false;
        }

        //res.thrust = thrustAmt;
        res.turn = rot;

        shotWait--;
        return res;
    }

    public void drawCircle( Graphics2D g, Vector2d center, double r )
    {
        g.drawOval((int)(center.x - r), (int)(center.y - r), (int)(r*2), (int)(r*2));
    }

    public void drawLine( Graphics2D g, Vector2d a, Vector2d b )
    {
        g.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
    }

    void drawArrow( Graphics2D g, Vector2d a, Vector2d b, double size )
    {

        drawLine(g, a,b);
        Vector2d d = Vector2d.subtract(b, a);
        d.normalise();
        Vector2d perp = new Vector2d(-d.y*size,d.x*size);
        d.multiply(size);
        d = Vector2d.subtract(b, d);
        //d.subtract(perp);

        drawLine( g, Vector2d.subtract(d,perp), b);
        drawLine(g, Vector2d.add(d, perp), b);
    }

    @Override
    public void render( Graphics2D g, NeuroShip s ) {
        AffineTransform at = g.getTransform();

        drawCircle(g, this.targetPos, this.viewRadius);
        g.setTransform(at);

        if (anyMissiles) {
            drawCircle(g, meanMissilePos, 4.0);
//            int dx = (int)(meanMissileDir.x * 100);
//            int dy = (int)(meanMissileDir.y * 100);
            drawLine(g, Vector2d.subtract(meanMissilePos, Vector2d.multiply(meanMissileDir, 100)),
                    Vector2d.add(meanMissilePos, Vector2d.multiply(meanMissileDir, 100)));
//            g.drawLine((int)meanMissilePos.x - dx, (int)meanMissilePos.y - dy, (int)meanMissilePos.x + dx, (int)meanMissilePos.y + dy);
            drawCircle(g, missileLineA, 7.0);
            drawCircle(g, missileLineB, 7.0);
        }

        g.setColor(Color.gray);

        for (int i = 0; i < grid.size(); i++)
        {
            Vector2d p = grid.get(i);
            Vector2d rt = rtGrid.get(i);
            Vector2d d = new Vector2d(Math.cos(rt.x) * rt.y, Math.sin(rt.x) * rt.y);
            drawArrow(g, p, Vector2d.add(p, d), 3);
        }

        g.setColor(Color.blue);
        for (int i = 0; i < bulSegs.size(); i+=2)
        {
            drawLine(g, bulSegs.get(i), bulSegs.get(i + 1));
        }

        drawLine(g, enemyLineA, enemyLineB );
        drawCircle(g, enemyTailPt, 5.0);
    }
}
