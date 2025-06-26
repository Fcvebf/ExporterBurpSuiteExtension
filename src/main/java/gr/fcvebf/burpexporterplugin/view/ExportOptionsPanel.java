package gr.fcvebf.burpexporterplugin.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ai.Ai;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import freemarker.template.*;

import freemarker.template.Template;
import gr.fcvebf.burpexporterplugin.controllers.ExporterController;
import gr.fcvebf.burpexporterplugin.models.pwndoc.*;
import gr.fcvebf.burpexporterplugin.utils.*;
import gr.fcvebf.burpexporterplugin.utils.Config.exportOptionsEnum;

import static gr.fcvebf.burpexporterplugin.utils.Config.debugHTTPrequests;
import static gr.fcvebf.burpexporterplugin.utils.Config.useMontoyaHTTPApi;

public class ExportOptionsPanel extends JPanel {

    private ExporterController exporterController;
    private DebugPanel dbgPanel;

    private JComboBox<Config.exportOptionsEnum> dropdown_exportoptions;
    private JButton btnExport;
    private JButton btnClear;
    private JPanel southPanel;

    private JPanel pwndocOptions;
    private JPanel CSVOptions;
    private JPanel JSONOptions;
    private JPanel XMLOptions;
    private JPanel MarkdownOptions;
    private JPanel DocxOptions;
    public ExecSummaryPanel docxExecSumPanel;
    public ExecSummaryPanel mdExecSumPanel;
    JPanel pwndocAuditSelectionOptions;
    JPanel pwndocFindingsOptions;

    PwndocApi p_api=null;
    JTextField txt_pwndocURL;
    JTextField txt_pwndocuser;
    JPasswordField txt_pwndocpassword;
    JTextField txt_auditname;
    JComboBox<AuditType.ComboAuditTypeItem> cmb_PwndocAuditType = new JComboBox<>();;
    JComboBox cmb_PwndocLanguage;
    boolean pwndoc_createNewAudit=true;
    String auditIdToApppend="";
    JComboBox cmb_PwndocFindingCategory= new JComboBox<>();;
    JComboBox cmb_PwndocFindingType= new JComboBox<>();;

    JTextField txt_CSVname;
    JCheckBox chkCSVIncludeHTTPOC;

    JTextField txt_JSONname;
    JCheckBox chkJSONIncludeHTTPOC;

    JTextField txt_XMLname;
    JCheckBox chkXMLIncludeHTTPOC;

    JTextField txt_MarkdownOutFile;
    JTextField txt_MarkdownTemplateFile;
    JTextField txt_MarkdownProjectName;
    JTextField txt_MarkdownProjectDate;
    JRadioButton radioMarkdownDefaultTemplate;
    JRadioButton radioMarkdownCustomTemplate;
    JCheckBox chkMarkdownIncludeHTTPOC;
    JLabel lblMarkdownloadDefaultTemplate;

    JTextField txt_DocxOutFile;
    JTextField txt_DocxTemplateFile;
    JTextField txt_DocxProjectName;
    JTextField txt_DocxProjectDate;
    JRadioButton radioDocxDefaultTemplate;
    JRadioButton radioDocxCustomTemplate;
    JCheckBox chkDocxIncludeHTTPOC;
    JLabel lblDocxloadDefaultTemplate;

    public MontoyaApi montoyaApi;
    public Ai ai;


