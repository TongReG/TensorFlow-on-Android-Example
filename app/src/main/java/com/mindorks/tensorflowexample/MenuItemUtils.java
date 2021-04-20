package com.mindorks.tensorflowexample;

class MenuItemUtils {

    static final int sideItemCount = 4;
    static final int settingsItemCount = 4;

    static Ritem imgclsfy = new Ritem();
    static Ritem objdct = new Ritem();
    static Ritem sets = new Ritem();
    static Ritem abt = new Ritem();

    static Ritem chmodel = new Ritem();
    static Ritem chout = new Ritem();
    static Ritem mthread = new Ritem();
    static Ritem ret = new Ritem();

    static void init() {

        imgclsfy.setAll("Image Classify", R.drawable.baseline_image_black_48, "Recognize Photo and show the type of object.");
        objdct.setAll("Object Detection", R.drawable.baseline_nature_people_black_48, "Recognize several types of object continuously in a period.");
        sets.setAll("Settings", R.drawable.baseline_settings_black_48, "Set different modes and change function.");
        abt.setAll("About", R.drawable.baseline_info_black_48, "About this application.");

        chmodel.setAll("Choose Model", R.drawable.baseline_input_black_36, "Change the model of Image Classify.");
        chout.setAll("Change Output", R.drawable.baseline_logout_black_36, "Change the output type.");
        mthread.setAll("Multi-threading", R.drawable.baseline_speed_black_36, "Choose what cores you want to use in the app.");
        ret.setAll("Return", R.drawable.baseline_arrow_back_black_36, "");

    }

}
