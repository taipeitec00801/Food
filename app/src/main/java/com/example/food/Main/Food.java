package com.example.food.Main;

public class Food {
    private String shop;
    private int image;
    private String name;

    public Food() {
        super();
    }

    public Food(String shop, int image, String name) {
        super();
        this.shop = shop;
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getshop() {return shop;}

    public void setShop(String shop) {
        this.shop = shop;
    }
}
