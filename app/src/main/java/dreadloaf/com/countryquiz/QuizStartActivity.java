package dreadloaf.com.countryquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class QuizStartActivity extends AppCompatActivity {

    Country[] mCountries;
    TextView mHeader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_start);

        mHeader = findViewById(R.id.quiz_start_header);

        //Get the region chosen by user
        Intent previousActivityIntent = getIntent();
        String region = null;
        if(previousActivityIntent.hasExtra("region")){
            region = previousActivityIntent.getStringExtra("region");
        }

        String headerText ="Capitals of " + region.substring(0, 1).toUpperCase() + region.substring(1);

        mHeader.setText(headerText);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    //Set up all questions for quiz, put them in a queue, send queue to next activity
    private void onQuizStart(String region){
        mCountries = Country.getCountries(region, this);
    }

}
