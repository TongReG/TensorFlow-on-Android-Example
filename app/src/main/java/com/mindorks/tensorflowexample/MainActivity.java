/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mindorks.tensorflowexample;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.CardView;
import android.support.v4.view.MenuItemCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    static final int INPUT_SIZE = 224;
    static final int IMAGE_MEAN = 117;
    static final float IMAGE_STD = 1;
    static final String INPUT_NAME = "input";
    static final String OUTPUT_NAME = "output";

    static String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    static String LABEL_FILE =
            "file:///android_asset/imagenet_comp_graph_label_strings.txt";
    private String rss = "";

    static Classifier classifier;
    static Executor executor = Executors.newSingleThreadExecutor();
    private Executor cached_executor = Executors.newCachedThreadPool();
    private TextView textViewResult;
    private Button btnDetectObject, btnToggleCamera;
    private DragFloatActionButton fabRestore;
    private RoundImageView imageViewResult;
    private CameraView cameraView;
    private RecyclerView sideRecView;
    private Toolbar mToolbar;
    private CardView RsltCard;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    static ActivityMgr activityMgr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.cameraView);
        imageViewResult = findViewById(R.id.imageViewResult);
        RsltCard = findViewById(R.id.ResultCard);
        sideRecView = findViewById(R.id.side_recycler);

        activityMgr = ActivityMgr.getActivityManager();
        activityMgr.pushActivity(MainActivity.this);

        final RecycleAdapter sideRecAdapter = new RecycleAdapter(this);
        sideRecView.setLayoutManager(new LinearLayoutManager(this));
        sideRecView.setHasFixedSize(true);
        sideRecView.setAdapter(sideRecAdapter);
        this.registerForContextMenu(sideRecView);
        sideRecView.setClickable(false);

        MenuItemUtils.init();
        sideRecAdapter.addData(0, MenuItemUtils.imgclsfy);
        sideRecAdapter.addData(1, MenuItemUtils.objdct);
        sideRecAdapter.addData(2, MenuItemUtils.sets);
        sideRecAdapter.addData(3, MenuItemUtils.abt);

        sideRecAdapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Ritem Item = RecycleAdapter.getItem(position);
                Toast.makeText(MainActivity.this, "Click " + position, Toast.LENGTH_SHORT).show();
                switch (Item.getName()) {
                    case "About":
                        Intent AboutActivity = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(AboutActivity);
                        cameraView.stop();
                        break;
                    case "Settings":
                        Intent SetActivity = new Intent(getApplicationContext(), Settings.class);
                        startActivity(SetActivity);
                        cameraView.stop();
                        break;
                    case "Image Classify":
                        String topActivityName = activityMgr.peekActivity().getLocalClassName();
/*                    if (!topActivityName.equals("MainActivity")) {
                        Intent Main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(Main);
                        am.popActivity(am.peekActivity().getClass());
                    }*/
                        Toast.makeText(MainActivity.this, "Now:" + topActivityName, Toast.LENGTH_SHORT).show();
                        break;
                    case "Object Detection":
                        //TODO: Add Object Detection Activity.
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        btnToggleCamera = findViewById(R.id.btnToggleCamera);
        btnDetectObject = findViewById(R.id.btnDetectObject);
        fabRestore = findViewById(R.id.floatingActionButton);

        mToolbar = findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        //mToolbar.setTitle("Rocko"); 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        /* 这些通过ActionBar来设置也是一样的，注意要在setSupportActionBar(toolbar);之后，不然就报错了 */
        // getSupportActionBar().setTitle("标题");
        // getSupportActionBar().setSubtitle("副标题");
        mDrawerLayout = findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);

        sideMenuDrawable();

        //TODO: Deprecate CameraKit API.

        cameraView.setFocus(CameraKit.Constants.FOCUS_TAP_WITH_MARKER);
        cameraView.setFlash(CameraKit.Constants.FLASH_AUTO);
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {
                if (!cameraKitEvent.getMessage().equals("")) {
                    Toast.makeText(MainActivity.this, cameraKitEvent.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(CameraKitError cameraKitError) {
                Exception CameraExc = cameraKitError.getException();
                assert CameraExc != null;
                CameraExc.printStackTrace();
                Toast.makeText(MainActivity.this, CameraExc.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                long t1 = System.currentTimeMillis();

                Bitmap bitmap = cameraKitImage.getBitmap();

                long t2 = System.currentTimeMillis();

                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap);

                long t3 = System.currentTimeMillis();

                final Bitmap finalBitmap = bitmap;
                cached_executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final List<Classifier.Recognition> results = classifier.recognizeImage(finalBitmap);
                        rss = results.toString();
                    }
                });

                textViewResult.setText(rss);

                long t4 = System.currentTimeMillis();

                long captureTime = t2 - t1;
                long execTime = t4 - t3;
                long totalTime = t4 - t1;
                String timestring = "CaptureTime:" + captureTime + " ExecTime:" + execTime + " Total:" + totalTime;

                Toast.makeText(MainActivity.this, timestring, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "CameraviewClick", Toast.LENGTH_SHORT).show();
            }
        });

        RsltCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabRestore.setClickable(false);
                cameraView.toggleFacing();
                fabRestore.setClickable(true);
            }
        });

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabRestore.setClickable(false);
                cameraView.captureImage();
                fabRestore.setClickable(true);
            }
        });

        fabRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDetectObject.setClickable(false);
                btnToggleCamera.setClickable(false);
                cameraView.stop();
                Toast.makeText(MainActivity.this, "Reset CameraView", Toast.LENGTH_SHORT).show();
                cameraView.start();
                btnDetectObject.setClickable(true);
                btnToggleCamera.setClickable(true);
            }
        });

        initTensorFlowAndLoadModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sidemenu, menu);
        MenuItem shareItem = menu.findItem(R.id.share_button);
        // https://blog.csdn.net/j086924/article/details/81233212
        if (shareItem != null) {
            ShareActionProvider SharePd = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
            SharePd.setShareIntent(getDefaultIntent());
            /*if (SharePd == null) {
                menu.removeItem(R.id.share_button);
                //如果没有第3方应用可以直接用，可以添加一个新的菜单项，可以跳转到自己的activity，然后处理等
            }*/
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent setting_intent = new Intent(this, Settings.class);
                startActivity(setting_intent);
                return true;
            case R.id.share_button:
                Toast.makeText(MainActivity.this, "Sharing", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_about:
                Intent about_intent = new Intent(this, AboutActivity.class);
                about_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(about_intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityMgr.popActivity();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDetectObject.setVisibility(View.VISIBLE);
                sideRecView.setClickable(true);
                fabRestore.show();
            }
        });
    }

    private void sideMenuDrawable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mDrawerToggle.syncState();
                mDrawerLayout.setDrawerListener(mDrawerToggle);
            }
        });
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        PackageManager pm = getPackageManager();
        //检查手机上是否存在可以处理这个动作的应用
        List<ResolveInfo> infolist = pm.queryIntentActivities(intent, 0);
        if (!infolist.isEmpty()) {
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_TEXT, rss);
            Toast.makeText(MainActivity.this, "Default sharing type is Image.", Toast.LENGTH_SHORT).show();
        } else {
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, rss);
            Toast.makeText(MainActivity.this, "No Sharing App Found.", Toast.LENGTH_SHORT).show();
        }
        return intent;
    }

    protected static void restoreSideMenuItem(RecycleAdapter adapter) {
        adapter.clearData();
        adapter.addData(0, MenuItemUtils.imgclsfy);
        adapter.addData(1, MenuItemUtils.objdct);
        adapter.addData(2, MenuItemUtils.sets);
        adapter.addData(3, MenuItemUtils.abt);
    }
}
