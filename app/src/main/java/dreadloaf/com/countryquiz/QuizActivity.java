package dreadloaf.com.countryquiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class QuizActivity extends AppCompatActivity {

    Country[] mCountries;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent previousActivityIntent = getIntent();
        String region = null;
        if(previousActivityIntent.hasExtra("region")){
            region = previousActivityIntent.getStringExtra("region");
        }

        mCountries = Country.getCountries(region, this);

    }


}
