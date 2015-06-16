package battle;

import asteroids.Action;
import asteroids.Constants;
import asteroids.GameObject;
import asteroids.Missile;
import math.Vector2d;
import utilities.JEasyFrame;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static asteroids.Constants.*;

import analytics.Datalyzer;
import msafluid.MSAFluidSolver2D;

/**
 * Created by simon lucas on 10/06/15.
 * <p>
 * Aim here is to have a simple battle class
 * that enables ships to fish with each other
 * <p>
 * Might start off with just two ships, each with their own types of missile.
 */

public class SimpleBattle {
    boolean DO_FLUID = true;
    static float FLUID_VEL_MULT = 1;
    static float FLUID_COLOR_MULT = 0.5f;
    static int FLUID_NX = 100;
    static int FLUID_NY = 100;
    static float FLUID_DT = 1.0f;
    static float FLUID_VISC = 0.00001f;
    static float FLUID_FADESPEED = 0.02f;
    static int FLUID_SOLVER_ITERATIONS = 2;


    // play a time limited game with a strict missile budget for
    // each player
    static int nMissiles = 100;
    static int nTicks = 1000;
    static int pointsPerKill = 10;
    static int releaseVelocity = 5;

    boolean visible = true;

    ArrayList<BattleController> controllers;

    ArrayList<GameObject> objects;
    ArrayList<PlayerStats> stats;

    NeuroShip s1, s2;
    BattleController p1, p2;
    BattleView view;
    int currentTick;

    MSAFluidSolver2D fluid;
    java.awt.image.BufferedImage fluid_image;


    public SimpleBattle() {
        this(true);
    }

