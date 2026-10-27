# OOP_Nhom05_SnakeGame — Trò chơi Rắn săn mồi

**Môn:** INT1332 – Lập trình hướng đối tượng (PTIT) · **Nhóm:** 05 · **Đề tài:** Snake Game
Game 2D nhiều màn chơi, nhiều loại mồi (thường, thưởng, độc), chế độ 2 người chơi, âm thanh, bảng xếp hạng điểm và biểu đồ thống kê (Swing + JFreeChart).

## Thành viên và phân công

| # | Họ tên | MSSV | Phần phụ trách | Lớp phụ trách |
|---|--------|------|----------------|---------------|
| 1 | Võ Thành Huy Hoàng (nhóm trưởng) | N24DCAT030 | Khung ứng dụng + tích hợp, README, UML, Git | `Main`, `BaseWindow`, `MainWindow`, `AboutDialog` |
| 2 | Ngô Minh Dũng | N24DCAT018 | Bước 1 – Đối tượng trong game | `GameObject`, `Snake`, `Food`, `BonusFood`, `Obstacle` |
| 3 | Vũ Hoàng Quốc Anh | N24DCAT006 | Bước 2 + 5 – Enum, cấu hình, màn chơi | `Direction`, `GameState`, `GameConfig`, `LevelConfig`, `AbstractLevel`, `Level1`, `Level2`, `Level3`, `Levels` |
| 4 | Vy Gia Bảo | N24DCAT009 | Bước 3 – Bàn chơi + Key Bindings | `GameBoardPanel`, `GameControls` |
| 5 | Phạm Trung Hiếu | N24DCAT027 | Bước 4 – Bộ máy trò chơi (Timer, va chạm, tăng tốc) | `GameEngine`, `GameListener` |
| 6 | Đoàn Quốc Thuận | N24DCAT077 | Bước 6A – Dữ liệu điểm, lưu file, ngoại lệ | `ScoreRecord`, `HighScoreRepository`, `FileHighScoreRepository`, `InMemoryHighScoreRepository`, `HighScoreException`, `InvalidScoreRecordException` |
| 7 | Đoàn Quốc Hòa | N24DCAT029 | Bước 6B – Hộp thoại nhập tên + bảng xếp hạng | `GameOverDialog`, `HighScoreDialog`, `HighScoreTableModel` |
| 8 | Nguyễn Đức Dương San | N20DCAT047 | Bước 7 – Biểu đồ JFreeChart | `ScoreStatistics`, `ChartHelper`, `ScoreChartsPanel`, `ChartWindow` |
| 9 | Lưu Đức Đạt | N22DCCN019 | BONUS – Âm thanh, mồi độc, 2 người chơi | `PoisonFood`, `Player`, `SoundEffect`, `SoundPlayer` (+ phần 2 người chơi trong `GameEngine`, `GameBoardPanel`) |

- Link Git: _(điền)_ · Link video demo: _(điền, nếu có)_

## Yêu cầu

- JDK 17 trở lên (tối thiểu 11)
- JFreeChart 1.5.4 (khai báo sẵn trong `pom.xml`; không cần JCommon)

## Cách chạy

**Eclipse (khuyến nghị):** `File ▸ Import ▸ Maven ▸ Existing Maven Projects` → chọn thư mục project →
chuột phải `Main.java` ▸ `Run As ▸ Java Application`.

**Dòng lệnh (Maven):**
```bash
mvn compile exec:java -Dexec.mainClass=vn.edu.ptit.oop.nhom05.Main
```

**Tạo file JAR chạy được (`app/OOP_Nhom05.jar`):** Eclipse `File ▸ Export ▸ Java ▸ Runnable JAR file`,
Launch configuration = `Main`, chọn *Package required libraries into generated JAR*, rồi chạy:
```bash
java -jar OOP_Nhom05.jar
```

Dữ liệu bảng xếp hạng được lưu ở `data/highscores.tsv` (tự tạo khi lưu điểm đầu tiên).

