package dreadloaf.com.countryquiz.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

public class JsonUtil {

    public static void saveJson(String jsonString, String fileName, Context context){
        SharedPreferences.Editor prefEditor = context.getSharedPreferences( "appData", Context.MODE_PRIVATE ).edit();
        prefEditor.putString(fileName, jsonString);
        prefEditor.apply();
    }

    public static JSONArray getSavedJson(String name, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("appData", Context.MODE_PRIVATE);

        if(!sharedPref.contains(name)){
            Log.d("JSON", name + " is not in sharedPrefs");
        }

        String strJson = sharedPref.getString(name,"0");
        JSONArray response = null;
        try {
            response = new JSONArray(strJson);
        } catch (JSONException e) {
            Log.e("JSON", e.toString());
        }
        return response;
    }
}
