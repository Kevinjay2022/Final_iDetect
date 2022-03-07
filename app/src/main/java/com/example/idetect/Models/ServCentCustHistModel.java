package com.example.idetect.Models;

public class ServCentCustHistModel {
    String Customer_Name, Customer_Address, Vehicle_Model, Vehicle_type;

    ServCentCustHistModel(){

    }

    public ServCentCustHistModel(String customer_Name, String customer_Address, String vehicle_Model, String vehicle_type) {
        Customer_Name = customer_Name;
        Customer_Address = customer_Address;
        Vehicle_Model = vehicle_Model;
        Vehicle_type = vehicle_type;
    }

    public String getCustomer_Name() {
        return Customer_Name;
    }

    public void setCustomer_Name(String customer_Name) {
        Customer_Name = customer_Name;
    }

    public String getCustomer_Address() {
        return Customer_Address;
    }

    public void setCustomer_Address(String customer_Address) {
        Customer_Address = customer_Address;
    }

    public String getVehicle_Model() {
        return Vehicle_Model;
    }

    public void setVehicle_Model(String vehicle_Model) {
        Vehicle_Model = vehicle_Model;
    }

    public String getVehicle_type() {
        return Vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        Vehicle_type = vehicle_type;
    }
}
