package com.mezun.app;

public class Announcement {
    private String post, uid, fullname, email, imgUrl, postId, date;
    private long time;

    public Announcement(String post, String uid, String fullname, String email, String imgUrl, String postId, String date, long time) {
        this.post = post;
        this.uid = uid;
        this.fullname = fullname;
        this.email = email;
        this.imgUrl = imgUrl;
        this.postId = postId;
        this.date = date;
        this.time = time;
    }

    public Announcement() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getFullname() {
        return fullname;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "post='" + post + '\'' +
                ", uid='" + uid + '\'' +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
