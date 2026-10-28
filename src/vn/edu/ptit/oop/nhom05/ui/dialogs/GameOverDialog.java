package vn.edu.ptit.oop.nhom05.ui.dialogs;

import vn.edu.ptit.oop.nhom05.exception.InvalidScoreRecordException;
import vn.edu.ptit.oop.nhom05.model.ScoreRecord;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

public class GameOverDialog extends JDialog {

    private static final Color ERROR_COLOR = new Color(190, 40, 40);

    private final int score;
    private final int level;
    private JTextField nameField;
    private JLabel errorLabel;
    private ScoreRecord result;

    public GameOverDialog(Frame owner, int score, int level, String levelName, String defaultName) {
        super(owner, "Kết thúc ván chơi", true);
        this.score = score;
        this.level = level;
        buildUI(levelName, defaultName == null ? "" : defaultName);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                skip();
            }
        });
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public Optional<ScoreRecord> showAndGetResult() {
        setVisible(true);
        return getResult();
    }

    public Optional<ScoreRecord> getResult() {
        return Optional.ofNullable(result);
    }

    private void buildUI(String levelName, String defaultName) {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 14, 20));

        JLabel title = new JLabel("GAME OVER", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(170, 40, 40));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 4, 3, 4);
        c.anchor = GridBagConstraints.WEST;

        addRow(form, c, 0, "Điểm của bạn:", boldLabel(String.valueOf(score)));
        addRow(form, c, 1, "Màn chơi:", boldLabel(level + (levelName == null || levelName.isBlank() ? "" : " – " + levelName)));

        nameField = new JTextField(defaultName, 16);
        nameField.setToolTipText("Tối đa " + ScoreRecord.MAX_NAME_LENGTH + " ký tự");
        addRow(form, c, 2, "Tên của bạn:", nameField);

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(ERROR_COLOR);
        c.gridx = 1; c.gridy = 3; c.gridwidth = 1;
        form.add(errorLabel, c);
        root.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Lưu điểm");
        JButton skipBtn = new JButton("Bỏ qua");
        saveBtn.addActionListener(e -> save());
        skipBtn.addActionListener(e -> skip());
        getRootPane().setDefaultButton(saveBtn);
        getRootPane().registerKeyboardAction(e -> skip(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(saveBtn);
        buttons.add(skipBtn);
        root.add(buttons, BorderLayout.SOUTH);

        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { clearError(); }
            @Override public void removeUpdate(DocumentEvent e) { clearError(); }
            @Override public void changedUpdate(DocumentEvent e) { clearError(); }
        });

        setContentPane(root);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                nameField.requestFocusInWindow();
                nameField.selectAll();
            }
        });
    }

    private static void addRow(JPanel form, GridBagConstraints c, int row, String caption, Component field) {
        c.gridy = row;
        c.gridx = 0;
        form.add(new JLabel(caption), c);
        c.gridx = 1;
        form.add(field, c);
    }

    private static JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 15f));
        return l;
    }

    private void save() {
        try {
            result = new ScoreRecord(nameField.getText(), score, level);
            dispose();
        } catch (InvalidScoreRecordException ex) {
            errorLabel.setText(ex.getMessage());
            nameField.requestFocusInWindow();
        }
    }

    private void skip() {
        result = null;
        dispose();
    }

    private void clearError() {
        errorLabel.setText(" ");
    }
}
