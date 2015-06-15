package battle;

import javax.swing.*;

import asteroids.Action;
import battle.controllers.EmptyController;
import battle.controllers.FireController;
import battle.controllers.RotateAndShoot;
import battle.controllers.Dani.DaniController;
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

        BattleController fire1 = new DaniController();
        //BattleController fire2 = new MemoControllerRandom();
        BattleController fire2 = new MMMCTS();
        //BattleController fire2 = new MemoController1();

        battle.playGame(fire1, fire2);
    }

}
