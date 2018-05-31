package com.example.food.Comment;

/**
 * Created by PC-26 on 5/25/2018.
 */

public class Member {
    private String name; // 會員名稱
    private int image; // 會員照片
    private String message ;  //會員部分評論

    public Member(String name, int image, String message) {
        this.name = name;
        this.image = image;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
