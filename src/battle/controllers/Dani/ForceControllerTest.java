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
import battle.Asteroid;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import math.Vector2d;
import math.Util;
import sun.java2d.pipe.SpanShapeRenderer;
import utilities.Gfx;
import battle.ForceField;
import java.awt.geom.AffineTransform;

/**
 * Created by simonlucas on 30/05/15.
 */
public class ForceControllerTest implements RenderableBattleController, KeyListener
{
    int myPlayerId = 0;

    ForceField ff = new ForceField();

    Action action;

    double viewRadius = 40.0;
    double thrustAmt = 1.5;
    double rotAmt = 1.5;
    int shotWait = 0;
    int shotDelay = 1;

    boolean anyMissiles = false;

    boolean debugDraw = true; // Use V key to toggle

    ArrayList<Vector2d> foo = new ArrayList<Vector2d>();

    public ForceControllerTest()
    {
        action = new Action();
        thrustAmt = 0.5 + Math.random();

    }

    public Action action(GameState game) {
        // action.thrust = 2.0;
        action.shoot = true;
        action.turn = 1;

        return action;
    }

    public boolean inView( NeuroShip ship, NeuroShip enemy )
    {
        Vector2d enemyPos = new Vector2d(enemy.s);
        Vector2d thisPos = new Vector2d(ship.s);
        double l = enemyPos.dist(thisPos);
        Vector2d tp = new Vector2d();

        Vector2d d = new Vector2d(ship.d, true);
        d.normalise();;
        tp.x = thisPos.x + d.x * l;
        tp.y = thisPos.y + d.y * l;

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

    ArrayList<Asteroid> getAsteroids(SimpleBattle gstate)
    {
        ArrayList<GameObject> O = gstate.getObjects();
        ArrayList<Asteroid> A = new ArrayList<Asteroid>();

        for( GameObject go : O )
        {
            if( go instanceof Asteroid )
            {
                A.add((Asteroid)go);
            }
        }

        return A;
    }

    void followTail( SimpleBattle gstate, double width, double weight )
    {
        NeuroShip enemy = getEnemyShip(gstate);
        Vector2d va = Vector2d.subtract( enemy.s, Vector2d.multiply(enemy.d, 20.0) );
        Vector2d vb = Vector2d.subtract(va, Vector2d.multiply(enemy.d, 200.0) );
        ff.segmentAttractionFollow( va, vb, width, weight );
    }

    void avoidTrail( SimpleBattle gstate, NeuroShip ship, double width, double weight )
    {
        foo.clear();
        int n = ship.getTrailLength();
        int skip = 20;
        for( int i = 0; i < n-skip; i+= skip)
        {
            //System.out.println(i + ", " + (i+skip) + "   " + n);
            Vector2d a = new Vector2d( ship.getTrailPoint(i) );
            Vector2d b = new Vector2d( ship.getTrailPoint(i+skip) );
            foo.add(a);
            ff.segmentRepulsion(a, b, width, weight);
        }
    }

    void avoidBullets( SimpleBattle gstate, double width, double weight )
    {
        Vector2d v = new Vector2d(0,0,true);
        ArrayList<Missile> M = getMissiles(gstate);
        for(Missile m : M) {
            Vector2d bp2 = Vector2d.add(m.s, Vector2d.multiply(m.v, 10.0));
            ff.segmentRepulsion(m.s, bp2, width, weight);
        }
    }

    void avoidAsteroids( SimpleBattle gstate, double weight )
    {
        Vector2d v = new Vector2d(0,0,true);
        ArrayList<Asteroid> A = getAsteroids(gstate);
        for(Asteroid a : A) {
            ff.radialRepulsion(a.s, a.r, weight);
        }
    }

    public Vector2d headingAndForceAt( SimpleBattle gstate, Vector2d shipPos, Vector2d enemyPos )
    {
        NeuroShip enemy = getEnemyShip(gstate);
        NeuroShip ship = gstate.getShip(myPlayerId);

        ff.clear();
        ff.pointAttraction(enemyPos, 5.0, 0.1);
        avoidBullets(gstate,10.0, 0.4);
        ff.radialRepulsion(enemyPos, 90, 0.2);
        followTail(gstate, 30.0, 1.8);
        avoidTrail(gstate, enemy, 30, 0.4);
        avoidAsteroids(gstate, 0.6);
        Vector2d rt = ff.headingAndForceAt(shipPos);

        return new Vector2d(rt.x,rt.y*0.1);
    }

    public NeuroShip getEnemyShip( SimpleBattle gstate )
    {
        return gstate.getShip((myPlayerId == 1) ? 0 : 1);
    }

    @Override
    public Action getAction(SimpleBattle gstate, int playerId)
    {
        myPlayerId = playerId;

        Action res = new Action(0,0,false);
        NeuroShip ship = gstate.getShip(playerId);
        NeuroShip enemy = gstate.getShip((playerId == 1) ? 0 : 1);

        Vector2d enemyPos = enemy.s;
        Vector2d shipPos = ship.s;

        Vector2d rt = headingAndForceAt(gstate, shipPos, enemyPos);
        Vector2d d = new Vector2d( Math.cos(rt.x)*rt.y, Math.sin(rt.x)*rt.y );
        res.thrust = rt.y;

        double rot = Util.angleBetween(ship.d, d)*rotAmt;

        if(inView(ship, enemy) && shotWait <= 0)
        {
            res.shoot = true;
            shotWait = shotDelay;
        }
        else {
            res.shoot = false;
        }

        res.turn = rot;

        shotWait--;
        return res;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_V:
                if(debugDraw==true)
                    debugDraw = false;
                else
                    debugDraw = true;
                break;

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void render( Graphics2D g, NeuroShip s ) {
        if(!debugDraw)
            return;
        
        AffineTransform at = g.getTransform();

        ff.draw(g, size.width, size.height, 50);

        for( Vector2d v : foo )
        {
            Gfx.drawCircle(g, v, 14);
        }
    }

}
