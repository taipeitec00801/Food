package com.example.food.Main;

import java.io.Serializable;

public class Food implements Serializable {
//    private int id;
    private int image;
//    private String name;

    public Food(int image) {
        super();
//        this.id = id;
        this.image = image;
//        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

//    public String getName() {
//        return name;
//    }

//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
}
