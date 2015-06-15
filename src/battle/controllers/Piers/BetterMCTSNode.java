package battle.controllers.Piers;

import asteroids.Action;
import battle.SimpleBattle;

import java.util.Random;

/**
 * Created by Piers on 12/06/2015.
 */
public class BetterMCTSNode {
    private static final double EPSILON = 1e-6;
    private static Action[] allActions;
    private static Random random = new Random();
    private Action ourMoveToThisState;

    private BetterMCTSNode parent;
    private BetterMCTSNode[] children;
    private int numberOfChildrenExpanded;
    private boolean ourNode;

    private int currentDepth;
    private int playerID;

    private double totalValue = 0;
    private int numberOfVisits = 1;

    private double explorationConstant;

    public BetterMCTSNode(double explorationConstant, int playerID) {
        this.explorationConstant = explorationConstant;
        currentDepth = 0;
        this.playerID = playerID;
        ourNode = true;
        children = new BetterMCTSNode[allActions.length];
    }

    private BetterMCTSNode(BetterMCTSNode parent, Action ourMoveToThisState) {
        this.explorationConstant = parent.explorationConstant;
        this.ourMoveToThisState = ourMoveToThisState;
        this.parent = parent;
        this.children = new BetterMCTSNode[allActions.length];
        this.currentDepth = parent.currentDepth + 1;
        this.playerID = parent.playerID;
        this.ourNode = parent.ourNode;
    }

    public static void setAllActions() {
        allActions = new Action[6];
//        notShootActions = new Action[6];
        int i = 0;
        int j = 0;
        for (double thrust = 1; thrust <= 1; thrust += 1) {
            for (double turn = -1; turn <= 1; turn += 1) {
                allActions[i++] = new Action(thrust, turn, true);
                allActions[i++] = new Action(thrust, turn, false);
            }
        }
    }

    public BetterMCTSNode select(SimpleBattle state, int maxDepth) {
        BetterMCTSNode current = this;
        while (current.currentDepth <= maxDepth) {
            if (current.fullyExpanded()) {
                current = current.selectBestChild();
                for (int i = 0; i < PiersMCTS.ACTIONS_PER_MACRO; i++) {
                    state.update(current.ourMoveToThisState, allActions[random.nextInt(allActions.length)]);
                }
            } else {
                return current.expand(state);
            }
        }
        return current;
    }

    public BetterMCTSNode expand(SimpleBattle state) {

        // Calculate the possible action spaces
        // can we shoot

        children = new BetterMCTSNode[allActions.length];

        int childToExpand = random.nextInt(allActions.length);
        while (children[childToExpand] != null) {
            childToExpand = random.nextInt(allActions.length);
        }
        children[childToExpand] = new BetterMCTSNode(this, allActions[childToExpand]);
        for (int i = 0; i < PiersMCTS.ACTIONS_PER_MACRO; i++) {
            state.update(allActions[childToExpand], allActions[random.nextInt(allActions.length)]);
        }
        numberOfChildrenExpanded++;
        return children[childToExpand];
    }

    public BetterMCTSNode selectBestChild() {
        double bestScore = -Double.MAX_VALUE;
        int bestIndex = -1;
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                double score = children[i].calculateChild();
                if (score > bestScore) {
                    bestScore = score;
                    bestIndex = i;
                }
            }
        }
        return children[bestIndex];
    }

    public double calculateChild() {
        return (totalValue / numberOfVisits) +
                (explorationConstant * (Math.sqrt(Math.log(parent.numberOfVisits) / numberOfVisits)));
    }

    public void updateValues(double value) {
        BetterMCTSNode current = this;
        double alteredValue = value / 1000;
        double discountFactor = 0.95;
        while (current.parent != null) {
            current.numberOfVisits++;
            alteredValue *= discountFactor;
            current.totalValue += (alteredValue);
            current = current.parent;
        }
        current.totalValue += alteredValue;
        current.numberOfVisits++;
    }

    public double rollout(SimpleBattle state, int maxDepth) {
        int currentRolloutDepth = this.currentDepth;
        while (maxDepth > currentRolloutDepth && !state.isGameOver()) {
            Action first = allActions[random.nextInt(allActions.length)];
            Action second = allActions[random.nextInt(allActions.length)];
            state.update(first, second);
        }
        int missilesUsed = 100 - state.getMissilesLeft(playerID);
        int ourPoints = state.getPoints(playerID);
        int enemyPoints = state.getPoints(playerID == 0 ? 1 : 0);
        return (ourPoints - (missilesUsed * 5) - (enemyPoints * 1.5));
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
        return numberOfChildrenExpanded == allActions.length;
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
