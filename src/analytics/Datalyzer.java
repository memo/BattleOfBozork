package analytics;

import battle.SimpleBattle;
import math.Vector2d;

import java.io.File;
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
        public boolean shooting = false;

        public boolean gotHit = false;
        public int numBullets = 0;
        public int score = 0;
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

    public void frame( SimpleBattle gameState )
    {
        // we assume players with id 0 and 1
        for( int i = 0; i < 2; i++ )
        {

        }
    }

}
