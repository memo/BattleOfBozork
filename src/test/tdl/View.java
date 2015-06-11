package test.tdl;


import utilities.Range;
import view.colors.ColorTable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * Shows the current state of the grid world.
 * <p/>
 * Question: how best to do this?
 * <p/>
 * There is the size issue: how to map squares to the view.
 * <p/>
 * Can dodge this for now by always assuming that we know the correct
 * size of the grid.
 * <p/>
 * An alternative way would be to to always write to a virtual grid,
 * and then simple map it to the actual grid size using a particular
 * scale.  That would be better in the long run.
 * <p/>
 * The other thing to take care of is in the representation of the
 * value function - this is the backgrounf for each grid square.
 * <p/>
 * Would be good to use the colour table that was set up previously
 * for the FunctionDisplay.
 * <p/>
 * Don't really need to go too overboard on this.  For the agent, simply
 * show a circle...
 */
public class View extends JComponent {

    Model model;
    Color goal = Color.green;
    Color agent = Color.red;
    double[][] a;
    Paint paint;

    public int size = 20;

    public View(Model model, double[][] a) {
        this.model = model;
        this.a = a;
        ColorTable.setColors2();
        // setPlainPaint();
        setPaint();
    }

    public void setPaint() {
        BufferedImage bim = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bim.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, size, size);
        g.setColor(Color.white);
        int c = 0;
        int ss = 5;
        for (int i=0; i<size; i+=ss) {
            for (int j = 0; j<size; j+=ss) {
                g.setColor((c % 2) == 0 ? Color.black : Color.white);
                c++;
                g.fillRect(i, j, ss, ss);
            }
            c++;
        }
        paint = new TexturePaint(bim, new Rectangle(0, 0, size, size));
    }

    public void setPlainPaint() {
        paint = Color.red;
    }

    public void paintComponent(Graphics gg) {
        Graphics2D g = (Graphics2D) gg;

        double min = min(a);
        double max = max(a);
        // System.out.println(min + " ... " + max);
        Range r = new Range(min, max);
        Stroke stroke = new BasicStroke();
        // Paint paint = new GradientPaint(0, 0, Color.red, 30, 30, Color.blue);
        // draw the value function background
        for (int i = 0; i < model.size; i++) {
            for (int j = 0; j < model.size; j++) {
                if (model.maze != null && model.maze[i][j] == 1) {
                    // g.setColor(Color.yellow);
                    // g.setStroke(stroke);
                    g.setPaint(paint);
                    g.fillRect(i * size, j * size, size, size);
                } else {
                    // g.setPaint(Color.black);
                    g.setColor(ColorTable.getColor(r.map(a[i][j])));
                    g.fill3DRect(i * size, j * size, size, size, true);
                }
            }
        }

        // draw the goal
        g.setColor(goal);
        g.fillOval(model.goal[0] * size, model.goal[1] * size,
                size, size);
        g.setColor(agent);
        g.fillOval(model.state[0] * size, model.state[1] * size,
                size, size);
    }

    public Dimension getPreferredSize() {
        return new Dimension(size * model.size, size * model.size);
    }

    double min(double[][] aa) {
        double min = aa[0][0];
        for (double[] a : aa)
            for (double x : a)
                min = Math.min(min, x);
        return min;
    }
    double max(double[][] aa) {
        double max = aa[0][0];
        for (double[] a : aa)
            for (double x : a)
                max = Math.max(max, x);
        return max;
    }
}
