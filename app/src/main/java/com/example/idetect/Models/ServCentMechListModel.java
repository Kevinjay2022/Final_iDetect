package com.example.idetect.Models;

public class ServCentMechListModel {
    String name, address, birth, skills, gender, status, mech_type;

    ServCentMechListModel(){

    }

    public ServCentMechListModel(String name, String address, String birth, String skills, String gender, String status, String mech_type) {
        this.name = name;
        this.address = address;
        this.birth = birth;
        this.skills = skills;
        this.gender = gender;
        this.status = status;
        this.mech_type = mech_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMech_type() {
        return mech_type;
    }

    public void setMech_type(String mech_type) {
        this.mech_type = mech_type;
    }
}
