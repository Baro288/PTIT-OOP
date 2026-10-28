package vn.edu.ptit.oop.nhom05.ui.dialogs;

import vn.edu.ptit.oop.nhom05.model.PoisonFood;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;

public class AboutDialog extends JDialog {

    private static final String[] MEMBERS = {
        "Võ Thành Huy Hoàng – N24DCAT030 (nhóm trưởng)",
        "Ngô Minh Dũng – N24DCAT018",
        "Vũ Hoàng Quốc Anh – N24DCAT006",
        "Vy Gia Bảo – N24DCAT009",
        "Phạm Trung Hiếu – N24DCAT027",
        "Đoàn Quốc Thuận – N24DCAT077",
        "Đoàn Quốc Hòa – N24DCAT029",
        "Nguyễn Đức Dương San – N20DCAT047",
        "Lưu Đức Đạt – N22DCCN019"
    };

    public AboutDialog(Frame owner) {
        super(owner, "Giới thiệu", true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(18, 28, 8, 28));

        JLabel title = new JLabel("Trò chơi Rắn săn mồi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(46, 110, 60));
        addLeft(content, title);
        addLeft(content, small("Snake Game – Nhóm 05"));
        addLeft(content, small("Bài tập lớn Lập trình hướng đối tượng (INT1332) – Java Swing"));
        content.add(Box.createVerticalStrut(12));

        addLeft(content, heading("Cách chơi"));
        addLeft(content, small("← ↑ ↓ → hoặc W A S D: điều khiển rắn"));
        addLeft(content, small("SPACE / ENTER: bắt đầu hoặc chơi lại   •   P / ESC: tạm dừng"));
        addLeft(content, small("Mồi đỏ +10 điểm; mồi vàng (xuất hiện thoáng chốc) +30 điểm. Ăn càng nhiều rắn càng nhanh."));
        addLeft(content, small("Màn 1 không có tường: đi qua mép bàn sẽ ra ở phía đối diện. Màn 2, 3 chạm tường là thua."));
        addLeft(content, small("Mồi tím (độc): rắn ngắn đi " + PoisonFood.SHRINK_BY + " đốt, không được điểm."));
        addLeft(content, small("Ctrl+T: chế độ 2 người chơi (P1: phím mũi tên, P2: W A S D)   •   Ctrl+M: bật/tắt âm thanh"));
        content.add(Box.createVerticalStrut(12));

        addLeft(content, heading("Kỹ thuật OOP"));
        addLeft(content, small("Trừu tượng: GameObject, LevelConfig, HighScoreRepository"));
        addLeft(content, small("Kế thừa: Food → BonusFood / PoisonFood, JDialog → GameOverDialog / HighScoreDialog / AboutDialog"));
        addLeft(content, small("Đa hình: draw(), applyTo(), getEatSound(), createObstacles(), TableCellRenderer"));
        addLeft(content, small("Đóng gói: Snake tự quản lý thân; ScoreRecord bất biến, tự kiểm tra dữ liệu"));
        addLeft(content, small("Kết hợp: GameEngine chứa List<Player> (mỗi Player chứa Snake), Food, LevelConfig, Timer"));

        if (MEMBERS.length > 0) {
            content.add(Box.createVerticalStrut(12));
            addLeft(content, heading("Thành viên"));
            for (String m : MEMBERS) {
                addLeft(content, small(m));
            }
        }

        JButton close = new JButton("Đóng");
        close.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(close);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(close);

        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private static JLabel small(String text) {
        JLabel l = new JLabel(text);
        l.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
        return l;
    }

    private static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 14f));
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        return l;
    }

    private static void addLeft(JPanel panel, JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(c);
    }
}
