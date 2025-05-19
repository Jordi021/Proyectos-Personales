package com.personalprojects.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "ProjectAppSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String userName) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1); // Devuelve -1 si no hay ID
    }
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }


    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}