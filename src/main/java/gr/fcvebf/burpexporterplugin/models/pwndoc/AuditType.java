package gr.fcvebf.burpexporterplugin.models.pwndoc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AuditType {

    public String _id;
    public String name;
    public List<Template> templates;
    public String stage="default";
    @JsonProperty("sections")
    private List<String> sections;

    @JsonIgnore // Mark the 'hidden' field as ignorable during serialization and deserialization
    private List<String> hidden;

    public AuditType()
    {

    }

    public AuditType(String _id, String name, List<Template> templates, String stage,List<String> sections) {
        this._id = _id;
        this.name = name;
        this.templates = templates;
        this.stage = stage;
        this.sections=sections;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    public String getStage() {
        return stage;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }


    public List<String> getHidden() {
        return hidden;
    }

    public void setHidden(List<String> hidden) {
        this.hidden = hidden;
    }



    public static class ComboAuditTypeItem {
        private String id;
        private String name;

        public ComboAuditTypeItem(String key, String value) {
            this.id = key;
            this.name = value;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name; // this is what is shown in the combo box
        }
    }


}
