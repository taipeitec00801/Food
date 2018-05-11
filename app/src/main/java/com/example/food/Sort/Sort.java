package com.example.food.Sort;

public class Sort {
    private String nameL,nameR;
    private int imageR,imageL,sortResUp,sortResDown;

    public Sort(String nameL, int imageR, int sortResUp, String nameR, int imageL,  int sortResDown) {
        this.nameL = nameL;
        this.nameR = nameR;
        this.imageR = imageR;
        this.imageL = imageL;
        this.sortResUp = sortResUp;
        this.sortResDown = sortResDown;
    }

    public String getNameL() {
        return nameL;
    }

    public void setNameL(String nameL) {
        this.nameL = nameL;
    }

    public String getNameR() {
        return nameR;
    }

    public void setNameR(String nameR) {
        this.nameR = nameR;
    }

    public int getImageR() {
        return imageR;
    }

    public void setImageR(int imageR) {
        this.imageR = imageR;
    }

    public int getImageL() {
        return imageL;
    }

    public void setImageL(int imageL) {
        this.imageL = imageL;
    }

    public int getSortResUp() {
        return sortResUp;
    }

    public void setSortResUp(int sortResUp) {
        this.sortResUp = sortResUp;
    }

    public int getSortResDown() {
        return sortResDown;
    }

    public void setSortResDown(int sortResDown) {
        this.sortResDown = sortResDown;
    }
}
