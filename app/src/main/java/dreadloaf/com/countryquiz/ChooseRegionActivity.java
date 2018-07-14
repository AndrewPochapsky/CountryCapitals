package dreadloaf.com.countryquiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import dreadloaf.com.countryquiz.util.AudioUtil;
import dreadloaf.com.countryquiz.util.JsonUtil;

public class ChooseRegionActivity extends AppCompatActivity implements View.OnClickListener{
    //TODO: Add a splashscreen to show before this activity is loaded
    /*
    * User will press button specifying the region
    * getRequest will be called to get the data
    * Next activity will be loaded once this is done
    * */

    Button mBackButton;
    ProgressBar mLoadingIndicator;
    GridLayout mRegionButtons;
    //This boolean is used to determine when the music should be paused
    //if the value is true, then the user has pressed a button and therefore the music should continue
    //if the value is false, then the user has exited another way and music should pause
    boolean mPressedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_region);

        mBackButton = findViewById(R.id.choose_region_back);
        mRegionButtons = findViewById(R.id.choose_region_buttons);
        mLoadingIndicator = findViewById(R.id.loading_indicator);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPressedButton = true;
                Intent intent = new Intent(ChooseRegionActivity.this, MainMenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        for(int i = 0; i < mRegionButtons.getChildCount(); i++){
            Button button = (Button)mRegionButtons.getChildAt(i);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPressedButton = true;
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioUtil.resumeMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!mPressedButton)
            AudioUtil.pauseMusic();
    }

    private void onRegionSelected(String region){
        mLoadingIndicator.setVisibility(View.VISIBLE);

        String fileName = "countries_" + region;
        SharedPreferences sharedPref = getSharedPreferences("appData", Context.MODE_PRIVATE);

        if(!sharedPref.contains(fileName)){
            getRequestForRegion(region);
        }
        else{
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
                JsonUtil.saveJson(response.toString(), fileName, ChooseRegionActivity.this);
                loadQuizActivity(region);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChooseRegionActivity.this, "Error with retrieving country information", Toast.LENGTH_SHORT).show();
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
        Intent startQuizIntent = new Intent(ChooseRegionActivity.this, QuizStartActivity.class);
        startQuizIntent.putExtra("region", region);
        startActivity(startQuizIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    public void onClick(View view) {
        mPressedButton = true;
        Button button = (Button)view;
        onRegionSelected(button.getText().toString().toLowerCase());
    }
}
