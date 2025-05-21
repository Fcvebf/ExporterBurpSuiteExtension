package gr.fcvebf.burpexporterplugin.models.pwndoc;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Creator {

    public  String _id;
    public String username;

    @JsonIgnore
    public String firstname;
    @JsonIgnore
    public String lastname;

    public Creator()
    {

    }

    public Creator(String _id, String username, String firstname, String lastname) {
        this._id = _id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
