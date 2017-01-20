package com.pmpulse.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.KeyValues;
import com.pmpulse.serviceutil.Parser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shradha on 8/1/17.
 */

public class PackageActivity extends AppCompatActivity {

    ImageView packageImage;
    ListView lv_package;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_package);
        TypefaceUtil.overrideFont(PackageActivity.this);

        // Set up the package view.
        initialiseView();
    }

    private void initialiseView() {

        lv_package = (ListView) findViewById(R.id.lv_package);
        PackageAdapter adapter = new PackageAdapter(this);
        lv_package.setAdapter(adapter);

        //  packageImage = (ImageView) findViewById(R.id.packageImage);
//        Picasso.with(this).load("http://dhwani.pm-pulse.com/PackageImages/ba4467b3-7229-4d58-8d8c-8401d414cf74.jpg").into(packageImage);

    }

    class PackageAdapter extends BaseAdapter {

        List<String> data;
        Context context;

        public PackageAdapter(Context context) {
            //  this.data = data;
            data = Parser.topic;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return data.hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_package, null);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
            return convertView;
        }
    }
}
