package com.mindorks.tensorflowexample;

public class Ritem {
    private int imageId;
    private String name;
    private String state;

    void setAll(String getname, int getimageId, String getDescription){
        name = getname;
        imageId = getimageId;
        state = getDescription;
    }

    public String getName() {
        return name;
    }

    String getState() {
        return name.equals("Return") ? "Return to MainView" : state;
    }

    int getImageId() {
        return imageId;
    }
}
