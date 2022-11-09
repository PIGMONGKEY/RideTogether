package kr.co.shinhan.www.Login;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class AutoLoginSharedPreferenceManager {

    private static final String PREFERENCE_NAME = "autoLoginInformation";

    public static SharedPreferences getPreference(Context mContext) {
        return mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static void setAutoLoginInformation(Context mContext, String pn, String pw) {
        SharedPreferences sp = getPreference(mContext);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("pn", pn);
        editor.putString("pw", pw);
        editor.commit();
    }

    public static Map<String, String> getAutoLoginInformation(Context mContext) {
        SharedPreferences sp = getPreference(mContext);
        Map<String, String> autoLoginInfo = new HashMap<>();

        autoLoginInfo.put("pn", (String) sp.getString("pn", ""));
        autoLoginInfo.put("pw", (String) sp.getString("pw", ""));

        return autoLoginInfo;
    }

    public static void clearAutoLoginInformation(Context mContext) {
        SharedPreferences sp = getPreference(mContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}
