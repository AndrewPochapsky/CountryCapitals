package dreadloaf.com.countryquiz;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dreadloaf.com.countryquiz.util.JsonUtil;

public class Country {

    private String name;
    private String capital;
    private String region;

    public Country(String name, String capital, String region){
        this.name = name;
        this.capital = capital;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public String getCapital() {
        return capital;
    }

    public String getRegion() {
        return region;
    }

    public static Country[] getCountries(String region, Context context){
        if(region == null){
            Log.e("getCountries", "entered region is null");
            return null;
        }
        String fileName = "countries_" + region;
        JSONArray countryJson = JsonUtil.getSavedJson(fileName, context);

        Country[] countries = new Country[countryJson.length()];

        for(int i = 0; i < countryJson.length(); i++){
            try {
                JSONObject obj = countryJson.getJSONObject(i);
                countries[i] = new Country(obj.getString("name"), obj.getString("capital"), region);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return countries;
    }
}
