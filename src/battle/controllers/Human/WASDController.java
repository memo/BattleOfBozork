package battle.controllers.Human;

import asteroids.Action;
import battle.BattleController;
import battle.SimpleBattle;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by jwalto on 12/06/2015.
 */
public class WASDController implements BattleController, KeyListener {
    public static final Action FORWARD = new Action(1, 0, false);
    public static final Action LEFT = new Action(0, -1, false);
    public static final Action RIGHT = new Action(0, 1, false);
    public static final Action FIRE = new Action(0, 0, true);
    public static final Action NOOP = new Action(0, 0, false);

    Action currentAction = NOOP;

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        if (currentAction == null) {
            return NOOP;
        }

        Action lastAction = currentAction;
        currentAction = null;
        return lastAction;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                currentAction = FORWARD;
                break;

            case KeyEvent.VK_A:
                currentAction = LEFT;
                break;

            case KeyEvent.VK_D:
                currentAction = RIGHT;
                break;

            case KeyEvent.VK_SPACE:
                currentAction = FIRE;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