    public ExportOptionsPanel(MontoyaApi montoyaApi,Ai ai, DebugPanel dbgPanel)
    {
        this.montoyaApi=montoyaApi;
        this.ai=ai;
        this.dbgPanel=dbgPanel;
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        //NORTH PANE; IS THE EXPORT OPTIONS DROP DOWN
        JPanel expOptionsPanel = new JPanel();
        expOptionsPanel.setPreferredSize(new Dimension(200, 100));
        //expOptionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        expOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel expformatLabel = new JLabel("Export Format",JLabel.LEFT );
        dropdown_exportoptions = new JComboBox<>(exportOptionsEnum.values());
        dropdown_exportoptions.setSelectedItem(exportOptionsEnum.Pwndoc);
        dropdown_exportoptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        expOptionsPanel.add(expformatLabel);
        expOptionsPanel.add(dropdown_exportoptions);

        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.add(expOptionsPanel);


        //WEST PANEL: THIS WILL CONTAIN THE VISIBLE PANEL EVERY TIME
        JPanel westPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        CreatePwndocPanel();
        CreateCSVPanel();
        CreateJSONPanel();
        CreateXMLPanel();
        CreateMDPanel();
        CreateDocxPanel();
        westPanel.add(pwndocOptions);


        dropdown_exportoptions.addActionListener(e -> {
            Config.exportOptionsEnum selectedValue = (Config.exportOptionsEnum) dropdown_exportoptions.getSelectedItem();

            if (selectedValue == exportOptionsEnum.Pwndoc ){
                CSVOptions.setVisible(false);
                JSONOptions.setVisible(false);
                XMLOptions.setVisible(false);
                MarkdownOptions.setVisible(false);
                DocxOptions.setVisible(false);
                westPanel.removeAll();
                westPanel.add(pwndocOptions);
                pwndocOptions.setVisible(true);
            }
            else if (selectedValue == exportOptionsEnum.CSV )
            {
                //disableComponents(westPanel);
                pwndocOptions.setVisible(false);
                JSONOptions.setVisible(false);
                XMLOptions.setVisible(false);
                MarkdownOptions.setVisible(false);
                DocxOptions.setVisible(false);
                westPanel.removeAll();
                westPanel.add(CSVOptions);
                CSVOptions.setVisible(true);

            }
            else if (selectedValue == exportOptionsEnum.JSON )
            {
                //disableComponents(westPanel);
                pwndocOptions.setVisible(false);
                CSVOptions.setVisible(false);
                XMLOptions.setVisible(false);
                MarkdownOptions.setVisible(false);
                DocxOptions.setVisible(false);
                westPanel.removeAll();
                westPanel.add(JSONOptions);
                JSONOptions.setVisible(true);
            }
            else if (selectedValue == exportOptionsEnum.XML )
            {
                //disableComponents(westPanel);
                pwndocOptions.setVisible(false);
                CSVOptions.setVisible(false);
                JSONOptions.setVisible(false);
                MarkdownOptions.setVisible(false);
                DocxOptions.setVisible(false);
                westPanel.removeAll();
                westPanel.add(XMLOptions);
                XMLOptions.setVisible(true);
            }
            else if (selectedValue == exportOptionsEnum.Markdown )
            {
                //disableComponents(westPanel);
                pwndocOptions.setVisible(false);
                CSVOptions.setVisible(false);
                JSONOptions.setVisible(false);
                XMLOptions.setVisible(false);
                DocxOptions.setVisible(false);
                westPanel.removeAll();
                westPanel.add(MarkdownOptions);
                MarkdownOptions.setVisible(true);
            }
            else if (selectedValue == exportOptionsEnum.Docx )
            {
                //disableComponents(westPanel);
                pwndocOptions.setVisible(false);
                CSVOptions.setVisible(false);
                JSONOptions.setVisible(false);
                XMLOptions.setVisible(false);
                MarkdownOptions.setVisible(false);
                westPanel.removeAll();
                westPanel.add(DocxOptions);

                DocxOptions.setVisible(true);

            }
            else {

            }
            //westPanel.revalidate();
            //westPanel.repaint();
            updateDividerLocation();

        });

        //CENTER PANEL: SPACE
        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(0, 350));
        centerPanel.add(new JLabel(""));

