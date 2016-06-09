package com.pmpulse.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pmpulse.R;
import com.pmpulse.data.KeyValues;
import com.pmpulse.serviceutil.ConnectionMaker;
import com.pmpulse.serviceutil.Parser;

/**
 * Launcher Activity
 */
public class FeaturesActivity extends AppCompatActivity {

    Button btn_feature_pmfreeaudio, btn_feature_audiotraining, btn_feature_a2ztraining;
    LinearLayout feature_content;
    View feature_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.features);
        TypefaceUtil.overrideFont(FeaturesActivity.this);
        initialisePage();
    }

    private void initialisePage() {

        feature_content = (LinearLayout) findViewById(R.id.feature_content);
        feature_progress = findViewById(R.id.feature_progress);
        btn_feature_audiotraining = (Button) findViewById(R.id.btn_feature_audiotraining);
        btn_feature_pmfreeaudio = (Button) findViewById(R.id.btn_feature_pmfreeaudio);
        btn_feature_a2ztraining = (Button) findViewById(R.id.btn_feature_a2ztraining);

        btn_feature_audiotraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferLogin();
            }
        });

        btn_feature_pmfreeaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionMaker().isConnected(FeaturesActivity.this)) {
                    new FreeAudioTask().execute();
                } else {
                    showAlert(getString(R.string.error_no_net));
                }
            }
        });

        btn_feature_a2ztraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionMaker().isConnected(FeaturesActivity.this)) {
                   transferA2Z();
                } else {
                    showAlert(getString(R.string.error_no_net));
                }
            }
        });
    }

    private void transferFreeAudio() {
        Intent intent = new Intent(FeaturesActivity.this, FreeAudioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KeyValues.KEY_FEATURE_NAME, getString(R.string.nav_freeaudios));
        startActivity(intent);
        finish();
    }

    private void transferLogin() {
        Intent intent = new Intent(FeaturesActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KeyValues.KEY_FEATURE_NAME, getString(R.string.nav_audiotraining));
        startActivity(intent);
        finish();
    }

    private void  transferA2Z(){
        Intent intent = new Intent(FeaturesActivity.this, A2ZLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KeyValues.KEY_FEATURE_NAME, getString(R.string.nav_a2zpm));
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //Shows the progress UI and hides the login form
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(/*final boolean show*/) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        feature_content.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

          /*  feature_content.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            feature_content.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    feature_content.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
                }
            });*/

            // feature_progress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            feature_progress.animate().setDuration(shortAnimTime).alpha(1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    feature_progress.setVisibility(View.VISIBLE);
                    //  feature_progress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else {


            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.

            feature_progress.setVisibility(View.VISIBLE);


            //  feature_progress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            //feature_content.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private void hideProgress() {
        feature_progress.setVisibility(View.INVISIBLE);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class FreeAudioTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String response = new ConnectionMaker().service(KeyValues.urlFreeAudio, ConnectionMaker.METHOD_GET);
                System.out.println("response " + response);
                return response;

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String response) {
            //  showProgress(false);
            hideProgress();
            // feature_content.setVisibility(View.INVISIBLE);
            if (response == null) {
                //timeout or some error
                showAlert(getString(R.string.error_timeout));
            } else {
                String status = new Parser().freeAudioParser(response);
                if (status == null) {
                    showAlert(getString(R.string.error_timeout));
                } else {
                    if (Integer.parseInt(status) > 0) {
                        transferFreeAudio();
                    } else {
                        showAlert(getString(R.string.error_noaudio));
                    }
                }


            }

        }

        @Override
        protected void onCancelled() {
            hideProgress();
        }
    }

    //show alert dialog
    private void showAlert(final String message) {
        feature_content.setVisibility(View.VISIBLE);
        AlertDialog.Builder alert = new AlertDialog.Builder(FeaturesActivity.this);
        alert.setMessage(message);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }
}
