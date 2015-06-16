package gameRunner;

import analytics.Datalyzer;
import battle.BattleController;
import battle.SimpleBattle;
import battle.controllers.Dani.DaniController;
import battle.controllers.Memo.MemoController1;
import battle.controllers.Piers.PiersMCTS;
import battle.controllers.RotateAndShoot;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for running multiple games and logging the results
 * <p>
 * Created by pwillic on 16/06/2015.
 */
public class GameRunner {


    private static ExecutorService threadPool;
    private int NUMBER_OF_THREADS = 4;
    private ArrayList<Callable<Object>> runnerThreads = new ArrayList<>();
    private ArrayList<BattleController> controllers;
    private int gamesPerMatchup = 10;

    public GameRunner(int numberOfThreads, ArrayList<BattleController> controllers, int gamesPerMatchup) {
        this.NUMBER_OF_THREADS = numberOfThreads;
        this.controllers = controllers;
        this.gamesPerMatchup = gamesPerMatchup;
        if(threadPool == null){
            threadPool = Executors.newFixedThreadPool(numberOfThreads);
        }
    }

    public void runTheGames() {
        SimpleBattle battle = new SimpleBattle(false);
        battle.DO_FLUID = false;
        battle.reset();

        for(BattleController p1 : controllers){
            for(BattleController p2 : controllers){
                if(p1 != p2){
                    for(int i = 0; i < gamesPerMatchup; i++) {
                        runnerThreads.add(new GameRunnerWrapper(battle.clone(), p1, p2, p1.getClass().getSimpleName() + "-" + p2.getClass().getSimpleName() + "-" + i));
                    }
                }
            }
        }
        System.out.println("Starting the games");
        try {
            threadPool.invokeAll(runnerThreads);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadPool.shutdown();
    }

    public static void main(String[] args) {
        ArrayList<BattleController> controllers = new ArrayList<>();
//        controllers.add(new DaniController());
        controllers.add(new MemoController1());
        controllers.add(new RotateAndShoot());
//        controllers.add(new PiersMCTS());

        GameRunner runner = new GameRunner(3, controllers, 100);

        runner.runTheGames();
    }

}

class GameRunnerWrapper implements Callable<Object> {
    SimpleBattle game;
    BattleController p1;
    BattleController p2;
    String name;

    public GameRunnerWrapper(SimpleBattle game, BattleController p1, BattleController p2, String name) {
        this.game = game;
        this.p1 = p1;
        this.p2 = p2;
        this.name = name;
    }

    @Override
    public Object call() throws Exception {
        try {
            game.playGame(p1, p2, new Datalyzer(name));
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Finished: " + name);
        return null;
    }
}
