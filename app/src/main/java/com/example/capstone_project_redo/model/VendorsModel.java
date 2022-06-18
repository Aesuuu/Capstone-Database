package com.example.capstone_project_redo.model;

public class VendorsModel {

    String ImageProfile, Username, FirstName, LastName, EmailAddress, Password, MobileNumber, MarketAddress, StallDescription, id, OverallScore, Raters;

    VendorsModel() {

    }

    public VendorsModel(String imageProfile, String username, String firstName, String lastName, String emailAddress, String password, String mobileNumber, String marketAddress, String stallDescription, String id, String overallScore, String raters) {
        ImageProfile = imageProfile;
        Username = username;
        FirstName = firstName;
        LastName = lastName;
        EmailAddress = emailAddress;
        Password = password;
        MobileNumber = mobileNumber;
        MarketAddress = marketAddress;
        StallDescription = stallDescription;
        this.id = id;
        OverallScore = overallScore;
        Raters = raters;
    }

    public String getImageProfile() {
        return ImageProfile;
    }

    public void setImageProfile(String imageProfile) {
        ImageProfile = imageProfile;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getMarketAddress() {
        return MarketAddress;
    }

    public void setMarketAddress(String marketAddress) {
        MarketAddress = marketAddress;
    }

    public String getStallDescription() {
        return StallDescription;
    }

    public void setStallDescription(String stallDescription) {
        StallDescription = stallDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOverallScore() {
        return OverallScore;
    }

    public void setOverallScore(String overallScore) {
        OverallScore = overallScore;
    }

    public String getRaters() {
        return Raters;
    }

    public void setRaters(String raters) {
        Raters = raters;
    }
}
