package com.mindorks.tensorflowexample;

public class Litem {
    private static int imageId;
    private static String name;

    Litem(String name, int imageId) {
        Litem.name = name;
        Litem.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    int getImageId() {
        return imageId;
    }
}
