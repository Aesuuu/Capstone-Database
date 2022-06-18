package com.example.capstone_project_redo.model;

public class ReviewsModel {
    String comment, rating, FirstName, LastName, ImageProfile;

    ReviewsModel() {

    }

    public ReviewsModel(String comment, String rating, String firstName, String lastName, String imageProfile) {
        this.comment = comment;
        this.rating = rating;
        FirstName = firstName;
        LastName = lastName;
        ImageProfile = imageProfile;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
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

    public String getImageProfile() {
        return ImageProfile;
    }

    public void setImageProfile(String imageProfile) {
        ImageProfile = imageProfile;
    }
}
