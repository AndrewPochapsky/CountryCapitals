package dreadloaf.com.countryquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class QuizStartActivity extends AppCompatActivity {

    Country[] mCountries;
    TextView mHeader;
    Button mStartButton;
    String region;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_start);

        mHeader = findViewById(R.id.quiz_start_header);
        mStartButton = findViewById(R.id.quiz_start_button);

        //Get the region chosen by user
        Intent previousActivityIntent = getIntent();
        if(previousActivityIntent.hasExtra("region")){
            region = previousActivityIntent.getStringExtra("region");
        }

        String headerText ="Capitals of " + region.substring(0, 1).toUpperCase() + region.substring(1);

        mHeader.setText(headerText);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onQuizStart(region);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    //Set up all questions for quiz, put them in a queue, send queue to next activity
    private void onQuizStart(String region){
        Random rand = new Random();
        mCountries = Country.getCountries(region, this);
        shuffleArray(mCountries);
        int max = mCountries.length;
        Queue<Question> questions = new LinkedList<>();
        //TODO: create constant for number of questions
        int numQuestions = 10;
        int numCapitals = 4;
        int current = 0;
        for(int i = 0; i < numQuestions; i++){
            //Key = Capital, Value = Name;
            HashMap<String, String> capitalsMap = new HashMap<>();
            //Set the answer to be the first index in the batch(arbitrary choice, should be random)
            String answer = mCountries[current].getCapital();
            for(int x = 0; x < numCapitals; x++){
                Country currentCountry = mCountries[current];
                capitalsMap.put(currentCountry.getCapital(), currentCountry.getName());
                current++;
            }
            questions.add(new Question(capitalsMap, answer));
        }
        /*
        while(questions.size() > 0){
            Question question = questions.poll();
            String options = "";
            for(String key: question.getCapitals().keySet()){
                options += key + " ";
            }
            Log.d("QUESTION", "Options: " + options + " | Answer: " + question.getCapitals().get(question.getAnswer()));
        }*/

    }

    private void shuffleArray(Country[] array)
    {
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
