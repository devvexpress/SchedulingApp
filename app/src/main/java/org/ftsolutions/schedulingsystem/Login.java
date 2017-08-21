package org.ftsolutions.schedulingsystem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.ftsolutions.schedulingsystem.Home.Home;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    String credentialsStore = "SchedulingSystem";
    EditText etUsername, etPassword;

    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref_clear();

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isNetworkAvailable()) {

                    Boolean fieldsValid = true;

                    if (etUsername.getText().toString().equals("")) {
                        etUsername.setError("Username is required");
                        fieldsValid = false;
                    }

                    if (etPassword.getText().toString().equals("")) {
                        etPassword.setError("Password is required");
                        fieldsValid = false;
                    }

                    if (fieldsValid) {
                        LoginDetails obj = new LoginDetails();
                        obj.username = etUsername.getText().toString();
                        obj.password = etPassword.getText().toString();

                        TelephonyManager tManager = (TelephonyManager)Login.this.getSystemService(Context.TELEPHONY_SERVICE);
                        assert tManager != null;
                        @SuppressLint({"MissingPermission", "HardwareIds"}) String uid = tManager.getDeviceId();
                        Log.d("ID", uid);

                        String ua=new WebView(Login.this).getSettings().getUserAgentString();
                        Log.d("AGENT: ", ua);

                        new downloadFromAPI(obj).execute();
                    }

                } else {

                    alertDialog = new AlertDialog.Builder(Login.this).create();
                    alertDialog.setTitle(getApplicationContext().getResources().getString(R.string.app_name));
                    alertDialog.setMessage("Internet connection is required!");
                    alertDialog.show();
                }
            }
        });

    }

    class downloadFromAPI extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        AlertDialog alertDialog;

        LoginDetails loginDetails = null;

        public downloadFromAPI(LoginDetails loginDetails) {
            this.loginDetails = loginDetails;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Login.this, getApplicationContext().getResources().getString(R.string.app_name), "Logging in...", true);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Util util = new Util();

            String url = "http://18.220.154.15/SchedulingSystem/";

            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("username", loginDetails.username);
            hashMap.put("password", loginDetails.password);

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .serializeNulls()
                    .create();
            String param = gson.toJson(hashMap);

            String jsonString = util.callApi(url, param, 1);

            LoginDetails obj = new Gson().fromJson(jsonString, LoginDetails.class);

            if (obj.status.equals("200")) {


                sharedPref_save(obj.FirstName + " " + obj.LastName, obj.active);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            if (sharedPref_check()) {
                if (sharedPref_getAccountStatus()==1) {

                    Toast.makeText(Login.this, "Welcome", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Login.this, Home.class);
                    startActivity(i);

                } else {

                    alertDialog = new AlertDialog.Builder(Login.this).create();
                    alertDialog.setTitle(getApplicationContext().getResources().getString(R.string.app_name));
                    alertDialog.setMessage("Your account is disabled. Contact the Administrator for more information.");
                    alertDialog.show();

                }
            } else {
                Toast.makeText(Login.this, "Incorrect credentials", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void sharedPref_save(String username, int acctStatus) {
        SharedPreferences sharedPref = getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);
        SharedPreferences.Editor append = sharedPref.edit();
        append.putBoolean("isLoggedIn", true);
        append.putString("name", username);
        append.putInt("status", acctStatus);
        append.commit();

    }

    private Boolean sharedPref_check() {
        SharedPreferences sharedPref = getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);
        SharedPreferences.Editor append = sharedPref.edit();
        Boolean loggedIn = sharedPref.getBoolean("isLoggedIn", false);
        return loggedIn;
    }

    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void sharedPref_clear() {
        SharedPreferences sharedPref = getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);
        SharedPreferences.Editor append = sharedPref.edit();
        append.putBoolean("isLoggedIn", false);
        append.putString("Username", null);
        append.commit();
    }

    private int sharedPref_getAccountStatus() {
        SharedPreferences sharedPref = getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);
        SharedPreferences.Editor append = sharedPref.edit();
        int status = sharedPref.getInt("status", 0);
        return status;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


