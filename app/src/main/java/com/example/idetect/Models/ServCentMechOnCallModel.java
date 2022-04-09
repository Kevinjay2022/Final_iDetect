package com.example.idetect.Models;

public class ServCentMechOnCallModel {
    String birth, certificate, key, mechID, skills, status, rate;

    ServCentMechOnCallModel(){

    }

    public ServCentMechOnCallModel(String birth, String certificate, String key, String mechID, String skills, String status, String rate) {
        this.birth = birth;
        this.certificate = certificate;
        this.key = key;
        this.mechID = mechID;
        this.skills = skills;
        this.status = status;
        this.rate = rate;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMechID() {
        return mechID;
    }

    public void setMechID(String mechID) {
        this.mechID = mechID;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
