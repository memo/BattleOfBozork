package battle.controllers;

import asteroids.Action;
import asteroids.Controller;
import asteroids.GameState;
import asteroids.Ship;
import battle.BattleController;
import battle.NeuroShip;
import battle.SimpleBattle;

/**
 * Created by simonlucas on 30/05/15.
 */
public class RotateAndShoot implements BattleController {

    NeuroShip ship;

    Action action;

    public RotateAndShoot() {
        action = new Action();
    }

    public Action action(GameState game) {
        // action.thrust = 2.0;
        action.shoot = true;
        action.turn = 1;

        return action;
    }

    public void setVehicle(NeuroShip ship) {
        // just in case the ship is needed ...
        this.ship = ship;
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        return new Action(0, 1, true);
    }
}
