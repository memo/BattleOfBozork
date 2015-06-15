package battle.controllers.webpigeon;

import asteroids.Action;
import battle.BattleController;
import battle.SimpleBattle;

/**
 * Created by jwalto on 12/06/2015.
 */
public class StupidGAWrapper implements BattleController {
    public static final Integer GENOME_LENGTH = 3;
    private double[] genome;

    public StupidGAWrapper(double[] genome) {
        this.genome = genome;
    }

    public Action getMove() {
        return new Action(genome[0], genome[1], genome[2] > 0.5);
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        return getMove();
    }
}
