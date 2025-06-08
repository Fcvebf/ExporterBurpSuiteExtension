package gr.fcvebf.burpexporterplugin.view;

import javax.swing.*;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


public class ExportPanel extends JPanel{

    public ExportPanel(ExportOptionsPanel expOptionsPanel,DebugPanel dbgPanel)
    {
        //super(new BorderLayout());
        super();
        this.setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(expOptionsPanel);
        splitPane.setRightComponent(dbgPanel);
        splitPane.setResizeWeight(0.0);
        //splitPane.setPreferredSize(new Dimension(800, 400));
        this.setVisible(true);
        add(splitPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.4));




    }

}
