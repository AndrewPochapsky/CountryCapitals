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
        //This is because I don't use "Hong Kong" and "Macau"
        if(overarchingRegion.equals("asia")){
            length -=2;
        }
        //Don't use "United States Minor Outlying Islands"
        if(overarchingRegion.equals("americas")){
            length--;
        }
        Country[] countries = new Country[length];

        for(int i = 0; i < countryJson.length(); i++){
            try {
                JSONObject obj = countryJson.getJSONObject(i);

                String name = obj.getString("name");
                String capital = obj.getString("capital");

                //Do not have valid capitals
                if(name.equals("Hong Kong") || name.equals("Macau") || name.equals("United States Minor Outlying Islands")){
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
                    case "Democratic Republic of the Congo":
                        //This one is just a name shortener
                        name = "DRC";
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
