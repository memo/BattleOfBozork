package utilities;

/**
 * Created by Memo Akten on 16/06/2015.
 */
public class DoubleWithRange {
    public double value;
    public Range range;

    public DoubleWithRange(double value, double min, double max) {
        this.value = value;
        range = new Range(min, max);
    }

    public void randomize() {
        this.value = Math.random() * (range.max - range.min) + range.min;
    }

}
