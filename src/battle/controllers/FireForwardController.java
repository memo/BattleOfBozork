package battle.controllers;

import asteroids.Action;
import battle.*;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by davidgundry on 11/06/15.
 */
public class FireForwardController extends DebugController {
    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        return new Action(1,0,true);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawLine(0, 0, 0, -10);
    }
}
