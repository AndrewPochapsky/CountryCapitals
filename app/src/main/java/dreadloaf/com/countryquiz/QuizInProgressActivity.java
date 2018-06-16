package dreadloaf.com.countryquiz;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class QuizInProgressActivity extends AppCompatActivity implements View.OnClickListener{

    String mRegion;
    Queue<Question> mQuestions;
    TextView mQuestionTextView, mProgressTextView;
    LinearLayout mFirstRow, mSecondRow;
    ProgressBar mTimer;
    ObjectAnimator mAnimation;
    Question mCurrentQuestion;
    Button[] mButtons;
    int mNumCorrect;
    int mProgress;
    boolean mFinished = false;

    final int mMaxTimerValue = 100;
    final int mNumQuestions = 10;
    final int mNumCapitals = 4;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_inprogress);

        mQuestionTextView = findViewById(R.id.quiz_inprogress_header);
        mFirstRow = findViewById(R.id.first_button_row);
        mSecondRow = findViewById(R.id.second_button_row);
        mTimer = findViewById(R.id.timer);
        mProgressTextView = findViewById(R.id.progress_text);
        mButtons = new Button[mSecondRow.getChildCount() + mFirstRow.getChildCount()];

        // Get the Drawable custom_progressbar
        Drawable draw=getResources().getDrawable(R.drawable.custom_progressbar);
        // set the drawable as progress drawable
        mTimer.setProgressDrawable(draw);

        //Get the region chosen by user
        Intent previousActivityIntent = getIntent();
        if(previousActivityIntent.hasExtra("region")){
            mRegion = previousActivityIntent.getStringExtra("region");
        }
        mQuestions = setupQuestions();
        Log.d("QUIZ", "There are " + mQuestions.size() + " Questions");
        int index = 0;
        for(int i = 0; i < mFirstRow.getChildCount(); i++){
            Button button = (Button)mFirstRow.getChildAt(i);
            mButtons[index] = button;
            button.setOnClickListener(this);
            index++;
        }
        for(int i = 0; i < mSecondRow.getChildCount(); i++){
            Button button = (Button)mSecondRow.getChildAt(i);
            mButtons[index] = button;
            button.setOnClickListener(this);
            index++;
        }
        mNumCorrect = 0;
        mProgress = 0;
        setupAnimation();
        setupNextQuestion();
        updateProgressText();
        mAnimation.start();
        Log.d("QUIZ", "OnCreate");
    }

    @Override
    public void onClick(View view) {
        Button button = (Button)view;
        Log.d("BUTTON", "Clicked " + button.getText().toString());
        if(mCurrentQuestion.isCorrectAnswer(button.getText().toString())){
            //Answer is right
            mNumCorrect++;
        }
        else{
            //Answer is wrong, do something
        }
        updateProgressText();
        setupNextQuestion();
        mAnimation.start();
    }

    private Queue<Question> setupQuestions(){
        int current = 0;

        Country[] countries = Country.getCountries(mRegion, this);
        shuffleArray(countries);
        Queue<Question> questions = new LinkedList<>();
        for(int i = 0; i < mNumQuestions; i++){
            //Key = Capital, Value = Name;
            HashMap<String, String> capitalsMap = new HashMap<>();

            //Set the answer to be the first index in the batch(arbitrary choice, should be random)
            String answer = countries[current].getCapital();
            for(int x = 0; x < mNumCapitals; x++){
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

    private void setupAnimation(){
        //Is multiplied by 100 to ensure for a smooth animation, going up by 1 makes it look choppy
        mTimer.setMax(mMaxTimerValue*100);

        mAnimation = ObjectAnimator.ofInt(mTimer, "progress", mTimer.getProgress(), mMaxTimerValue * 100);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setDuration(5000);
        mAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!mFinished){
                    //if this happens, then the user has not clicked any option in time
                    //start next question and mark this one is wrong
                    updateProgressText();
                    setupNextQuestion();
                    Log.d("Animation", "Animation mFinished");
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void setupNextQuestion(){
        Question nextQuestion = mQuestions.poll();
        Log.d("QUIZ", "Setting up queestion");
        if(nextQuestion ==  null){
            Log.d("QUIZ", "ENDING");
            //End of quiz
            Intent intent = new Intent(QuizInProgressActivity.this, QuizEndActivity.class);
            String score = mNumCorrect + "/" + mNumQuestions;
            intent.putExtra("score", score);
            mFinished = true;
            startActivity(intent);
        }
        else{
            Log.d("Animation", "There is another question indeed");
            mAnimation.start();
            mCurrentQuestion = nextQuestion;
            String[] capitals = mCurrentQuestion.getCapitals();
            for(int i = 0; i < capitals.length; i++){
                mButtons[i].setText(capitals[i]);
            }
            String prefix = "What is the capital of ";
            String suffix = mCurrentQuestion.getAnswerCountry() + "?";
            String text = prefix + suffix;
            mQuestionTextView.setText(text, TextView.BufferType.SPANNABLE);

            Spannable s = (Spannable)mQuestionTextView.getText();
            int start = prefix.length();
            int end = text.length() -1;
            s.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void updateProgressText(){
        if(mProgress < mNumQuestions){
            mProgress++;
            String text = mProgress + "/" + mNumQuestions;
            mProgressTextView.setText(text);
        }
    }
}
