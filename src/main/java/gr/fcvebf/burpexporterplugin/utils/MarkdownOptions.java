package gr.fcvebf.burpexporterplugin.utils;

import freemarker.template.Template;

public class MarkdownOptions
{
    public String exportFilename;
    public  Template template;
    public String projectName;
    public String projectDate;
    public boolean includeHTTPPOC;

    public MarkdownOptions(String exportFilename, Template md_template,String projectName,String projectDate, boolean includeHTTPPOC)
    {
        this.exportFilename=exportFilename;
        this.template=md_template;
        this.projectName=projectName;
        this.projectDate=projectDate;
        this.includeHTTPPOC=includeHTTPPOC;
    }

}
