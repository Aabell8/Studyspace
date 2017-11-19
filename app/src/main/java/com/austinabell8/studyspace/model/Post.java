package com.austinabell8.studyspace.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by aabell on 7/12/2017.
 */

public class Post implements Comparable<Post>, Parcelable {

    private String name;
    private String course;
    private String description;
    private String price;
    private String status;
    private String uid;
    private String pid;
    private Map<String, Object> applied;

    public Post(){

    }

    public Post(String name, String course, String description, String price, String status, String uid){
        this.name = name;
        this.course = course;
        this.description = description;
        this.price = price;
        this.status = status;
        this.uid = uid;
    }

//    public static ArrayList<Post> getPosts(){
//        ArrayList<Post> posts = new ArrayList<>();
//        posts.add(new Post("Austin Abell", "STATS 2141A", "1 hour tutor Session", "$40.00", "Active", ""));
//        posts.add(new Post("Austin Abell", "BUSINESS 2257A", "1 hour review", "$30.00", "Active", ""));
//        posts.add(new Post("Austin Abell", "PHILOSOP 2032F", "2 hour tutor Session", "$60.00", "Active", ""));
//        posts.add(new Post("Austin Abell", "CALC 1501A", "1.5 hour exam preparation for upcoming exam on thursday", "$35.00", "Active", ""));
//        return posts;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String Id) {
        this.description = Id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Map<String, Object> getApplied() {
        return applied;
    }

    public void setApplied(Map<String, Object> applied) {
        this.applied = applied;
    }

    @Override
    public int compareTo(@NonNull Post o) {
        if (this.getName().compareTo(o.getName()) < 0)
            return 1;
        else
            return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(course);
        out.writeString(description);
        out.writeString(price);
        out.writeString(status);
        out.writeString(uid);
        out.writeString(pid);
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Post(Parcel in) {
        name = in.readString();
        course = in.readString();
        description = in.readString();
        price = in.readString();
        status = in.readString();
        uid = in.readString();
        pid = in.readString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Post)) {
            return false;
        }
        Post that = (Post) other;
        // Custom equality check here.

        return this.pid.equals(that.pid);
//                && this.course.equals(that.course)
//                && this.description.equals(that.description)
//                && this.price.equals(that.price)
////                && this.status.equals(that.status)
//                && this.uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        int hashCode = 1;

        hashCode = hashCode * 37 + this.pid.hashCode();
        hashCode = hashCode * 37 + this.name.hashCode();
        hashCode = hashCode * 37 + this.course.hashCode();
        hashCode = hashCode * 37 + this.description.hashCode();
        hashCode = hashCode * 37 + this.price.hashCode();
        hashCode = hashCode * 37 + this.status.hashCode();
        hashCode = hashCode * 37 + this.uid.hashCode();

        return hashCode;
    }

}
