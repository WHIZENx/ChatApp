package com.cmu.surrussent.chatapp.app.Activities;

import android.content.Context;
import android.content.SharedPreferences;

public class SherePref {

    SharedPreferences mySharePref;

    public SherePref(Context context) {
        mySharePref = context.getSharedPreferences("filename",Context.MODE_PRIVATE);
    }

    public void  setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = mySharePref.edit();
        editor.putBoolean("NightMode",state);
        editor.commit();
    }

    public Boolean loadNightModeState () {
        Boolean state = mySharePref.getBoolean("NightMode",false);
        return state;
    }
}
