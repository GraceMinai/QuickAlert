package com.melvin.quickalert;

public class Model {

    private String userName;
    private String userNumber;
    private String emergencyMessage;

    public Model() {
    }


    public Model(String userName, String userNumber, String emergencyMessage) {
        this.userName = userName;
        this.userNumber = userNumber;
        this.emergencyMessage = emergencyMessage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getEmergencyMessage() {
        return emergencyMessage;
    }

    public void setEmergencyMessage(String emergencyMessage) {
        this.emergencyMessage = emergencyMessage;
    }
}
