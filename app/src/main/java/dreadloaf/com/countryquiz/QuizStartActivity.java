package dreadloaf.com.countryquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dreadloaf.com.countryquiz.util.AudioUtil;

public class QuizStartActivity extends AppCompatActivity {

    TextView mHeader;
    Button mStartButton, mBackButton;
    String mRegion;
    //This boolean is used to determine when the music should be paused
    //if the value is true, then the user has pressed a button and therefore the music should continue
    //if the value is false, then the user has exited another way and music should pause
    boolean mPressedButton = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_start);

        mHeader = findViewById(R.id.quiz_start_header);
        mStartButton = findViewById(R.id.quiz_start_button);
        mBackButton = findViewById(R.id.quiz_start_back);

        //Get the region chosen by user
        Intent previousActivityIntent = getIntent();
        if(previousActivityIntent.hasExtra("region")){
            mRegion = previousActivityIntent.getStringExtra("region");
        }

        String headerText ="Capitals of " + mRegion.substring(0, 1).toUpperCase() + mRegion.substring(1);

        mHeader.setText(headerText);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onQuizStart();
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPressedButton = true;
                startActivity(new Intent(QuizStartActivity.this, ChooseRegionActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    @Override
    public void onBackPressed() {
        mPressedButton = true;
        startActivity(new Intent(QuizStartActivity.this, ChooseRegionActivity.class));
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

    //Set up all questions for quiz, put them in a queue, send queue to next activity
    private void onQuizStart(){
        mPressedButton = true;
        Intent intent = new Intent(QuizStartActivity.this, QuizInProgressActivity.class);
        AudioUtil.stopMusic();
        intent.putExtra("region", mRegion);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }
}


