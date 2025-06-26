package gr.fcvebf.burpexporterplugin.utils;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

public class Config {


    public static enum exportOptionsEnum {Pwndoc, CSV, JSON, XML,Markdown, Docx };

    public static boolean debugHTTPrequests=false;
    public static boolean proxyHTTPrequests=false;
    public static String default_proxyHost="127.0.0.1";
    public static int default_proxyPort=8080;

    public static boolean useMontoyaHTTPApi=true;
    public static long longTimeoutMillis = 30000;
    public static int LLMtimeout = 300;
    public static int LLMmaxRetries = 1;
    public static int LLMdefaultTemperature = 20;

    public static boolean applyFormat=false;
    public static boolean useOnlyBurpAI=true;
}
