package vn.edu.ptit.oop.nhom05.model;

public enum SoundEffect {
    EAT(880, 1100, 70),
    BONUS(1000, 1700, 140),
    POISON(320, 140, 240),
    GAME_OVER(420, 110, 520);

    private final int startHz;
    private final int endHz;
    private final int durationMs;

    SoundEffect(int startHz, int endHz, int durationMs) {
        this.startHz = startHz;
        this.endHz = endHz;
        this.durationMs = durationMs;
    }

    public int getStartHz() { return startHz; }
    public int getEndHz() { return endHz; }
    public int getDurationMs() { return durationMs; }
}
