package battle.controllers;

import asteroids.Action;
import battle.BattleController;
import battle.SimpleBattle;

/**
 * Created by davidgundry on 11/06/15.
 */
public class EmptyController implements BattleController {
    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        return new Action(0,0,false);
    }
}
