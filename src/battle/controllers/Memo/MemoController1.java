package battle.controllers.Memo;

import asteroids.*;
import battle.BattleController;
import battle.NeuroShip;
import battle.RenderableBattleController;
import battle.SimpleBattle;
import math.Vector2d;
import battle.controllers.Memo.MemoControllerUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Created by Memo Akten on 11/06/15.
 */


/*
Evolved:
[0.2778726792239472, 0.5444174871655298, -0.18302984419042218, 4.9674545803614985, 0.774151581822357, -0.06817898591746861, 0.4337294770803466, 0.053651332141001, 0.6368934200933084, 0.2436632757426203, 0.4366302426536873, 0.8966473590665536, 0.15787122214317573, 0.23265484452056812, 1.789757691310432, 0.03479481858967811
 */
public class MemoController1 implements RenderableBattleController {
    static final public double DIST_RANGE = 500;   // used for normalizing distances
    static final public double TICK_RANGE = 10;     // used for normalizing tick

    static public double DESIRED_DIST_TO_ENEMY_MIN = 100;
    static public double DESIRED_DIST_TO_ENEMY_MAX = 300;
    static public double DESIRED_ROT_OFF_ENEMY = 00 * Math.PI / 180.0; // how many radians variance (+-) in axis off enemies back
    static public double DESIRED_POS_CHANGE_PROB = 0.005;  //probability of picking new position to goto
    static public double DESIRED_POS_ROT_THRESH = 45 * Math.PI / 180.0;   // angle threshold for when we're aiming at desired pos

    static public double ATTACK_PROB = 0.1;
    static public double ATTACK_SHOOT_PROB = 0.3;
    static public double ATTACK_THRUST_PROB = 0.00;
    static public double ATTACK_ROT_THRESH = 45 * Math.PI / 180.0;

    static public double CHASE_SHOOT_PROB = 0.1;
    static public double CHASE_THRUST_PROB = 0.5;
    static public double CHASE_DOT_THRESH = 0.7;

    static public double MISSILE_AVOID_DIST = 80;
    static public double MISSILE_AVOID_PROB = 0.2;

    static public double SHOOT_DIST_THRESH = 1000;
    static public int TICK_STEP = 1;

    Action action;
    double desired_dist_to_enemy = 0;
    double desired_rot_off_enemy = 0;
    double desired_pos_radius = 0;
    Vector2d desired_pos = new Vector2d(true);
    Vector2d vec_to_desired_pos = new Vector2d(true);
    Vector2d target_pos = new Vector2d(true);

    boolean do_attack = false;  // turn and attack
    boolean do_chase = false;   // go behind
    boolean do_avoid = false;   // avoid missiles
    boolean do_chicken = false; // go to edge and hide

    public MemoController1() {
        action = new Action();
    }

    static public double[] getFeatures() {
        double []v = new double[16];
        v[0] = DESIRED_DIST_TO_ENEMY_MIN / DIST_RANGE;
        v[1] = DESIRED_DIST_TO_ENEMY_MAX / DIST_RANGE;
        v[2] = DESIRED_ROT_OFF_ENEMY;
        v[3] = DESIRED_POS_CHANGE_PROB * 1000;
        v[4] = DESIRED_POS_ROT_THRESH;

        v[5] = ATTACK_PROB;
        v[6] = ATTACK_SHOOT_PROB;
        v[7] = ATTACK_THRUST_PROB;
        v[8] = ATTACK_ROT_THRESH;

        v[9] = CHASE_SHOOT_PROB;
        v[10] = CHASE_THRUST_PROB;
        v[11] = CHASE_DOT_THRESH;

        v[12] = MISSILE_AVOID_DIST / DIST_RANGE;
        v[13] = MISSILE_AVOID_PROB;
        v[14] = SHOOT_DIST_THRESH / DIST_RANGE;
        //v[15] = TICK_STEP / TICK_RANGE;
        return v;
    }

    static public void setFeatures(double [] v) {
        DESIRED_DIST_TO_ENEMY_MIN = v[0] * DIST_RANGE;
        DESIRED_DIST_TO_ENEMY_MAX = v[1] * DIST_RANGE;
        DESIRED_ROT_OFF_ENEMY = v[2];
        DESIRED_POS_CHANGE_PROB = v[3] / 1000;
        DESIRED_POS_ROT_THRESH = v[4];

        ATTACK_PROB = v[5];
        ATTACK_SHOOT_PROB = v[6];
        ATTACK_THRUST_PROB = v[7];
        ATTACK_ROT_THRESH = v[8];

        CHASE_SHOOT_PROB = v[9];
        CHASE_THRUST_PROB = v[10];
        CHASE_DOT_THRESH = v[11];

        MISSILE_AVOID_DIST = v[12] * DIST_RANGE;
        MISSILE_AVOID_PROB = v[13];
        SHOOT_DIST_THRESH = v[14] * DIST_RANGE;
        //TICK_STEP = (int)(v[15] * TICK_RANGE);
    }

