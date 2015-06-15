package battle.controllers.webpigeon;


import asteroids.Action;
import battle.DebugController;
import battle.SimpleBattle;
import battle.controllers.EmptyController;
import ga.SimpleRandomHillClimberEngine;

import java.awt.*;
import java.util.Arrays;

/**
 * Created by jwalto on 12/06/2015.
 */
public class StaticEvolver {
    private GameEvaluator eval;
    private SimpleRandomHillClimberEngine rch;

    public StaticEvolver(SimpleBattle battle) {
        this.eval = new GameEvaluator(battle, true);
        this.rch = new SimpleRandomHillClimberEngine(new double[]{2.7631328506251744, 0.746687716615824, 0.11574670823251669}, eval);
    }

    public double[] getBest() {
        return rch.run(100);
    }

    public static void main(String[] args) {
        SimpleBattle start = new SimpleBattle(false);
        start.reset();

        StaticEvolver evo = new StaticEvolver(start);
        double[] best = evo.getBest();

        System.out.println(Arrays.toString(best));
    }


}
