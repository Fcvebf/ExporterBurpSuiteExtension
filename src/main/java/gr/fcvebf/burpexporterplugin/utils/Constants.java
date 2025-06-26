package gr.fcvebf.burpexporterplugin.utils;

public class Constants {

    public static final String extentionName="Exporter++";

    public static final String msgNotValidUrl="Please provide a valid URL";
    public static final int msgMaxTextField=256;


    public static final String msgMissingPwnDocURL="Provide also the Pwndoc URL";
    public static final String msgMissingPwnDocUser="Provide also the Pwndoc username";
    public static final String msgMissingPwnDocPass="Provide also the Pwndoc password";
    public static final String msgMissingPwnDocAudit="Provide also the Pwndoc Audit to export to";
    public static final String msgPwnDocLoginFirst="Please Login to Pwndoc";

    public static final String msgPwnDocAuditAlraeadyExists="The Audit already exists";
    public static final String msgPwnDocAuditConfirmationNeeded="Select an action";
    public static final String msgPwnDocAuditExportedSuccessfully="-> The findings were successfully uploaded to Pwndoc!";
    public static final String msgPwnDocAuditExportFailed="An error has occured while uploading the findings  to Pwndoc!";

    public static final String defaultCSVExportName="Burp Suite Exporter++ Results.csv";
    public static final String msgMissingCsvName="Select the CSV file name to export to";
    public static final String msgCSVExportedSuccessfully="-> CSV exported successfully!";

    public static final String defaultJSONExportName="Burp Suite Exporter++ Results.json";
    public static final String msgMissingJSONName="Select the JSON file name to export to";
    public static final String msgJSONExportedSuccessfully="-> JSON file exported successfully!";

    public static final String defaultXMLExportName="Burp Suite Exporter++ Results.xml";
    public static final String msgMissingXMLName="Select the XML file name to export to";
    public static final String msgXMLExportedSuccessfully="-> XML file exported successfully!";

    public static final String defaultMDExportName="Burp Suite Exporter++ Results.md";
    public static final String msgMissingMarkdownOutFile="Select the Markdown file name to export to";
    public static final String msgMissingMarkdownTemplateFile="Provide also the custom Markdown template file";
    public static final String msgMarkdownExportedSuccessfully="-> Markdown Report exported successfully!";
    public static final String default_markdown_template="default_md_template.md.ftl";

    public static final String defaultDocxExportName="Burp Suite Exporter++ Results.docx";
    public static final String msgMissingDocxOutFile="Select the Docx file name to export to";
    public static final String msgMissingDocxTemplateFile="Provide also the custom Docx template file";
    public static final String msgDocxExportedSuccessfully="-> Docx Report exported successfully!";
    public static final String default_Docx_template="default_docx_template.docx";

    public static final String initial_LLMPrompt="No issues have been found! The application has a very good security posture.";
    public static final String initial_LLMPromptMD="No issues have been found!";
    public static final String AIOptions_ollamaEndpointEmpty="Please provide a valid Ollama API URL!";
    public static final String AIOptions_ollamaModelNotSelected="Please click 'Connect' and select a model!";
    public static final String BurpAI_notEnabledInBurp="AI is not enabled. Enable it in Burp.";
    public static final String BurpI_defaultsystemMessage="You are a web security assistant specialized in summarizing security findings and proposing remediation actions.";

    public static final String export_Validatingdata="-> Validating user data...";
    public static final String export_ExportStarted="-> Export started...";
    public static final String export_AIGeneratingSummary="-> Generating the executive summary...";
    public static final String export_AI_Error="-> A Burp AI Exception has occurred. Check the Extensions tab for more info...";


}
