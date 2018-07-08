package in.co.ashclan.mirchithunder.model;

public class ParticipantModel {
    private String name,email,password,status,mobile;

    public ParticipantModel(String name, String email, String password, String status, String mobile) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = status;
        this.mobile = mobile;
    }

    public ParticipantModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
