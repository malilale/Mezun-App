package com.mezun.app;

public class User {
    private String name;
    private String lastname;
    private String startyear;
    private String endyear;
    private String email;
    private String education;
    private String country;
    private String city;
    private String firm;
    private String job;
    private String social;
    private String tel;
    private String imgUrl;

    public User(String name, String lastname, String startyear, String endyear, String email, String education, String country, String city, String firm, String job, String social, String tel, String imgUrl, String uid) {
        this.name = name;
        this.lastname = lastname;
        this.startyear = startyear;
        this.endyear = endyear;
        this.email = email;
        this.education = education;
        this.country = country;
        this.city = city;
        this.firm = firm;
        this.job = job;
        this.social = social;
        this.tel = tel;
        this.imgUrl = imgUrl;
        this.uid = uid;
    }

    private String uid;


    public User(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getStartyear() {
        return startyear;
    }

    public void setStartyear(String startyear) {
        this.startyear = startyear;
    }

    public String getEndyear() {
        return endyear;
    }

    public void setEndyear(String endyear) {
        this.endyear = endyear;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocation(){
        String location;
        if(country.isEmpty()) {
            location = city;
            if (city.isEmpty())
                location = "";
        }else if(city.isEmpty())
            location= country;
        else
            location = city+"/"+country;
        return location;
    }

    public String getYears() {
        return startyear+"-"+endyear;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirm() {
        return firm;
    }

    public void setFirm(String firm) {
        this.firm = firm;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
