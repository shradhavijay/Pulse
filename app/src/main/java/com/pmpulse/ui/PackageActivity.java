package com.pmpulse.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.ChapterAudio;
import com.pmpulse.data.KeyValues;
import com.pmpulse.data.Package;
import com.pmpulse.serviceutil.ConnectionMaker;
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

    private void transferToMain() {
        Intent intent = new Intent(PackageActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // intent.putExtra(KeyValues.KEY_FEATURE_NAME, getString(R.string.nav_freeaudios));
        startActivity(intent);
        finish();
    }

    class PackageAdapter extends BaseAdapter {

        List<Package> data = Parser.packageList;
        Context context;

        public PackageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position).getPackageName();
        }

        @Override
        public long getItemId(int position) {
            return data.hashCode();
        }

        private class ViewHolder {
            TextView trainingName;
            TextView trainingDays;
            TextView trainingPrice;
            ImageView packageImage;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_package, null);
                holder = new ViewHolder();
                holder.trainingName = (TextView) convertView.findViewById(R.id.trainingName);
                holder.trainingDays = (TextView) convertView.findViewById(R.id.trainingDays);
                holder.trainingPrice = (TextView) convertView.findViewById(R.id.trainingPrice);
                holder.packageImage = (ImageView) convertView.findViewById(R.id.packageImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.trainingName.setText(data.get(position).getPackageName());
            holder.trainingDays.setText(data.get(position).getTotalDays());
            holder.trainingPrice.setText(data.get(position).getPrice());
            Log.d("TAG", "data.get(position).getImagePath().trim() " + data.get(position).getImagePath().trim() + "trim");
            Picasso.with(getApplicationContext()).load(data.get(position).getImagePath().trim()).into(holder.packageImage);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GetTopics(Integer.parseInt(data.get(position).getPackageId())).execute();
                }
            });
            return convertView;
        }
    }

    //web service call for getting topics in package
    private class GetTopics extends AsyncTask<Void, Void, Void> {
        int packageId;

        public GetTopics(int packageId) {
            this.packageId = packageId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showProgress();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //get value from db
            try {
                // Simulate network access.
                String response = new ConnectionMaker().service(KeyValues.urlGetCategoriesandOtherDetails + "/" + Parser.userNumber + "/" + packageId, ConnectionMaker.METHOD_GET);
                if (KeyValues.isDebug)
                    System.out.println("response " + response);
                //parse topics
                Parser parser = new Parser();
                parser.topicParser(response);

            } catch (Exception e) {
                if (KeyValues.isDebug)
                    System.out.println("exception in  GetTopics" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // hideProgress();
            transferToMain();
        }
    }
}

