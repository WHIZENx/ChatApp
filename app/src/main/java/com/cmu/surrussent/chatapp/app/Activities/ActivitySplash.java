package com.cmu.surrussent.chatapp.app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cmu.surrussent.chatapp.R;

public class ActivitySplash extends AppCompatActivity {

    private static int SPLASH_TIME = 3000; //This is 3 seconds

    SherePref sherePref;

    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sherePref = new SherePref(this);

        if (sherePref.loadNightModeState()==true) {
            setTheme(R.style.darktheme);
        }
        else setTheme(R.style.AppTheme);
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            setTheme(Constant.theme);
        }else if (appTheme == 0){
            setTheme(Constant.theme);
        }else{
            setTheme(appTheme);
        }
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

        //Code to start timer and take action after the timer ends
         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 //Do any action here. Now we are moving to next page
                 Intent mySuperIntent = new Intent(ActivitySplash.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 startActivity(mySuperIntent);
                 /* This 'finish()' is for exiting the app when back button pressed
                 *  from Home page which is ActivityHome
                 */
                 finish();
             }
             }, SPLASH_TIME);
    }

}
