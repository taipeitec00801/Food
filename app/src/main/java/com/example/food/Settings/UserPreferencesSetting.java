package com.example.food.Settings;

import java.io.Serializable;

public class UserPreferencesSetting implements Serializable {
    private int ivPreferencesLeft, ivPreferencesRight;
    private String tvPreferencesLeft, tvPreferencesRight;

    public UserPreferencesSetting(int ivPreferencesLeft, String tvPreferencesLeft, int ivPreferencesRight, String tvPreferencesRight) {
        this.ivPreferencesLeft = ivPreferencesLeft;
        this.ivPreferencesRight = ivPreferencesRight;
        this.tvPreferencesLeft = tvPreferencesLeft;
        this.tvPreferencesRight = tvPreferencesRight;
    }

    public int getIvPreferencesLeft() {
        return ivPreferencesLeft;
    }

    public void setIvPreferencesLeft(int ivPreferencesLeft) {
        this.ivPreferencesLeft = ivPreferencesLeft;
    }

    public int getIvPreferencesRight() {
        return ivPreferencesRight;
    }

    public void setIvPreferencesRight(int ivPreferencesRight) {
        this.ivPreferencesRight = ivPreferencesRight;
    }

    public String getTvPreferencesLeft() {
        return tvPreferencesLeft;
    }

    public void setTvPreferencesLeft(String tvPreferencesLeft) {
        this.tvPreferencesLeft = tvPreferencesLeft;
    }

    public String getTvPreferencesRight() {
        return tvPreferencesRight;
    }

    public void setTvPreferencesRight(String tvPreferencesRight) {
        this.tvPreferencesRight = tvPreferencesRight;
    }
}
