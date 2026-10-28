package vn.edu.ptit.oop.nhom05.repository;

import vn.edu.ptit.oop.nhom05.exception.HighScoreException;
import vn.edu.ptit.oop.nhom05.model.ScoreRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FileHighScoreRepository implements HighScoreRepository {

    private static final String SEP = "\t";
    private static final String HEADER = "# name" + SEP + "score" + SEP + "level" + SEP + "date";

    private final Path file;
    private List<String> lastWarnings = Collections.emptyList();

    public FileHighScoreRepository(Path file) {
        this.file = Objects.requireNonNull(file, "file không được null");
    }

    public static Path defaultPath() {
        return Path.of("data", "highscores.tsv");
    }

    public Path getFile() {
        return file;
    }

    @Override
    public synchronized void save(ScoreRecord record) throws HighScoreException {
        Objects.requireNonNull(record, "record không được null");
        try {
            Path dir = file.toAbsolutePath().getParent();
            if (dir != null) {
                Files.createDirectories(dir);
            }
            StringBuilder line = new StringBuilder();
            if (!Files.exists(file) || Files.size(file) == 0) {
                line.append(HEADER).append(System.lineSeparator());
            }
            line.append(format(record)).append(System.lineSeparator());
            Files.writeString(file, line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new HighScoreException("Không ghi được bảng điểm vào file " + file + ": " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized List<ScoreRecord> findAll() throws HighScoreException {
        List<String> warnings = new ArrayList<>();
        List<ScoreRecord> result = new ArrayList<>();
        if (Files.exists(file)) {
            List<String> lines;
            try {
                lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new HighScoreException("Không đọc được bảng điểm từ file " + file + ": " + e.getMessage(), e);
            }
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                try {
                    result.add(parse(line));
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    warnings.add("Dòng " + (i + 1) + " bị bỏ qua: " + e.getMessage());
                }
            }
        }
        lastWarnings = Collections.unmodifiableList(warnings);
        return Collections.unmodifiableList(result);
    }

    public synchronized List<String> getLastWarnings() {
        return lastWarnings;
    }

    static String format(ScoreRecord r) {
        return r.getName() + SEP + r.getScore() + SEP + r.getLevel() + SEP + r.getDate();
    }

    static ScoreRecord parse(String line) {
        String[] f = line.split(SEP, -1);
        if (f.length != 4) {
            throw new IllegalArgumentException("cần 4 cột nhưng có " + f.length);
        }
        return new ScoreRecord(f[0], Integer.parseInt(f[1].trim()), Integer.parseInt(f[2].trim()),
                LocalDateTime.parse(f[3].trim()));
    }
}
