package com.austinabell8.studyspace.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Map;

public class User implements Parcelable {

    private String username;
    private String email;
    private String role;
    private String fullName;
    private String age;
    private String profilePicLocation;
    private String rating;
    private String rate;
    private boolean accepted;
    private String uid;

    public User(){

    }

    public User(String _username, String _email, String _fullName, String _age){
        username = _username;
        email = _email;
        fullName = _fullName;
        age = _age;
        role = "N";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getProfilePicLocation() {
        return profilePicLocation;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(username);
        out.writeString(email);
        out.writeString(role);
        out.writeString(fullName);
        out.writeString(age);
        out.writeString(profilePicLocation);
        out.writeString(rating);
        out.writeString(rate);
        out.writeString(uid);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private User(Parcel in) {
        username = in.readString();
        email = in.readString();
        role = in.readString();
        fullName = in.readString();
        age = in.readString();
        profilePicLocation = in.readString();
        rating = in.readString();
        rate = in.readString();
        uid = in.readString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        User that = (User) other;
        // Custom equality check here.

        return this.username.equals(that.getUsername());
    }

//    @Override
//    public int hashCode() {
//        int hashCode = 1;
//
//        hashCode = hashCode * 37 + this.username.hashCode();
//        hashCode = hashCode * 37 + this.email.hashCode();
//        hashCode = hashCode * 37 + this.role.hashCode();
//        hashCode = hashCode * 37 + this.fullName.hashCode();
//        hashCode = hashCode * 37 + this.price.hashCode();
//        hashCode = hashCode * 37 + this.status.hashCode();
//        hashCode = hashCode * 37 + this.uid.hashCode();
//
//        return hashCode;
//    }
}
