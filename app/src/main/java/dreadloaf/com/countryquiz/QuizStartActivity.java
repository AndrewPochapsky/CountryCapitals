package dreadloaf.com.countryquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class QuizStartActivity extends AppCompatActivity {

    TextView mHeader;
    Button mStartButton;
    String mRegion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_start);

        mHeader = findViewById(R.id.quiz_start_header);
        mStartButton = findViewById(R.id.quiz_start_button);

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
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    //Set up all questions for quiz, put them in a queue, send queue to next activity
    private void onQuizStart(){
        Intent intent = new Intent(QuizStartActivity.this, QuizInProgressActivity.class);

        intent.putExtra("region", mRegion);
        startActivity(intent);
    }


}


