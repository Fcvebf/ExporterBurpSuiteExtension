package gr.fcvebf.burpexporterplugin.models.pwndoc;

public class ApiResponse<T> {
    private String status;
    private T datas;

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public T getDatas() { return datas; }
    public void setDatas(T datas) { this.datas = datas; }
}