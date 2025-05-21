package gr.fcvebf.burpexporterplugin.view;

import gr.fcvebf.burpexporterplugin.models.IssuesModel;
import gr.fcvebf.burpexporterplugin.utils.Events;
import gr.fcvebf.burpexporterplugin.utils.Events.IssuesSelectedEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import static java.awt.BorderLayout.CENTER;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import static java.util.Arrays.stream;


public class IssuesPanel  extends JPanel {

    public IssuesPanel(IssuesModel model) {
        super(new BorderLayout());
        //super();
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        JTable table = new JTable(model);
        TableColumn URLColumn = table.getColumnModel().getColumn(3); // or use name lookup
        JTableHeader header = table.getTableHeader();
        TableCellRenderer headerRenderer = header.getDefaultRenderer();
        if (headerRenderer instanceof JLabel labelRenderer) {
            labelRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        }

        table.setDefaultRenderer(JLabel.class, new LabelCellRenderer());
        table.getSelectionModel().addListSelectionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            int[] modelSelectedRows = stream(selectedRows).map(table::convertRowIndexToModel).toArray();

            Events.publish(new IssuesSelectedEvent(modelSelectedRows));
        });

        add(new JScrollPane(table), CENTER);


    }


    private static class LabelCellRenderer extends JLabel implements TableCellRenderer {
        public LabelCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value instanceof JLabel) {
                JLabel label = (JLabel) value;
                setText(label.getText()); // Get the HTML text from the JLabel
                setIcon(label.getIcon()); // If the JLabel has an icon
                setHorizontalAlignment(label.getHorizontalAlignment());
                setVerticalAlignment(label.getVerticalAlignment());

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
            } else {
                // This should ideally not happen if getColumnClass() is correct
                setText(value == null ? "" : value.toString());
                setForeground(table.getForeground());
                setBackground(table.getBackground());
                setFont(table.getFont());
                setBorder(UIManager.getBorder("Table.cellBorder"));
                return this;
            }
        }
    }
}

