package in.co.ashclan.mirchithunder.model;

public class VolunteerModel {

    private String Name,Email,Mobile,Password,IsStaff,Status;

    public VolunteerModel(String name, String email, String mobile, String password, String isStaff, String status) {
        Name = name;
        Email = email;
        Mobile = mobile;
        Password = password;
        IsStaff = isStaff;
        Status = status;
    }

    public VolunteerModel() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
