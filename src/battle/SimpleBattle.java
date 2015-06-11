package battle;

import asteroids.Action;
import asteroids.GameObject;
import math.Vector2d;

import java.awt.*;
import java.util.ArrayList;

import static asteroids.Constants.bg;
import static asteroids.Constants.size;

/**
 * Created by simon lucas on 10/06/15.
 *
 *  Aim here is to have a simple battle class
 *  that enables ships to fish with each other
 *
 *  Might start off with just two ships, each with their own types of missile.
 *
 *
 *
 */

public class SimpleBattle {

    // play a time limited game with a strict missile budget for
    // each player
    static int nMissiles = 100;
    static int nTicks = 1000;
    static int pointsPerKill = 10;

    ArrayList<BattleController> controllers;

    ArrayList<GameObject> objects;
    ArrayList<PlayerStats> stats;

    BattleController b1, b2;
    NeuroShip s1, s2;
    BattleController p1, p2;

    public int playGame(BattleController p1, BattleController p2) {
       return 0;
    }

    public void update() {
        // get the actions from each player

        // apply them to each player's ship, taking actions as necessary


        Action a1 = p1.getAction(this, 0);
        Action a2 = p2.getAction(this, 1);

        // now apply them to the ships

        s1.update(a1);
        s2.update(a2);

        // and fire any missiles as necessary
        if (a1.shoot) fireMissile(s1.s, s1.d, 0);
        if (a2.shoot) fireMissile(s2.s, s2.d, 1);

        // here need to add the game objects ...

    }

    public void fireMissile(Vector2d s, Vector2d d, int playerId) {
        // need all the usual missile firing code here

    }

    public void draw(Graphics2D g) {
        // for (Object ob : objects)

        // System.out.println("In draw(): " + n);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fillRect(0, 0, size.width, size.height);
        for (GameObject go : objects) {
            go.draw(g);
        }
        s1.draw(g); s2.draw(g);
    }

    static class PlayerStats {
        int nMissiles;
        int nPoints;

        public PlayerStats(int nMissiles, int nPoints) {
            this.nMissiles = nMissiles;
            this.nPoints = nPoints;
        }

        public String toString() {
            return nMissiles + " : " + nPoints;
        }
    }

}
