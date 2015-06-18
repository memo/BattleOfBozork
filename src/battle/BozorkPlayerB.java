package battle;

import analytics.Datalyzer;

import battle.controllers.Dani.ForceControllerTest;
import battle.controllers.Human.ArrowsController;
import battle.controllers.Memo.MemoControllerRandom;
import battle.controllers.Memo.MemoController1;
import battle.controllers.mmmcts.MMMCTS;
import battle.controllers.Human.WASDController;

/**
 * Created by simon lucas on 10/06/15.
 */
public class BozorkPlayerB {
    BattleView view;

    public static void main(String[] args) {

        SimpleBattle battle = new SimpleBattle();
        SimpleBattle.game_version = "B";
        BattleController player1 = new ArrowsController();
        BattleController player2 = new ForceControllerTest();
        battle.playGame(player1, player2 );
    }

}
