package analytics;

import asteroids.Action;
import battle.NeuroShip;
import battle.SimpleBattle;
import math.Vector2d;
import math.Util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by colormotor on 15/06/15.
 */
public class Datalyzer {
    String name;
    static String path = "data/";
    File frameFile;
    File resultsFile;


    public class PlayerFrame
    {
        public Vector2d pos = new Vector2d(0,0);

        // action
        public double thrust = 0.0;
        public double turn = 0.0;
        public int shooting = 0;

        public int numBullets = 0;
        public int score = 0;


    }

    public String getCsvLabels()
    {
        return "pos_x, pos_y, thrust, turn, shooting, num_bullets, score\n";
    }

    public class PlayerResult
    {
        public int score;
        public boolean win;
    }

    public ArrayList<PlayerFrame>[] playerFrames;
    public PlayerResult [] results;

    public Datalyzer()
    {
    }

    public void begin()
    {
        // :S
        playerFrames =  (ArrayList<PlayerFrame>[])new ArrayList[2];
        results = new PlayerResult[2];
    }

    public void end( SimpleBattle gameState, String name )
    {
        for( int i = 0; i < 2; i++ )
        {
            /*
            FileWriter frameFile = new FileWriter(path + name + "_player" + i + ".csv");

            frameFile.write( getCsvLabels() );

            int n = playerFrames[i].size();

            for( int j = 0; j < n; j++ )
            {
                PlayerFrame pf = playerFrames[i].get(j);
                String line =   pf.pos.x + ", " +
                                pf.pos.y + ", " +
                                pf.thrust + ", " +
                                pf.turn + ", " +
                                pf.shooting + ", " +
                                pf.numBullets + ", " +
                                pf.score;
            }*/
        }
    }

    public void frame( SimpleBattle gameState, Action[] actions )
    {
        // we assume players with id 0 and 1
        for( int i = 0; i < 2; i++ )
        {
            NeuroShip ship = gameState.getShip(i);
            Action action = actions[i];

            PlayerFrame frame = new PlayerFrame();

            frame.pos = new Vector2d(ship.s);

            frame.thrust = action.thrust;
            frame.turn = action.turn;
            frame.shooting = Util.boolToInt(action.shoot);

            frame.numBullets = gameState.getMissilesLeft(i);
            frame.score = gameState.getPoints(i);

            playerFrames[i].add(frame);
        }
    }

}
