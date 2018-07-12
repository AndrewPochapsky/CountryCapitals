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

    public static Country[] getCountries(String overarchingRegion, Context context){
        if(overarchingRegion == null){
            return null;
        }
        String fileName = "countries_" + overarchingRegion;
        JSONArray countryJson = JsonUtil.getSavedJson(fileName, context);
        int index = 0;
        int length = countryJson.length();
        //This is because I don't use "Hong Kong" and "Macau" from the
        if(overarchingRegion.equals("asia")){
            Log.d("Country", "Region is Asia");
            length -=2;

        }
        Country[] countries = new Country[length];

        for(int i = 0; i < countryJson.length(); i++){
            try {
                JSONObject obj = countryJson.getJSONObject(i);

                String name = obj.getString("name");
                String capital = obj.getString("capital");

                //Do not have valid capitals
                if(name.equals("Hong Kong") || name.equals("Macau")){
                    continue;
                }

                //Fix few errors in the API
                switch (name) {
                    case "Holy See":
                        capital = "Vatican City";
                        break;
                    case "Luxembourg":
                        capital = "Luxembourg City";
                        break;
                    case "Mongolia":
                        capital = "Ulaanbaatar";
                        break;
                }
                countries[index] = new Country(name, capital, overarchingRegion);
                //Log.d("Country", "Adding country with name = " + name + " and capital = " + capital);
                index++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return countries;
    }
}
