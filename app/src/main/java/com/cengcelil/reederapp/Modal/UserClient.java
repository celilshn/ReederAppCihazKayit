package com.cengcelil.reederapp.Modal;

import android.app.Application;

public class UserClient extends Application {
    private UserInformation userInformation;

    public UserInformation getUserInformation() {
        return userInformation;
    }

    public void setUserInformation(UserInformation userInformation) {
        this.userInformation = userInformation;
    }
}
