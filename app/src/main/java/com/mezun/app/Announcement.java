package com.mezun.app;

public class Announcement {
    private String post, uid, postId, date;
    private long time;

    public Announcement(String post, String uid, String postId, String date, long time) {
        this.post = post;
        this.uid = uid;
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
