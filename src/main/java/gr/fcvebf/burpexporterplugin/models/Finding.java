package gr.fcvebf.burpexporterplugin.models;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import gr.fcvebf.burpexporterplugin.utils.Utilities;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;
import static org.apache.commons.collections.CollectionUtils.size;

public class Finding  implements Serializable {

    public String baseURL;
    public String issueName;
    public String severity;
    public String confidence;
    public List<String> URL;
    public List<String> issueDetails;
    public String issueDetailsSummary;
    public String issueDetailsSummaryHTML;
    private List<String> cleanedIssueDetails;
    public String issueDescrBackground;
    public String issueRemediation;
    public List<String[]> HTTPPOC;
    public String formattedHTTPPOC;
    public String formattedHTTPPOC2;


    public Finding()
    {}



    public Finding(AuditIssue issue)
    {
        try
        {
            this.baseURL = issue.httpService().toString();
            this.issueName = issue.name();
            this.severity = issue.severity().name();
            this.confidence = issue.confidence().name();

            this.URL = new ArrayList<>();
            this.URL.add(issue.baseUrl());
            this.issueDetails = new ArrayList<>();
            this.issueDetails.add(issue.detail());

            this.issueDescrBackground = issue.definition().background();
            this.issueRemediation = issue.definition().remediation();
            this.HTTPPOC = new ArrayList<>();

            if (issue.requestResponses() != null && size(issue.requestResponses()) > 0) {
                String req= (issue.requestResponses().get(0).request() != null) ? issue.requestResponses().get(0).request().toString() : " ";
                String res= (issue.requestResponses().get(0).response() != null) ? issue.requestResponses().get(0).response().toString() : " ";
                String[] newHTTPReqRes = {req, res};
                this.HTTPPOC.add(newHTTPReqRes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static final Map<String, Integer> SEVERITY_ORDER = new HashMap<>();
    static {
        //SEVERITY_ORDER.put("Critical", 1);
        SEVERITY_ORDER.put("High", 1);
        SEVERITY_ORDER.put("Medium", 2);
        SEVERITY_ORDER.put("Low", 3);
        SEVERITY_ORDER.put("Informational", 4);
        // Add any other severity strings you might encounter
    }

    public static final Map<String, Integer> CONFIDENCE_ORDER = new HashMap<>();
    static {
        CONFIDENCE_ORDER.put("Certain", 1);
        CONFIDENCE_ORDER.put("Firm", 2);
        CONFIDENCE_ORDER.put("Tentative", 3);
    }


    public static String serializeHTTPReqRes(String req,String res)
    {
        return "HTTP Request\n"+req+"\nHTTP Response\n"+res;
    }



    public static List<Finding> getUniqueFindingsList(List<AuditIssue> issueList)
    {
        try {
            List<Finding> finalList = new ArrayList<Finding>();
            for (AuditIssue auditIssue : issueList) {
                Finding existing_Finding = getFinding(finalList, auditIssue);
                if (existing_Finding == null) {
                    finalList.add(new Finding(auditIssue));
                } else {
                    //Merge the auditIssue to existing_Finding, e.g: baseUrl, issue details, Request,Response
                    existing_Finding.URL.add(auditIssue.baseUrl());
                    existing_Finding.issueDetails.add(auditIssue.detail());

                    //add http requests and responses
                    for (HttpRequestResponse httpreqres : auditIssue.requestResponses())
                    {
                        String req= (auditIssue.requestResponses().get(0).request() != null) ? auditIssue.requestResponses().get(0).request().toString() : " ";
                        String res= (auditIssue.requestResponses().get(0).response() != null) ? auditIssue.requestResponses().get(0).response().toString() : " ";
                        String[] newHTTPReqRes = {req, res};
                        existing_Finding.HTTPPOC.add(newHTTPReqRes);
                    }
                }
            }


            //format the HTTPOC..
            for (Finding f : finalList) {
                StringBuilder httpPocBuilder = new StringBuilder();
                StringBuilder httpPocBuilder2 = new StringBuilder();
                for (String[] pair : f.HTTPPOC) {
                    httpPocBuilder.append("Request\n").append(pair[0]);
                    httpPocBuilder.append("\nResponse\n").append(pair[1]).append("\n\n");

                    httpPocBuilder2.append("<p>Request</p>").append("<pre><code>\n" + StringEscapeUtils.escapeHtml4(pair[0].replace("\r", "")) + "\n</code></pre>");
                    httpPocBuilder2.append("<p><br>Response</p>").append("<pre><code>\n" + StringEscapeUtils.escapeHtml4(pair[1].replace("\r", "")) + "\n</code></pre><p><br></p>");
                }
                f.formattedHTTPPOC = httpPocBuilder.toString();
                f.formattedHTTPPOC2 = httpPocBuilder2.toString();
            }

            return finalList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static String getProjectScope(List<Finding> f_list)
    {
        StringBuilder sb=new StringBuilder();
        if(f_list!=null) {
            for(Finding f:f_list)
            {
                if(!sb.toString().contains(f.baseURL))
                {
                    sb.append(f.baseURL+",");
                }
            }
        }
        if(!sb.toString().isEmpty())
            sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    public static String getProjectOverallSecurityPosture(List<Finding> f_list)
    {
        StringBuilder sb=new StringBuilder();
        boolean hasHigh=false;
        boolean hasMedium=false;
        boolean hasLow=false;
        boolean hasInformational=false;
        if(f_list!=null) {
            for(Finding f:f_list)
            {
                if(f.severity.toLowerCase().equals("high"))
                    hasHigh=true;
                if(f.severity.toLowerCase().equals("medium"))
                    hasMedium=true;
                if(f.severity.toLowerCase().equals("low"))
                    hasLow=true;
                if(f.severity.toLowerCase().equals("informational"))
                    hasInformational=true;
            }
        }
        if(hasHigh)
            sb.append("High severity vulnerabilities have been detected, requiring immediate remediation to prevent system compromise and data breach.");
        else
        {
            if(hasMedium)
            {
                sb.append("While no immediate, critical threats leading to unauthenticated systemic compromise were identified, prompt remediation of these findings is necessary to strengthen the organization's defenses and prevent potential adverse business impacts.");
            }
            else
            {
                if(hasLow)
                {
                    sb.append("Minor vulnerabilities were identified that, while present, pose a minimal direct threat to critical assets or operations and typically require specific, unlikely conditions to be exploited.");
                }
                else
                {
                    sb.append("No direct security vulnerabilities were identified; findings primarily consist of observations or best practice deviations that provide insight but carry no immediate exploitable risk.");
                }
            }
        }

        return sb.toString();
    }


    public static Finding getFinding(List<Finding> findingsList,Finding cur_finding)
    {
        for (Finding finding : findingsList)
        {
            if (finding.baseURL.equals(cur_finding.baseURL) && finding.issueName.equals(cur_finding.issueName))
            {
                return finding;
            }
        }
        return null;
    }



    public static Finding getFinding(List<Finding> findingsList,AuditIssue cur_issue)
    {
        for (Finding finding : findingsList)
        {
            if (finding.baseURL.equals(cur_issue.httpService().toString()) && finding.issueName.equals(cur_issue.name()))
            {
                return finding;
            }
        }
        return null;
    }


    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.toLowerCase().substring(1);
    }



    public String getBaseURL() {
        return baseURL != null ? baseURL : "";
    }

    public String getIssueName() {
        return issueName;
    }

    public String getSeverity() {
        return capitalize(severity);
    }

    public String getConfidence() {
        return capitalize(confidence);
    }

    public List<String> getURL() {
        return URL;
    }


    public List<String> getIssueDetail() {
        return issueDetails;
    }


    public String getIssueDetailsSummary()
    {
        String escaped;
        String detailsSummary=ExportModel.summarizeFindingsDetails(Utilities.extractText(issueDetails),this.URL,false);
        String cleaned=StringEscapeUtils.escapeXml11(detailsSummary);
        escaped = cleaned.replace("\n","</w:t></w:r><w:r><w:br/><w:t>");
        return escaped;
    }

    public String getIssueDetailsSummaryHTML()
    {
        String escaped;
        //String detailsSummary=ExportModel.summarizeFindingsDetails(Utilities.extractText(issueDetails),this.URL,true);
        String detailsSummary=ExportModel.summarizeFindingsDetails(Utilities.extractText(issueDetails),this.URL,false);
        //return extractText(detailsSummary);
        //String cleaned=StringEscapeUtils.escapeXml11(detailsSummary);
        //escaped = cleaned.replace("\n","</w:t></w:r><w:r><w:br/><w:t>");
        return detailsSummary;
    }


    public static String cleanStringValue(String input) {
        if (input == null) {
            return null; // Keep nulls as nulls for JSON
        }
        Pattern REMOVE_NON_PRINTABLE_BUT_KEEP_WHITESPACE_PATTERN = Pattern.compile("\\p{C}");
        Matcher matcher = REMOVE_NON_PRINTABLE_BUT_KEEP_WHITESPACE_PATTERN.matcher(input);
        return matcher.replaceAll("");
    }

    public String getIssueDetailsSummaryPwndoc()
    {
        String escaped;
        //String detailsSummary=ExportModel.summarizeFindingsDetails(Utilities.extractText(issueDetails),this.URL,true);
        String detailsSummary=ExportModel.summarizeFindingsDetails(Utilities.extractText(issueDetails),this.URL,true);
        //return extractText(detailsSummary);
        //String cleaned=StringEscapeUtils.escapeXml11(detailsSummary);
        //escaped = cleaned.replace("\n","</w:t></w:r><w:r><w:br/><w:t>");
        return detailsSummary;
    }

    public String getIssueDetailsScope()
    {
        String escaped;
        //String detailsSummary=ExportModel.summarizeFindingsDetails(Utilities.extractText(issueDetails),this.URL,true);
        String detailsSummary=ExportModel.summarizeFindingsDetailsScope(Utilities.extractText(issueDetails),this.URL,true);
        //return extractText(detailsSummary);
        //String cleaned=StringEscapeUtils.escapeXml11(detailsSummary);
        //escaped = cleaned.replace("\n","</w:t></w:r><w:r><w:br/><w:t>");
        return detailsSummary;
    }


    //ok
    public String getIssueDescrBackground() {
        String cleaned;
        cleaned= issueDescrBackground != null ? issueDescrBackground : "";
        return Utilities.extractText(cleaned);
    }

    //ok
    public String getIssueRemediation() {
        String cleaned;
        cleaned= issueRemediation != null ? issueRemediation : "";
        return Utilities.extractText(cleaned);
    }

    public List<String[]> getHTTPPOC() {
        return HTTPPOC;
    }

    public String getFormattedHTTPPOC() {
        String cleaned;
        cleaned= formattedHTTPPOC != null ? formattedHTTPPOC : "";
        String escaped = StringEscapeUtils.escapeXml11(cleaned);
        escaped=escaped.replace("Request\n", "</w:t><w:rPr><w:b/></w:rPr><w:t>Request</w:t></w:r><w:r><w:br/><w:t>");
        escaped = escaped.replace("\nResponse\n", "</w:t><w:br/><w:br/><w:rPr><w:b/></w:rPr><w:t>Response</w:t></w:r><w:r><w:br/><w:t>");
        escaped = escaped.replace("\n","</w:t></w:r><w:r><w:br/><w:t>");

        return escaped;
    }


    public List<String> getCleanedIssueDetails() {
        List<String> cleaned = new ArrayList<>();

        if (issueDetails != null) {
            for (String detail : issueDetails) {
                if (detail != null) {
                    // Sanitize or strip unsafe HTML
                    String safe = Jsoup.clean(detail, Safelist.basic());
                    cleaned.add(safe);
                }
            }
        }

        return cleaned;
    }

    public static enum FindingSeverity
    {
        HIGH,
        MEDIUM,
        LOW,
        INFORMATION,
        FALSE_POSITIVE
    }




}
