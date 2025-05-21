package gr.fcvebf.burpexporterplugin.utils;

public class CSVOptions
{
    public String exportFilename;
    public boolean includeHTTPPOC;
    public CSVOptions(String exportFilename, boolean includeHTTPPOC)
    {
        this.exportFilename=exportFilename;
        this.includeHTTPPOC=includeHTTPPOC;
    }

}
