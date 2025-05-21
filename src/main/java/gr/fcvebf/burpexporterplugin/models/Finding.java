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

import org.apache.commons.text.StringEscapeUtils;
import static org.apache.commons.collections.CollectionUtils.size;

public class Finding  implements Serializable {

    public String baseURL;
    public String issueName;
    public String severity;
    public String confidence;
    public List<String> URL;
    public List<String> issueDetails;
    private String issueDetailsSummary;
    private String issueDetailsSummaryHTML;
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

                    httpPocBuilder2.append("<p>Request</p>").append("<pre><code>" + StringEscapeUtils.escapeHtml4(pair[0].replace("\r", "")) + "</code></pre>");
                    httpPocBuilder2.append("<p><br>Response</p>").append("<pre><code>" + StringEscapeUtils.escapeHtml4(pair[1].replace("\r", "")) + "</code></pre><p><br></p>");
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
