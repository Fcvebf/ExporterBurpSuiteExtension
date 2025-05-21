package gr.fcvebf.burpexporterplugin.models;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MultilineTableCellRenderer extends JLabel implements TableCellRenderer {

    public MultilineTableCellRenderer() {
        setVerticalAlignment(SwingConstants.TOP);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setText((value == null) ? "" : "<html>" + value.toString().replaceAll("\n", "<br>") + "</html>");

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        setFont(table.getFont());
        setBorder(UIManager.getBorder("Table.cellBorder"));

        return this;
    }
}