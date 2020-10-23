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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";

    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/imagenet_comp_graph_label_strings.txt";

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult;
    private Button btnDetectObject, btnToggleCamera;
    private FloatingActionButton fabRestore;
    private ImageView imageViewResult;
    private CameraView cameraView;
    private ListView listView;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    // List用于存储数据
    private List<Litem> sideList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = findViewById(R.id.cameraView);
        imageViewResult = findViewById(R.id.imageViewResult);
        listView = findViewById(R.id.list_view);

        for (int i = 0; i < 3; i++) {
            sideList.add(new Litem("Image Classify", R.drawable.baseline_image_black_48));
            sideList.add(new Litem("Object Detection", R.drawable.baseline_nature_people_black_48));
            sideList.add(new Litem("Settings", R.drawable.baseline_settings_black_48));
            sideList.add(new Litem("About", R.drawable.baseline_info_black_48));
        }

        ListAdapter list_adapter = new ListAdapter(MainActivity.this, R.layout.list_item, sideList);
        // 把适配器给ListView
        listView.setAdapter(list_adapter);

        // 为ListView注册一个监听器，当用户点击了ListView中的任何一个子项时，就会回调onItemClick()方法
        // 在这个方法中可以通过position参数判断出用户点击的是那一个子项
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Litem pstn = sideList.get(position);
                Toast.makeText(MainActivity.this, pstn.getName(), Toast.LENGTH_SHORT).show();
                if (pstn.getName().equals("About")) {
                    Intent aboutintent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(aboutintent);
                } else if (pstn.getName().equals("Settings")) {
                    Intent Setintent = new Intent(MainActivity.this, Settings.class);
                    startActivity(Setintent);
                }
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


        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();

                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap);

                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

                textViewResult.setText(results.toString());

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        fabRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.stop();
                Toast.makeText(MainActivity.this, "Reset CameraView", Toast.LENGTH_SHORT).show();
                cameraView.start();
            }
        });

        fabRestore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(MainActivity.this, "OnTouchListener", Toast.LENGTH_SHORT).show();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

        initTensorFlowAndLoadModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sidemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent setting_intent = new Intent(this, Settings.class);
                startActivity(setting_intent);
                return true;
            case R.id.menu_share:
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
                fabRestore.setVisibility(View.VISIBLE);
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
}
