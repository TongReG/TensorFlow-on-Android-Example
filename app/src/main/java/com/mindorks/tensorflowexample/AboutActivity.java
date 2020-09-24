package com.mindorks.tensorflowexample;

//import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    private static final String CustomFontPath = "font/sourcecodepro_regular_12.ttf";
    private static final String DescriptionOne = "DescriptionTest";
    private static final String VersionCode = "Version 1.1";
    //private Typeface CustomTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        //CustomTypeface = Typeface.createFromAsset(getAssets(), "fonts/.ttf");
        Element versionElement = new Element().setTitle(VersionCode);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
/*        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                //.enableDarkMode(false)
                .setCustomFont(CustomFontPath) // or Typeface
                .setDescription(DescriptionOne)
                .setImage(R.mipmap.ic_launcher)
                //.addItem(versionElement)
                .addGroup("Connect Me")
                .addEmail("tongrui_tr@163.com")
                .addWebsite("https://www.zhihu.com/people/ren-sheng-ru-lu")
                //.addFacebook("the.medy")
                //.addTwitter("medyo80")
                //.addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addPlayStore("com.mindorks.tensorflowexample")
                .addGitHub("zerofreeze")
                //.addInstagram("medyo80")
                .create();
        relativeLayout.addView(aboutPage);
    }

    /*  多个Activity同时使用一个menu 只要记住menu是可以叠加的就可以。
        只要在onCreateOptionsMenu方法中加载父类的menu就可以了
        https://www.jianshu.com/p/86866a877d3b
        */
/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);//调用这一句保证父类的菜单项可以正常加载
        getMenuInflater().inflate(R.menu.sidemenu, menu);//加载子类自己的菜单项
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:

        }
        return true;
    }*/
}
