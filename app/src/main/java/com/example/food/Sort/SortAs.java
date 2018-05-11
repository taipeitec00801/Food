package com.example.food.Sort;

public class SortAs {
    private int sortRes;
    private String name;
    private int likeNumber;


    public SortAs(int sortRes, String name, int likeNumber) {
        this.sortRes = sortRes;
        this.name = name;
        this.likeNumber = likeNumber;
    }

    public int getSortRes() {
        return sortRes;
    }

    public void setSortRes(int sortRes) {
        this.sortRes = sortRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return likeNumber;
    }

    public void setNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }
}
