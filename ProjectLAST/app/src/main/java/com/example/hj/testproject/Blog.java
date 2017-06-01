package com.example.hj.testproject;

/**
 * Created by SoYeon on 2017. 5. 22..
 */

public class Blog {
    private String title, desc, image, email;

    public Blog(){

    }

    public Blog(String title, String desc, String image, String email) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.email = email;
    }

    public String getTitle() {

        return title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = username;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
