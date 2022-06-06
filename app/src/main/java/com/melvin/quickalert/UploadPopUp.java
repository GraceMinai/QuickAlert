package com.melvin.quickalert;

import android.app.Dialog;

public class UploadPopUp {
    String userName, userPhoneNumber, emergencyMessage;

    public UploadPopUp() {
        //Empty constructor
    }

    public UploadPopUp(String userName, String userPhoneNumber, String emergencyMessage) {
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.emergencyMessage = emergencyMessage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getEmergencyMessage() {
        return emergencyMessage;
    }

    public void setEmergencyMessage(String emergencyMessage) {
        this.emergencyMessage = emergencyMessage;
    }
}
