package gr.fcvebf.burpexporterplugin.models.pwndoc;

public class Language {

    public String language;
    public String locale;

    public Language()
    {

    }

    public Language(String language, String locale) {
        this.language = language;
        this.locale = locale;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }



    public static class ComboLanguageItem {
        private String language;    // internal value
        private String locale;  // display value

        public ComboLanguageItem(String key, String value) {
            this.locale = key;
            this.language = value;
        }

        public String getLanguage() {
            return language;
        }

        public String getLocale() {
            return locale;
        }

        @Override
        public String toString() {
            return language; // this is what is shown in the combo box
        }
    }


}
