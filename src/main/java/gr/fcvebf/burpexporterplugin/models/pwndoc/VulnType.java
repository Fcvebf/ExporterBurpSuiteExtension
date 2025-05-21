package gr.fcvebf.burpexporterplugin.models.pwndoc;

public class VulnType {

    public String name;
    public String locale;

    public VulnType()
    {

    }

    public VulnType(String name, String locale) {
        this.name = name;
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }


    public static class ComboVulnTypeItem {
        private String name;
        private String locale;

        public ComboVulnTypeItem(String key, String value) {
            this.name = key;
            this.locale = value;
        }

        public String getVulnTypeItem() {
            return name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }


}
