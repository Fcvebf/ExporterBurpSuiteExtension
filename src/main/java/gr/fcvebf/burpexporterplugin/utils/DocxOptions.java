package gr.fcvebf.burpexporterplugin.utils;

import java.io.InputStream;

public class DocxOptions
{
    public String exportFilename;
    public InputStream template;
    public String projectName;
    public String projectDate;
    public String execSummary;
    public boolean includeHTTPPOC;


    public DocxOptions(String exportFilename, InputStream docx_template,String projectName,String projectDate, String execSummary, boolean includeHTTPPOC)
    {
        this.exportFilename=exportFilename;
        this.template=docx_template;
        this.projectName=projectName;
        this.projectDate=projectDate;
        this.execSummary=execSummary;
        this.includeHTTPPOC=includeHTTPPOC;

    }

}
