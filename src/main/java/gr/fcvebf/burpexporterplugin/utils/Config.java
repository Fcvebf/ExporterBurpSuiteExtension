package gr.fcvebf.burpexporterplugin.utils;

public class Config {


    public static enum exportOptionsEnum {Pwndoc, CSV, JSON, XML,Markdown, Docx };

    public static boolean debugHTTPrequests=false;
    public static boolean useMontoyaHTTPApi=true;
    public static long longTimeoutMillis = 30000;

}
