package gr.fcvebf.burpexporterplugin.view;

import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.ui.contextmenu.AuditIssueContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import gr.fcvebf.burpexporterplugin.utils.Constants;
import gr.fcvebf.burpexporterplugin.utils.Events;

import javax.swing.JMenuItem;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class ExportIssueMenuItemsProvider implements  ContextMenuItemsProvider {

    @Override
    public List<Component> provideMenuItems(AuditIssueContextMenuEvent event)
    {
        List<AuditIssue> auditIssues = event.selectedIssues();

        JMenuItem menuItem = new JMenuItem();
        menuItem.setText("Send to "+ Constants.extentionName);
        menuItem.addActionListener(e -> Events.publish(new Events.AddIssuesEvent(auditIssues)));

        List<Component> menuItems = new ArrayList<Component>();
        menuItems.add(menuItem);

        return menuItems;
    }
}
