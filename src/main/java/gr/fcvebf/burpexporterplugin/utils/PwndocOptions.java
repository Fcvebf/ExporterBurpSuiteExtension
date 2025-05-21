package gr.fcvebf.burpexporterplugin.utils;

import gr.fcvebf.burpexporterplugin.models.pwndoc.PwndocApi;

public class PwndocOptions
{
    public String pwndocURL;
    public String pwndocUser;
    public String pwndocPass;
    public String pwndocAuditName;
    public String pwndocAuditCategory;
    public String pwndocAuditVulnType;
    public String pwndocAuditType;
    public String pwndocLanguage;
    public String pwndocAuditId;
    public boolean createNew;
    public PwndocApi p_api;

    public PwndocOptions(String pwndocURL,String pwndocUser,String pwndocPass,String pwndocAuditName,String pwndocAuditCategory,String pwndocAuditVulnType,String pwndocAuditType, String pwndocLanguage,String pwndocAuditId,boolean createNew,PwndocApi p_api)
    {
        this.pwndocURL=pwndocURL;
        this.pwndocUser=pwndocUser;
        this.pwndocPass=pwndocPass;
        this.pwndocAuditName=pwndocAuditName;
        this.pwndocAuditCategory=pwndocAuditCategory;
        this.pwndocAuditVulnType=pwndocAuditVulnType;
        this.pwndocAuditType=pwndocAuditType;
        this.pwndocLanguage=pwndocLanguage;
        this.pwndocAuditId=pwndocAuditId;
        this.createNew=createNew;
        this.p_api=p_api;
    }


}
