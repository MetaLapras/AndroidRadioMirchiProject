package in.co.ashclan.mirchithunder.model;

public class ParticipantModel {
    private String firstname,lastname,gender,dob,email,password,status,mobile,image,Bkid;

    public ParticipantModel(String firstname,String lastname,String gender,String dob, String email, String password, String status, String mobile,String image,String bkid) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.dob = dob;
        this.email = email;
        this.password = password;
        this.status = status;
        this.mobile = mobile;
        this.image = image;
        this.Bkid = bkid;
    }


    @Override
    public String toString() {
        return "Participants :"+
                " firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", gender='" + gender + '\'' +
                ", dob='" + dob + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", mobile='" + mobile + '\'' +
                ", image='" + image + '\'' +
                ", bkid='" + Bkid + '\''+
                '}';
    }

    public ParticipantModel() {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBkid() {
        return Bkid;
    }

    public void setBkid(String bkid) {
        Bkid = bkid;
    }
}
