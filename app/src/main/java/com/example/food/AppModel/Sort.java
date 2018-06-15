package com.example.food.AppModel;

public class Sort {
    private int ivLsrc,sortLnum;
    private String tvLname;
    private int ivRsrc,sortRnum;
    private String tvRname;

    public Sort( int ivLsrc, int sortLnum, String tvLname,int ivRsrc, int sortRnum, String tvRname) {

        this.ivLsrc = ivLsrc;
        this.sortLnum = sortLnum;
        this.tvLname = tvLname;
        this.ivRsrc = ivRsrc;
        this.sortRnum = sortRnum;
        this.tvRname = tvRname;
    }


    public int getIvLsrc() {
        return ivLsrc;
    }

    public void setIvLsrc(int ivLsrc) {
        this.ivLsrc = ivLsrc;
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

    public int getIvRsrc() {
        return ivRsrc;
    }

    public void setIvRsrc(int ivRsrc) {
        this.ivRsrc = ivRsrc;
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
}
