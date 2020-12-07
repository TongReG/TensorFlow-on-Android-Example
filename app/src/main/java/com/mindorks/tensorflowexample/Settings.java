package com.mindorks.tensorflowexample;

//import android.os.Build;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import static com.mindorks.tensorflowexample.MainActivity.IMAGE_MEAN;
import static com.mindorks.tensorflowexample.MainActivity.IMAGE_STD;
import static com.mindorks.tensorflowexample.MainActivity.INPUT_NAME;
import static com.mindorks.tensorflowexample.MainActivity.INPUT_SIZE;
import static com.mindorks.tensorflowexample.MainActivity.LABEL_FILE;
import static com.mindorks.tensorflowexample.MainActivity.MODEL_FILE;
import static com.mindorks.tensorflowexample.MainActivity.OUTPUT_NAME;

public class Settings extends AppCompatActivity {

    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final AlertDialog.Builder model_builder = new AlertDialog.Builder(Settings.this);
        model_builder.setTitle(R.string.change_model);
        final String[] items = {"Default (ImageNet_Inception_V1)", "ImageNet_MobileNet_V1_100_224", "EfficientNet_b0"};
        //-1代表没有条目被选中
        model_builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //把选中的条目取出来
                String item = items[which];
                switch (which) {
                    case 0:
                        MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
                        LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";
                        reloadTensorflowModel();
                        break;
                    case 1:
                        MODEL_FILE = "file:///android_asset/tf1_imagenet_mobilenet_v1_100_224_classification.pb";
                        LABEL_FILE = "file:///android_asset/ImageNetLabels.txt";
                        reloadTensorflowModel();
                        break;
                    case 2:
                        MODEL_FILE = "file:///android_asset/tf1_efficientnet_b0_classification.pb";
                        LABEL_FILE = "file:///android_asset/ImageNetLabels.txt";
                        reloadTensorflowModel();
                        break;
                    default:
                        break;
                }
                Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG).show();
                //把对话框关闭
                dialog.dismiss();
            }
        });

        RecyclerView rec = findViewById(R.id.settings_recycler);
        rec.setLayoutManager(new LinearLayoutManager(this));
        // use this setting to improve performance if you know that changes
        // in content do not change the rec_item size of the RecyclerView
        rec.setHasFixedSize(true);
        //rec.addItemDecoration();
        RecycleAdapter rec_adapter = new RecycleAdapter(this);
        rec.setAdapter(rec_adapter);
        this.registerForContextMenu(rec);
        rec_adapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Ritem psn = RecycleAdapter.mData.get(position);
                Toast.makeText(Settings.this, "Click " + position, Toast.LENGTH_SHORT).show();
                if (psn.getName().equals("Return")) {
                    Settings.this.finish();
                } else if (psn.getName().equals("Choose Model")) {
                    model_builder.show();
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });


   /*   RadioGroup mRadioGroup = findViewById(R.id.model_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.Default_R:
                        MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
                        LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";
                        reloadTensorflowModel();
                        break;
                    case R.id.Mobile_R:
                        MODEL_FILE = "file:///android_asset/tf1_imagenet_mobilenet_v1_100_224_classification.pb";
                        LABEL_FILE = "file:///android_asset/ImageNetLabels.txt";
                        reloadTensorflowModel();
                        break;
                    case R.id.Efficient_R:
                        MODEL_FILE = "file:///android_asset/tf1_efficientnet_b0_classification.pb";
                        LABEL_FILE = "file:///android_asset/ImageNetLabels.txt";
                        reloadTensorflowModel();
                        break;
                    default:
                        break;
                }
            }
        });*/

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Settings saved.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void reloadTensorflowModel() {
        MainActivity.classifier.close();
        MainActivity.executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MainActivity.classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }
}
