package com.pmpulse.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pmpulse.R;
import com.pmpulse.data.KeyValues;
import com.pmpulse.data.User;
import com.pmpulse.database.DBQuery;
import com.pmpulse.serviceutil.ConnectionMaker;
import com.pmpulse.serviceutil.Parser;

/**
 * A login screen that offers login via email and password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KeyValues.isDebug)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login);
        TypefaceUtil.overrideFont(LoginActivity.this);
        // Set up the login form.
        initialiseView();

        Intent intent = getIntent();
        if (intent != null) {
            getSupportActionBar().setTitle(intent.getStringExtra(KeyValues.KEY_FEATURE_NAME));
        }
    }

    //initalize views
    private void initialiseView() {

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        User user = new User().getCreds(getApplicationContext());
        mEmailView.setText(user.getUserName());
        mPasswordView.setText(user.getUserPassword());

        mEmailView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mPasswordView.requestFocus();
                return false;
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                attemptLogin();
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (isEmailValid(email))
            if (isPasswordValid(password, mPasswordView)) {
                if (new ConnectionMaker().isConnected(LoginActivity.this)) {
                    WifiManager m_wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    String m_wlanMacAdd = m_wm.getConnectionInfo().getMacAddress();
                    m_wlanMacAdd = m_wlanMacAdd.replace(":","");
                    String udid = android.provider.Settings.System.getString(super.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                    mAuthTask = new UserLoginTask(email, password, m_wlanMacAdd);
                    mAuthTask.execute((Void) null);
                } else {
                    showAlert(getString(R.string.error_no_net));
                }
            }
    }

    //check validation for email ids
    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            mEmailView.requestFocus();
            return false;
        } else if (email.startsWith(" ")) {
            mEmailView.requestFocus();
            mEmailView.setError(getString(R.string.error_invalid_email));
            mEmailView.setSelection(email.length());
            return false;
        } else if (email.length() < 5) {
            mEmailView.requestFocus();
            mEmailView.setError("Length should be greater than 4 characters");
            mEmailView.setSelection(email.length());
            return false;
        } else if (!KeyValues.emailAddress.matcher(email).matches()
                && !KeyValues.emailAddressDot.matcher(email).matches()
                && !KeyValues.emailAddressUnderscore.matcher(email).matches()
                && !KeyValues.emailAddressUnderscoreDouble.matcher(email).matches()
                && !KeyValues.emailAddressDotDouble.matcher(email).matches()
                && !KeyValues.emailAddressDouble.matcher(email).matches()) {
            mEmailView.requestFocus();
            mEmailView.setSelection(email.length());
            mEmailView.setError(getString(R.string.error_invalid_email));
            return false;
        }
        return true;
    }

    //check validation for password
    private boolean isPasswordValid(String password, View focusView) {
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView.requestFocus();
            return false;
        } /*else if (password.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView.requestFocus();
            return false;
        }*/
        return true;
    }

    //Shows the progress UI and hides the login form
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(/*final boolean show*/) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        mLoginFormView.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

          /*  mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });*/
            mProgressView.setVisibility(View.VISIBLE);
            // mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(View.VISIBLE);
                    // mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(View.VISIBLE);
            //mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            //  mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void hideProgress() {
        mProgressView.setVisibility(View.GONE);
    }


    private void performDBOperation() {
        DBQuery dbQuery = new DBQuery(this);
        //store training name in db
        for (int countMainTopic = 0; countMainTopic < Parser.topicId.size(); countMainTopic++)
            dbQuery.addTopic(Parser.topic.get(countMainTopic));

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private String udid;

        UserLoginTask(String email, String password, String udid) {
            mEmail = email;
            mPassword = password;
            this.udid = udid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Simulate network access.
                String response = new ConnectionMaker().service(KeyValues.urlLogin + "/" + mEmail + "/" + mPassword+"^"+udid, ConnectionMaker.METHOD_GET);
                if (KeyValues.isDebug)
                    System.out.println("response " + response);
                return response;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String response) {
            mAuthTask = null;
            hideProgress();
            // mProgressView.setVisibility(View.GONE);
            //   mLoginFormView.setVisibility(View.INVISIBLE);
            if (response == null) {
                //timeout or some error
                showAlert(getString(R.string.error_timeout));
            } else {
                String status = new Parser().loginParse(response);
                if (status.equals(Parser.success)) {
                   /* //perform db operation
                    performDBOperation();*/

                    //call next service

                    // mLoginFormView.setVisibility(View.GONE);
                    //store cred
                    new User().storeCreds(mEmail, mPassword, getApplicationContext());
                  KeyValues.udid = udid;
                    //navigate to main
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    //otherwise
                    if (status.length() == 0) status = getString(R.string.error_timeout);
                    showAlert(status);

                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            hideProgress();
            //showProgress();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, FeaturesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    //show alert dialog
    private void showAlert(final String message) {
        String messageForPositiveButton = getString(R.string.ok);

        mLoginFormView.setVisibility(View.VISIBLE);
        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        alert.setMessage(message);
        if (message.equals(getString(R.string.error_logged_in))) {
            messageForPositiveButton = getString(R.string.reset);
            alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
        }
        alert.setPositiveButton(messageForPositiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //already logged in, call logout service
                if (message.equals(getString(R.string.error_logged_in))) {
                    if (new ConnectionMaker().isConnected(LoginActivity.this)) {
                        new UserResetTask().execute();
                    } else {
                        showAlert(getString(R.string.error_no_net));
                    }
                }
            }
        });
        alert.show();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserResetTask extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Simulate network access.
                String response = new ConnectionMaker().service(KeyValues.urlLogout + "/" + Parser.userNumber, ConnectionMaker.METHOD_GET);
                if (KeyValues.isDebug)
                    System.out.println("response " + response);
                return response;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String response) {
            mAuthTask = null;
            hideProgress();
            mLoginFormView.setVisibility(View.VISIBLE);
            if (response == null) {
                //timeout or some error
                showAlert(getString(R.string.error_timeout));
            } else {
                String status = new Parser().logoutParser(response);
                if (!status.equals(Parser.Success)) {
                    showAlert(status);
                    showAlert(status);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            hideProgress();
            mLoginFormView.setVisibility(View.VISIBLE);
        }
    }
}
