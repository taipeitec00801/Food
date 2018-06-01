package com.example.food.Map;

public class StoreInfo {
    private int StoreID,StoreImg;
    private String StoreName,StoreAddress;

    public StoreInfo() {
    }

    public StoreInfo(int storeID, int storeImg, String storeName, String storeAddress) {
        StoreID = storeID;
        StoreImg = storeImg;
        StoreName = storeName;
        StoreAddress = storeAddress;
    }

    public int getStoreID() {
        return StoreID;
    }

    public void setStoreID(int storeID) {
        StoreID = storeID;
    }

    public int getStoreImg() {
        return StoreImg;
    }

    public void setStoreImg(int storeImg) {
        StoreImg = storeImg;
    }

    public String getStoreName() {
        return StoreName;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public String getStoreAddress() {
        return StoreAddress;
    }

    public void setStoreAddress(String storeAddress) {
        StoreAddress = storeAddress;
    }
}
