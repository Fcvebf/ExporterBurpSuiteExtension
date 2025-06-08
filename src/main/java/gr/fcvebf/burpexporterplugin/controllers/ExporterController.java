package gr.fcvebf.burpexporterplugin.controllers;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import gr.fcvebf.burpexporterplugin.models.ExportModel;
import gr.fcvebf.burpexporterplugin.models.Finding;
import gr.fcvebf.burpexporterplugin.models.IssuesModel;
import gr.fcvebf.burpexporterplugin.utils.*;
import gr.fcvebf.burpexporterplugin.view.DebugPanel;
import gr.fcvebf.burpexporterplugin.view.ExportOptionsPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ExporterController
{
    public final IssuesModel issuesModel;
    private final ExportModel exportModel;
    public final ExportOptionsPanel expoptionsPanel;
    private final DebugPanel dbgPanel;
    private MontoyaApi montoyaApi;
    public DocxOptions docxOptions=null;
    public PwndocOptions pwndocOptions=null;
    public CSVOptions csvOptions=null;
    public JSONOptions jsonOptions=null;
    public XMLOptions xmlOptions=null;
    public MarkdownOptions markddownOptions=null;
    public List<Finding> items;
    public Config.exportOptionsEnum selectedOption;

    public ExporterController(IssuesModel issuesModel, ExportModel exportModel, ExportOptionsPanel expoptionsPanel, DebugPanel dbgPanel, MontoyaApi montoyaApi)
    {

        this.issuesModel = issuesModel;
        this.exportModel=exportModel;
        this.expoptionsPanel=expoptionsPanel;
        this.dbgPanel=dbgPanel;
        this.montoyaApi=montoyaApi;

        expoptionsPanel.setController(this);

        Events.subscribe(Events.ExportFindingsClick.class, event -> exportIssues());
        Events.subscribe(Events.UpdateDebugEvent.class, event -> UpdateDebugView(event));
        Events.subscribe(Events.PwndocExportedEvent.class,event -> PwndocExportFinished(event));
        Events.subscribe(Events.CSVExportedEvent.class, event -> CSVExportFinished(event));
        Events.subscribe(Events.JSONExportedEvent.class, event -> JSONExportFinished(event));
        Events.subscribe(Events.XMLExportedEvent.class, event -> XMLExportFinished(event));
        Events.subscribe(Events.MarkdownExportedEvent.class, event -> MarkdownExportFinished(event));
        Events.subscribe(Events.DocxExportedEvent.class, event -> DocxExportFinished(event));


        Events.subscribe(Events.AddIssuesEvent.class, e -> addIssues(e.auditIssues()));
        Events.subscribe(Events.IssuesSelectedEvent.class, e -> setIssuesSelected(e.selectedRows()));
        Events.subscribe(Events.RemoveAllIssues.class, e -> removeSelectedIssues());

    }

    private void addIssues(List<AuditIssue> auditIssues)
    {
        issuesModel.addIssues(auditIssues);

        setIssuesPopulated(!auditIssues.isEmpty());
        //Events.publish(AddIssuesEventToModel);
        Events.publish(new Events.AddIssuesEventToModel(auditIssues));
    }

    private void removeSelectedIssues()
    {
        issuesModel.removeSelectedIssues();

        setIssuesPopulated(!issuesModel.getIssues().isEmpty());
    }

    private void removeAllIssues()
    {
        issuesModel.removeAllIssues();
        setIssuesPopulated(!issuesModel.getIssues().isEmpty());
    }

    private void setIssuesPopulated(boolean issuesPopulated)
    {
        //outputController.setIssuesPopulated(issuesPopulated);
    }

    private void setIssuesSelected(int[] selectedRows)
    {
        issuesModel.setSelectedRows(selectedRows);
    }



    public void handleExportClicked()
    {
        //SwingUtilities.invokeLater(() -> dbgPanel.clearArea());
        List<Finding> items= issuesModel.getFindings();
        Events.publish(new Events.ExportFindingsClick(items));
    }

    public void handleExportClicked(ActionEvent e)
    {
        //SwingUtilities.invokeLater(() -> dbgPanel.clearArea());
        List<Finding> items= issuesModel.getFindings();
        Events.publish(new Events.ExportFindingsClick(items));
    }

    public void handleClearClicked(ActionEvent e)
    {
        SwingUtilities.invokeLater(() -> dbgPanel.clearArea());
        this.issuesModel.removeAllIssues();
    }

/*
    private void exportIssues()
    {
        List<Finding> items= issuesModel.getFindings();

        Config.exportOptionsEnum selectedOption=(Config.exportOptionsEnum)this.expoptionsPanel.getSelectedExportOption();

        switch(selectedOption){
            case Pwndoc -> pwndocOptions = this.expoptionsPanel.getPwndocOptions();
            case CSV -> csvOptions=this.expoptionsPanel.getCSVOptions();
            case JSON -> jsonOptions=this.expoptionsPanel.getJSONOptions();
            case XML -> xmlOptions=this.expoptionsPanel.getXMLOptions();
            case Markdown -> markddownOptions=this.expoptionsPanel.getMarkdownOptions();
            case Docx -> {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        docxOptions=ExporterController.this.expoptionsPanel.getDocxOptions();
                        return null;
                    }

                    @Override
                    protected void done() {
                        //clean
                        docxOptions.execSummary=docxOptions.execSummary.replace("\n","</w:t></w:r><w:r><w:br/><w:t>");
                        Events.publish(new Events.ExportIssuesEvent(items,selectedOption,ExporterController.this.montoyaApi, pwndocOptions,csvOptions,jsonOptions,xmlOptions,markddownOptions,docxOptions));
                        return;
                    }
                }.execute();
            }
        }

        Events.publish(new Events.ExportIssuesEvent(items,selectedOption,this.montoyaApi, pwndocOptions,csvOptions,jsonOptions,xmlOptions,markddownOptions,docxOptions));
    }

 */

    private void exportIssues() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                items = issuesModel.getFindings();

                selectedOption = (Config.exportOptionsEnum) ExporterController.this.expoptionsPanel.getSelectedExportOption();

                switch (selectedOption) {
                    case Pwndoc -> pwndocOptions = ExporterController.this.expoptionsPanel.getPwndocOptions();
                    case CSV -> csvOptions = ExporterController.this.expoptionsPanel.getCSVOptions();
                    case JSON -> jsonOptions = ExporterController.this.expoptionsPanel.getJSONOptions();
                    case XML -> xmlOptions = ExporterController.this.expoptionsPanel.getXMLOptions();
                    case Markdown -> markddownOptions = ExporterController.this.expoptionsPanel.getMarkdownOptions();
                    case Docx ->  docxOptions = ExporterController.this.expoptionsPanel.getDocxOptions();
                }
                return null;
            }

            @Override
            protected void done() {
                //clean
                Events.publish(new Events.ExportIssuesEvent(items, selectedOption, ExporterController.this.montoyaApi, pwndocOptions, csvOptions, jsonOptions, xmlOptions, markddownOptions, docxOptions));
                return;
            }
        }.execute();


    }






    public void UpdateDebugView(Events.UpdateDebugEvent e)
    {
        SwingUtilities.invokeLater(() -> dbgPanel.appendMessage(e.message()));
    }

    public void PwndocExportFinished(Events.PwndocExportedEvent e)
    {
        //SwingUtilities.invokeLater(() -> dbgPanel.ShowPopup(e.message()));
    }

    public void CSVExportFinished(Events.CSVExportedEvent e)
    {
        //SwingUtilities.invokeLater(() -> dbgPanel.ShowPopup(e.message()));
    }

    public void JSONExportFinished(Events.JSONExportedEvent e)
    {
        //SwingUtilities.invokeLater(() -> dbgPanel.ShowPopup(e.message()));
    }

    public void XMLExportFinished(Events.XMLExportedEvent e)
    {
        //SwingUtilities.invokeLater(() -> dbgPanel.ShowPopup(e.message()));
    }

    public void MarkdownExportFinished(Events.MarkdownExportedEvent e)
    {
        //SwingUtilities.invokeLater(() -> dbgPanel.ShowPopup(e.message()));
    }

    public void DocxExportFinished(Events.DocxExportedEvent e)
    {
        //SwingUtilities.invokeLater(() -> dbgPanel.ShowPopup(e.message()));
    }

}
