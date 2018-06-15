package com.example.food.AppModel;

import java.io.Serializable;

public class SortAs implements Serializable{
    private Integer StoreId;
    private String StoreName;
    private String StoreAddress;
    private String StorePhone;
    private String ServiceHours;
    private String StorePicture;
    private Integer SortNumber;
    private Integer StoreRecomCount;
    private Integer StoreCommentCount;

    public SortAs(Integer storeId, String storeName, String storeAddress, String storePhone, String serviceHours, String storePicture, Integer sortNumber, Integer storeRecomCount, Integer storeCommentCount) {
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

    public Integer getStoreId() {
        return StoreId;
    }

    public void setStoreId(Integer storeId) {
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

    public Integer getSortNumber() {
        return SortNumber;
    }

    public void setSortNumber(Integer sortNumber) {
        SortNumber = sortNumber;
    }

    public Integer getStoreRecomCount() {
        return StoreRecomCount;
    }

    public void setStoreRecomCount(Integer storeRecomCount) {
        StoreRecomCount = storeRecomCount;
    }

    public Integer getStoreCommentCount() {
        return StoreCommentCount;
    }

    public void setStoreCommentCount(Integer storeCommentCount) {
        StoreCommentCount = storeCommentCount;
    }
}
