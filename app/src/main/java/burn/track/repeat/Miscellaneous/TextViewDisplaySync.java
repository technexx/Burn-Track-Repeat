package burn.track.repeat.Miscellaneous;

public class TextViewDisplaySync {
    String modeOneFirstTextView = "";
    String modeOneSecondTextView = "";
    String modeThreeFirstTextView = "";
    String modeThreeSecondTextView = "";
    String stopWatchFirstTextView = "";
    String stopWatchSecondTextView = "";

    public String getModeOneFirstTextView() {
        return modeOneFirstTextView;
    }

    public void setModeOneFirstTextView(String modeOneFirstTextView) {
        this.modeOneFirstTextView = modeOneFirstTextView;
    }

    public String getModeOneSecondTextView() {
        return modeOneSecondTextView;
    }

    public void setModeOneSecondTextView(String modeOneSecondTextView) {
        this.modeOneSecondTextView = modeOneSecondTextView;
    }

    public String getModeThreeFirstTextView() {
        return modeThreeFirstTextView;
    }

    public void setModeThreeFirstTextView(String modeThreeFirstTextView) {
        this.modeThreeFirstTextView = modeThreeFirstTextView;
    }

    public String getModeThreeSecondTextView() {
        return modeThreeSecondTextView;
    }

    public void setModeThreeSecondTextView(String modeThreeSecondTextView) {
        this.modeThreeSecondTextView = modeThreeSecondTextView;
    }

    public String getStopWatchFirstTextView() {
        return stopWatchFirstTextView;
    }

    public void setStopWatchFirstTextView(String stopWatchFirstTextView) {
        this.stopWatchFirstTextView = stopWatchFirstTextView;
    }

    public String getStopWatchSecondTextView() {
        return stopWatchSecondTextView;
    }

    public void setStopWatchSecondTextView(String stopWatchSecondTextView) {
        this.stopWatchSecondTextView = stopWatchSecondTextView;
    }

    public boolean areModeOneTextViewsDifferent() {
        return !modeOneFirstTextView.equals(modeOneSecondTextView);
    }

    public boolean areModeThreeTextViewsDifferent() {
        return !modeThreeFirstTextView.equals(modeThreeSecondTextView);
    }

    public boolean areStopWatchTextViewsDifferent() {
        return !stopWatchFirstTextView.equals(stopWatchSecondTextView);
    }
}