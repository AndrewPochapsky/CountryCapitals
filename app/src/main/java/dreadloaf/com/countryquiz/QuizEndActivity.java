package dreadloaf.com.countryquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class QuizEndActivity extends AppCompatActivity {

    TextView mScoreText, mHighscoreText;
    Button mReturnButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_end);

        Log.d("ACTIVITY", "Loaded final activity");

        mScoreText = findViewById(R.id.quiz_end_score);
        mHighscoreText = findViewById(R.id.quiz_end_highscore);
        mReturnButton = findViewById(R.id.return_button);

        String score = "";
        String region = "";
        Intent previousActivityIntent = getIntent();
        if(previousActivityIntent.hasExtra("score")){
            score = previousActivityIntent.getStringExtra("score");
        }
        if(previousActivityIntent.hasExtra("region")){
            region = previousActivityIntent.getStringExtra("region");
        }
        int highscore = determineHighscore(Integer.parseInt(score), region);

        mScoreText.setText(score);
        mHighscoreText.setText(String.valueOf(highscore));

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizEndActivity.this, ChooseRegionActivity.class);
                startActivity(intent);
            }
        });
    }

    private int determineHighscore(int score, String region){
        SharedPreferences shared = getSharedPreferences("appData", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();

        String scoreName = "highscore_" + region;

        int highscore = shared.getInt(scoreName, -1);
        //There is no highscore
        if(highscore == -1){
            editor.putInt(scoreName, score);
            editor.apply();
            return score;
        }else{
            //new highscore
            if(score > highscore){
                editor.putInt(scoreName, score);
                editor.apply();
                return score;
            }
        }
        //Existing highscore remains
        return highscore;

    }
}
