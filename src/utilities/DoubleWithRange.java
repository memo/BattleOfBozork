package utilities;

/**
 * Created by Memo Akten on 16/06/2015.
 */
public class DoubleWithRange {
    public double value;
    public double default_value;
    public Range range;

    public DoubleWithRange(double value, double min, double max) {
        this.value = value;
        this.default_value = value;
        range = new Range(min, max);
    }

    public double getDouble() {
        return value;
    }

    public int getInt() { return (int) value; }

    public void randomize() {
        this.value = Math.random() * (range.max - range.min) + range.min;
    }

    public void reset() {
        this.value = this.default_value;
    }
}
