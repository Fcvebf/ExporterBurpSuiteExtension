package gr.fcvebf.burpexporterplugin.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import gr.fcvebf.burpexporterplugin.controllers.ExporterController;
import gr.fcvebf.burpexporterplugin.models.ExportModel;
import gr.fcvebf.burpexporterplugin.models.Finding;
import gr.fcvebf.burpexporterplugin.models.ollama.OllamaClient;
import gr.fcvebf.burpexporterplugin.models.ollama.OllamaModel;
import gr.fcvebf.burpexporterplugin.models.pwndoc.AuditType;
import gr.fcvebf.burpexporterplugin.utils.Config;
import gr.fcvebf.burpexporterplugin.utils.Constants;
import gr.fcvebf.burpexporterplugin.utils.Events;
import gr.fcvebf.burpexporterplugin.utils.Utilities;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Map;



public class ExecSummaryPanel extends JPanel {

    public ExporterController exportController;
    ButtonGroup groupExecSumModes;
    JRadioButton radioExecSumMode_userSupplied;
    JRadioButton radioExecSumMode_AIgenerated;
    JPanel AIOptionsPanel;
    public JTextField txtOllamaEndpoint;
    public MontoyaApi montoyapi;
    public Boolean debug;
    public JComboBox<OllamaModel> cmb_OllamaModels;
    private String ExecutiveSummaryText = "";
    private String ExecutiveSummary_userSupplied="Executive summary text goes here";
    public String ExecutiveSummary_AIgenerated="";
    public String ExecutiveSummaryMD_AIgenerated="";
    public JSlider temperatureSlider;
    public JLabel temperatureLabel;
    public JLabel temperatureValueLabel;

    private Boolean PromptUpdatedByUser = false;
    private Boolean UserAddedMoreIssues = false;
    private Boolean IssuedRemovedByUser = false;
    public String LLMPrompt = Constants.initial_LLMPrompt;

    private OllamaClient ollama_client;
    public boolean useMarkdown;
    private float LLMTemperature;
    // Constants for slider
    private static final int SLIDER_MIN = 0;       // Corresponds to 0.0 temperature
    private static final int SLIDER_MAX = 200;     // Corresponds to 2.0 temperature
    private static final int SLIDER_INITIAL = Config.LLMdefaultTemperature;  // Corresponds to 0.2 temperature
    private static final int SLIDER_SCALE = 100;   // Divisor to convert int to float


    private String LM_persona="You are PenetrationTesterAI, a specialized AI assistant for security professionals. Your primary function is to summarize the provided list of security findings generate Executive Summaries.\n";

    private String LM_core_resp= """

Your core characteristics are:
1.  Professionalism and Objectivity: Your tone is consistently professional, objective, and authoritative. You maintain a neutral stance, presenting facts and recommendations without exaggeration or sensationalism.
2.  Action-Oriented: When providing recommendations, you prioritize actionable and practical advice that directly addresses the vulnerability and guides remediation efforts effectively.
""";

    private String LM_task="\nGenerate a summary of the following list of Findings and the Overall security posture given below:\n";

    public String getExecutiveSummaryText()
    {
        String execsum="";
        if (radioExecSumMode_userSupplied.isSelected()) {
            execsum = ExecutiveSummary_userSupplied;
            return execsum;
        }
        else {
            /*
            ollamaQueryLLM(this.cmb_OllamaModels.getSelectedItem().toString(), LLMPrompt);
            return  this.ExecutiveSummary_AIgenerated;
             */
            String model = cmb_OllamaModels.getSelectedItem().toString();
            String result = ollamaQueryLLMBlocking(model, LLMPrompt);
            this.ExecutiveSummary_AIgenerated = result;
            return result;
        }

    }


    public String getExecutiveSummaryTextMD()
    {
        String execsum="";
        if (radioExecSumMode_userSupplied.isSelected()) {
            execsum = ExecutiveSummary_userSupplied;
            return execsum;
        }
        else {
            /*
            ollamaQueryLLM(this.cmb_OllamaModels.getSelectedItem().toString(), LLMPrompt);
            return  this.ExecutiveSummary_AIgenerated;
             */
            String model = cmb_OllamaModels.getSelectedItem().toString();
            String result = ollamaQueryLLMBlocking(model, LLMPrompt);
            this.ExecutiveSummary_AIgenerated = result;
            return result;
        }

    }


