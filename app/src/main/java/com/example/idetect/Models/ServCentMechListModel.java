package com.example.idetect.Models;

public class ServCentMechListModel {
    String name, address, birth, skills, gender, status, mech_type, ID, mechID, key, rate;
    boolean delete;

    ServCentMechListModel(){

    }

    public ServCentMechListModel(String name, String address, String birth, String skills, String gender, String status, String mech_type, String ID, String mechID, String key, String rate, boolean delete) {
        this.name = name;
        this.address = address;
        this.birth = birth;
        this.skills = skills;
        this.gender = gender;
        this.status = status;
        this.mech_type = mech_type;
        this.ID = ID;
        this.mechID = mechID;
        this.key = key;
        this.rate = rate;
        this.delete = delete;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMechID() {
        return mechID;
    }

    public void setMechID(String mechID) {
        this.mechID = mechID;
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
