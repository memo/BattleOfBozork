import analytics.Datalyzer;
import battle.BattleController;
import battle.SimpleBattle;
import battle.controllers.Dani.DaniController;
import battle.controllers.Piers.PiersMCTS;

import java.util.ArrayList;

/**
 * Created by pwillic on 17/06/2015.
 */
public class RunGames {

    public static void main(String[] args) {
        SimpleBattle game = new SimpleBattle(false);
        String prefix = args[0];
        int runsPerMatchup =Integer.parseInt(args[1]);

        ArrayList<BattleController> controllers = new ArrayList<>();
        controllers.add(new DaniController());
        controllers.add(new PiersMCTS());

        for (int i = 0; ; i++) {
            game.randomizeParams();
            for (BattleController p1 : controllers) {
                for (BattleController p2 : controllers) {
                    for (int runs = 0; runs < runsPerMatchup; runs++) {
                        String name = prefix + "_pSet" + i +
                                "_p1" + p1.getClass().getSimpleName() +
                                "_p2" + p2.getClass().getSimpleName()
                                + "_run" + runs;
                        System.out.println("Starting: " + name);
                        game.playGame(p1, p2, new Datalyzer(name), false);
                        game.reset();
                    }
                }
            }

        }
    }
}
