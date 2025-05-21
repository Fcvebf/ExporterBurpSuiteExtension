package gr.fcvebf.burpexporterplugin.view;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.*;


public class DebugPanel  extends JPanel {

    JTextArea txtAreaLogs;

    public DebugPanel()
    {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.txtAreaLogs = new JTextArea();
        txtAreaLogs.setLineWrap(true);
        txtAreaLogs.setEditable(false);
        txtAreaLogs.setWrapStyleWord(true);
        txtAreaLogs.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        this.add(new JScrollPane(txtAreaLogs));

    }

    public void appendMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            txtAreaLogs.append(msg + "\n");
        });
    }

    public void ShowPopup(String msg)
    {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, msg, "Export Results", JOptionPane.INFORMATION_MESSAGE);
        });
    }


    public void clearArea() {
        SwingUtilities.invokeLater(() -> {
            txtAreaLogs.setText("");
        });
    }

}
