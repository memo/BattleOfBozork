package battle.controllers.Piers;

import asteroids.Action;
import battle.NeuroShip;
import battle.SimpleBattle;

import java.util.Random;

/**
 * Created by pwillic on 11/06/2015.
 */
public class MCTSNode {

    private static final double EPSILON = 1e-6;
    private static Action[] allActions;
    private static Action[] notShootActions;
    private static Action[][] allActionPairs;
    private static Action[][] p1Notp2Not;
    private static Action[][] p1Notp2Yes;
    private static Action[][] p1Yesp2Not;
    private static Random random = new Random();
    private static int numberOfActionsPerState = 15;
    private Action ourMoveToThisState;
    private Action enemyMoveToThisState;

    private Action[][] possibleActions;

    private MCTSNode parent;
    private MCTSNode[] children;
    private int numberOfChildrenExpanded;
    private boolean ourNode;

    private int currentDepth;
    private int playerID;

    private double totalValue = 0;
    private double enemyTotalValue = 0;
    private int numberOfVisits = 1;

    private double explorationConstant;

    public MCTSNode(double explorationConstant, int playerID) {
        this.explorationConstant = explorationConstant;
        currentDepth = 0;
        this.playerID = playerID;
        ourNode = true;
        children = new MCTSNode[allActionPairs.length];
        possibleActions = p1Yesp2Not;
    }

    private MCTSNode(MCTSNode parent, Action ourMoveToThisState, Action enemyMoveToThisState, Action[][] possibleActions) {
        this.explorationConstant = parent.explorationConstant;
        this.ourMoveToThisState = ourMoveToThisState;
        this.enemyMoveToThisState = enemyMoveToThisState;
        this.parent = parent;
        this.children = new MCTSNode[allActionPairs.length];
        this.currentDepth = parent.currentDepth + 1;
        this.playerID = parent.playerID;
        this.ourNode = parent.ourNode;
        this.possibleActions = possibleActions;
    }

    public static void setAllActions() {
        allActions = new Action[12];
        notShootActions = new Action[6];
        int i = 0;
        int j = 0;
        for (double thrust = 0; thrust <= 1; thrust += 1) {
            for (double turn = -1; turn <= 1; turn += 1) {
                allActions[i] = new Action(thrust, turn, true);
                i++;
                allActions[i] = new Action(thrust, turn, false);
                notShootActions[j] = allActions[i];
                i++;
                j++;
            }
        }
        i = 0;
        allActionPairs = new Action[144][2];
        p1Yesp2Not = new Action[72][2];
        p1Notp2Yes = new Action[72][2];
        p1Notp2Not = new Action[36][2];
        for (Action action : allActions) {
            for (Action otherAction : allActions) {
                allActionPairs[i++] = new Action[]{
                        action, otherAction
                };
            }
        }
        i = 0;
        for (Action action : notShootActions) {
            for (Action otherAction : notShootActions) {
                p1Notp2Not[i++] = new Action[]{action, otherAction};
            }
        }
        i = 0;
        for (Action action : notShootActions) {
            for (Action otherAction : allActions) {
                p1Notp2Yes[i] = new Action[]{action, otherAction};
                p1Yesp2Not[i] = new Action[]{otherAction, action};
                i++;
            }
        }
    }

    public MCTSNode select(SimpleBattle state, int maxDepth) {
        MCTSNode current = this;
        while (current.currentDepth <= maxDepth) {
            if (current.fullyExpanded()) {
                current = current.selectBestChild();
                state.update(current.ourMoveToThisState, current.enemyMoveToThisState);
            } else {
                return current.expand(state);
            }
        }
        return current;
    }

