package com.example.food.AppModel;

import java.io.Serializable;

public class Store implements Serializable{
    private int StoreId,SortNumber,StoreRecomCount,StoreCommentCount;
    private String StoreName,StoreAddress,StorePhone,ServiceHours,StorePicture;

    public Store() {
    }

    public Store(int storeId, String storeName, String storeAddress, String storePhone,
                 String serviceHours, String storePicture, int sortNumber, int storeRecomCount, int storeCommentCount
                  ) {
        StoreId = storeId;
        StoreName = storeName;
        StoreAddress = storeAddress;
        StorePhone = storePhone;
        ServiceHours = serviceHours;
        StorePicture = storePicture;
        SortNumber = sortNumber;
        StoreRecomCount = storeRecomCount;
        StoreCommentCount = storeCommentCount;
    }

    public int getStoreId() {
        return StoreId;
    }

    public void setStoreId(int storeId) {
        StoreId = storeId;
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

    public String getStorePhone() {
        return StorePhone;
    }

    public void setStorePhone(String storePhone) {
        StorePhone = storePhone;
    }

    public String getServiceHours() {
        return ServiceHours;
    }

    public void setServiceHours(String serviceHours) {
        ServiceHours = serviceHours;
    }

    public String getStorePicture() {
        return StorePicture;
    }

    public void setStorePicture(String storePicture) {
        StorePicture = storePicture;
    }

    public int getSortNumber() {
        return SortNumber;
    }

    public void setSortNumber(int sortNumber) {
        SortNumber = sortNumber;
    }

    public int getStoreRecomCount() {
        return StoreRecomCount;
    }

    public void setStoreRecomCount(int storeRecomCount) {
        StoreRecomCount = storeRecomCount;
    }

    public int getStoreCommentCount() {
        return StoreCommentCount;
    }

    public void setStoreCommentCount(int storeCommentCount) {
        StoreCommentCount = storeCommentCount;
    }

}