    public void setExecutiveSummaryText(String executiveSummaryText) {
        ExecutiveSummaryText = executiveSummaryText;
    }





    public ExecSummaryPanel(MontoyaApi montoyapi,Boolean debug,ExporterController exportController,boolean useMarkdown)
    {
        super();
        this.montoyapi=montoyapi;
        this.debug=debug;
        this.exportController=exportController;
        this.useMarkdown=useMarkdown;

        Events.subscribe(Events.AddIssuesEventToModel.class, e -> IssuesAdded());
        Events.subscribe(Events.RemoveAllIssues.class, e -> IssuesRemoved());

        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder("Exec Summary Options"));


        //Mode radio butons
        this.radioExecSumMode_userSupplied = new JRadioButton("User Supplied");
        this.radioExecSumMode_userSupplied.setSelected(true);


        this.radioExecSumMode_AIgenerated = new JRadioButton("AI generated");
        ButtonGroup groupTemplates = new ButtonGroup();
        groupTemplates.add(radioExecSumMode_userSupplied);
        groupTemplates.add(radioExecSumMode_AIgenerated);




        JPanel panelExecSumModes=new JPanel(new GridBagLayout());
        panelExecSumModes.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelExecSumModes.setLayout(new GridBagLayout());
        //panelExecSumModes.setBorder(new LineBorder(Color.BLUE,4));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        panelExecSumModes.add(radioExecSumMode_userSupplied,gbc);


        JLabel lblUploadExecSummary = new JLabel("Current Summary");
        Font font = lblUploadExecSummary.getFont();
        Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        lblUploadExecSummary.setFont(font.deriveFont(attributes));
        lblUploadExecSummary.setForeground(Color.BLUE);
        lblUploadExecSummary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.anchor=GridBagConstraints.WEST;
        panelExecSumModes.add(lblUploadExecSummary,gbc);

        // Add the click handler
        lblUploadExecSummary.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Get the parent Frame for the dialog
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(ExecSummaryPanel.this);

                // Create and show the custom dialog
                EditTextDialog dialog = new EditTextDialog(parentFrame, ExecutiveSummary_userSupplied,ExecutiveSummary_userSupplied, "Edit Executive Summary");
                String updatedText = dialog.showDialog(); // This will block until dialog is closed