    public SimpleBattle(boolean visible) {
        this.objects = new ArrayList<>();
        this.stats = new ArrayList<>();
        this.visible = visible;

        if (visible) {
            view = new BattleView(this);
            new JEasyFrame(view, "battle");
        }

        if(DO_FLUID) {
          //  FLUID_NY = FLUID_NX * Constants.height / Constants.width;
            fluid = new MSAFluidSolver2D(FLUID_NX, FLUID_NY);
            fluid.enableRGB(true);
            fluid.setDeltaT(FLUID_DT);
            fluid.setFadeSpeed(FLUID_FADESPEED);
            fluid.setSolverIterations(FLUID_SOLVER_ITERATIONS);
            fluid.setVisc(FLUID_VISC);
            fluid.randomizeColor();

            fluid_image = new BufferedImage(fluid.getWidth(), fluid.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        }

    }

    public int getTicks() {
        return currentTick;
    }

    public int playGame(BattleController p1, BattleController p2 ) {
        return playGame(p1, p2, null);
    }

    public int playGame(BattleController p1, BattleController p2, Datalyzer datalyzer ) {
        this.p1 = p1;
        this.p2 = p2;
        reset();
        makeAsteroids(numberOfAsteroids);
        stats.add(new PlayerStats(0, 0));
        stats.add(new PlayerStats(0, 0));

        if(datalyzer!=null)
            datalyzer.begin();

        if (p1 instanceof KeyListener) {
            view.addKeyListener((KeyListener) p1);
            view.setFocusable(true);
            view.requestFocus();
        }

        if (p2 instanceof KeyListener) {
            view.addKeyListener((KeyListener) p2);
            view.setFocusable(true);
            view.requestFocus();
        }

        while (!isGameOver()) {
            update(datalyzer);
        }

        if (p1 instanceof KeyListener) {
            view.removeKeyListener((KeyListener) p1);
        }
        if (p2 instanceof KeyListener) {
            view.removeKeyListener((KeyListener) p2);
        }

        if(datalyzer!=null)
            datalyzer.end(this);
        return 0;
    }

    public void reset() {
        stats.clear();
        objects.clear();
        s1 = buildShip(100, 250, 0);
        s2 = buildShip(500, 250, 1);
        this.currentTick = 0;

        stats.add(new PlayerStats(0, 0));
        stats.add(new PlayerStats(0, 0));

        if(DO_FLUID) fluid.reset();
    }

    protected NeuroShip buildShip(int x, int y, int playerID) {
        Vector2d position = new Vector2d(x, y, true);
        Vector2d speed = new Vector2d(true);
        Vector2d direction = new Vector2d(1, 0, true);

        return new NeuroShip(position, speed, direction, playerID);
    }

    public void update()
    {
        update(null);
    }

    public void update( Datalyzer datalyzer)
    {
        // get the actions from each player

        // apply them to each player's ship, taking actions as necessary
        Action a1 = p1.getAction(this.clone(), 0);
        Action a2 = p2.getAction(this.clone(), 1);
        update(a1, a2);

        if(datalyzer!=null)
            datalyzer.frame(this, new Action[]{a1,a2});
    }

    private void advect_fluid(GameObject o) {
        float norm_x = (float)o.s.x / (float)Constants.width;
        float norm_y = (float)o.s.y / (float)Constants.height;
        float vel_x = (float)o.v.x / (float)Constants.width;
        float vel_y = (float)o.v.y / (float)Constants.height;


        int fluid_index = fluid.getIndexForNormalizedPosition(norm_x, norm_y);

        Color drawColor;

        float hue = (norm_x + norm_y); // + time
        drawColor = Color.getHSBColor(hue, 1, 1);

        fluid.rOld[fluid_index]  += drawColor.getRed() / 255.0f * FLUID_COLOR_MULT;
        fluid.gOld[fluid_index]  += drawColor.getGreen() / 255.0f * FLUID_COLOR_MULT;
        fluid.bOld[fluid_index]  += drawColor.getBlue() / 255.0f * FLUID_COLOR_MULT;

        fluid.uOld[fluid_index] += vel_x * FLUID_VEL_MULT;
        fluid.vOld[fluid_index] += vel_y * FLUID_VEL_MULT;
    }

    public void update(Action a1, Action a2) {
        // now apply them to the ships
        s1.update(a1);
        s2.update(a2);

        checkCollision(s1);
        checkCollision(s2);
        for(GameObject object : objects) checkCollision(object);

        // check collision with trail
        if(s1.collisionWithTrail(s2, 0)) {
            System.out.println("s1 HIT");
            s2.hit();
        }

        if(s2.collisionWithTrail(s1, 0)) {
            System.out.println("s1 HIT");
            s1.hit();
        }

        // and fire any missiles as necessary
        if (a1.shoot) fireMissile(s1.s, s1.d, 0);
        if (a2.shoot) fireMissile(s2.s, s2.d, 1);

        wrap(s1);
        wrap(s2);

        // here need to add the game objects ...
        java.util.List<GameObject> killList = new ArrayList<GameObject>();
        for (GameObject object : objects) {
            object.update();
            wrap(object);

            if (object.dead()) {
                killList.add(object);
            } else {

                // advect fluid
                if(DO_FLUID) advect_fluid(object);
                s1.collisionWithTrail(object, 1);
                s2.collisionWithTrail(object, 1);
            }
        }

        // solve fluid
        if(DO_FLUID) {
            advect_fluid(s1);
            advect_fluid(s2);
            fluid.update();
        }

        objects.removeAll(killList);
        currentTick++;

        if (visible) {
            view.repaint();
            sleep();
        }
    }


    public SimpleBattle clone() {
        SimpleBattle state = new SimpleBattle(false);
        state.objects = copyObjects();
        state.stats = copyStats();
        state.currentTick = currentTick;
        state.visible = false; //stop MCTS people having all the games :p
        state.DO_FLUID = false;

        state.s1 = s1.copy();
        state.s2 = s2.copy();
        return state;
    }

    protected ArrayList<GameObject> copyObjects() {
        ArrayList<GameObject> objectClone = new ArrayList<GameObject>();
        for (GameObject object : objects) {
            objectClone.add(object.copy());
        }

        return objectClone;
    }

    protected ArrayList<PlayerStats> copyStats() {
        ArrayList<PlayerStats> statsClone = new ArrayList<PlayerStats>();
        for (PlayerStats object : stats) {
            statsClone.add(new PlayerStats(object.nMissiles, object.nPoints));
        }

        return statsClone;
    }

    protected void checkCollision(GameObject actor) {
        // check with all other game objects
        // but use a hack to only consider interesting interactions
        // e.g. asteroids do not collide with themselves
        if (!actor.dead() &&
                (actor instanceof BattleMissile
                        || actor instanceof NeuroShip)) {
            for (GameObject ob : objects) {
                if (overlap(actor, ob)) {
                    // the object is hit, and the actor is also

                    int playerID = (actor == s1 ? 1 : 0);
                    PlayerStats stats = this.stats.get(playerID);
                    stats.nPoints += pointsPerKill;

                    ob.hit();
                    actor.hit();
                    return;
                }
            }
        }
    }

    private boolean overlap(GameObject actor, GameObject ob) {
        if (actor.equals(ob)) {
            return false;
        }
        // otherwise do the default check
        double dist = actor.s.dist(ob.s);
        boolean ret = dist < (actor.r() + ob.r());
        return ret;
    }

    public void sleep() {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void fireMissile(Vector2d s, Vector2d d, int playerId) {
        // need all the usual missile firing code here
        NeuroShip currentShip = playerId == 0 ? s1 : s2;
        PlayerStats stats = this.stats.get(playerId);
        if (stats.nMissiles < nMissiles) {
            Missile m = new Missile(s, new Vector2d(0, 0, true));
            m.v.add(d, releaseVelocity);
            // make it clear the ship
            m.s.add(m.v, (currentShip.r() + missileRadius) * 1.5 / m.v.mag());
            objects.add(m);
            // System.out.println("Fired: " + m);
            // sounds.fire();
            stats.nMissiles++;
        }
    }

    public void draw(Graphics2D g) {
        // for (Object ob : objects)
        if (s1 == null || s2 == null) {
            return;
        }

        // System.out.println("In draw(): " + n);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fillRect(0, 0, size.width, size.height);

        // draw fluid
        if(DO_FLUID) {
            int []fluid_image_data = new int[fluid.getNumCells()];
            for (int i = 0; i < fluid.getNumCells(); i++) {
                int cr = (int) Math.min(255.0, fluid.r[i] * 255.0);
                int cg = (int) Math.min(255.0, fluid.g[i] * 255.0);
                int cb = (int) Math.min(255.0, fluid.b[i] * 255.0);
                int col = (cr << 16) | (cg << 8) | cb;
                fluid_image_data[i] = col;
               // int ix = i % fluid_image.getWidth();
               // int iy = i / fluid_image.getHeight();
               // fluid_image.setRGB(ix, iy, col);
            }
            fluid_image.setRGB(0, 0, fluid_image.getWidth(), fluid_image.getHeight(), fluid_image_data, 0, fluid_image.getWidth());
            g.drawImage(fluid_image, 0, 0, Constants.width, Constants.height, null);
        }

        for (GameObject go : objects) {
            go.draw(g);
        }

        s1.draw(g);
        if (p1 instanceof RenderableBattleController) {
            RenderableBattleController rbc = (RenderableBattleController) p1;
            rbc.render(g, s1.copy());
        }

        s2.draw(g);
        if (p2 instanceof RenderableBattleController) {
            RenderableBattleController rbc = (RenderableBattleController) p2;
            rbc.render(g, s2.copy());
        }
    }

    public NeuroShip getShip(int playerID) {
        assert playerID < 2;
        assert playerID >= 0;

        if (playerID == 0) {
            return s1.copy();
        } else {
            return s2.copy();
        }
    }

    public ArrayList<GameObject> getObjects() {
        return new ArrayList<>(objects);
    }

    public int getPoints(int playerID) {
        assert playerID < 2;
        assert playerID >= 0;

        return stats.get(playerID).nPoints;
    }

    public int getMissilesLeft(int playerID) {
        assert playerID < 2;
        assert playerID >= 0;

        return stats.get(playerID).nMissiles - nMissiles;
    }

    private void wrap(GameObject ob) {
        // only wrap objects which are wrappable
        if (ob.wrappable()) {
            ob.s.x = (ob.s.x + width) % width;
            ob.s.y = (ob.s.y + height) % height;
        }
    }

    public boolean isGameOver() {
        if (getMissilesLeft(0) >= 0 && getMissilesLeft(1) >= 0) {
            //ensure that there are no bullets left in play
            if (objects.isEmpty()) {
                return true;
            }
        }

        return currentTick >= nTicks;
    }

    // Only call this after making the ships for the safety code to work
    private void makeAsteroids(int numberOfAsteroids) {
        ArrayList<GameObject> createdAsteroids = new ArrayList<>(numberOfAsteroids);
        double safeRadius = height / 20;
        while(createdAsteroids.size() < numberOfAsteroids){
            Vector2d randomPosition = Vector2d.getRandomCartesian(width, height, true);
            Vector2d randomVelocity = Vector2d.getRandomPolar(2 * Math.PI, 0.5, 1.0, true);
            if(Math.min(randomPosition.dist(s1.s), randomPosition.dist(s2.s)) > safeRadius){
                createdAsteroids.add(new Asteroid(randomPosition, randomVelocity, 0));
            }
        }

        objects.addAll(createdAsteroids);
    }

    static class PlayerStats {
        int nMissiles;
        int nPoints;

        public PlayerStats(int nMissiles, int nPoints) {
            this.nMissiles = nMissiles;
            this.nPoints = nPoints;
        }

        public int getMissilesFired() {
            return nMissiles;
        }

        public int getPoints() {
            return nPoints;
        }

        public String toString() {
            return nMissiles + " : " + nPoints;
        }
    }

}
