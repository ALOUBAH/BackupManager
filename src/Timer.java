import java.text.DecimalFormat;
import java.util.Date;

public class Timer {

    private long _firstCheckpoint = 0;
    private long _latestCheckpoint = 0;
    private int _latestRatio = 0;

    /**
     * Equivalent to checkPoint("")
     * 
     */
    public void start() {
        this.checkPoint(""); //$NON-NLS-1$
    }

    public void checkPoint(String action) {
        if (action == null || action.equals("")) { //$NON-NLS-1$
            this._latestCheckpoint = 0;
        }

        long l = new Date().getTime();
        if (this._latestCheckpoint != 0) {
            // if (l - _lastCheckpoint != 0) {
            System.err.println(action + " " + (l - this._latestCheckpoint) + " ms"); //$NON-NLS-1$//$NON-NLS-2$
            // }
        } else {
            System.err.println("--"); //$NON-NLS-1$
            this._firstCheckpoint = l;
        }
        this._latestCheckpoint = l;
    }

    public void total() {
        System.err.println("Total " + (this._latestCheckpoint - this._firstCheckpoint) + " ms"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public long getMsSinceStart() {
        return new Date().getTime() - this._firstCheckpoint;
    }

    public long getMsSinceLatest() {
        return new Date().getTime() - this._latestCheckpoint;
    }

    public void displayRatio(String text, int newRatio) {
        long ms = this.getMsSinceLatest();
        this._latestCheckpoint = new Date().getTime();
        double r = (double) ms / (double) (newRatio - this._latestRatio);
        System.out.println(text + " - " + ms + " ms - " + newRatio + " - " + new DecimalFormat("0.0").format(r) + " ms/pct"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        this._latestRatio = newRatio;
    }
}
