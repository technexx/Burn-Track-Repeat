package com.example.tragic.irate.simple.stopwatch;

public class StateOfTimers {
    boolean modeOneTimerActive;
    boolean modeOneTimerPaused;
    boolean modeOneTimerEnded;
    boolean modeOneTimerDisabled;

    boolean modeThreeTimerActive;
    boolean modeThreeTimerPaused;
    boolean modeThreeTimerEnded;
    boolean modeThreeTimerDisabled;

    boolean stopWatchTimerActive;
    boolean stopWatchTimerPaused = true;
    boolean stopWatchTimerEnded;

    public boolean isModeOneTimerActive() {
        return modeOneTimerActive;
    }

    public void setModeOneTimerActive(boolean modeOneTimerActive) {
        this.modeOneTimerActive = modeOneTimerActive;
    }

    public boolean isModeOneTimerPaused() {
        return modeOneTimerPaused;
    }

    public void setModeOneTimerPaused(boolean modeOneTimerPaused) {
        this.modeOneTimerPaused = modeOneTimerPaused;
    }

    public boolean isModeOneTimerEnded() {
        return modeOneTimerEnded;
    }

    public void setModeOneTimerEnded(boolean modeOneTimerEnded) {
        this.modeOneTimerEnded = modeOneTimerEnded;
    }

    public boolean isModeOneTimerDisabled() {
        return modeOneTimerDisabled;
    }

    public void setModeOneTimerDisabled(boolean modeOneTimerDisabled) {
        this.modeOneTimerDisabled = modeOneTimerDisabled;
    }

    public boolean isModeThreeTimerActive() {
        return modeThreeTimerActive;
    }

    public void setModeThreeTimerActive(boolean modeThreeTimerActive) {
        this.modeThreeTimerActive = modeThreeTimerActive;
    }

    public boolean isModeThreeTimerPaused() {
        return modeThreeTimerPaused;
    }

    public void setModeThreeTimerPaused(boolean modeThreeTimerPaused) {
        this.modeThreeTimerPaused = modeThreeTimerPaused;
    }

    public boolean isModeThreeTimerEnded() {
        return modeThreeTimerEnded;
    }

    public void setModeThreeTimerEnded(boolean modeThreeTimerEnded) {
        this.modeThreeTimerEnded = modeThreeTimerEnded;
    }

    public boolean isModeThreeTimerDisabled() {
        return modeThreeTimerDisabled;
    }

    public void setModeThreeTimerDisabled(boolean modeThreeTimerDisabled) {
        this.modeThreeTimerDisabled = modeThreeTimerDisabled;
    }

    public boolean isStopWatchTimerActive() {
        return stopWatchTimerActive;
    }

    public void setStopWatchTimerActive(boolean stopWatchTimerActive) {
        this.stopWatchTimerActive = stopWatchTimerActive;
    }

    public boolean isStopWatchTimerPaused() {
        return stopWatchTimerPaused;
    }

    public void setStopWatchTimerPaused(boolean stopWatchTimerPaused) {
        this.stopWatchTimerPaused = stopWatchTimerPaused;
    }

    public boolean isStopWatchTimerEnded() {
        return stopWatchTimerEnded;
    }

    public void setStopWatchTimerEnded(boolean stopWatchTimerEnded) {
        this.stopWatchTimerEnded = stopWatchTimerEnded;
    }
}