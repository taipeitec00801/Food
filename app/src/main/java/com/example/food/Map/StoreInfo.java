package com.example.food.Map;

public class StoreInfo {
    private String Strore_Name,Store_simpinfo;
    private int Stor_id,Store_img;
    private double latitude, longitude;

    public StoreInfo(){super();}

    public StoreInfo(int stor_id,int store_img,String strore_Name, String store_simpinfo, double latitude, double longitude) {
        Strore_Name = strore_Name;
        Store_simpinfo = store_simpinfo;
        Stor_id = stor_id;
        Store_img = store_img;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStrore_Name() {
        return Strore_Name;
    }

    public void setStrore_Name(String strore_Name) {
        Strore_Name = strore_Name;
    }

    public String getStore_simpinfo() {
        return Store_simpinfo;
    }

    public void setStore_simpinfo(String store_simpinfo) {
        Store_simpinfo = store_simpinfo;
    }

    public int getStor_id() {
        return Stor_id;
    }

    public void setStor_id(int stor_id) {
        Stor_id = stor_id;
    }

    public int getStore_img() {
        return Store_img;
    }

    public void setStore_img(int store_img) {
        Store_img = store_img;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
