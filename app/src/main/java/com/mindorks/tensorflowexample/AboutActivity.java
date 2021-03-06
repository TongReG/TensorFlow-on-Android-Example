package com.mindorks.tensorflowexample;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

import static com.mindorks.tensorflowexample.MainActivity.activityMgr;

public class AboutActivity extends AppCompatActivity {

    private static final String CustomFontPath = "fonts/Droid_Sans_Mono_for_Powerline.otf";
    private static final String DescriptionOne = "TensorFlow on Android";
    //private Typeface CustomTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        //CustomTypeface = Typeface.createFromAsset(getAssets(), "fonts/.ttf");
        activityMgr.pushActivity(this);

        String verNum = AppInfoUtils.getVersionName(this);
        String versionCode = "Version" + verNum + "based on Mindorks";
        Element versionElement = new Element().setTitle(versionCode);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                //.enableDarkMode(false)
                .setCustomFont(CustomFontPath) // or Typeface
                .setDescription(DescriptionOne)
                .setImage(R.drawable.ic_launcher)
                .addItem(versionElement)
                .addGroup("Contact Us")
                .addEmail("tongrui_tr@163.com")
                .addWebsite("https://www.zhihu.com/people/ren-sheng-ru-lu")
                //.addFacebook("the.medy")
                //.addTwitter("medyo80")
                //.addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addPlayStore("com.mindorks.tensorflowexample")
                .addGitHub("TongReG")
                //.addInstagram("medyo80")
                .create();
        relativeLayout.addView(aboutPage);
    }

    /*  多个Activity同时使用一个menu 只要记住menu是可以叠加的就可以。
        只要在onCreateOptionsMenu方法中加载父类的menu就可以了
        https://www.jianshu.com/p/86866a877d3b
        */
    @Override
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
                return true;
            default:
                return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityMgr.popActivity();
        AboutActivity.this.finish();
    }

}
