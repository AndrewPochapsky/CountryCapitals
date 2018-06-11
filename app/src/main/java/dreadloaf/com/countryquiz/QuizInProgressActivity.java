package dreadloaf.com.countryquiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class QuizInProgressActivity extends AppCompatActivity implements View.OnClickListener{
    
    String mRegion;
    Queue<Question> mQuestions;
    TextView mQuestionTextView;
    GridLayout mButtonGrid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_inprogress);

        mQuestionTextView = findViewById(R.id.quiz_inprogress_header);
        mButtonGrid = findViewById(R.id.quiz_button_grid);

        //Get the region chosen by user
        Intent previousActivityIntent = getIntent();
        if(previousActivityIntent.hasExtra("region")){
            mRegion = previousActivityIntent.getStringExtra("region");
        }
        mQuestions = setupQuestions();

        for(int i = 0; i < mButtonGrid.getChildCount(); i++){
            Button button = (Button)mButtonGrid.getChildAt(i);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        Button button = (Button)view;
        Log.d("BUTTON", "Clicked " + button.getText().toString());
    }

    private Queue<Question> setupQuestions(){
        int numQuestions = 10;
        int numCapitals = 4;
        int current = 0;

        Country[] countries = Country.getCountries(mRegion, this);
        shuffleArray(countries);
        Queue<Question> questions = new LinkedList<>();
        for(int i = 0; i < numQuestions; i++){
            //Key = Capital, Value = Name;
            HashMap<String, String> capitalsMap = new HashMap<>();

            //Set the answer to be the first index in the batch(arbitrary choice, should be random)
            String answer = countries[current].getCapital();
            for(int x = 0; x < numCapitals; x++){
                Country currentCountry = countries[current];
                capitalsMap.put(currentCountry.getCapital(), currentCountry.getName());
                current++;
            }
            questions.add(new Question(capitalsMap, answer));
        }
        return questions;
    }


    private void shuffleArray(Country[] array) {
        if(array == null){
            Log.e("SHUFFLE", "Specified array to shuffle is null");
            return;
        }
        int index;
        Country temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

}
