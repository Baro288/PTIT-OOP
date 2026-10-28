package vn.edu.ptit.oop.nhom05.model;

public enum GameState {
    READY("Sẵn sàng"),
    RUNNING("Đang chơi"),
    PAUSED("Tạm dừng"),
    GAME_OVER("Kết thúc");

    private final String label;

    GameState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean canTransitionTo(GameState next) {
        if (next == null) {
            return false;
        }
        return switch (this) {
            case READY     -> next == RUNNING;
            case RUNNING   -> next == PAUSED || next == GAME_OVER || next == READY;
            case PAUSED    -> next == RUNNING || next == READY;
            case GAME_OVER -> next == READY;
        };
    }

    public boolean isRunning() {
        return this == RUNNING;
    }

    public boolean isGameOver() {
        return this == GAME_OVER;
    }

    public GameState togglePause() {
        return switch (this) {
            case RUNNING -> PAUSED;
            case PAUSED -> RUNNING;
            default -> this;
        };
    }
}
