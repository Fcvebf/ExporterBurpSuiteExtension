package gr.fcvebf.burpexporterplugin.utils;

public class JSONOptions
{
    public String exportFilename;
    public boolean includeHTTPPOC;

    public JSONOptions(String exportFilename, boolean includeHTTPPOC)
    {
        this.exportFilename=exportFilename;
        this.includeHTTPPOC=includeHTTPPOC;
    }

}
