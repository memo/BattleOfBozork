package battle;

import asteroids.GameState;
import asteroids.Ship;
import math.Vector2d;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import static asteroids.Constants.*;
import static java.awt.Color.black;


public class BattleView extends JComponent {
    static int offset = 0;
    int scale;
    // static int carSize = 5;
    static Color bg = black;
    SimpleBattle game;
    // Font font;

    Ship ship;

    static double viewScale = 1.0;


    public BattleView(SimpleBattle game) {
        this.game = game;
        scale = size.width - 2 * offset;

    }

    public void paintComponent(Graphics gx) {
        Graphics2D g = (Graphics2D) gx;
        AffineTransform at = g.getTransform();
        g.translate((1 - viewScale) * width / 2, (1-viewScale)*height / 2);


        // this was an experiment to turn it into a side-scroller
        // but it produces a weird moving screen effect
        // needs more logic in the drawing process
        // to wrap the asteroids that have been projected off the screen
        // g.translate(-(game.ship.s.x - width/2), 0);

        g.scale(viewScale, viewScale);

        game.draw(g);
        g.setTransform(at);
        paintState(g);
    }


    public void paintState(Graphics2D g) {
        g.setColor(Color.white);
        g.setFont(font);
        // String str = "" + game.score + " : " + game.list.nShips() + " : " + game.state
        //         + " : " + game.list.isSafe(game.ship) + " : " + game.nLives;
        // FontMetrics fm = font.

        String str = "Battle state goes here";
        g.drawString(str, 10, 20);
    }


    public Dimension getPreferredSize() {
        return size;
    }


}
