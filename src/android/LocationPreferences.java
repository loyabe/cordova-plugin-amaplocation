package com.mobishift.cordova.plugins.amaplocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Gamma on 15/8/21
 */
public class LocationPreferences {
    private static final String TAG = "LocationPreferences";
    private static final String PREFERENCE_NAME = "LocationPreferences";
    private static final String KEY = "locations";
    private static final int MAX_LENGTH = 30;
    private static LocationPreferences locationPreferences = null;

    private SharedPreferences sharedPreferences;

    public static boolean isBackground = false;

    private LocationPreferences(Context context){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
    }

    public static LocationPreferences getLocationPreferences(Context context){
        if(locationPreferences == null){
            locationPreferences = new LocationPreferences(context);
        }
        return locationPreferences;
    }

    public JSONArray getLocations(){
        String jsonArrayString = sharedPreferences.getString("locations", "[]");
        JSONArray array;
        try{
            array = new JSONArray(jsonArrayString);
        }catch (JSONException ex){
            array = new JSONArray();
            Log.e(TAG, ex.getMessage());
        }
        return array;
    }

    public void setLocation(JSONArray jsonArray){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, jsonArray.toString());
        editor.apply();
    }

    public void clearLocations(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void putLocation(com.amap.api.location.AMapLocation aMapLocation){
        JSONArray array = getLocations();
        JSONArray result;

        LocationObject locationObject = new LocationObject(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        array.put(locationObject);
        if(array.length() > MAX_LENGTH){
            result = new JSONArray();
            for(int i = 1; i < array.length(); i++){
                try{
                    result.put(array.get(i));
                }catch (JSONException ex){
                    Log.e(TAG, ex.getMessage());
                }
            }
        }else{
            result = array;
        }
        setLocation(result);
    }


    public class LocationObject {
        private double latitude;
        private double longitude;
        private String createdAt;
        private boolean isBackground;

        public LocationObject(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.isBackground = LocationPreferences.isBackground;
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            createdAt = simpleDateFormat.format(date);
        }
    }
}
