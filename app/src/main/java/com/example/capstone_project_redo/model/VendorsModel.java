package com.example.capstone_project_redo.model;

public class VendorsModel {

    String ImageProfile, Username, FirstName, LastName, MobileNumber, Municipality, Province, StallDescription, id;

    VendorsModel() {

    }

    public VendorsModel(String imageProfile, String username, String firstName, String lastName, String mobileNumber, String municipality, String province, String stallDescription, String id) {
        ImageProfile = imageProfile;
        Username = username;
        FirstName = firstName;
        LastName = lastName;
        MobileNumber = mobileNumber;
        Municipality = municipality;
        Province = province;
        StallDescription = stallDescription;
        this.id = id;
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

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getMunicipality() {
        return Municipality;
    }

    public void setMunicipality(String municipality) {
        Municipality = municipality;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
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
}
