package battle;

import asteroids.GameObject;
import math.Vector2d;

import java.awt.*;

import static asteroids.Constants.*;

public class BattleMissile extends GameObject {

    int ttl;
    int id;
    Color color;

    public BattleMissile(int start_ttl, Vector2d s, Vector2d v, int id) {
        super(s, v);
        this.id = id;
        color = pColors[id];
        ttl = start_ttl;
        r = 4;
    }

    @Override
    public void update() {
        if (!dead()) {
            s.add(v);
            ttl--;
        }
    }

    @Override
    public BattleMissile copy() {
        BattleMissile copy = new BattleMissile(ttl, s, v, id);
        updateClone(copy);
       // copy.ttl = ttl;
        copy.color = color;
        return copy;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) (s.x-r), (int) (s.y-r), (int) r * 2, (int) r * 2);
    }

    public boolean dead() {
        return ttl <= 0;
    }

    public void hit() {
        // kill it by setting ttl to zero
        ttl = 0;
    }

    public String toString() {
        return ttl + " :> " + s;
    }


}
