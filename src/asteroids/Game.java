package asteroids;

import utilities.ElapsedTimer;
import utilities.JEasyFrame;

import static asteroids.Constants.*;

public class Game {

    // todo: Implement and Test Saucer
    // todo: a high-score facility
    // todo: experiment with steering mechanisms
    // todo: make a string centering mechanism

    View view;
    GameState gameState;
    JEasyFrame frame;
    Controller controller;

    static boolean copyTest = false;

    public static void main(String[] args) {
        System.out.println(font);
        boolean visible = true;
        int nTicks = 10000;
        Game game = new Game(visible);

        ElapsedTimer t = new ElapsedTimer();
        game.run(nTicks);
        System.out.println(t);


//        if (visible) {
//            game.run(nTicks);
//        } else {
//            ElapsedTimer t = new ElapsedTimer();
//            game.runHeadless(nTicks);
//            System.out.println(t);
//        }
    }

    public Game(boolean visible) {
        gameState = new GameState(3);
        if (visible) {
            view = new View(gameState);
            frame = new JEasyFrame(view, "Simple Asteroids");
        }
        if (visible) {
            controller = new KeyController();
            // controller = new RotateAndShoot();
        } else {
            controller = new RotateAndShoot();
        }
        if (controller instanceof KeyController) {
            frame.addKeyListener((KeyController) controller);
        }
    }

    public void run(int maxTicks) {
        int i = 0;
        while (gameState.gameOn() && ++i < maxTicks) {
            gameState.ship.action = controller.action(gameState);

            // check that copying works!
            // question: what part of the game state is not being copied correctly?
            // The Ship perhaps?

            // System.out.println("Original: " + gameState.ship.action);
            // System.out.println("Copy: " + gameState.ship.action);
            if (copyTest)
                gameState = gameState.copy();

            gameState.update();
            // System.out.println("After update: " + gameState.ship.action);

            // update the view to show this one
            if (view != null) {
                // in case the gameState has been copied
                view.game = gameState;
                // System.out.println(gameState.ship.s);
                view.repaint();
                sleep();
            }
        }
        System.out.println("Ran for " + i + " game ticks");
        System.out.println("Score = " + gameState.score);
    }

    public void sleep() {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