    public MCTSNode expand(SimpleBattle state) {

        // Calculate the possible action spaces
        // can we shoot
        NeuroShip p1 = state.getShip(playerID);
        NeuroShip p2 = state.getShip(playerID == 0 ? 1 : 0);
        double p1Seesp2 = p1.s.dist(p2.s);

        double p2Seesp1 = p2.s.dist(p1.s);

        boolean canP1SeeP2 = (p1Seesp2 < 100);
        boolean canP2SeeP1 = (p2Seesp1 < 100);
        Action[][] childPossibleActions = allActionPairs;

        if (canP1SeeP2 && !canP2SeeP1) {
            childPossibleActions = p1Yesp2Not;
        }
        if (!canP1SeeP2 && canP2SeeP1) {
            childPossibleActions = p1Notp2Yes;
        }
        if (!canP1SeeP2 && !canP2SeeP1) {
            childPossibleActions = p1Notp2Not;
        }
        children = new MCTSNode[childPossibleActions.length];

        int childToExpand = random.nextInt(childPossibleActions.length);
        while (children[childToExpand] != null) {
            childToExpand = random.nextInt(childPossibleActions.length);
        }
        children[childToExpand] = new MCTSNode(this, childPossibleActions[childToExpand][0], childPossibleActions[childToExpand][1], childPossibleActions);
        numberOfChildrenExpanded++;
        return children[childToExpand];
    }

    public MCTSNode selectBestChild() {
        return selectBestChildFeaturingEnemyMove(selectBestOpposingAction());
    }

    public Action selectBestOpposingAction() {
        if (children == null) return notShootActions[0];
        double bestScore = -Double.MAX_VALUE;
        int bestIndex = -1;
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                double score = children[i].enemyTotalValue;
                if (score > bestScore) {
                    bestScore = score;
                    bestIndex = i;
                }
            }
        }
        if (bestIndex == -1) return notShootActions[0];
        return children[bestIndex].enemyMoveToThisState;
    }

    public MCTSNode selectBestChildFeaturingEnemyMove(Action enemyMove) {
        double bestScore = -Double.MAX_VALUE;
        int bestIndex = -1;
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                if (children[i].enemyMoveToThisState == enemyMove) {
                    double score = children[i].calculateChild();
                    if (score > bestScore) {
                        bestScore = score;
                        bestIndex = i;
                    }
                }
            }
        }
        return children[bestIndex];
    }

    public double calculateChild() {
        return (totalValue / numberOfVisits) +
                (explorationConstant * (Math.sqrt(Math.log(parent.numberOfVisits) / numberOfVisits)));
    }

    public void updateValues(double value, double enemyScore) {
        MCTSNode current = this;
        double alteredValue = value / 1000;
        double alteredEnemyValue = enemyScore / 1000;
        while (current.parent != null) {
            current.numberOfVisits++;
            current.totalValue += alteredValue;
            current.enemyTotalValue += alteredEnemyValue;
            current = current.parent;
        }
        current.totalValue += alteredValue;
        current.enemyTotalValue += alteredEnemyValue;
        current.numberOfVisits++;
    }

    public double[] rollout(SimpleBattle state, int maxDepth) {
        int currentRolloutDepth = this.currentDepth;
        while (maxDepth > currentRolloutDepth && !state.isGameOver()) {
            Action first = allActions[random.nextInt(allActions.length)];
            Action second = allActions[random.nextInt(allActions.length)];
            state.update(first, second);
        }
        return new double[]{state.getPoints(playerID), state.getPoints((playerID == 1) ? 0 : 1)};
    }

    public Action getBestAction() {
        double bestScore = -Double.MAX_VALUE;
        int bestIndex = -1;
        if (children == null) return allActions[0];

        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                double childScore = children[i].totalValue + (random.nextFloat() * EPSILON);
                if (childScore > bestScore) {
                    bestScore = childScore;
                    bestIndex = i;
                }
            }
        }
        if (bestIndex == -1) return allActions[0];
        return children[bestIndex].ourMoveToThisState;
    }

    private boolean fullyExpanded() {
        return numberOfChildrenExpanded == possibleActions.length;
    }

    public void printAllChildren() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < children.length; i++) {
            builder.append("Value: ");
            builder.append(children[i].totalValue / children[i].numberOfVisits);
            builder.append(" Action: ");
            builder.append(children[i].ourMoveToThisState);
            builder.append("UCB: ");
            builder.append(children[i].calculateChild());
            builder.append("\n");
        }

        System.out.println(builder.toString());
    }

}
