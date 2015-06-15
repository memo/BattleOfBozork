package battle.controllers.Piers;

/**
 * Created by pwillic on 14/05/2015.
 */
public class GameTimer {

    private long startTime;
    private long timeBudget;

    private boolean timeSet = false;

    public GameTimer() {
        startTime = System.nanoTime();
    }

    public GameTimer(long budget) {
        this();
        setTimeBudgetMilliseconds(budget);
    }

    public long elapsed() {
        return System.nanoTime() - startTime;
    }

    public long elapsedMilliseconds() {
        return (long) (elapsed() / 1.0E6);
    }

    public void setTimeBudgetMilliseconds(long budget) {
        if(timeSet) throw new IllegalAccessError("You shouldn't try to set the time budget now");
        timeBudget = (long) (budget * 1.0E6);
        timeSet = true;
    }

    public long remainingTimeMilliseconds() {
        long difference = timeBudget - elapsed();
        return (long) (difference / 1.0E6);
    }

    private long remainingTime() {
        return (timeBudget - elapsed());
    }

    public double remainingTimePercent() {
        return (remainingTime() * 100.0d / timeBudget);
    }

    public boolean exceeded() {
        return elapsed() > timeBudget;
    }
}
