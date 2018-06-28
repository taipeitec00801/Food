package com.example.food.Map;

public class StoreInfo {
    private int storeID, storeImg;
    private String storeName,storeAddress,businessHours,tel;

    public StoreInfo() {
    }

    public StoreInfo(int storeID, int storeImg, String storeName, String storeAddress, String businessHours, String tel) {
        this.storeID = storeID;
        this.storeImg = storeImg;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.businessHours = businessHours;
        this.tel = tel;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public int getStoreImg() {
        return storeImg;
    }

    public void setStoreImg(int storeImg) {
        this.storeImg = storeImg;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
