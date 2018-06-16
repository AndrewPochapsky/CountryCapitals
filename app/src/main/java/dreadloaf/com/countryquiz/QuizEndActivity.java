package dreadloaf.com.countryquiz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class QuizEndActivity extends AppCompatActivity {

    TextView mHeader;
    Button mReturnButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_end);

        Log.d("ACTIVITY", "Loaded final activity");

        mHeader = findViewById(R.id.quiz_end_header);
        mReturnButton = findViewById(R.id.return_button);

        String score = "";

        Intent previousActivityIntent = getIntent();
        if(previousActivityIntent.hasExtra("score")){
            score = previousActivityIntent.getStringExtra("score");
        }

        String text = "You got " + score + " correct!";
        mHeader.setText(text);

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizEndActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
