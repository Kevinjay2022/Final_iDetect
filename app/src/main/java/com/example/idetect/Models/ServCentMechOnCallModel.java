package com.example.idetect.Models;

public class ServCentMechOnCallModel {
    String firstname, address, Input_Details;

    ServCentMechOnCallModel(){

    }

    public ServCentMechOnCallModel(String firstname, String address, String input_Details) {
        this.firstname = firstname;
        this.address = address;
        Input_Details = input_Details;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInput_Details() {
        return Input_Details;
    }

    public void setInput_Details(String input_Details) {
        Input_Details = input_Details;
    }
}
