package battle.controllers.Piers;

import battle.BattleController;
import battle.SimpleBattle;
import battle.controllers.EmptyController;
import battle.controllers.Memo.MemoControllerRandom;
import battle.controllers.RotateAndShoot;
import battle.controllers.mmmcts.MMMCTS;

/**
 * Created by pwillic on 12/06/2015.
 */
public class PiersBattleTest {

    public static void main(String[] args) {
        SimpleBattle battle = new SimpleBattle();
        BattleController player1 = new EmptyController();
        BattleController player2 = new RotateAndShoot();
        battle.playGame(player1, player2);
    }
}
