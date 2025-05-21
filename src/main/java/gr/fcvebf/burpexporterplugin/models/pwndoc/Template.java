package gr.fcvebf.burpexporterplugin.models.pwndoc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Template {

    @JsonProperty("template")
    public String _id;
    public String name;

    public  String locale;

    public  Template()
    {

    }

    public Template(String _id, String name,String locale) {
        this._id = _id;
        this.name = name;
        this.locale=locale;
    }
}
