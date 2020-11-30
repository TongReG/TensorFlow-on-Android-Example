package com.mindorks.tensorflowexample;

//import android.os.Build;

import android.os.Bundle;
//import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView rec = findViewById(R.id.settings_recycler);
        rec.setLayoutManager(new LinearLayoutManager(this));
        // use this setting to improve performance if you know that changes
        // in content do not change the rec_item size of the RecyclerView
        rec.setHasFixedSize(true);
        //rec.addItemDecoration();
        RecycleAdapter rec_adapter = new RecycleAdapter(this);
        rec.setAdapter(rec_adapter);
        rec_adapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Ritem psn = RecycleAdapter.mData.get(position);
                Toast.makeText(Settings.this, "Click " + position, Toast.LENGTH_SHORT).show();
                if (psn.getName().equals("Return")) {
                    Settings.this.finish();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Settings saved.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