    @Override
    public Action getAction(SimpleBattle gs, int playerId) {
        if(TICK_STEP < 1) TICK_STEP = 1;
        if(TICK_STEP > 1) {
            if(gs.getTicks() % TICK_STEP != 0) return action;
        }

        NeuroShip thisShip = gs.getShip(playerId);
        NeuroShip otherShip = gs.getShip(1 - playerId);

        do_attack = false;
        do_chase = false;
        do_avoid = false;
        do_chicken = false;

        // random probability of attacking
        if(Math.random() < ATTACK_PROB) {
            do_attack = true;

        } else {
            do_chase = true;
            // pick desired distance to ship
            if(desired_dist_to_enemy == 0 || Math.random() < DESIRED_POS_CHANGE_PROB) {
                desired_dist_to_enemy = Math.random() * (DESIRED_DIST_TO_ENEMY_MAX - DESIRED_DIST_TO_ENEMY_MIN) + DESIRED_DIST_TO_ENEMY_MIN;
                desired_rot_off_enemy = (Math.random() - 0.5) * DESIRED_ROT_OFF_ENEMY * 2;
            }

            // set desired position behind enemy ship
            Vector2d desired_pos_offset = Vector2d.multiply(otherShip.d, desired_dist_to_enemy);
            desired_pos_offset.rotate(desired_rot_off_enemy);

            desired_pos = Vector2d.subtract(otherShip.s, desired_pos_offset);
            vec_to_desired_pos = Vector2d.subtract(desired_pos, thisShip.s);

            // goto desired pos
            boolean reached_desired_pos = MemoControllerUtils.thrustTo(thisShip.s, thisShip.d, desired_pos, desired_dist_to_enemy, DESIRED_POS_ROT_THRESH, action);

            // attack if we're there
            if(reached_desired_pos) {
                do_attack = true;
            } else {
                // otherwise thrust (random)
                if(Vector2d.dot(thisShip.d, Vector2d.normalise(vec_to_desired_pos)) > CHASE_DOT_THRESH) {
                    action.thrust = Math.random() < CHASE_THRUST_PROB ? 1 : 0;
                }
            }
        }

        if(do_attack) {
            action.thrust = Math.random() < ATTACK_THRUST_PROB ? 1 : 0;

            // TODO, include velocity vector in target pos
            target_pos = new Vector2d(otherShip.s, true);
            action.shoot = MemoControllerUtils.lookAt(thisShip.s, thisShip.d, otherShip.s, ATTACK_ROT_THRESH, action) && (Math.random() < ATTACK_SHOOT_PROB);
        }

        if(thisShip.s.dist(otherShip.s) > SHOOT_DIST_THRESH) {
            action.shoot = false;
        }

        do_chicken = gs.getMissilesLeft(playerId) == 0;
        if(do_chicken) {
            MemoControllerUtils.thrustTo(thisShip.s, thisShip.d, new Vector2d(0, 0), desired_dist_to_enemy, DESIRED_POS_ROT_THRESH, action);
            action.thrust = 1;
        }

//        action.thrust = 0;

        if(Math.random() < MISSILE_AVOID_PROB) {
           // ArrayList<Missile> missiles = new ArrayList<Missile>();
            Vector2d avg_missile_pos = new Vector2d(true);
            int num_missiles = 0;

            // look for missiles nearby
            for (GameObject go : gs.getObjects()) {
                if (go instanceof Missile) {
                    if (go.s.dist(thisShip.s) < MISSILE_AVOID_DIST) {
                        //missiles.add((Missile) go);
                        num_missiles++;
                        avg_missile_pos.add(go.s);
                    }
                }
            }


            if(num_missiles > 0) {
                avg_missile_pos.multiply(1.0/num_missiles);
                do_avoid = true;
                Vector2d vec_to_avg_missile_pos = Vector2d.subtract(avg_missile_pos, thisShip.s);
                action.turn = Vector2d.crossMag(vec_to_avg_missile_pos, thisShip.d) < 0 ? - 1 : 1;
                action.thrust = 1;
            }
            //for(Missile m : missiles) {

            //}
        }



        return action;
    }

    @Override
    public void render(Graphics2D g, NeuroShip s) {
        if(do_chase) {
            g.setColor(Color.yellow);
            g.drawOval((int) (desired_pos.x - desired_dist_to_enemy), (int) (desired_pos.y - desired_dist_to_enemy), (int) desired_dist_to_enemy * 2, (int) desired_dist_to_enemy * 2);
        }
       // System.out.println(desired_pos);

        if(do_attack) {
            g.setColor(Color.red);
            int r = 20;
            g.drawOval((int) (target_pos.x) - r, (int) (target_pos.y) - r, r * 2, r * 2);
        }

        if(do_avoid) {
            g.setColor(Color.green);
            int r = (int)MISSILE_AVOID_DIST;
            g.drawOval((int) (s.s.x) - r, (int) (s.s.y) - r, r * 2, r * 2);
        }

        if(do_chicken) {
            g.setColor(Color.pink);
            int r = 50;
            g.drawOval((int) (s.s.x) - r, (int) (s.s.y) - r, r * 2, r * 2);
        }

        /*
        g.drawO
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
        */
    }
}