## Điều khiển

| Phím | Chức năng |
|------|-----------|
| ↑ ↓ ← → / W A S D | Đổi hướng rắn (không quay đầu 180°) |
| ↑ ↓ ← → và W A S D (chế độ 2 người) | P1 dùng phím mũi tên, P2 dùng W A S D |
| Space / Enter | Bắt đầu / chơi lại |
| P / Esc | Tạm dừng / tiếp tục |
| Ctrl+1 / Ctrl+2 / Ctrl+3 | Chọn màn chơi |
| Ctrl+H | Bảng xếp hạng |
| Ctrl+G | Biểu đồ điểm |
| Ctrl+T | Bật/tắt chế độ 2 người chơi |
| Ctrl+M | Bật/tắt âm thanh |
| Ctrl+Q | Thoát |

## Màn chơi

| Màn | Tên | Luật |
|-----|-----|------|
| 1 | Đồng cỏ | Không có tường: đi qua mép bàn sẽ ra ở phía đối diện; chỉ thua khi tự cắn (hoặc đâm rắn kia) |
| 2 | Tường viền | Một vòng tường quanh bàn, chạm tường là thua |
| 3 | Mê cung | Tường viền cùng các bức tường chắn đường |

## Cấu trúc thư mục

```
OOP_Nhom05_SnakeGame/
├── src/
│   ├── vn/edu/ptit/oop/nhom05/
│   │   ├── Main.java                 // Điểm vào – SwingUtilities.invokeLater
│   │   ├── common/BaseWindow.java    // Lớp trừu tượng bắt buộc (initUI())
│   │   ├── model/                    // GameObject, Snake, Food, BonusFood, PoisonFood, Obstacle,
│   │   │                             // Player, SoundEffect,
│   │   │                             // Direction, GameState, LevelConfig/Level1-3, ScoreRecord
│   │   ├── service/                  // GameEngine, GameListener, GameControls, ScoreStatistics, SoundPlayer
│   │   ├── repository/               // HighScoreRepository + File/InMemory
│   │   ├── exception/                // HighScoreException, InvalidScoreRecordException
│   │   ├── ui/MainWindow.java        // extends BaseWindow
│   │   ├── ui/ChartWindow.java       // extends JFrame (DISPOSE_ON_CLOSE)
│   │   ├── ui/panels/                // GameBoardPanel, ScoreChartsPanel
│   │   ├── ui/dialogs/               // GameOverDialog, HighScoreDialog, AboutDialog, ...
│   │   └── util/ChartHelper.java     // Tạo biểu đồ JFreeChart
│   └── resources/icons/              // app.png (32x32), trophy.png, chart.png (16x16)
├── docs/
│   ├── uml/                          // class_diagram.puml / .dot / .png
│   └── screenshots/                  // Ảnh chụp màn hình từng bước
├── pom.xml
└── README.md
```

## Tiến độ theo đề

| Bước | Nội dung | Trạng thái |
|------|----------|-----------|
| 1 | GameObject, Snake, Food, BonusFood, Obstacle | ✓ |
| 2 | enum Direction, GameState | ✓ |
| 3 | GameBoardPanel + Key Bindings | ✓ |
| 4 | GameEngine (javax.swing.Timer, va chạm, tăng tốc) | ✓ |
| 5 | LevelConfig: Level1, Level2, Level3 | ✓ |
| 6 | GameOverDialog, HighScoreRepository, HighScoreDialog | ✓ |
| 7 | Biểu đồ JFreeChart (đường + cột) | ✓ |
| 8 (BONUS) | Âm thanh (SoundPlayer) / PoisonFood / 2 người chơi (Player) | ✓ |

## Kịch bản demo

Chơi Level 1 ăn vài mồi (tốc độ tăng) → Pause/Resume → chuyển Level 3 → game over, nhập tên →
bảng xếp hạng và biểu đồ điểm.
