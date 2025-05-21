package gr.fcvebf.burpexporterplugin.models.pwndoc;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VulnCategory {

    public String _id;
    public String name;

    @JsonIgnore
    public String sortOrder;
    @JsonIgnore
    public String sortValue;
    @JsonIgnore
    public boolean sortAuto;

    public VulnCategory() {

    }

    public VulnCategory(String _id, String name) {
        this._id = _id;
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static class ComboVulnCateogryItem {
        private String _id;
        private String name;

        public ComboVulnCateogryItem(String key, String value) {
            this._id = key;
            this.name = value;
        }

        public String getVulnCateogryItem() {
            return _id;
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
