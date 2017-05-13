package com.offlinepaymentmethods.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.offlinepaymentmethods.R;

/**
 * Created by aakashsharma on 12/05/17.
 */

public class PreferenceManager {

    private static PreferenceManager prefmanager;
    private String MY_PREF;

    private static final String AVAILABLE_CLIENT_BALANCE = "avlcltbal";

    private static final String AVAILABLE_MERCHANT_BALANCE = "avlmerbal";

    private Context context;

    private PreferenceManager(Context context) {
        this.context = context.getApplicationContext();
        this.MY_PREF = context.getResources().getString(R.string.app_name);
    }

    public static PreferenceManager getInstance(Context context) {
        if (prefmanager == null)
            prefmanager = new PreferenceManager(context);
        return prefmanager;
    }

    public SharedPreferences getPrefernces() {
        return context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
    }

    public void ClearAllPrefernces() {
        getPrefernces().edit().clear().commit();
    }

    public void setClientBalance(Float clientBalance) {

        getPrefernces().edit().putFloat(AVAILABLE_CLIENT_BALANCE, clientBalance).commit();
    }

    public Float getClientBalance() {
        return getPrefernces().getFloat(AVAILABLE_CLIENT_BALANCE, 1000.00f);
    }

    public void setMerchantBalance(Float merchantBalance) {

        getPrefernces().edit().putFloat(AVAILABLE_MERCHANT_BALANCE, merchantBalance).commit();
    }

    public Float getMerchantBalance() {
        return getPrefernces().getFloat(AVAILABLE_MERCHANT_BALANCE, 1000.00f);
    }

}
