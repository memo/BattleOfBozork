package battle;

import javax.swing.*;

import analytics.Datalyzer;
import asteroids.Action;
import battle.controllers.EmptyController;
import battle.controllers.FireController;
import battle.controllers.Human.WASDController;
import battle.controllers.Piers.PiersMCTS;
import battle.controllers.RotateAndShoot;
import battle.controllers.Dani.DaniController;
import battle.controllers.Dani.DaniControllerEvo;
import battle.controllers.Dani.ForceControllerTest;
import battle.controllers.Memo.MemoController1;
import battle.controllers.Memo.MemoControllerRandom;
import battle.controllers.mmmcts.MMMCTS;
import math.Vector2d;
import utilities.JEasyFrame;

/**
 * Created by simon lucas on 10/06/15.
 */
public class DaniBattleTest {
    BattleView view;

    public static void main(String[] args) {

        SimpleBattle battle = new SimpleBattle();

        for( int i = 0; i < 10; i++ )
        {
            BattleController fire1 = new ForceControllerTest();
            //BattleController fire2 = new MemoControllerRandom();
            //BattleController fire2 = new MMMCTS();
            //BattleController fire1 = new WASDController();
            BattleController fire2 = new ForceControllerTest();

            battle.playGame(fire1, fire2, new Datalyzer("Game_"+i));
        }


    }

}
