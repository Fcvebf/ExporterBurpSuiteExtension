package gr.fcvebf.burpexporterplugin;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.EnhancedCapability;

import burp.api.montoya.ai.Ai;
import burp.api.montoya.http.HttpMode;
import burp.api.montoya.proxy.Proxy;
import gr.fcvebf.burpexporterplugin.view.ExportIssueMenuItemsProvider;
import gr.fcvebf.burpexporterplugin.view.ExportPanel;
import gr.fcvebf.burpexporterplugin.view.ExportOptionsPanel;
import gr.fcvebf.burpexporterplugin.view.DebugPanel;
import gr.fcvebf.burpexporterplugin.view.IssuesExporterTab;
import gr.fcvebf.burpexporterplugin.view.IssuesPanel;
import gr.fcvebf.burpexporterplugin.models.IssuesModel;
import gr.fcvebf.burpexporterplugin.models.ExportModel;
import gr.fcvebf.burpexporterplugin.controllers.ExporterController;
import java.util.Set;


public class IssuesExporter implements BurpExtension {

    private Ai ai;

    @Override
    public void initialize(MontoyaApi montoyaApi)
    {
        if(montoyaApi!=null) {
            montoyaApi.extension().setName("Exporter++");
            montoyaApi.logging().logToOutput("Exporter++ loaded successfully!");
            this.ai = montoyaApi.ai();

            //Models Initialization
            IssuesModel issuesModel = new IssuesModel();
            IssuesPanel issuesPanel = new IssuesPanel(issuesModel);
            ExportModel exportModel=new ExportModel(montoyaApi);

            //Views Initialization
            DebugPanel dbgPanel=new DebugPanel();
            ExportOptionsPanel expOptionsPanel=new ExportOptionsPanel(montoyaApi,this.ai,dbgPanel);
            ExportPanel exportPanel = new ExportPanel(expOptionsPanel,dbgPanel);
            IssuesExporterTab exporterTab = new IssuesExporterTab(issuesPanel,exportPanel);

            //Register extension to Burp
            montoyaApi.userInterface().registerSuiteTab("Exporter++", exporterTab);
            montoyaApi.userInterface().registerContextMenuItemsProvider(new ExportIssueMenuItemsProvider());


            //Controller Initialization
            ExporterController exporterController=new ExporterController(issuesModel,exportModel,expOptionsPanel,dbgPanel,montoyaApi);

            montoyaApi.extension().registerUnloadingHandler(() -> {

            });
        }
    }


    @Override
    public Set<EnhancedCapability> enhancedCapabilities() {
        return Set.of(EnhancedCapability.AI_FEATURES);
    }


}