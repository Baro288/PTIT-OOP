package vn.edu.ptit.oop.nhom05;

import vn.edu.ptit.oop.nhom05.ui.MainWindow;
import vn.edu.ptit.oop.nhom05.util.ChartHelper;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        ChartHelper.installTheme();
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
