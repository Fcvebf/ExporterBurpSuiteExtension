package gr.fcvebf.burpexporterplugin.view;

import javax.swing.*;
import static java.awt.BorderLayout.CENTER;

public class IssuesExporterTab extends JPanel
{
    public IssuesExporterTab(IssuesPanel issuesPanel, ExportPanel exporterTab)
    {
        //super(new BorderLayout());
        super();
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));


        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(issuesPanel);
        splitPane.setBottomComponent(exporterTab);
        splitPane.setResizeWeight(0.5);
        add(splitPane, CENTER);

        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.5));
    }
}

