package view.colors;

import java.awt.*;

/**
 * Created by Simon M. Lucas
 * sml@essex.ac.uk
 * Date: 15-Dec-2010
 * Time: 13:27:56
 */
public class ColorTable {
    public static int nColors = 1024;
    public static Color[] color = new Color[nColors];

    static {
        setColors();
    }

    public static Color getColor(double x) {
        // assumes x is between zero and one
        int ix = (int) ((nColors-1) * x);
        ix = Math.abs(ix);
        return color[(ix + nColors) % nColors];
    }

    public static void setColors() {
        // System.out.println("Red");
        IntRamp rr = new IntRamp(nColors, 0);
        rr.prog(384, 0); // set 0 .. 383
        rr.prog(255, 1);
        rr.prog(257, 0);
        rr.prog(128, -1);

        // System.out.println("Green");
        IntRamp gr = new IntRamp(nColors, 0);
        gr.prog(128, 0);
        gr.prog(255, 1);
        gr.prog(257, 0);
        gr.prog(255, -1);
        gr.prog(129, 0);

        // System.out.println("Blue");
        IntRamp br = new IntRamp(nColors, 128);
        br.prog(127, 1);
        br.prog(257, 0);
        br.prog(255, -1);
        br.prog(385, 0);

        for (int i=0;i<nColors; i++) {
            color[i] = new Color(rr.a[i], gr.a[i], br.a[i]);
        }
    }

    public static void setColors2() {
        nColors = 256 * 3;
        color = new Color[nColors];
        IntRamp rr = new IntRamp(nColors, 0);
        rr.prog(512, 0); // set 0 .. 383
        rr.prog(256, 1);

        IntRamp gr = new IntRamp(nColors, 0);
        gr.prog(256, 0);
        gr.prog(255, 1);
        gr.prog(257, 0);

        IntRamp br = new IntRamp(nColors, 0);
        br.prog(255, 1);
        br.prog(513, 0);

        for (int i=0;i<nColors; i++) {
            color[i] = new Color(rr.a[i], gr.a[i], br.a[i]);
        }
    }

}
