package com.example.food.Sort;

public class Sort {
    private int cvLcolor,ivLsrc,sortLnum;
    private String tvLname;
    private int cvRcolor,ivRsrc,sortRnum;
    private String tvRname;

    public Sort(int cvLcolor, int ivLsrc, int sortLnum, String tvLname, int cvRcolor, int ivRsrc, int sortRnum, String tvRname) {
        this.cvLcolor = cvLcolor;
        this.ivLsrc = ivLsrc;
        this.sortLnum = sortLnum;
        this.tvLname = tvLname;
        this.cvRcolor = cvRcolor;
        this.ivRsrc = ivRsrc;
        this.sortRnum = sortRnum;
        this.tvRname = tvRname;
    }

    public int getCvLcolor() {
        return cvLcolor;
    }

    public void setCvLcolor(int cvLcolor) {
        this.cvLcolor = cvLcolor;
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

    public int getCvRcolor() {
        return cvRcolor;
    }

    public void setCvRcolor(int cvRcolor) {
        this.cvRcolor = cvRcolor;
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