                // Check if text was saved (not cancelled)
                if (updatedText != null) {
                    ExecutiveSummary_userSupplied = updatedText;
                    //ExecSummaryPanel.this.montoyapi.logging().logToOutput(ExecutiveSummaryText);
                }
            }
        });




        //2nd radio
        gbc.gridy = 1;
        gbc.gridx = 0;
        panelExecSumModes.add(radioExecSumMode_AIgenerated,gbc);

        JLabel lblReviewPrompt = new JLabel("Review Prompt");
        font = lblUploadExecSummary.getFont();
        attributes = new HashMap<>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        lblReviewPrompt.setFont(font.deriveFont(attributes));
        lblReviewPrompt.setForeground(Color.BLUE);
        lblReviewPrompt.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        //gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor=GridBagConstraints.WEST;
        panelExecSumModes.add(lblReviewPrompt,gbc);

        lblReviewPrompt.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                EditTextDialog dialog;
                String updatedText;

                // Get the parent Frame for the dialog
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(ExecSummaryPanel.this);
                String defaultPrompt=GenerateInitialPrompt(ExecSummaryPanel.this.exportController.issuesModel.getFindings());

                // Create and show the custom dialog
                if(!PromptUpdatedByUser || UserAddedMoreIssues)
                {
                    UserAddedMoreIssues=false;
                    LLMPrompt = defaultPrompt;
                }
                if(IssuedRemovedByUser)
                {
                    IssuedRemovedByUser=false;
                    LLMPrompt= Constants.initial_LLMPrompt;
                }


                dialog = new EditTextDialog(parentFrame, LLMPrompt, defaultPrompt, "Edit the LLM Prompt");

                updatedText = dialog.showDialog(); // This will block until dialog is closed

                // Check if text was saved (not cancelled)
                if (updatedText != null) {
                    LLMPrompt = updatedText;
                    PromptUpdatedByUser=true;
                }
            }
        });






        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx=0;
        gbc.weightx = 1.0;
        this.add(panelExecSumModes,gbc);


        //AI OPtions
        AIOptionsPanel = new JPanel(new GridBagLayout());
        AIOptionsPanel.setBorder(BorderFactory.createTitledBorder("Ollama options"));
        gbc = new GridBagConstraints();
        //gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        gbc.gridx=0;
        //gbc.gridwidth=2;
        this.add(AIOptionsPanel,gbc);


        JLabel lblOllama=new JLabel("Ollama Endpoint");
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx=0;
        gbc.weightx=0.2f;
        AIOptionsPanel.add(lblOllama,gbc);

        txtOllamaEndpoint = new JTextField(15);
        txtOllamaEndpoint.setText("http://127.0.0.1:11434");
        gbc.gridx = 1;
        gbc.weightx=0.7f;
        AIOptionsPanel.add(txtOllamaEndpoint,gbc);

        gbc.gridx = 2;
        gbc.weightx=0.1f;
        JButton btnOllamaConnect = new JButton("Connect");
        btnOllamaConnect.addActionListener(e -> {
            String error_msg=validate_AIConnect();
            if(error_msg.isEmpty())
                ollamaConnectInBackground();
            else
            {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, error_msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
                });
            }

        });
        AIOptionsPanel.add(btnOllamaConnect,gbc);


        //2nd row
        JLabel lblOllamaModels=new JLabel("Ollama Models");
        //gbc = new GridBagConstraints();
        //gbc.fill = GridBagConstraints.BOTH;
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        gbc.gridx=0;
        AIOptionsPanel.add(lblOllamaModels,gbc);

        cmb_OllamaModels=new JComboBox<>();
        gbc.gridx=1;
        //gbc.fill = GridBagConstraints.BOTH;
        AIOptionsPanel.add(cmb_OllamaModels,gbc);



        // --- ADD ACTION LISTENERS TO RADIO BUTTONS ---
        radioExecSumMode_userSupplied.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disableComponents(AIOptionsPanel);
            }
        });

        radioExecSumMode_AIgenerated.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableComponents(AIOptionsPanel);
            }
        });
        // ---------------------------------------------


        //temperature Slider
        temperatureLabel = new JLabel("Temperature");
        temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        //temperatureValueLabel.setFont(new Font("Arial", Font.BOLD, 16));

        this.LLMTemperature = (float) SLIDER_INITIAL / SLIDER_SCALE;
        temperatureSlider = new JSlider(JSlider.HORIZONTAL, SLIDER_MIN, SLIDER_MAX, SLIDER_INITIAL);
        temperatureSlider.setMajorTickSpacing(50);
        temperatureSlider.setMinorTickSpacing(10);
        temperatureSlider.setPaintTicks(true);
        temperatureSlider.setPaintLabels(true);



        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 2;
        gbc.gridx=0;
        AIOptionsPanel.add(temperatureLabel,gbc);

        gbc.gridx=1;
        //gbc.weightx=2;
        //gbc.fill = GridBagConstraints.BOTH;
        AIOptionsPanel.add(temperatureSlider,gbc);

        temperatureValueLabel = new JLabel(String.format("%.2f", LLMTemperature));
        temperatureValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx=2;
        gbc.anchor=GridBagConstraints.EAST;
        AIOptionsPanel.add(temperatureValueLabel,gbc);

        // 3. Add a ChangeListener to the JSlider
        temperatureSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Get the current integer value from the slider
                int sliderValue = temperatureSlider.getValue();
                LLMTemperature = (float) sliderValue / SLIDER_SCALE;
                temperatureValueLabel.setText(String.format("%.2f", LLMTemperature));

                // You can add logic here to use the 'currentTemperature'
                // For example, if you had an LLM API client, you'd update its temperature setting
                // System.out.println("New Temperature: " + currentTemperature);
            }
        });


        disableComponents(AIOptionsPanel);

    }


    private void IssuesAdded()
    {
        UserAddedMoreIssues=true;
        LLMPrompt=GenerateInitialPrompt(ExecSummaryPanel.this.exportController.issuesModel.getFindings());
    }


    private void IssuesRemoved()
    {

    }






    private String GenerateInitialPrompt(List<Finding> findings)
    {
        StringBuilder promptBuilder=new StringBuilder();
        //System Message - Persona
        //promptBuilder.append("You summarize security findings so as your output is put inside an Executive Summary. Generate a summary of the following list of Findings, focusing more on the High/Medium severity findings:\n");
        //promptBuilder.append("You summarize security findings so as your output is put inside an Executive Summary. Generate a executive summary of the following list of Findings, focusing more on the more severe severity findings mentioning also their name and their impact:\n");
        //promptBuilder.append("Create a summary of the following security findings below, so as your output is put inside an Executive Summary report. Here is the list of Findings:\n");
        promptBuilder.append("You are a Penetration Tester and have discovered the following list of Security Findings. You have two tasks: a) create an Executive Summary presenting each one of the findings below with its name, impact and the corresponding web pages it was found and b) at the end provide a general evaluation of the security posture. Here is the list of findings that you must summarize:\n\n---\n");
        //promptBuilder.append(LM_persona);

        //Define Core Responsibilities and Characteristics
        //promptBuilder.append(LM_core_resp);


        //One-Shot Example
        //String example1=ExecSummaryExample1();
        //promptBuilder.append("Here is an example of raw penetration testing findings and how I would like yo uto summarise these in an Executive Summary. Pay attention to the tone, level of detail and focus on business impact in the summary.\n");
        //promptBuilder.append(example1);

        //Task
        //promptBuilder.append(LM_task);
        int i=1;
        //promptBuilder.append("Scope: "+Finding.getProjectScope(findings));
        //promptBuilder.append("\nDuring the recent penetration testing the following Security findings have been found:\n");
        if(findings.size()>0) {
            for (Finding f : findings) {
                promptBuilder.append("\n"+i + ". Finding name: " +f.issueName+". ");
                //promptBuilder.append("\n"+i + ". "+f.issueName+".");
                //promptBuilder.append("\n    Severity: "+ f.severity);
                //promptBuilder.append("\n    Description: " +Utilities.extractText(f.getIssueDetailsSummaryHTML())+". "+Utilities.extractText(f.issueDescrBackground));
                promptBuilder.append("\n    " +Utilities.extractText(f.issueDescrBackground)+". "+Utilities.extractText(f.getIssueDetailsSummaryHTML()));
                //promptBuilder.append("\n    Impact: "+ Utilities.extractText(f.issueDescrBackground));
                //promptBuilder.append("\n    Remediation: "+Utilities.extractText(f.issueRemediation));
                i++;
            }
            promptBuilder.append("\n\nOverall Security Posture: "+Finding.getProjectOverallSecurityPosture(findings)+"\n---");
        }
        else
        {
            promptBuilder.append(Constants.initial_LLMPrompt);
        }

        return promptBuilder.toString();
    }


    private static String ExecSummaryExample1()
    {
        String example1= """
                ---
                Example Data:
                
                Scope: Public-facing corporate website (www.example.com), Mail Server, DNS Server, VPN Gateway
                List of Findings:
                1. Finding: Unauthenticated Remote Code Execution (RCE) on Web Server.
                   Severity: High
                   Description: A vulnerability was identified in the web application's file upload functionality (specifically, a legacy photo gallery plugin). This vulnerability allows an unauthenticated attacker to upload arbitrary executable files to the web server, leading to full system compromise.
                   Impact: Complete compromise of the public-facing web server, potential for data exfiltration from the underlying database, defacement of the website, and use of the compromised server as a pivot point into the internal network if network segmentation is insufficient.                
                2. Finding: Outdated Software (Apache HTTP Server)
                   Severity: Medium
                   Description: The Apache HTTP Server running on the web server is an outdated version (2.4.X), containing several known vulnerabilities that have public exploits available. While no direct exploitation was achieved during the test, this poses a significant risk.
                   Impact: Increased attack surface, potential for future exploitation by adversaries leveraging publicly known vulnerabilities, potential for denial-of-service or information disclosure.                
                3. Finding: Missing Security Headers on Web Application
                   Severity:Low
                   Description: HTTP security headers (e.g., Content-Security-Policy, X-Frame-Options) are not fully implemented, slightly increasing the risk of client-side attacks like XSS or clickjacking.
                   Impact: Minor increase in client-side attack risk, minor impact on user experience if client-side attacks occur.                
                Overall Security Posture: High vulnerabilities detected, requiring immediate remediation to prevent system compromise and data breach.
                
                Example of Desired Executive Summary:
                This penetration test of Public-facing corporate website (www.example.com), Mail Server, DNS Server, VPN Gateway identified high security vulnerabilities that pose a significant and immediate risk to the organization's digital assets, customer data, and overall business operations.                
                
                The most severe finding is a Remote Code Execution (RCE) vulnerability on the primary web server, which could allow an unauthenticated attacker to gain complete control of the system. This presents a direct path to data theft, website defacement, and potential pivoting into the internal network. Further analysis revealed that the web server is running outdated Apache HTTP Server software with known vulnerabilities. While not directly exploited during this assessment, this significantly increases the attack surface and leaves the system susceptible to future compromise. Additionally, the web application is missing crucial HTTP security headers, which slightly elevates the risk of client-side attacks such as cross-site scripting (XSS) or clickjacking, potentially impacting user experience.
                
                Immediate action is strongly recommended to remediate these high-severity vulnerabilities to safeguard organizational data, maintain customer trust, and ensure business continuity.
                ---                 
                """;

        return example1;
    }


    private void ollamaConnectInBackground()
    {
        new SwingWorker<Boolean, Void>()
        {
            private List<OllamaModel> lst_models=null;

            @Override
            protected Boolean doInBackground() throws Exception
            {
                try
                {
                    ollama_client=new OllamaClient(txtOllamaEndpoint.getText().trim(),ExecSummaryPanel.this.montoyapi,ExecSummaryPanel.this.debug);

                    lst_models=ollama_client.GetOllamaModels();
                    if(lst_models == null)
                    {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, "Unable to connect to Ollama","Connection Error", JOptionPane.WARNING_MESSAGE);
                        });
                        return false;
                    }
                    return true;
                }
                catch (Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            protected void done()
            {
                try {
                    boolean result = get();
                    cmb_OllamaModels.removeAllItems();
                    if (result)
                    {
                        for (OllamaModel model:lst_models) {
                            cmb_OllamaModels.addItem(new OllamaModel(model.name,model.model));
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();
    }


    private void ollamaQueryLLM(String model,String prompt)
    {
        String escapedPrompt=prompt.replace("'", "\\'").replace("<p>", "").replace("</p>", "");
        this.montoyapi.logging().logToOutput(escapedPrompt);
        new SwingWorker<String, Void>()
        {
            @Override
            protected String doInBackground() throws Exception
            {
                try
                {
                    String answer="";
                     //answer=ollama_client.QueryLLM(model,escapedPrompt);
                    return ollama_client.QueryLLMLangchain(model,prompt,(double)LLMTemperature);
                    //return answer;
                }
                catch (Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            protected void done()
            {
                try {
                    String result = get();
                    ExecSummaryPanel.this.ExecutiveSummary_AIgenerated=result;

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();
    }


    public String ollamaQueryLLMBlocking(String model, String prompt) {
        Events.publish(new Events.UpdateDebugEvent(Constants.export_AIGeneratingSummary+"\n"));
        String escapedPrompt = prompt.replace("'", "\\'").replace("<p>", "").replace("</p>", "");

        try {
            if (ollama_client != null) {
                return sanitizeLLMOutput(ollama_client.QueryLLMLangchain(model, prompt,(double)LLMTemperature));
            }
        } catch (Exception ex) {
            this.montoyapi.logging().logToError("Error calling LLM: "+ Utilities.getStackTraceAsString(ex));
            return ExecutiveSummary_userSupplied;
        }
        return "";
    }


    public static String sanitizeLLMOutput(String input) {
        if (input == null) {
            return null;
        }

        //String temp=StringEscapeUtils.escapeXml11(input);
        String temp=input;

        //first santize with input StringEscapeUtils.escapeXml11
        StringBuilder out = new StringBuilder();
        int length = temp.length();
        for (int i = 0; i < length; ) {
            int codePoint = temp.codePointAt(i);

            // Filter out non-XML 1.0 valid characters (which generally work for HTML as well)
            // This excludes control characters like null, SOH, etc.
            if ((codePoint == 0x9 || codePoint == 0xA || codePoint == 0xD) || // Tab, Line Feed, Carriage Return
                    (codePoint >= 0x20 && codePoint <= 0xD7FF) ||                 // Basic Multilingual Plane (BMP) excluding surrogates
                    (codePoint >= 0xE000 && codePoint <= 0xFFFD) ||               // BMP excluding noncharacters
                    (codePoint >= 0x10000 && codePoint <= 0x10FFFF)) {            // Supplementary Plane characters

                // Escape special HTML characters
                switch (codePoint) {
                    case '&':
                        out.append("&amp;");
                        break;
                    case '<':
                        out.append("&lt;");
                        break;
                    case '>':
                        out.append("&gt;");
                        break;
                    default:
                        out.appendCodePoint(codePoint);
                        break;
                }
            }

            i += Character.charCount(codePoint);
        }

        return out.toString();
    }


    public void disableComponents(Container container)
    {
        container.setEnabled(false);
        for (Component comp : container.getComponents()) {
            if (comp instanceof Container) {
                disableComponents((Container) comp); // Recursive call for nested panels
            }
            comp.setEnabled(false);
        }
    }

    public void enableComponents(Container container)
    {
        container.setEnabled(true);
        for (Component comp : container.getComponents()) {
            if (comp instanceof Container) {
                enableComponents((Container) comp); // Recursive call for nested panels
            }
            comp.setEnabled(true);
        }
    }


    public String validate_AIConnect()
    {
        String error_msg="";
        if (radioExecSumMode_AIgenerated.isSelected())
        {
            //validate if Ollama URL Is valid URL
            try {
                boolean isvalidURL=Utilities.isValidURL(txtOllamaEndpoint.getText().trim());
                if (!isvalidURL)
                    error_msg=Constants.msgNotValidUrl;
            } catch (MalformedURLException ex) {
                error_msg=Constants.msgNotValidUrl; // String is not a well-formed URL
            }
        }
        return error_msg;
    }


    public String validate_AIOptions()
    {
        String error_msg="";
        if (radioExecSumMode_AIgenerated.isSelected())
        {
            //validate if Ollama URL Is valid URL
            try {
                boolean isvalidURL=Utilities.isValidURL(txtOllamaEndpoint.getText().trim());
                if (!isvalidURL)
                    error_msg=Constants.msgNotValidUrl;
            } catch (MalformedURLException ex) {
                error_msg=Constants.msgNotValidUrl; // String is not a well-formed URL
            }
            if (txtOllamaEndpoint.getText().isBlank())
            {
                error_msg=Constants.AIOptions_ollamaEndpointEmpty;
            }
            if (cmb_OllamaModels.getSelectedItem()==null)
            {
                error_msg=Constants.AIOptions_ollamaModelNotSelected;
            }
        }
        return error_msg;
    }


    public class EditTextDialog extends JDialog {
        private JTextArea textArea;
        private JButton saveButton;
        private JButton cancelButton;
        private JButton loadInitialButton; // The new button
        private String savedText; // To store the text when saved
        private String initialText; // Store the original initial text
        private boolean cancelled = true; // To know if the dialog was cancelled

        public EditTextDialog(Frame owner, String initialText,String defaultPrompt, String title) {
            super(owner, title, true); // true makes it modal (blocks parent until closed)
            setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Close on X button

            this.initialText = initialText; // Store the initial text

            textArea = new JTextArea(20, 60); // 20 rows, 60 columns
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setText(initialText); // Set initial text

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            saveButton = new JButton("Save");
            cancelButton = new JButton("Cancel");
            loadInitialButton = new JButton("Load Initial"); // Initialize the new button

            // Layout the dialog components
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(saveButton);
            buttonPanel.add(loadInitialButton);
            buttonPanel.add(cancelButton);

            setLayout(new BorderLayout(10, 10)); // Padding between components
            add(scrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            // Add action listeners
            saveButton.addActionListener(e -> {
                savedText = textArea.getText();
                cancelled = false;
                dispose(); // Close the dialog
            });

            cancelButton.addActionListener(e -> {
                cancelled = true;
                dispose(); // Close the dialog without saving
            });

            // Action listener for the new "Load Initial" button
            loadInitialButton.addActionListener(e -> {
                textArea.setText(defaultPrompt); // Set the text area back to the stored initial text
                // You might want to provide some user feedback, e.g., a small sound or a temporary message.
                // JOptionPane.showMessageDialog(this, "Text reverted to initial content.", "Info", JOptionPane.INFORMATION_MESSAGE);
            });

            pack(); // Pack components to their preferred size
            setLocationRelativeTo(owner); // Center relative to the parent frame
        }

        /**
         * Shows the dialog and returns the saved text.
         * Returns null if the dialog was cancelled.
         */
        public String showDialog() {
            setVisible(true); // Make the dialog visible (blocks until dispose() is called)
            return cancelled ? null : savedText;
        }

        /**
         * Returns true if the dialog was cancelled (user clicked cancel or closed via X).
         */
        public boolean isCancelled() {
            return cancelled;
        }
    }




}
