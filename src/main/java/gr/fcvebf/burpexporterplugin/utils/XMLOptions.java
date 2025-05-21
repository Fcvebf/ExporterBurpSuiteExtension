package gr.fcvebf.burpexporterplugin.utils;

public class XMLOptions
{
    public String exportFilename;
    public boolean includeHTTPPOC;

    public XMLOptions(String exportFilename, boolean includeHTTPPOC)
    {
        this.exportFilename=exportFilename;
        this.includeHTTPPOC=includeHTTPPOC;
    }

}
