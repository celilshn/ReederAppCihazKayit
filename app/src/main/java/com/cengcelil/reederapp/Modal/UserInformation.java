package com.cengcelil.reederapp.Modal;

public class UserInformation {
    private String personalName;
    private String uId;

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public UserInformation() {
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getPersonalSurname() {
        return personalSurname;
    }

    public void setPersonalSurname(String personalSurname) {
        this.personalSurname = personalSurname;
    }

    private String personalSurname;
}