        this.add(northPanel,BorderLayout.NORTH);
        this.add(westPanel,BorderLayout.WEST);
        this.add(centerPanel, BorderLayout.CENTER);

    }


    private void updateDividerLocation()
    {
        Component currentVisibleComponent = ExportOptionsPanel.this;
        JSplitPane splitpane = (JSplitPane) this.getParent().getParent().getComponents()[0];

        int preferredWidth = currentVisibleComponent.getPreferredSize().width;
        preferredWidth += this.getInsets().left + this.getParent().getInsets().right;

        splitpane.setDividerLocation(preferredWidth + splitpane.getDividerSize());
    }





    private void CreatePwndocPanel()
    {
        pwndocOptions = new JPanel();
        pwndocOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        pwndocOptions.setLayout(new GridBagLayout());
        pwndocOptions.setBorder(BorderFactory.createTitledBorder("Pwndoc Options"));


        JPanel pwndocLoginOptions = new JPanel();
        pwndocLoginOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        pwndocLoginOptions.setLayout(new GridBagLayout());
        pwndocLoginOptions.setBorder(BorderFactory.createTitledBorder("Pwndoc Login"));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridwidth=2;
        pwndocOptions.add(pwndocLoginOptions,gbc);


        JLabel lblPwnDocURL=new JLabel("Pwndoc URL", JLabel.LEFT);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx=0;
        gbc.weightx=0.3f;
        pwndocLoginOptions.add(lblPwnDocURL,gbc);

        txt_pwndocURL = new JTextField("https://127.0.0.1:8443", 20);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx=1;
        gbc.weightx=0.7f;
        pwndocLoginOptions.add(txt_pwndocURL,gbc);

        //2nd row
        JLabel lblUsername=new JLabel("Username", JLabel.LEFT);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        gbc.gridx=0;
        pwndocLoginOptions.add(lblUsername,gbc);

        txt_pwndocuser = new JTextField("admin", 20);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        gbc.gridx=1;
        pwndocLoginOptions.add(txt_pwndocuser,gbc);


        //3rd row
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 2;
        gbc.gridx=0;
        pwndocLoginOptions.add(new JLabel("Password", JLabel.LEFT),gbc);

        txt_pwndocpassword = new JPasswordField(20);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 2;
        gbc.gridx=1;
        pwndocLoginOptions.add(txt_pwndocpassword,gbc);

        //4th row
        JButton btnPwndocLogin = new JButton("Login");
        gbc = new GridBagConstraints();
        //gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 3;
        gbc.gridx=1;
        pwndocLoginOptions.add(btnPwndocLogin,gbc);
        btnPwndocLogin.setBorder(new LineBorder(Color.LIGHT_GRAY,2));
        btnPwndocLogin.addActionListener(e ->
        {
            String errorMsg=validatePwndocLogin();
            if(errorMsg.isEmpty())
            {
                fetchPwndocDataInBackground();
            }
            else
            {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, errorMsg, "Validation Error", JOptionPane.WARNING_MESSAGE);
                });

            }
        });


        /*
                        AUDIT OPTIONS
         */
        pwndocAuditSelectionOptions = new JPanel();
        pwndocAuditSelectionOptions.setEnabled(false);
        pwndocAuditSelectionOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        pwndocAuditSelectionOptions.setLayout(new GridBagLayout());
        pwndocAuditSelectionOptions.setBorder(BorderFactory.createTitledBorder("Audit Options"));

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 1;
        gbc.gridwidth=2;
        pwndocOptions.add(pwndocAuditSelectionOptions,gbc);

        //1st row
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx=0;
        gbc.weightx=0.3f;
        pwndocAuditSelectionOptions.add(new JLabel("Audit Name", JLabel.LEFT),gbc);

        txt_auditname = new JTextField(20);
        txt_auditname.setText("Burp Suite Scan");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx=1;
        gbc.weightx=0.7f;
        pwndocAuditSelectionOptions.add(txt_auditname,gbc);

        //2nd row
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        gbc.gridx=0;
        pwndocAuditSelectionOptions.add(new JLabel("Audit Type", JLabel.LEFT),gbc);

        cmb_PwndocAuditType= new JComboBox<>();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        gbc.gridx=1;
        pwndocAuditSelectionOptions.add(cmb_PwndocAuditType,gbc);


        //3rd row
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 2;
        gbc.gridx=0;
        pwndocAuditSelectionOptions.add(new JLabel("Language", JLabel.LEFT),gbc);

        cmb_PwndocLanguage=new JComboBox();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 2;
        gbc.gridx=1;
        pwndocAuditSelectionOptions.add(cmb_PwndocLanguage,gbc);
        disableComponents(pwndocAuditSelectionOptions);




        /*
                        FINDINGS OPTIONS
         */
        pwndocFindingsOptions = new JPanel();
        pwndocFindingsOptions.setEnabled(false);
        pwndocFindingsOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        pwndocFindingsOptions.setLayout(new GridBagLayout());
        pwndocFindingsOptions.setBorder(BorderFactory.createTitledBorder("Findings Options"));
        gbc = new GridBagConstraints();

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 2;
        gbc.gridwidth=2;
        pwndocOptions.add(pwndocFindingsOptions,gbc);

        //1st row
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx=0;
        gbc.weightx=0.3f;
        pwndocFindingsOptions.add(new JLabel("Finding Category", JLabel.LEFT),gbc);

        cmb_PwndocFindingCategory= new JComboBox<>();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx=1;
        gbc.weightx=0.7f;
        pwndocFindingsOptions.add(cmb_PwndocFindingCategory,gbc);

        //2nd row
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        gbc.gridx=0;
        pwndocFindingsOptions.add(new JLabel("Finding Type", JLabel.LEFT),gbc);

        cmb_PwndocFindingType= new JComboBox<>();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        gbc.gridx=1;
        pwndocFindingsOptions.add(cmb_PwndocFindingType,gbc);
        disableComponents(pwndocFindingsOptions);
    }




    private void fetchPwndocDataInBackground()
    {
        new SwingWorker<Boolean, Void>() {
            private List<AuditType> auditTypes;
            private List<Language> languages;
            private List<VulnCategory> vulnCategories;
            private List<VulnType> vulnTypes;

            @Override
            protected Boolean doInBackground() throws Exception {
                // Perform login
                ProxyConfig proxy_conf=null;
                if(Config.proxyHTTPrequests)
                    proxy_conf=new ProxyConfig(Config.default_proxyHost,Config.default_proxyPort);

                p_api = new PwndocApi(txt_pwndocURL.getText(),proxy_conf,debugHTTPrequests,useMontoyaHTTPApi,montoyaApi);
                boolean logged_in = p_api.Login(txt_pwndocuser.getText(), new String(txt_pwndocpassword.getPassword()));
                if (logged_in)
                {
                    // Fetch data
                    auditTypes = p_api.AuditTypesGet();
                    languages = p_api.LanguagesGet();
                    vulnCategories = p_api.VulnCategoriesGet();
                    vulnTypes = p_api.VulnTypesGet();
                }
                else
                {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "Unable to Login to Pwndoc","Login Error", JOptionPane.WARNING_MESSAGE);
                    });

                }
                return logged_in;
            }

            @Override
            protected void done()
            {
                try
                {
                    Boolean logged_in = get();
                    if(logged_in) {
                        pwndocAuditSelectionOptions.setEnabled(true);
                        enableComponents(pwndocAuditSelectionOptions);
                        enableComponents(pwndocFindingsOptions);

                        // Update UI on EDT
                        cmb_PwndocAuditType.removeAllItems();
                        //List<AuditType> auditTypes=new ArrayList<>();
                        cmb_PwndocAuditType.removeAllItems();
                        for (AuditType audtype : auditTypes) {
                            cmb_PwndocAuditType.addItem(new AuditType.ComboAuditTypeItem(audtype.get_id(), audtype.getName()));
                        }

                        cmb_PwndocLanguage.removeAllItems();
                        for (Language lang : languages) {
                            cmb_PwndocLanguage.addItem(new Language.ComboLanguageItem(lang.getLocale(), lang.getLanguage()));
                        }

                        cmb_PwndocFindingCategory.removeAllItems();
                        for (VulnCategory cat : vulnCategories) {
                            cmb_PwndocFindingCategory.addItem(new VulnCategory.ComboVulnCateogryItem(cat.name, cat.name));
                        }

                        cmb_PwndocFindingType.removeAllItems();
                        for (VulnType vtype : vulnTypes) {
                            cmb_PwndocFindingType.addItem(new VulnType.ComboVulnTypeItem(vtype.name, vtype.name));
                        }
                    }
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(ExportOptionsPanel.this,"Failed to load data: " + e.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }





    private void  CreateCSVPanel()
    {
        CSVOptions = new JPanel();
        CSVOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        CSVOptions.setLayout(new GridBagLayout());
        CSVOptions.setBorder(BorderFactory.createTitledBorder("CSV Options")); // <-- Here is the line + title
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 1st row, 1st colum (30%)   |  gridX: horizontal axe
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        CSVOptions.add(new JLabel("CSV Filename"), gbc);
        // 1st row, 2nd column (70%)
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txt_CSVname=new JTextField("",30);
        txt_CSVname.setEditable(false);
        CSVOptions.add(txt_CSVname,gbc);

        // 2nd row, 2nd column (to the right)
        gbc.gridx = 1;
        gbc.gridy=1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.7;
        JButton browseCSVButton = new JButton("  Choose File  ");
        browseCSVButton.setBorder(new LineBorder(Color.LIGHT_GRAY,2));
        browseCSVButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(Constants.defaultCSVExportName));
            int result = fileChooser.showOpenDialog(this); // or frame
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                txt_CSVname.setText(selectedFile.getAbsolutePath());
            }
        });
        CSVOptions.add(browseCSVButton,gbc);


        //3rd row
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor=GridBagConstraints.WEST;
        chkCSVIncludeHTTPOC = new JCheckBox("Include HTTP Requests");
        chkCSVIncludeHTTPOC.setSelected(false);
        CSVOptions.add(chkCSVIncludeHTTPOC,gbc);


        CSVOptions.setVisible(false);
    }


    private void CreateJSONPanel()
    {
        JSONOptions = new JPanel();
        JSONOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        JSONOptions.setLayout(new GridBagLayout());
        JSONOptions.setBorder(BorderFactory.createTitledBorder("JSON Options")); // <-- Here is the line + title

        // 1st row, 1st colum (30%)   |  gridX: horizontal axe
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JSONOptions.add(new JLabel("JSON Filename"), gbc);

        // Second column (70%)
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txt_JSONname=new JTextField("",30);
        txt_JSONname.setEditable(false);
        JSONOptions.add(txt_JSONname,gbc);


        //2nd row
        JButton browseJSONButton = new JButton("  Choose File  ");
        browseJSONButton.setBorder(new LineBorder(Color.LIGHT_GRAY,2));
        browseJSONButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(Constants.defaultJSONExportName));
            int result = fileChooser.showOpenDialog(this); // or frame
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                txt_JSONname.setText(selectedFile.getAbsolutePath());
            }
        });

        //2nd row
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.gridy=1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JSONOptions.add(browseJSONButton,gbc);

        //3rd row
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor=GridBagConstraints.WEST;
        chkJSONIncludeHTTPOC = new JCheckBox("Include HTTP Requests");
        chkJSONIncludeHTTPOC.setSelected(false);
        JSONOptions.add(chkJSONIncludeHTTPOC,gbc);

        JSONOptions.setVisible(false);
    }


    private void CreateXMLPanel()
    {
        XMLOptions = new JPanel();
        XMLOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        XMLOptions.setLayout(new GridBagLayout());
        XMLOptions.setBorder(BorderFactory.createTitledBorder("XML Options")); // <-- Here is the line + title

        //1st row
        GridBagConstraints gbc = new GridBagConstraints();
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // First column (30%)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        XMLOptions.add(new JLabel("XML Filename"), gbc);

        // Second column (70%)
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txt_XMLname=new JTextField("",30);
        txt_XMLname.setEditable(false);
        XMLOptions.add(txt_XMLname,gbc);

        //2nd row
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.7;
        gbc.gridy=1;
        JButton browseXMLButton = new JButton("  Choose File  ");
        browseXMLButton.setBorder(new LineBorder(Color.LIGHT_GRAY,2));
        browseXMLButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(Constants.defaultXMLExportName));
            int result = fileChooser.showOpenDialog(this); // or frame
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                txt_XMLname.setText(selectedFile.getAbsolutePath());
            }
        });
        XMLOptions.add(browseXMLButton,gbc);


        //3rd row
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor=GridBagConstraints.WEST;
        chkXMLIncludeHTTPOC = new JCheckBox("Include HTTP Requests");
        chkXMLIncludeHTTPOC.setSelected(false);
        XMLOptions.add(chkXMLIncludeHTTPOC,gbc);


        XMLOptions.setVisible(false);


    }



    private void CreateMDPanel()
    {

        MarkdownOptions = new JPanel();
        MarkdownOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        MarkdownOptions.setLayout(new GridBagLayout());
        MarkdownOptions.setBorder(BorderFactory.createTitledBorder("Markdown Options"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 1st row, 1st colum (30%)   |  gridX: horizontal axe
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        MarkdownOptions.add(new JLabel("Report Filename"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txt_MarkdownOutFile=new JTextField("",30);
        txt_MarkdownOutFile.setEditable(false);
        MarkdownOptions.add(txt_MarkdownOutFile,gbc);


        // 2nd row
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JButton browseMarkdownButton = new JButton("  Choose File  ");
        browseMarkdownButton.setBorder(new LineBorder(Color.LIGHT_GRAY,2));
        browseMarkdownButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(Constants.defaultMDExportName));
            int result = fileChooser.showOpenDialog(this); // or frame
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                txt_MarkdownOutFile.setText(selectedFile.getAbsolutePath());
            }
        });
        MarkdownOptions.add(browseMarkdownButton,gbc);


        //Templates
        JPanel MarkdownTemplateOptions = new JPanel();
        MarkdownTemplateOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        MarkdownTemplateOptions.setLayout(new GridBagLayout());
        MarkdownTemplateOptions.setBorder(BorderFactory.createTitledBorder("Template Selection"));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 2;
        gbc.gridwidth=2;
        //MarkdownTemplateOptions.setBorder(new LineBorder(Color.RED,5));
        MarkdownOptions.add(MarkdownTemplateOptions,gbc);


        JPanel panelTemplates=new JPanel(new GridBagLayout());
        panelTemplates.setAlignmentX(Component.CENTER_ALIGNMENT);
        ButtonGroup groupTemplates = new ButtonGroup();

        radioMarkdownDefaultTemplate = new JRadioButton("Default Template");
        radioMarkdownDefaultTemplate.setSelected(true);
        groupTemplates.add(radioMarkdownDefaultTemplate);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor=GridBagConstraints.WEST;
        panelTemplates.add(radioMarkdownDefaultTemplate,gbc);
        MarkdownTemplateOptions.add(panelTemplates, gbc);


        lblMarkdownloadDefaultTemplate = new JLabel("Download");
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor=GridBagConstraints.NORTHEAST;
        Font font = lblMarkdownloadDefaultTemplate.getFont();
        Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        lblMarkdownloadDefaultTemplate.setFont(font.deriveFont(attributes));
        lblMarkdownloadDefaultTemplate.setForeground(Color.BLUE);
        lblMarkdownloadDefaultTemplate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelTemplates.add(lblMarkdownloadDefaultTemplate,gbc);


        // 2nd row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor=GridBagConstraints.WEST;
        radioMarkdownCustomTemplate = new JRadioButton("Custom Template");
        groupTemplates.add(radioMarkdownCustomTemplate);
        panelTemplates.add(radioMarkdownCustomTemplate,gbc);

        //3rd row
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor=GridBagConstraints.EAST;
        txt_MarkdownTemplateFile=new JTextField("",30);
        txt_MarkdownTemplateFile.setEditable(false);
        panelTemplates.add(txt_MarkdownTemplateFile,gbc);

        // 2nd row
        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JButton browseMarkdownTemplateButton = new JButton("  Choose Template  ");
        browseMarkdownTemplateButton.setBorder(new LineBorder(Color.LIGHT_GRAY,2));
        browseMarkdownTemplateButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this); // or frame
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                txt_MarkdownTemplateFile.setText(selectedFile.getAbsolutePath());
            }
        });
        panelTemplates.add(browseMarkdownTemplateButton,gbc);


        // 1st row, First column (30%)
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridwidth=2;
        gbc.gridy = 0;
        //panelTemplates.setBorder(new LineBorder(Color.GREEN,5));



        JPanel TemplateParameterspanel = new JPanel();
        TemplateParameterspanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        TemplateParameterspanel.setLayout(new GridBagLayout());
        TemplateParameterspanel.setBorder(BorderFactory.createTitledBorder("Template Parameters"));
        gbc = new GridBagConstraints();
        gbc.gridy = 3;
        gbc.gridwidth=2;
        gbc.fill = GridBagConstraints.BOTH;
        MarkdownOptions.add(TemplateParameterspanel,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor=GridBagConstraints.WEST;
        chkMarkdownIncludeHTTPOC = new JCheckBox("Include HTTP Requests");
        chkMarkdownIncludeHTTPOC.setSelected(false);
        TemplateParameterspanel.add(chkMarkdownIncludeHTTPOC,gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        TemplateParameterspanel.add(new JLabel("Project Name"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txt_MarkdownProjectName=new JTextField("",30);
        txt_MarkdownProjectName.setText("Burp Suite Scan");
        txt_MarkdownProjectName.setEditable(true);
        TemplateParameterspanel.add(txt_MarkdownProjectName,gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        TemplateParameterspanel.add(new JLabel("Project Date"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txt_MarkdownProjectDate=new JTextField("",30);
        txt_MarkdownProjectDate.setEditable(true);
        TemplateParameterspanel.add(txt_MarkdownProjectDate,gbc);

        Locale locale = Locale.getDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(formatter);
        txt_MarkdownProjectDate.setText(formattedDate);



        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 4;
        //gbc.gridx = 0;
        gbc.gridwidth=2;
        gbc.anchor=GridBagConstraints.WEST;
        MarkdownOptions.setVisible(false);


        lblMarkdownloadDefaultTemplate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblMarkdownloadDefaultTemplate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try (InputStream in = ExportOptionsPanel.class.getResourceAsStream("/templates/"+Constants.default_markdown_template)) {
                    if (in == null) {
                        JOptionPane.showMessageDialog(null, "Template not found in resources.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save Markdown Template");
                    fileChooser.setSelectedFile(new File(Constants.default_markdown_template));

                    int result = fileChooser.showSaveDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        Files.copy(in, selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(null, "Template downloaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error downloading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        //Add exec summary
        mdExecSumPanel=new ExecSummaryPanel(this.montoyaApi, debugHTTPrequests,this.exporterController,true,Config.useOnlyBurpAI);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor=GridBagConstraints.WEST;
        gbc.gridy = 5;
        gbc.gridwidth=2;
        MarkdownOptions.add(mdExecSumPanel,gbc);

    }


    private void CreateDocxPanel()
    {
        DocxOptions = new JPanel();
        DocxOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        DocxOptions.setLayout(new GridBagLayout());
        DocxOptions.setBorder(BorderFactory.createTitledBorder("Docx Options"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 1st row, 1st colum (30%)   |  gridX: horizontal axe
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        DocxOptions.add(new JLabel("Report Filename"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txt_DocxOutFile=new JTextField("",30);
        txt_DocxOutFile.setEditable(false);
        DocxOptions.add(txt_DocxOutFile,gbc);


        // 2nd row
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JButton browseDocxButton = new JButton("  Choose File  ");
        browseDocxButton.setBorder(new LineBorder(Color.LIGHT_GRAY,2));
        browseDocxButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(Constants.defaultDocxExportName));
            int result = fileChooser.showOpenDialog(this); // or frame
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                txt_DocxOutFile.setText(selectedFile.getAbsolutePath());
            }
        });
        DocxOptions.add(browseDocxButton,gbc);


        //Templates
        JPanel DocxTemplateOptions = new JPanel();
        DocxTemplateOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        DocxTemplateOptions.setLayout(new GridBagLayout());
        DocxTemplateOptions.setBorder(BorderFactory.createTitledBorder("Template Selection"));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 2;
        gbc.gridwidth=2;
        DocxOptions.add(DocxTemplateOptions,gbc);


        JPanel panelTemplates=new JPanel(new GridBagLayout());
        panelTemplates.setAlignmentX(Component.CENTER_ALIGNMENT);
        ButtonGroup groupTemplates = new ButtonGroup();

        radioDocxDefaultTemplate = new JRadioButton("Default Template");
        radioDocxDefaultTemplate.setSelected(true);
        groupTemplates.add(radioDocxDefaultTemplate);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor=GridBagConstraints.WEST;
        panelTemplates.add(radioDocxDefaultTemplate,gbc);
        DocxTemplateOptions.add(panelTemplates, gbc);


        lblDocxloadDefaultTemplate = new JLabel("Download");
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor=GridBagConstraints.NORTHEAST;
        Font font = lblDocxloadDefaultTemplate.getFont();
        Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        lblDocxloadDefaultTemplate.setFont(font.deriveFont(attributes));
        lblDocxloadDefaultTemplate.setForeground(Color.BLUE);
        lblDocxloadDefaultTemplate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelTemplates.add(lblDocxloadDefaultTemplate,gbc);


        // 2nd row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor=GridBagConstraints.WEST;
        radioDocxCustomTemplate = new JRadioButton("Custom Template");
        groupTemplates.add(radioDocxCustomTemplate);
        panelTemplates.add(radioDocxCustomTemplate,gbc);

        //3rd row
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor=GridBagConstraints.EAST;
        txt_DocxTemplateFile=new JTextField("",30);
        txt_DocxTemplateFile.setEditable(false);
        panelTemplates.add(txt_DocxTemplateFile,gbc);

        // 2nd row
        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JButton browseDocxTemplateButton = new JButton("  Choose Template  ");
        browseDocxTemplateButton.setBorder(new LineBorder(Color.LIGHT_GRAY,2));
        browseDocxTemplateButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this); // or frame
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                txt_DocxTemplateFile.setText(selectedFile.getAbsolutePath());
            }
        });
        panelTemplates.add(browseDocxTemplateButton,gbc);


        // 1st row, First column (30%)
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridwidth=2;
        gbc.gridy = 0;



        JPanel TemplateParameterspanel = new JPanel();
        TemplateParameterspanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        TemplateParameterspanel.setLayout(new GridBagLayout());
        TemplateParameterspanel.setBorder(BorderFactory.createTitledBorder("Template Parameters"));
        gbc = new GridBagConstraints();
        gbc.gridy = 3;
        gbc.gridwidth=2;
        gbc.fill = GridBagConstraints.BOTH;
        DocxOptions.add(TemplateParameterspanel,gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor=GridBagConstraints.WEST;
        chkDocxIncludeHTTPOC = new JCheckBox("Include HTTP Requests");
        chkDocxIncludeHTTPOC.setSelected(false);
        TemplateParameterspanel.add(chkDocxIncludeHTTPOC,gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        TemplateParameterspanel.add(new JLabel("Project Name"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txt_DocxProjectName=new JTextField("",30);
        txt_DocxProjectName.setText("Burp Suite Scan");
        txt_DocxProjectName.setEditable(true);
        TemplateParameterspanel.add(txt_DocxProjectName,gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        TemplateParameterspanel.add(new JLabel("Project Date"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txt_DocxProjectDate=new JTextField("",30);
        txt_DocxProjectDate.setEditable(true);
        TemplateParameterspanel.add(txt_DocxProjectDate,gbc);

        Locale locale = Locale.getDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(formatter);
        txt_DocxProjectDate.setText(formattedDate);



        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 4;
        gbc.gridwidth=2;
        gbc.anchor=GridBagConstraints.WEST;
        DocxOptions.setVisible(false);


        lblDocxloadDefaultTemplate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblDocxloadDefaultTemplate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try (InputStream in = ExportOptionsPanel.class.getResourceAsStream("/templates/"+Constants.default_Docx_template)) {
                    if (in == null) {
                        JOptionPane.showMessageDialog(null, "Template not found in resources.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save Docx Template");
                    fileChooser.setSelectedFile(new File(Constants.default_Docx_template));

                    int result = fileChooser.showSaveDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        Files.copy(in, selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(null, "Template downloaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error downloading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });




        //Add exec summary
        docxExecSumPanel=new ExecSummaryPanel(this.montoyaApi, debugHTTPrequests,this.exporterController,false,Config.useOnlyBurpAI);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor=GridBagConstraints.WEST;
        gbc.gridy = 5;
        gbc.gridwidth=2;
        DocxOptions.add(docxExecSumPanel,gbc);

    }







    private void exportPwndocInBackground(ExporterController exporterController)
    {
        new SwingWorker<Boolean, Void>()
        {
            @Override
            protected Boolean doInBackground() throws Exception
            {
                try
                {
                    //relogin
                    boolean logged_in= false;
                    logged_in=p_api.Login(txt_pwndocuser.getText(),new String(txt_pwndocpassword.getPassword()));
                    if(logged_in)
                    {
                        auditIdToApppend = p_api.AuditsGetAuditIdByName(txt_auditname.getText().trim());
                        if (auditIdToApppend == null || auditIdToApppend.trim().isEmpty())
                        {
                            pwndoc_createNewAudit = true;
                        }
                        else
                        {
                            final int[] result = new int[1];
                            SwingUtilities.invokeAndWait(() -> {
                                Object[] options = {"Create Duplicate", "Update Existing"};
                                result[0] = JOptionPane.showOptionDialog(
                                        null,
                                        Constants.msgPwnDocAuditAlraeadyExists,
                                        Constants.msgPwnDocAuditConfirmationNeeded,
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        options,
                                        options[0]
                                );
                            });

                            if (result[0] == 0) {
                                pwndoc_createNewAudit = true;

                            } else if (result[0] == 1) {
                                pwndoc_createNewAudit = false;

                            } else {
                                pwndoc_createNewAudit = true;
                                //throw  new Exception("User aborted");
                                return false;
                            }
                        }
                        return true;
                    }
                    else
                    {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, "Unable to Login to Pwndoc","Login Error", JOptionPane.WARNING_MESSAGE);
                        });
                        return false;
                    }
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
                    if (result) {
                        exporterController.handleExportClicked();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();
    }



    public void setController(ExporterController exporterController)
    {
        this.exporterController=exporterController;
        this.docxExecSumPanel.exportController=exporterController;
        this.mdExecSumPanel.exportController=exporterController;

        btnExport = new JButton("Export");
        btnExport.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> dbgPanel.clearArea());
            Events.publish(new Events.UpdateDebugEvent(Constants.export_Validatingdata+"\n"));
            boolean allok = validateBeforeExport();
            if (allok) {
                Events.publish(new Events.UpdateDebugEvent(Constants.export_ExportStarted+"\n"));
                //For pwndoc, raise a pop up in case the auditname already exists. if user confirms, it will append the audits to it
                if (dropdown_exportoptions.getSelectedItem() == exportOptionsEnum.Pwndoc)
                {
                    try {
                        exportPwndocInBackground(this.exporterController);
                    } catch (Exception ex) {
                        return;
                    }
                }
                else
                {
                    this.exporterController.handleExportClicked(e);
                }
            }
        });

        btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> {
            this.exporterController.handleClearClicked(e);
        });

        southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(btnClear);
        southPanel.add(btnExport);

        this.add(southPanel,BorderLayout.SOUTH);

    }


    public boolean validateBeforeExport()
    {
        String errorMsg="";

        if(dropdown_exportoptions.getSelectedItem()== exportOptionsEnum.Pwndoc)
        {
            String pwndocURLtemp="";
            errorMsg=validatePwndocLogin();
            if(txt_auditname.getText().strip()=="")
            {
                errorMsg=Constants.msgMissingPwnDocAudit;
            }
            if(!cmb_PwndocAuditType.isEnabled())
            {
                errorMsg=Constants.msgPwnDocLoginFirst;
            }

        }
        else if(dropdown_exportoptions.getSelectedItem()== exportOptionsEnum.CSV)
        {
            if(txt_CSVname.getText().strip()=="")
            {
                errorMsg=Constants.msgMissingCsvName;
            }
        }
        else if(dropdown_exportoptions.getSelectedItem()== exportOptionsEnum.JSON)
        {
            if(txt_JSONname.getText().strip()=="")
            {
                errorMsg=Constants.msgMissingJSONName;
            }
        }
        else if(dropdown_exportoptions.getSelectedItem()== exportOptionsEnum.XML)
        {
            if(txt_XMLname.getText().strip()=="")
            {
                errorMsg=Constants.msgMissingXMLName;
            }
        }
        else if(dropdown_exportoptions.getSelectedItem()== exportOptionsEnum.Markdown)
        {
            String execSummaryErrormsg="";
            if(txt_MarkdownOutFile.getText().strip()=="")
            {
                errorMsg=Constants.msgMissingMarkdownOutFile;
            }

            if(radioMarkdownCustomTemplate.isSelected()) {
                if (txt_MarkdownTemplateFile.getText().strip() == "") {
                    errorMsg = Constants.msgMissingMarkdownTemplateFile;
                }
            }
            execSummaryErrormsg=mdExecSumPanel.validate_AIOptions();
            if(!execSummaryErrormsg.isEmpty())
            {
                errorMsg=execSummaryErrormsg;
            }
        }
        else if(dropdown_exportoptions.getSelectedItem()== exportOptionsEnum.Docx)
        {
            String execSummaryErrormsg="";
            if(txt_DocxOutFile.getText().strip()=="")
            {
                errorMsg=Constants.msgMissingDocxOutFile;
            }

            if(radioDocxCustomTemplate.isSelected()) {
                if (txt_DocxTemplateFile.getText().strip() == "") {
                    errorMsg = Constants.msgMissingDocxTemplateFile;
                }
            }

            execSummaryErrormsg=docxExecSumPanel.validate_AIOptions();
            if(!execSummaryErrormsg.isEmpty())
            {
                errorMsg=execSummaryErrormsg;
            }
        }
        else
        {

        }


        if(errorMsg!="")
        {
            final String finalErrorMsg=errorMsg;
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, finalErrorMsg, "Validation Error", JOptionPane.WARNING_MESSAGE);
            });
            return false;
        }
        else
        {
            return true;
        }
    }


    public String validatePwndocLogin()
    {
        String errorMsg="";
        if(txt_pwndocURL.getText().strip()=="")
        {
            errorMsg=Constants.msgMissingPwnDocURL;
        }
        //validate if Pwndoc URL Is valid URL
        try {
            boolean isvalidURL=Utilities.isValidURL(txt_pwndocURL.getText().trim());
            if (!isvalidURL)
                errorMsg=Constants.msgNotValidUrl;
        } catch (MalformedURLException ex) {
            errorMsg=Constants.msgNotValidUrl; // String is not a well-formed URL
        }
        if(txt_pwndocuser.getText().strip()=="")
        {
            errorMsg=Constants.msgMissingPwnDocUser;
        }
        if( (new String(txt_pwndocpassword.getPassword())).strip() =="")
        {
            errorMsg=Constants.msgMissingPwnDocPass;
        }
        return errorMsg;
    }


    public exportOptionsEnum getSelectedExportOption()
    {
        return (Config.exportOptionsEnum) this.dropdown_exportoptions.getSelectedItem();
    }



    public PwndocOptions getPwndocOptions()
    {
        String txtPwndocURL=this.txt_pwndocURL.getText();
        String txtPwndocUSer=this.txt_pwndocuser.getText();
        String txtPwndocPAss=this.txt_pwndocpassword.getPassword().toString();
        String txtAuditName=this.txt_auditname.getText();

        AuditType.ComboAuditTypeItem selectedAuditType= (AuditType.ComboAuditTypeItem) cmb_PwndocAuditType.getSelectedItem();
        Language.ComboLanguageItem selectedLanguage= (Language.ComboLanguageItem) cmb_PwndocLanguage.getSelectedItem();
        VulnCategory.ComboVulnCateogryItem selectedCateg= (VulnCategory.ComboVulnCateogryItem) cmb_PwndocFindingCategory.getSelectedItem();
        VulnType.ComboVulnTypeItem selectedVulnType= (VulnType.ComboVulnTypeItem) cmb_PwndocFindingType.getSelectedItem();


        return new PwndocOptions(txtPwndocURL,txtPwndocUSer,txtPwndocPAss,txtAuditName,selectedCateg.getName(),selectedVulnType.getName(),selectedAuditType.getName(),selectedLanguage.getLocale(), auditIdToApppend,pwndoc_createNewAudit,p_api);
    }


    public CSVOptions getCSVOptions()
    {
        return new CSVOptions(this.txt_CSVname.getText(),this.chkCSVIncludeHTTPOC.isSelected());
    }

    public JSONOptions getJSONOptions()
    {
        return new JSONOptions(this.txt_JSONname.getText(),this.chkJSONIncludeHTTPOC.isSelected());
    }

    public XMLOptions getXMLOptions()
    {
        return new XMLOptions(this.txt_XMLname.getText(),this.chkXMLIncludeHTTPOC.isSelected());
    }


    public MarkdownOptions getMarkdownOptions()
    {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            Template template;

            if(radioMarkdownDefaultTemplate.isSelected()) {
                cfg.setClassLoaderForTemplateLoading(ExportOptionsPanel.class.getClassLoader(), "templates");
                template = cfg.getTemplate(Constants.default_markdown_template);
            }
            else
            {
                File templateFile = new File(txt_MarkdownTemplateFile.getText());
                try (Reader reader = new FileReader(templateFile)) {
                    template = new Template("userTemplate", reader, cfg);
                }
            }

            return new MarkdownOptions(this.txt_MarkdownOutFile.getText(),template,txt_MarkdownProjectName.getText().trim(),txt_MarkdownProjectDate.getText().trim(),this.mdExecSumPanel.getExecutiveSummaryTextMD(),chkMarkdownIncludeHTTPOC.isSelected());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public DocxOptions getDocxOptions()
    {
        try {
            InputStream templateStream;
            if(radioDocxDefaultTemplate.isSelected())
            {
                templateStream = getClass().getClassLoader().getResourceAsStream("templates/"+Constants.default_Docx_template);
            }
            else
            {
                templateStream = new FileInputStream(new File(txt_DocxTemplateFile.getText()));
            }
            return new DocxOptions(this.txt_DocxOutFile.getText(),templateStream,txt_DocxProjectName.getText().trim(),txt_DocxProjectDate.getText().trim(),this.docxExecSumPanel.getExecutiveSummaryText(),chkDocxIncludeHTTPOC.isSelected());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

}