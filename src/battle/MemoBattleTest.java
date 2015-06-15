package battle;

import battle.controllers.Memo.MemoControllerRandom;
import battle.controllers.Memo.MemoController1;
import battle.controllers.mmmcts.MMMCTS;
import battle.controllers.Human.WASDController;

/**
 * Created by simon lucas on 10/06/15.
 */
public class MemoBattleTest {
    BattleView view;

    public static void main(String[] args) {

        SimpleBattle battle = new SimpleBattle();

        BattleController player1 = new MemoController1();
        BattleController player2 = new MMMCTS();
        battle.playGame(player1, player2);
    }

}
