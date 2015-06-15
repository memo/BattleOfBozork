package battle.controllers.Memo;

import asteroids.Action;
import asteroids.Controller;
import asteroids.GameState;
import asteroids.Ship;
import battle.BattleController;
import battle.NeuroShip;
import battle.SimpleBattle;
import math.Vector2d;
import battle.controllers.Memo.MemoControllerUtils;

/**
 * Created by Memo Akten on 11/06/15.
 */
public class MemoControllerRandom implements BattleController {
    public double MULT = 1;
    //public double MULT = 0.001;    // to be super dumb and easy

    public double ATTACK_PROB = 0.2 * MULT;
    public double ATTACK_SHOOT_PROB = 0.5 * MULT;
    public double ATTACK_THRUST_PROB = 0.01 * MULT;
    public double ATTACK_ROT_THRESH = 5 * Math.PI / 180.0;
    final public double ATTACK_ROT_THRESH_RANGE = Math.PI/2;

    public double FLEE_ROT_CHANGE_PROB = 0.3;
    public double FLEE_SHOOT_PROB = 0.02 * MULT;
    public double FLEE_THRUST_PROB = 0.8 * MULT;
    public double FLEE_TURN_PROB = 0.3 * MULT;
    public double FLEE_TURN_LEFT_PROB = 0.3333 * MULT;
    public double FLEE_TURN_RIGHT_PROB = 0.3333 * MULT;

    public double SHOOT_DIST_THRESH = 300;
    Action action;

    public MemoControllerRandom() {
        action = new Action();
    }

    public double[] getFeatures() {
        double []v = new double[10];
        v[0] = ATTACK_PROB;
        v[1] = ATTACK_SHOOT_PROB;
        v[2] = ATTACK_THRUST_PROB;
        v[3] = ATTACK_ROT_THRESH / ATTACK_ROT_THRESH_RANGE;
        v[4] = FLEE_ROT_CHANGE_PROB;
        v[5] = FLEE_SHOOT_PROB;
        v[6] = FLEE_THRUST_PROB;
        v[7] = FLEE_TURN_PROB;
        v[8] = FLEE_TURN_LEFT_PROB;
        v[9] = FLEE_TURN_RIGHT_PROB;
        v[10] = SHOOT_DIST_THRESH;
        return v;
    }

    public void setFeatures(double []v) {
        ATTACK_PROB = v[0];
        ATTACK_SHOOT_PROB = v[1];
        ATTACK_THRUST_PROB = v[2];
        ATTACK_ROT_THRESH  = v[3] * ATTACK_ROT_THRESH_RANGE;
        FLEE_ROT_CHANGE_PROB = v[4];
        FLEE_SHOOT_PROB = v[5];
        FLEE_THRUST_PROB = v[6];
        FLEE_TURN_PROB = v[7];
        FLEE_TURN_LEFT_PROB = v[8];
        FLEE_TURN_RIGHT_PROB = v[9];
        SHOOT_DIST_THRESH = v[10];
    }

    @Override
    public Action getAction(SimpleBattle gs, int playerId) {
        NeuroShip thisShip = gs.getShip(playerId);
        NeuroShip otherShip = gs.getShip(1 - playerId);

        if(Math.random() < ATTACK_PROB) {
            action.thrust = Math.random() < ATTACK_THRUST_PROB ? 1 : 0;
            action.shoot = Math.random() < ATTACK_SHOOT_PROB;

            // TODO, include velocity vector in target pos
            MemoControllerUtils.lookAt(thisShip.s, thisShip.d, otherShip.s, ATTACK_ROT_THRESH, action);
        } else{
            action.thrust = Math.random() < FLEE_THRUST_PROB ? 1 : 0;
            action.shoot = Math.random() < FLEE_SHOOT_PROB;

            if (Math.random() < FLEE_TURN_PROB) {
                double v = Math.random();
                if (v < FLEE_TURN_LEFT_PROB) action.turn = -1;
                else if (v > 1 - FLEE_TURN_RIGHT_PROB) action.turn = 1;
                else action.turn = 0;
            }
        }

        if(thisShip.s.dist(otherShip.s) > SHOOT_DIST_THRESH) {
            action.shoot = false;
        }

        return action;
    }
}
