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

import dreadloaf.com.countryquiz.util.AudioUtil;

public class QuizEndActivity extends AppCompatActivity {

    TextView mScoreText, mHighscoreText;
    Button mReturnButton, mPlayAgainButton;
    String mRegion;
    //This boolean is used to determine when the music should be paused
    //if the value is true, then the user has pressed a button and therefore the music should continue
    //if the value is false, then the user has exited another way and music should pause
    boolean mPressedButton = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_end);

        mScoreText = findViewById(R.id.quiz_end_score);
        mHighscoreText = findViewById(R.id.quiz_end_highscore);
        mReturnButton = findViewById(R.id.return_button);
        mPlayAgainButton = findViewById(R.id.play_again_button);

        String score = "";
        Intent previousActivityIntent = getIntent();
        if(previousActivityIntent.hasExtra("score")){
            score = previousActivityIntent.getStringExtra("score");
        }
        if(previousActivityIntent.hasExtra("region")){
            mRegion = previousActivityIntent.getStringExtra("region");
        }
        int highscore = determineHighscore(Integer.parseInt(score), mRegion);

        mScoreText.setText(score);
        mHighscoreText.setText(String.valueOf(highscore));

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPressedButton = true;
                Intent intent = new Intent(QuizEndActivity.this, MainMenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mPlayAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPressedButton = true;
                Intent intent = new Intent(QuizEndActivity.this, QuizInProgressActivity.class);
                intent.putExtra("region", mRegion);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
            }
        });

        AudioUtil.playMusic(this, AudioUtil.menuMusic);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioUtil.resumeMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mPressedButton)
            AudioUtil.pauseMusic();
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
