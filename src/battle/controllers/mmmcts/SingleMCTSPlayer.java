package battle.controllers.mmmcts;

import battle.SimpleBattle;
import battle.controllers.mmmcts.tools.ElapsedCpuTimer;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 07/11/13
 * Time: 17:13
 */
public class SingleMCTSPlayer
{
    /**
     * Root of the tree.
     */
    public SingleTreeNode m_root;

    /**
     * Random generator.
     */
    public Random m_rnd;

    public int playerID;

    /**
     * Creates the MCTS player with a sampleRandom generator object.
     * @param a_rnd sampleRandom generator object.
     */
    public SingleMCTSPlayer(Random a_rnd)
    {
        m_rnd = a_rnd;
        m_root = new SingleTreeNode(a_rnd);
    }

    /**
     * Inits the tree with the new observation state in the root.
     * @param a_gameState current state of the game.
     */
    public void init(SimpleBattle a_gameState, int playerId)
    {
        //Set the game observation to a newly root node.
        m_root = new SingleTreeNode(m_rnd);
        m_root.state = a_gameState;

        playerID = playerId;
    }

    public int run(ElapsedCpuTimer elapsedTimer)
    {
        //Do the search within the available time.
        m_root.mctsSearch(elapsedTimer, playerID);

        //Determine the best action to take and return it.
        int action = m_root.mostVisitedAction();
        //int action = m_root.bestAction();
        return action;
    }

}
