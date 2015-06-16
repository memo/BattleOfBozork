package asteroids;

import math.Vector2d;

import java.awt.*;

import static asteroids.Constants.missileTTL;

public class Missile extends GameObject {

    int ttl;
    int playerID = -1;

    public Missile(Vector2d s, Vector2d v) {
        super(s, v);
        ttl = missileTTL;
        r = 6;
    }

    public Missile(Vector2d s, Vector2d v, int playerID) {
        this(s, v);
        this.playerID = playerID;
    }

    @Override
    public void update() {
        if (!dead()) {
            s.add(v);
            ttl--;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.black);
        g.fillOval((int) (s.x-r), (int) (s.y-r), (int) r * 2, (int) r * 2);
        g.setColor(Color.white);
        g.drawOval((int) (s.x-r), (int) (s.y-r), (int) r * 2, (int) r * 2);
    }

    @Override
    public GameObject copy() {
        Missile object = new Missile(s, v);
        object.ttl = ttl;

        return object;
    }

    public boolean dead() {
        return ttl <= 0;
    }

    public void hit() {
        // kill it by setting ttl to zero
        ttl = 0;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String toString() {
        return ttl + " :> " + s;
    }


}
