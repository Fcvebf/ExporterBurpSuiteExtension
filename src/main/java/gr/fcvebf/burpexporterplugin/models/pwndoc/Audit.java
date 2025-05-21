package gr.fcvebf.burpexporterplugin.models.pwndoc;

public class Audit {

    public String _id;
    public String name;
    public Language languageobj;
    public String language;
    public String auditType;
    public AuditType auditTypeobj;
    public Template template;
    public Creator creator;


    public Audit(String _id, String name, AuditType auditType, Language language, Template template, Creator creator) {
        this._id = _id;
        this.name = name;
        this.auditTypeobj = auditType;
        this.languageobj = language;
        this.template = template;
        this.creator = creator;
    }


    public static final String CVSS_HIGH="CVSS:3.1/AV:N/AC:H/PR:L/UI:N/S:U/C:H/I:H/A:L";
    public static final String CVSS_MEDIUM="CVSS:3.1/AV:N/AC:H/PR:L/UI:N/S:U/C:L/I:L/A:L";
    public static final String CVSS_LOW="CVSS:3.1/AV:N/AC:H/PR:H/UI:N/S:U/C:L/I:L/A:N";
    public static final String CVSS_INFORMATIONAL="CVSS:3.1/AV:N/AC:H/PR:H/UI:N/S:U/C:N/I:N/A:N";

    public static String cvssConverter(String burpSeverity)
    {
        if (burpSeverity.toLowerCase().equals("high"))
            return CVSS_HIGH;
        else if (burpSeverity.toLowerCase().equals("medium"))
            return CVSS_MEDIUM;
        else if (burpSeverity.toLowerCase().equals("low"))
            return CVSS_LOW;
        else
            return CVSS_INFORMATIONAL;

    }


}
