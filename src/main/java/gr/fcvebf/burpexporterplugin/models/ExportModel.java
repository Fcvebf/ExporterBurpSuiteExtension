package gr.fcvebf.burpexporterplugin.models;

import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import burp.api.montoya.MontoyaApi;
import gr.fcvebf.burpexporterplugin.models.pwndoc.Audit;
import gr.fcvebf.burpexporterplugin.models.pwndoc.PwndocApi;
import gr.fcvebf.burpexporterplugin.utils.Constants;
import gr.fcvebf.burpexporterplugin.utils.Utilities;
import gr.fcvebf.burpexporterplugin.utils.Config;
import gr.fcvebf.burpexporterplugin.utils.Events;
import gr.fcvebf.burpexporterplugin.utils.Events.ExportIssuesEvent;
import gr.fcvebf.burpexporterplugin.utils.Config.exportOptionsEnum;
import static gr.fcvebf.burpexporterplugin.models.Finding.capitalize;

//for json export
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

//for docx export
import fr.opensagres.xdocreport.template.ITemplateEngine;
import fr.opensagres.xdocreport.template.velocity.internal.VelocityTemplateEngine;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;

//for Markdown export
import freemarker.template.*;

//for CSV
import com.opencsv.CSVWriter;

//for XML
import javax.swing.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class ExportModel {

    public MontoyaApi montoyaApi;


    public ExportModel(MontoyaApi montoyaApi) {
        this.montoyaApi=montoyaApi;
        Config.exportOptionsEnum selectedOption=Config.exportOptionsEnum.CSV;
        Events.subscribe(ExportIssuesEvent.class, e -> onExportIssuesAsync(e));

    }


    public void onExportIssuesAsync(ExportIssuesEvent e){
        try
        {
            CompletableFuture.runAsync(() -> {
                if (e.selectedOption() == exportOptionsEnum.Pwndoc) {
                    Export_ToPwndoc(e.auditFindings(),e.pwndocOptions().pwndocAuditName,e.pwndocOptions().pwndocAuditCategory,e.pwndocOptions().pwndocAuditVulnType,e.pwndocOptions().pwndocAuditType,e.pwndocOptions().pwndocLanguage,e.pwndocOptions().pwndocAuditId, e.pwndocOptions().createNew,e.pwndocOptions().p_api,this.montoyaApi);
                }
                else if (e.selectedOption() == exportOptionsEnum.CSV) {
                    Export_ToCSV(e.auditFindings(), e.csvOptions().exportFilename,e.csvOptions().includeHTTPPOC,this.montoyaApi);
                } else if (e.selectedOption() == exportOptionsEnum.JSON) {
                    Export_ToJSON(e.auditFindings(), e.jsonOptions().exportFilename,e.jsonOptions().includeHTTPPOC,this.montoyaApi);
                }
                else if (e.selectedOption() == exportOptionsEnum.XML) {
                    Export_ToXML(e.auditFindings(), e.XMLOptions().exportFilename,e.XMLOptions().includeHTTPPOC,this.montoyaApi);
                }
                else if (e.selectedOption() == exportOptionsEnum.Markdown) {
                    Export_ToMarkdown(sanitizeForXML(e.auditFindings(),this.montoyaApi), e.markdownOptions().exportFilename,e.markdownOptions().template,e.markdownOptions().projectName,e.markdownOptions().projectDate,sanitizeForXML(e.markdownOptions().execSummary,this.montoyaApi), e.markdownOptions().includeHTTPPOC,this.montoyaApi);
                }
                else if (e.selectedOption() == exportOptionsEnum.Docx) {
                    e.docxOptions().execSummary = e.docxOptions().execSummary.replace("\n", "</w:t></w:r><w:r><w:br/><w:t>");
                    Export_ToDocx(sanitizeForXML(e.auditFindings(),this.montoyaApi), e.docxOptions().exportFilename,e.docxOptions().template,e.docxOptions().projectName,e.docxOptions().projectDate,e.docxOptions().execSummary, e.docxOptions().includeHTTPPOC,this.montoyaApi);
                }
                else {

                }
            });
        }
        catch (Exception ex)
        {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(ex));
            throw new RuntimeException(ex);
        }
    }



    public void Export_ToPwndoc(List<Finding> lstAuditIFindings,String pwndocNewAuditName,String category,String vulnType,String pwndocNewAuditType, String pwndocNewAuditLanguage,String pwndocExistingAuditId,boolean createNew,PwndocApi p_api,MontoyaApi montoyaApi)
    {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try
                {
                    String target_auditId = "";
                    if (createNew) {
                        String type = "default";
                        target_auditId = p_api.AuditsCreateNewAudit(pwndocNewAuditName, pwndocNewAuditType, pwndocNewAuditLanguage, type);
                    } else {
                        target_auditId = pwndocExistingAuditId;
                    }

                    for (Finding f : lstAuditIFindings)
                    {
                        Events.publish(new Events.UpdateDebugEvent("     Processing: " + f.issueName));

                        String issueDescrBackgr_clean = f.issueDescrBackground.replace("\n", "");
                        //String issueDescrBackgr_clean = sanitizeForHTML(Utilities.extractText(f.issueDescrBackground),montoyaApi).replace("\n", "");

                        Pattern NON_PRINTABLE_UTF_PATTERN =Pattern.compile("[\\s\\p{C}]");
                        Matcher matcher = NON_PRINTABLE_UTF_PATTERN.matcher(f.formattedHTTPPOC2);
                        String http_poc_filtered= matcher.replaceAll("").replace("</w:t></w:r><w:r><w:br/><w:t>","");

                        //String httppoc="<p><pre><code>"+http_poc_filtered.replace("\r\n","<br/>").replace("\n","<br/>").replace("\r","")+"</pre></code></p>";


                        String httppoc = "";
                        if (f.formattedHTTPPOC2 != null) {
                            //httppoc = sanitizeForXML(f.formattedHTTPPOC2, montoyaApi).replace("</w:t></w:r><w:r><w:br/><w:t>","");
                            //httppoc = getAsciiOnly(f.formattedHTTPPOC2); ->WORKED
                            httppoc=cleanStringValue(f.formattedHTTPPOC2);

                        }


                        //String remediation_cleaned =sanitizeForHTML(f.issueRemediation,montoyaApi) ;
                        String remediation_cleaned = "";
                        if (f.issueRemediation != null)
                            remediation_cleaned = f.issueRemediation.replace("\n", "").replace("<li>", "<li><p>").replace("</li>", "</p></li>").replace("</w:t></w:r><w:r><w:br/><w:t>","");

                        String result = p_api.AuditsCreateFinding(target_auditId, f.issueName, category, vulnType, issueDescrBackgr_clean, f.getIssueDetailsSummaryPwndoc(), remediation_cleaned, httppoc, "<p>" + f.getIssueDetailsScope() + "</p>", 2, 2, Audit.cvssConverter(f.severity));
                    }

                    return true;
                }
                catch (Exception e)
                {
                    montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success)
                    {
                        //JOptionPane.showMessageDialog(null, "Export successful!");
                        Events.publish(new Events.PwndocExportedEvent(Constants.msgPwnDocAuditExportedSuccessfully));
                        Events.publish(new Events.UpdateDebugEvent("\n" + Constants.msgPwnDocAuditExportedSuccessfully));
                    }
                    else
                    {
                        //JOptionPane.showMessageDialog(null, "Export failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        Events.publish(new Events.PwndocExportedEvent(Constants.msgPwnDocAuditExportFailed));
                        Events.publish(new Events.UpdateDebugEvent("\n" + Constants.msgPwnDocAuditExportFailed));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public void Export_ToCSV(List<Finding> lstAuditIFindings,String csvFileName, boolean includeHTTPPOC,MontoyaApi montoyaApi )
    {

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFileName)))
        {
            String[] header;
            if (includeHTTPPOC)
            {
                header = new String[] {"HTTPHost", "IssueName", "Severity","Confidence","IssueDetail","IssueBackground","Remediation","HTTPPOC"};
            }
            else
            {
                header = new String[] {"HTTPHost", "IssueName", "Severity","Confidence","IssueDetail","IssueBackground","Remediation"};
            }


            writer.writeNext(header);
            for (Finding i : lstAuditIFindings)
            {
                String[] row;
                if (includeHTTPPOC) {
                    row = new String[] {i.baseURL, i.issueName, capitalize(i.severity), capitalize(i.confidence), summarizeFindingsDetails(Utilities.extractText(i.issueDetails), i.URL, false), Utilities.extractText(i.issueDescrBackground), Utilities.extractText(i.issueRemediation), convertListToString_csv(i.HTTPPOC)};
                }
                else
                {
                    row = new String[] {i.baseURL, i.issueName, capitalize(i.severity), capitalize(i.confidence), summarizeFindingsDetails(Utilities.extractText(i.issueDetails), i.URL, false), Utilities.extractText(i.issueDescrBackground), Utilities.extractText(i.issueRemediation)};
                }
                writer.writeNext(row);
                String message = "    Processing: " + i.issueName;
                Events.publish(new Events.UpdateDebugEvent(message));
            }
            Events.publish(new Events.UpdateDebugEvent("\n"+Constants.msgCSVExportedSuccessfully));
            Events.publish(new Events.CSVExportedEvent(Constants.msgCSVExportedSuccessfully));
        }
        catch (IOException e)
        {
            montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
        catch (Exception ex)
        {
            montoyaApi.logging().logToError(Utilities.getStackTraceAsString(ex));
            throw new RuntimeException(ex);
        }

    }


    public void Export_ToJSON(List<Finding> lstAuditIFindings,String jsonFileName, boolean includeHTTPPOC,MontoyaApi montoyaApi)
    {
        JsonFactory jsonFactory = new JsonFactory();
        File outputFile = new File(jsonFileName);

        // Create a FileOutputStream from the File
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             JsonGenerator jsonGenerator = jsonFactory.createGenerator(fileOutputStream)) {
            jsonGenerator.writeStartArray(); // Start the JSON array

            for (Finding obj : lstAuditIFindings) {
                jsonGenerator.writeStartObject(); // Start a JSON object
                jsonGenerator.writeStringField("baseURL", obj.baseURL);
                jsonGenerator.writeStringField("issueName", obj.issueName);
                jsonGenerator.writeStringField("severity", capitalize(obj.severity));
                jsonGenerator.writeStringField("confidence", capitalize(obj.confidence));
                jsonGenerator.writeStringField("issueDetail",  summarizeFindingsDetails(Utilities.extractText(obj.issueDetails),obj.URL,false));
                jsonGenerator.writeStringField("issueBackground", Utilities.extractText(obj.issueDescrBackground));
                jsonGenerator.writeStringField("issueRemediation", Utilities.extractText(obj.issueRemediation));

                if(includeHTTPPOC) {
                    jsonGenerator.writeArrayFieldStart("HTTPPOC");
                    for (String[] item : obj.HTTPPOC) {
                        jsonGenerator.writeStartObject("POC");
                        jsonGenerator.writeStringField("HTTPRequest", item[0]);
                        jsonGenerator.writeStringField("HTTPResponse", item[1]);
                        jsonGenerator.writeEndObject();
                    }
                    jsonGenerator.writeEndArray();
                }
                jsonGenerator.writeEndObject(); // End the JSON object
                Events.publish(new Events.UpdateDebugEvent("    Processing: "+obj.issueName));
            }

            jsonGenerator.writeEndArray(); // End the JSON array
            Events.publish(new Events.UpdateDebugEvent("\n"+Constants.msgJSONExportedSuccessfully));
        }
        catch (IOException e)
        {
            montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
        Events.publish(new Events.JSONExportedEvent(Constants.msgJSONExportedSuccessfully));
    }


    public void Export_ToXML(List<Finding> lstAuditIFindings, String xmlFileName, boolean includeHTTPPOC,MontoyaApi montoyaApi) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("findingsReport");
            doc.appendChild(rootElement);

            // Add a generated date field
            Element generatedOn = doc.createElement("generatedOn");
            CDATASection generatedDate = doc.createCDATASection(LocalDateTime.now().toString());
            generatedOn.appendChild(generatedDate);
            rootElement.appendChild(generatedOn);

            Element findingsElement = doc.createElement("findings");
            rootElement.appendChild(findingsElement);

            for (Finding obj : lstAuditIFindings) {
                Element findingElement = doc.createElement("finding");
                findingsElement.appendChild(findingElement);

                appendCdataElement(doc, findingElement, "baseURL", obj.baseURL);
                appendCdataElement(doc, findingElement, "issueName", obj.issueName);
                appendCdataElement(doc, findingElement, "severity", capitalize(obj.severity));
                appendCdataElement(doc, findingElement, "confidence", capitalize(obj.confidence));
                appendCdataElement(doc, findingElement, "issueDetail", summarizeFindingsDetails(Utilities.extractText(obj.issueDetails), obj.URL, false));
                appendCdataElement(doc, findingElement, "issueBackground", Utilities.extractText(obj.issueDescrBackground));
                appendCdataElement(doc, findingElement, "issueRemediation", Utilities.extractText(obj.issueRemediation));

                if( includeHTTPPOC)
                {
                    Element httpPocList = doc.createElement("HTTPPOC");
                    findingElement.appendChild(httpPocList);

                    for (String[] item : obj.HTTPPOC) {
                        Element poc = doc.createElement("POC");
                        appendCdataElement(doc, poc, "HTTPRequest", item[0]);
                        appendCdataElement(doc, poc, "HTTPResponse", Utilities.escapeBinaryChars(item[1].getBytes(StandardCharsets.UTF_8)));
                        httpPocList.appendChild(poc);
                    }
                }
                Events.publish(new Events.UpdateDebugEvent("    Processing: " + obj.issueName));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFileName));
            transformer.transform(source, result);

            Events.publish(new Events.UpdateDebugEvent("\n"+Constants.msgXMLExportedSuccessfully));
            Events.publish(new Events.XMLExportedEvent(Constants.msgXMLExportedSuccessfully));

        } catch (Exception e) {
            montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
    }



    public void Export_ToMarkdown(List<Finding> lstAuditIFindings,String mdFileName,Template md_template,String projectname,String projectDate,String execSummary, boolean includeHTTPPOC,MontoyaApi montoyaApi)
    {
        try (Writer out = new FileWriter(mdFileName)) {

            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
            cfg.setClassLoaderForTemplateLoading(ExportModel.class.getClassLoader(), "templates");
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("findings", lstAuditIFindings);
            dataModel.put("projectname", projectname);
            dataModel.put("projectDate", projectDate);
            dataModel.put("execSummary",execSummary);
            dataModel.put("includeHttpRequests", includeHTTPPOC);

            md_template.process(dataModel, out);
            Events.publish(new Events.UpdateDebugEvent(Constants.msgMarkdownExportedSuccessfully));
            Events.publish(new Events.JSONExportedEvent(Constants.msgMarkdownExportedSuccessfully));
        }catch (IOException e) {
            montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
        catch (TemplateException e) {
            montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }

    }


    public void Export_ToDocx(List<Finding> lstAuditIFindings,String outputPath,InputStream templateInputStream,String projectname,String projectDate,String execSummary,boolean includeHTTPPOC,MontoyaApi montoyaApi )
    {
        try
        {
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport( templateInputStream);
            Properties properties = new Properties();
            properties.setProperty("htmlFieldSupport", "true");

            ITemplateEngine templateEngine = new VelocityTemplateEngine(properties);
            report.setTemplateEngine(templateEngine);

            IContext context = report.createContext();
            context.put("findings",lstAuditIFindings);
            context.put("projectName",projectname);
            context.put("projectDate",projectDate);
            context.put("execSummary",execSummary);
            context.put("includeHTTPPOC",includeHTTPPOC);

            // Generate output docx
            try (OutputStream out = new FileOutputStream(outputPath)) {
                report.process(context, out);
            }

            Events.publish(new Events.UpdateDebugEvent(Constants.msgDocxExportedSuccessfully));
            Events.publish(new Events.JSONExportedEvent(Constants.msgDocxExportedSuccessfully));
        } catch (Exception e) {
            //e.printStackTrace();
            montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
    }



    public static String getAsciiOnly(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder asciiOnly = new StringBuilder(input.length()); // Pre-allocate capacity
        for (char c : input.toCharArray()) {
            if (c >= 0 && c <= 127) { // Check if the character's Unicode value is within ASCII range
                asciiOnly.append(c);
            }
        }
        return asciiOnly.toString();
    }


    public static String cleanStringValue(String input) {
        if (input == null) {
            return null; // Keep nulls as nulls for JSON
        }
        Pattern REMOVE_NON_PRINTABLE_BUT_KEEP_WHITESPACE_PATTERN = Pattern.compile("\\p{C}");
        Matcher matcher = REMOVE_NON_PRINTABLE_BUT_KEEP_WHITESPACE_PATTERN.matcher(input);
        return matcher.replaceAll("");
    }

    public String convertListToString_csv(List<String[]> items) {
        StringBuilder sb = new StringBuilder();
        for (String[] item : items) {
            sb.append("HTTP Request").append(System.lineSeparator());
            sb.append(item[0]).append(System.lineSeparator()).append("HTTP Response").append(System.lineSeparator()).append(item[0]).append(System.lineSeparator()).append(System.lineSeparator());
        }
        return sb.toString();
    }


    public static String summarizeFindingsDetails(List<String> issueDetails,List<String> URLs,boolean asHTML) {

        //If the FInding has one URL then the IssueDetail is automatically filled by Montoya API.
        // Otherwise the Issue detail is null and the GUI renders as Issue details the distinct URLs that it was found
        StringBuilder sb=new StringBuilder();
        String newline = asHTML ? "<br/>" : System.lineSeparator();

        if(issueDetails!=null) {
            if (issueDetails.size() == 1)
            {
                if (URLs.size() > 0)
                    sb.append("The issue was identified in the following case: ").append(newline).append(URLs.get(0));
                if(issueDetails.get(0) !=null)
                    sb.append(issueDetails.get(0));
            }
            else
            {
                sb = new StringBuilder(issueDetails.size()+" instances of this issue were identified, at the following locations:"+newline);
                for (String url : URLs) {
                    sb.append( url + newline);
                }

                //Append all possible details of each one. (unique)
                StringBuilder sb_spec=new StringBuilder();
                List<String> uniqueIssueDetails = issueDetails.stream().distinct().toList();
                for (String detail : uniqueIssueDetails) {
                    if(!detail.isEmpty())
                        sb_spec.append(newline).append(detail).append(newline);
                }
                if(!sb_spec.toString().isEmpty()) {
                    sb.append(newline+"More specifically: ");
                    sb.append(sb_spec.toString());
                }

            }
        }
        else
        {
            if(URLs.size()>0)
                sb.append("The issue was identified in the following case: ").append(newline).append(URLs.get(0));
        }
        return sb.toString();
    }


    public static String summarizeFindingsDetailsScope(List<String> issueDetails,List<String> URLs,boolean asHTML) {


        StringBuilder sb=new StringBuilder();
        String newline = asHTML ? "<br/>" : System.lineSeparator();

        if(issueDetails!=null) {
            if (issueDetails.size() == 1)
            {
                if (URLs.size() > 0)
                    sb.append(URLs.get(0));
            }
            else
            {
                sb = new StringBuilder();
                for (String url : URLs) {
                    sb.append( url + newline);
                }

            }
        }
        else
        {
            if(URLs.size()>0)
                sb.append("The issue was identified in the following case: ").append(newline).append(URLs.get(0));
        }
        return sb.toString();
    }





    private void appendCdataElement(Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        CDATASection cdata = doc.createCDATASection(textContent != null ? textContent : "");
        element.appendChild(cdata);
        parent.appendChild(element);
    }


    public static String sanitizeForXML(String input,MontoyaApi montoyaApi) {
        if (input == null) return null;

        StringBuilder out = new StringBuilder();
        int length = input.length();
        for (int i = 0; i < length; )
        {
            int codePoint = input.codePointAt(i);
            if ((codePoint == 0x9 || codePoint == 0xA || codePoint == 0xD) ||
                    (codePoint >= 0x20 && codePoint <= 0xD7FF) ||
                    (codePoint >= 0xE000 && codePoint <= 0xFFFD) ||
                    (codePoint >= 0x10000 && codePoint <= 0x10FFFF)) {
                out.appendCodePoint(codePoint);
            }
            else
            {
                montoyaApi.logging().logToOutput("Character "+input.codePointAt(i) + " from: "+input+" was removed");
            }

            i += Character.charCount(codePoint);
        }

        String sanitized= out.toString();

        sanitized=sanitized.replace("&","&amp;");

        return sanitized;
    }


    public static List<Finding> sanitizeForXML(List<Finding> lst,MontoyaApi montoyaApi)
    {
        try {
            List<Finding> newlst = new ArrayList<>();

            for (Finding f : lst) {
                Finding f_sanitized = org.apache.commons.lang3.SerializationUtils.clone(f);
                f_sanitized.formattedHTTPPOC = sanitizeForXML(f.formattedHTTPPOC,montoyaApi);
                f_sanitized.formattedHTTPPOC2 = sanitizeForXML(f.formattedHTTPPOC2,montoyaApi);
                f_sanitized.issueRemediation=sanitizeForXML(f.issueRemediation,montoyaApi);
                newlst.add(f_sanitized);
            }
            return newlst;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static String sanitizeForHTML(String input, MontoyaApi montoyaApi) {
        if (input == null) {
            return null;
        }

        //first santize with input StringEscapeUtils.escapeXml11
        StringBuilder out = new StringBuilder(StringEscapeUtils.escapeXml11(input));
        int length = input.length();
        for (int i = 0; i < length; ) {
            int codePoint = input.codePointAt(i);

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
                    case '\"': // Double quote for attribute values
                        out.append("&quot;");
                        break;
                    case '\'': // Single quote/apostrophe for attribute values (using numeric entity for broader support)
                        out.append("&#x27;"); // &#39; is also valid
                        break;
                    default:
                        out.appendCodePoint(codePoint);
                        break;
                }
            } else {
                montoyaApi.logging().logToOutput(
                        "Character U+" + String.format("%04X", codePoint) +
                                " (decimal: " + codePoint + ") from: \"" + input + "\" was removed."
                );
            }

            i += Character.charCount(codePoint);
        }

        return out.toString().replace("\n","");
    }


    public static List<Finding> sanitizeForHTML(List<Finding> lst,MontoyaApi montoyaApi)
    {
        try {
            List<Finding> newlst = new ArrayList<>();

            for (Finding f : lst) {
                Finding f_sanitized = org.apache.commons.lang3.SerializationUtils.clone(f);
                f_sanitized.formattedHTTPPOC = sanitizeForHTML(f.formattedHTTPPOC,montoyaApi);
                f_sanitized.formattedHTTPPOC2 = sanitizeForHTML(f.formattedHTTPPOC2,montoyaApi);
                f_sanitized.issueRemediation=sanitizeForHTML(f.issueRemediation,montoyaApi);
                newlst.add(f_sanitized);
            }
            return newlst;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
