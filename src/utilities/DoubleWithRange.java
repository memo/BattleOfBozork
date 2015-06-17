package utilities;

/**
 * Created by colormotor on 17/06/15.
 */
public class DoubleWithRange {
    public double value;
    public double default_value;
    public Range range;
    public String name;

    public DoubleWithRange(String name, double default_value, double min, double max) {
        this.name = name;
        this.value = default_value;
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

    public String toString() { return name + "," + value; }
}
