package battle;

import asteroids.Action;
import asteroids.GameObject;
import asteroids.GameState;
import asteroids.Missile;
import math.Vector2d;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static asteroids.Constants.*;

public class NeuroShip extends GameObject {

    // define the shape of the ship
    static int[] xp = {-2, 0, 2, 0};
    static int[] yp = {2, -2, 2, 0};

    // this is the thrust poly that will be drawn when the ship
    // is thrusting
    static int[] xpThrust = {-2, 0, 2, 0};
    static int[] ypThrust = {2, 3, 2, 0};
    public static double scale = 5;

    // define how quickly the ship will rotate
    static double steerStep = 10 * Math.PI / 180;
    static double maxSpeed = 3;

    // this is the friction that makes the ship slow down over time
    static double loss = 0.99;

    double releaseVelocity = 0;
    double minVelocity = 2;
    public static double maxRelease = 10;
    Color color = Color.white;
    boolean thrusting = false;

    static double gravity = 0.0;

    // position and velocity
    public Vector2d d;

    // played id (used for drawing)
    int playerID;



    // trail parameters
    static int trail_length = 100;
    static double trail_momentum = 0.98;
    static boolean trail_wrap_x = false;
    static boolean trail_wrap_y = false;
    static boolean trail_close_loop = false;

    // trail vars
    int trail_index = 0;
    static int trail_length_max = 5000;
    Vector2d[] trail_pos = new Vector2d[trail_length_max];
    Vector2d[] trail_vel = new Vector2d[trail_length_max];

    static public void setTrailLength(int n) {
        if(n < 1) n = 1;
        else if (n>= trail_length_max - 1) n = trail_length_max - 1;
        trail_length = n;
    }




    public NeuroShip(Vector2d s, Vector2d v, Vector2d d, int playerID) {
        super(new Vector2d(s, true), new Vector2d(v, true));
        this.d = new Vector2d(d, true);
        this.playerID = playerID;

        setTrailLength(trail_length);
        for(int i=0; i<trail_length_max; i++) {
            trail_pos[i] = new Vector2d(s.x, s.y, true);
            trail_vel[i] = new Vector2d(0, 0, true);
        }
    }

    public NeuroShip copy() {
        NeuroShip ship = new NeuroShip(s, v, d, playerID);
        ship.releaseVelocity = releaseVelocity;
        return ship;
    }

    public double r() {
        return scale * 2.4;
    }

//    public Ship() {
//        super(new Vector2d(), new Vector2d());
//        d = new Vector2d(0, -1);
//    }
//

    public void reset() {
        s.set(width / 2, height / 2);
        v.zero();
        d.set(0, -1);
        dead = false;
        // System.out.println("Reset the ship ");
    }

    private static double clamp(double v, double min, double max) {
        if (v > max) {
            return max;
        }

        if (v < min) {
            return min;
        }

        return v;
    }

    public NeuroShip update(Action action) {

        // what if this is always on?

        // action has fields to specify thrust, turn and shooting

        // action.thrust = 1;

        if (action.thrust > 0) {
            thrusting = true;
        } else {
            thrusting = false;
        }

        //prevent people from cheating
        double thrustSpeed = clamp(action.thrust, 0, 1);
        double turnAngle = clamp(action.turn, -1, 1);

        d.rotate(turnAngle * steerStep);
        v.add(d, thrustSpeed * t * 0.3 / 2);
        v.y += gravity;
        // v.x = 0.5;
        v.multiply(loss);

        // This is fairly basic, but it'll do for now...
        v.x = clamp(v.x, -maxSpeed, maxSpeed);
        v.y = clamp(v.y, -maxSpeed, maxSpeed);

        s.add(v);


        trail_index = trail_index % trail_length;
        trail_pos[trail_index].x = s.x;
        trail_pos[trail_index].y = s.y;
        trail_vel[trail_index].x = v.x;
        trail_vel[trail_index].y = v.y;
        trail_index = (trail_index + 1) % trail_length;

        for(int i=0; i<trail_length; i++) {
            trail_vel[i].multiply(trail_momentum);
            trail_pos[i].add(trail_vel[i]);
        }

        return this;
    }

    private void tryMissileLaunch() {
        // System.out.println("Trying a missile launch");
        if (releaseVelocity > maxRelease) {
            releaseVelocity = Math.max(releaseVelocity, missileMinVelocity * 2);
            Missile m = new Missile(s, new Vector2d(0, 0, true));
            releaseVelocity = Math.min(releaseVelocity, maxRelease);
            m.v.add(d, releaseVelocity);
            // make it clear the ship
            m.s.add(m.v, (r() + missileRadius) * 1.5 / m.v.mag());
            releaseVelocity = 0;
            // System.out.println("Fired: " + m);
            // sounds.fire();
        } else {
            // System.out.println("Failed!");
        }
    }

    public String toString() {
        return s + "\t " + v;
    }

    @Override
    public void update() {
        throw new IllegalArgumentException("You shouldn't be calling this...");
    }

    public void draw(Graphics2D g) {
        color = playerID == 0 ? Color.green : Color.blue;
        AffineTransform at = g.getTransform();
        g.translate(s.x, s.y);
        double rot = Math.atan2(d.y, d.x) + Math.PI / 2;
        g.rotate(rot);
        g.scale(scale, scale);
        g.setColor(color);
        g.fillPolygon(xp, yp, xp.length);
        if (thrusting) {
            g.setColor(Color.red);
            g.fillPolygon(xpThrust, ypThrust, xpThrust.length);
        }
        g.setTransform(at);

        int trail_end_index = trail_close_loop ? trail_length : trail_length - 1;
        for(int i=0; i<trail_end_index; i++) {
            int i1 = (i + trail_index) % trail_length;
            int i2 = (i1 + 1) % trail_length;
            Vector2d p1 = trail_pos[i1];
            Vector2d p2 = trail_pos[i2];
            boolean doDraw = true;

            // HACK! use vars for window width height
            if(!trail_wrap_x && Math.abs(p1.x - p2.x) > 1920 * 0.9) doDraw = false;
            if(!trail_wrap_y && Math.abs(p1.y - p2.y) > 1080 * 0.9) doDraw = false;
            if(doDraw) g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
        }

    }

    public void hit() {
        // super.hit();
        // System.out.println("Ship destroyed");
        dead = true;
        // sounds.play(sounds.bangLarge);
    }

    public boolean dead() {
        return dead;
    }


}
