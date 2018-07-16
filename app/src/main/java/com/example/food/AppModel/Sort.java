package com.example.food.AppModel;

import android.graphics.drawable.Drawable;

public class Sort {
    private int sortLnum;
    private String tvLname;
    private int sortRnum;
    private String tvRname;
    private Drawable sortR , sortL;
    private Integer ivLsrc , ivRsrc;

    public Sort(Integer ivLsrc, int sortLnum,String tvLname,  Integer ivRsrc, int sortRnum , String tvRname) {
        this.sortLnum = sortLnum;
        this.tvLname = tvLname;
        this.sortRnum = sortRnum;
        this.tvRname = tvRname;
        this.ivRsrc = ivRsrc;
        this.ivLsrc = ivLsrc;
    }

    public Sort(Drawable sortL, int sortLnum,String tvLname,  Drawable sortR, int sortRnum , String tvRname) {
        this.sortLnum = sortLnum;
        this.tvLname = tvLname;
        this.sortRnum = sortRnum;
        this.tvRname = tvRname;
        this.sortR = sortR;
        this.sortL = sortL;
    }

    public Integer getIvLsrc() {
        return ivLsrc;
    }

    public void setIvLsrc(Integer ivLsrc) {
        this.ivLsrc = ivLsrc;
    }

    public Integer getIvRsrc() {
        return ivRsrc;
    }

    public void setIvRsrc(Integer ivRsrc) {
        this.ivRsrc = ivRsrc;
    }

    public int getSortLnum() {
        return sortLnum;
    }

    public void setSortLnum(int sortLnum) {
        this.sortLnum = sortLnum;
    }

    public String getTvLname() {
        return tvLname;
    }

    public void setTvLname(String tvLname) {
        this.tvLname = tvLname;
    }

    public int getSortRnum() {
        return sortRnum;
    }

    public void setSortRnum(int sortRnum) {
        this.sortRnum = sortRnum;
    }

    public String getTvRname() {
        return tvRname;
    }

    public void setTvRname(String tvRname) {
        this.tvRname = tvRname;
    }

    public Drawable getSortR() {
        return sortR;
    }

    public void setSortR(Drawable sortR) {
        this.sortR = sortR;
    }

    public Drawable getSortL() {
        return sortL;
    }

    public void setSortL(Drawable sortL) {
        this.sortL = sortL;
    }
}
