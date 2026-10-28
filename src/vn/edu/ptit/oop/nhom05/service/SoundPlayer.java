package vn.edu.ptit.oop.nhom05.service;

import vn.edu.ptit.oop.nhom05.model.Food;
import vn.edu.ptit.oop.nhom05.model.SoundEffect;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundPlayer implements GameListener {

    private static final float SAMPLE_RATE = 22050f;
    private static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
    private static final double VOLUME = 0.25;

    private final ExecutorService worker;
    private final Map<SoundEffect, byte[]> cache = new EnumMap<>(SoundEffect.class);
    private volatile boolean enabled = true;
    private volatile boolean available = true;

    public SoundPlayer() {
        worker = Executors.newSingleThreadExecutor(task -> {
            Thread t = new Thread(task, "snake-sound");
            t.setDaemon(true);
            return t;
        });
        for (SoundEffect effect : SoundEffect.values()) {
            cache.put(effect, synthesize(effect));
        }
    }

    public void play(SoundEffect effect) {
        if (effect == null || !enabled || !available) {
            return;
        }
        byte[] samples = cache.get(effect);
        worker.execute(() -> playNow(samples));
    }

    private void playNow(byte[] samples) {
        try (SourceDataLine line = AudioSystem.getSourceDataLine(FORMAT)) {
            line.open(FORMAT);
            line.start();
            line.write(samples, 0, samples.length);
            line.drain();
        } catch (LineUnavailableException | IllegalArgumentException | SecurityException e) {
            available = false;
            System.err.println("Không phát được âm thanh, đã tắt âm thanh: " + e.getMessage());
        }
    }

    static byte[] synthesize(SoundEffect effect) {
        int count = (int) (SAMPLE_RATE * effect.getDurationMs() / 1000);
        int fade = Math.max(1, (int) (SAMPLE_RATE * 0.008));
        byte[] data = new byte[count * 2];
        double phase = 0;
        for (int i = 0; i < count; i++) {
            double t = i / (double) count;
            double freq = effect.getStartHz() + (effect.getEndHz() - effect.getStartHz()) * t;
            phase += 2 * Math.PI * freq / SAMPLE_RATE;
            double envelope = Math.min(1.0, Math.min(i, count - 1 - i) / (double) fade);
            short value = (short) (Math.sin(phase) * envelope * VOLUME * Short.MAX_VALUE);
            data[2 * i] = (byte) (value & 0xff);
            data[2 * i + 1] = (byte) ((value >> 8) & 0xff);
        }
        return data;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public void onUpdate(GameEngine engine) {
    }

    @Override
    public void onFoodEaten(GameEngine engine, Food food) {
        play(food.getEatSound());
    }

    @Override
    public void onGameOver(GameEngine engine) {
        play(SoundEffect.GAME_OVER);
    }
}
