package battle.controllers.Piers;

import asteroids.Action;
import asteroids.GameObject;
import asteroids.Missile;
import battle.BattleController;
import battle.SimpleBattle;

/**
 * Created by pwillic on 11/06/2015.
 */
public class PiersMCTS implements BattleController {

    protected static final int ACTIONS_PER_MACRO_ENEMY_CLOSE = 3;
    protected static final int ACTIONS_PER_MACRO_ENEMY_FAR = 10;
    protected static final double DISTANCE_THRESHOLD = 100;
    protected int ACTIONS_PER_MACRO = 10;
    int rolloutsPerMacroAction = 0;
    private MacroAction currentBestAction = new MacroAction(new Action(1, 0, false));
    private BetterMCTSNode root;

    public PiersMCTS() {
//        MCTSNode.setAllActions();
//        BetterMCTSNode.setAllActions();
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        GameTimer timer = new GameTimer();
        timer.setTimeBudgetMilliseconds(40);
        Action action = currentBestAction.getAction();
        double shortestDistance = Double.MAX_VALUE;
        for (GameObject object : gameStateCopy.getObjects()) {
            if (object instanceof Missile) {
                Missile missile = (Missile) object;
                if (missile.getPlayerID() == playerId) continue;
            }
            double distance = gameStateCopy.getShip(playerId).s.dist(object.s);
            if (distance < shortestDistance) {
                shortestDistance = distance;
            }
        }
        ACTIONS_PER_MACRO = (shortestDistance > DISTANCE_THRESHOLD) ? ACTIONS_PER_MACRO_ENEMY_FAR : ACTIONS_PER_MACRO_ENEMY_CLOSE;


        if (root == null) root = new BetterMCTSNode(2.0, playerId, this);

        if (currentBestAction.getTimesUsed() >= ACTIONS_PER_MACRO) root = new BetterMCTSNode(2.0, playerId, this);

        int i = 0;
        while (timer.remainingTimePercent() > 10) {
            SimpleBattle copy = gameStateCopy.clone();
//            System.out.println(root);
            BetterMCTSNode travel = root.select(copy, 3);
            if(travel != null) {
                double result = travel.rollout(copy, 5);
                travel.updateValues(result);
            }
            i++;
        }
        rolloutsPerMacroAction += i;

//        System.out.println("Rollouts achieved: " + i);

        if (currentBestAction.getTimesUsed() >= ACTIONS_PER_MACRO) {
            currentBestAction = new MacroAction(root.getBestAction());
//            System.out.println("Rollouts Achieved this macro action: " + rolloutsPerMacroAction);
            rolloutsPerMacroAction = 0;
        }
        return action;
    }


}

class MacroAction {
    private Action action;
    private int timesUsed;
    private Action nonShootingVersion;

    public MacroAction(Action action) {
        this.action = action;
        nonShootingVersion = new Action(action.thrust, action.turn, false);
        timesUsed = 0;
    }

    /**
     * returns the action and increments the number of times it has been used
     *
     * @return
     */
    public Action getAction() {
        timesUsed++;
        return (timesUsed <= 2) ? action : nonShootingVersion;
    }

    public int getTimesUsed() {
        return timesUsed;
    }
}
