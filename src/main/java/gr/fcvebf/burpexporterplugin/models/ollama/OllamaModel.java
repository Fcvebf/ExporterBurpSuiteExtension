package gr.fcvebf.burpexporterplugin.models.ollama;

public class OllamaModel {

    public String name;
    public String model;

    public OllamaModel(String name, String model) {
        this.name = name;
        this.model = model;
    }

    @Override
    public String toString() {
        return model; // this is what is shown in the combo box
    }


}



