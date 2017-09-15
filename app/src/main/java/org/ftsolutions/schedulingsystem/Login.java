package org.ftsolutions.schedulingsystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.ftsolutions.schedulingsystem.ActiveAndroidClasses.LoadList_AA;
import org.ftsolutions.schedulingsystem.ActiveAndroidClasses.SubjectList_AA;
import org.ftsolutions.schedulingsystem.GsonModels.LoadList;
import org.ftsolutions.schedulingsystem.GsonModels.LoginDetails;
import org.ftsolutions.schedulingsystem.GsonModels.SubjectList;
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

        etUsername = (MaterialEditText) findViewById(R.id.etUsername);
        etPassword = (MaterialEditText) findViewById(R.id.etPassword);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (NetworkAvailable()) {

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

                        /*TelephonyManager tManager = (TelephonyManager) Login.this.getSystemService(Context.TELEPHONY_SERVICE);
                        assert tManager != null;
                        @SuppressLint({"MissingPermission", "HardwareIds"}) String uid = tManager.getDeviceId();
                        Log.d("ID", uid);

                        String ua = new WebView(Login.this).getSettings().getUserAgentString();
                        Log.d("AGENT: ", ua);*/

                        new loginAsync(obj).execute();
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

    //region Async Login

    class loginAsync extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        AlertDialog alertDialog;

        LoginDetails loginDetails = null;

        public loginAsync(LoginDetails loginDetails) {
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

            String url = "http://13.113.232.165/SchedulingSystem/";


            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("username", loginDetails.username);
            hashMap.put("password", loginDetails.password);

            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();
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
                if (sharedPref_getAccountStatus() == 1) {

                    Toast.makeText(Login.this, "Welcome", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Login.this, Home.class);
                    startActivity(i);
                    new getData().execute();

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
    //endregion

    //region Async Load

    class LoadAsync extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            Util util=new Util();

            String url="http://13.113.232.165/SchedulingSystem/getLoad.php";

            String jsonString=util.callApi(url, "{\"userId\":"+sharedPref_getUserId()+"}", 1);

            LoadList[] gsonObj=new Gson().fromJson(jsonString, LoadList[].class);

            if(!jsonString.equals("")){

                LoadList_AA obj=new LoadList_AA();

                for(int i=0;i<gsonObj.length;i++){
                    obj.classcode=gsonObj[i].classcode;
                    obj.subjectDesc=gsonObj[i].subjectDesc;
                    obj.days=gsonObj[i].days;
                    obj.timeFrom=gsonObj[i].timeFrom;
                    obj.timeTo=gsonObj[i].timeTo;

                    obj.insert(obj);
                }

            }

            return null;
        }
    }
    //endregion
    
    //region Async SubjectList
    
    class subjectListAsync extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            Util util=new Util();

            String url="http://13.113.232.165/SchedulingSystem/getSubjects.php";

            String jsonString=util.callApi(url, null, 0);

            SubjectList[] gsonObj=new Gson().fromJson(jsonString, SubjectList[].class);

            SubjectList_AA obj=new SubjectList_AA();

            for (int i=0;i<gsonObj.length;i++){

                obj.classcode=gsonObj[i].classcode;
                obj.subjDesc=gsonObj[i].subjDesc;
                obj.unit=gsonObj[i].unit;

                obj.insert(obj);
            }
            
            
            return null;
        }
    }
    
    //endregion

    //region Async getData

    class getData extends AsyncTask<Void, Void, Void>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Login.this, getApplicationContext().getResources().getString(R.string.app_name), "Downloading data from server...", true);

        }

        @Override
        protected Void doInBackground(Void... voids) {

            //new LoadAsync().execute();
            new subjectListAsync().execute();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    //endregion

    //region Methods

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
        append.putInt("userId", 0);
        append.commit();
    }

    private int sharedPref_getAccountStatus() {
        SharedPreferences sharedPref = getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);
        SharedPreferences.Editor append = sharedPref.edit();
        int status = sharedPref.getInt("status", 0);
        return status;
    }

    private boolean NetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private int sharedPref_getUserId(){
        SharedPreferences sharedPref = getSharedPreferences(credentialsStore, Context.MODE_PRIVATE);
        SharedPreferences.Editor append = sharedPref.edit();
        int userId = sharedPref.getInt("userId", 0);
        return userId;
    }

    //endregion me

}


