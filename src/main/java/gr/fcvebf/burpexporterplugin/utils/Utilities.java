package gr.fcvebf.burpexporterplugin.utils;

import org.jsoup.Jsoup;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Utilities
{

    public static String extractText(String html) {
        String cleaned="";
        if(html!=null) {
            cleaned= Jsoup.parse(html).text();
        }
        return cleaned;
    }

    public static List<String> extractText(List<String> html) {
        List<String> newList=new ArrayList<>();
        if(html!=null) {
            for (String cleaned : html)
                newList.add(extractText(cleaned));
        }
        return newList;
    }


    public static String escapeBinaryChars(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            int unsigned = b & 0xFF;
            if ((unsigned >= 0x20 && unsigned != 0x7F) || unsigned == 0x09 || unsigned == 0x0A || unsigned == 0x0D) {
                // Printable character
                if (b == '\\') {
                    sb.append("\\\\");
                } else {
                    sb.append((char) unsigned);
                }
            } else {
                // Escape as hex
                sb.append(String.format("\\x%02X", unsigned));
            }
        }
        return sb.toString();
    }



    public static String getStackTraceAsString(Throwable throwable) {
        if (throwable == null) {
            return ""; // Or null, depending on your preference for null input
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }



}
