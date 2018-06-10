package dreadloaf.com.countryquiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import dreadloaf.com.countryquiz.util.JsonUtil;

public class MainActivity extends AppCompatActivity {
    //TODO: Add a splashscreen to show before this activity is loaded
    /*
    * User will press button specifying the region
    * getRequest will be called to get the data
    * Next activity will be loaded once this is done
    * */

    Button mEuropeButton, mAsiaButton;
    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Remove this when done testing(next two lines)
        //SharedPreferences pref = getSharedPreferences("appData", Context.MODE_PRIVATE);
        //pref.edit().clear().apply();

        mEuropeButton = findViewById(R.id.region_europe);
        mAsiaButton = findViewById(R.id.region_asia);
        mLoadingIndicator = findViewById(R.id.loading_indicator);

        mEuropeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegionSelected(mEuropeButton.getText().toString().toLowerCase());
            }
        });

        mAsiaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegionSelected(mAsiaButton.getText().toString().toLowerCase());
            }
        });

    }

    private void onRegionSelected(String region){
        mLoadingIndicator.setVisibility(View.VISIBLE);

        String fileName = "countries_" + region;
        SharedPreferences sharedPref = getSharedPreferences("appData", Context.MODE_PRIVATE);

        if(!sharedPref.contains(fileName)){
            Log.d("JSON", "JSON not saved yest");
            getRequestForRegion(region);
        }
        else{
            Log.d("JSON", "JSON already saved");
            loadQuizActivity(region);
        }
    }

    private void getRequestForRegion(final String region) {
        String URL = "https://restcountries-v1.p.mashape.com/region/" + region;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, URL,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String fileName = "countries_" + region;
                JsonUtil.saveJson(response.toString(), fileName, MainActivity.this);
                Log.d("JSON", "Saved Json");
                loadQuizActivity(region);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Rest Response", error.toString());
            }
        }) {
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

    private void loadQuizActivity(String region){
        Intent startQuizIntent = new Intent(MainActivity.this, QuizStartActivity.class);
        startQuizIntent.putExtra("region", region);
        startActivity(startQuizIntent);
    }

    
}
