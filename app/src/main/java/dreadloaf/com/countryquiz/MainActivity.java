package dreadloaf.com.countryquiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import dreadloaf.com.countryquiz.util.JsonUtil;

public class MainActivity extends AppCompatActivity {

    /*
    * User will press button specifying the region
    * getRequest will be called to get the data
    * Next activity will be loaded once this is done
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void getRequest(final String region){
        Log.d("REQUEST", "Request called");
        String URL = "https://restcountries-v1.p.mashape.com/region/" + region;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, URL,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String fileName = "countries_" + region;
                SharedPreferences sharedPref = getSharedPreferences("appData", Context.MODE_PRIVATE);
                if(!sharedPref.contains(fileName)){
                    JsonUtil.saveJson(response.toString(), fileName, MainActivity.this);
                    Log.d("JSON", "Saved Json");
                }else{
                    Log.d("JSON", "Json already saved");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Rest Response", error.toString());
            }
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Mashape-Key", getString(R.string.APIKey));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        requestQueue.add(arrayRequest);
    }

    private void onRegionSelection(String region, Class destination){
        String fileName = "countries_" + region;
        SharedPreferences sharedPref = getSharedPreferences("appData", Context.MODE_PRIVATE);
        if(sharedPref.contains(fileName)){
            Intent startQuizIntent = new Intent(MainActivity.this, destination);
            startActivity(startQuizIntent);
        }else{
            //Get the requested info from api
            getRequest(region);
            //Once response is acquired and saved to sharedPref, then continue
        }
    }

    
}
