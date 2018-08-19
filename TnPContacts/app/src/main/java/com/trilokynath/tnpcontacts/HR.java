package com.trilokynath.tnpcontacts;

public class HR {

    Integer ID;
    String name, city,mobile,email,note, company;

    HR(){
        name="";
        city="";
        mobile="";
        email="";
        company="";
        note="";
    }
    public HR(String name, String email, String mobile, String company, String city, String note) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.company = company;
        this.city = city;
        this.note = note;
    }
    public HR set(String name, String email, String mobile, String company, String city, String note) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.company = company;
        this.city = city;
        this.note = note;
        return this;
    }

    public Integer getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getCompany() {
        return company;
    }

    public String getNote() {
        return note;
    }
}