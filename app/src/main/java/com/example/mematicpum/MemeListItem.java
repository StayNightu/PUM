package com.example.mematicpum;

import android.graphics.Bitmap;

public class MemeListItem {
    String name;
    Bitmap image;

    public MemeListItem(String name, Bitmap image){
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage(){
        return image;
    }
}
