package dreadloaf.com.countryquiz;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import dreadloaf.com.countryquiz.util.AudioUtil;

public class QuizInProgressActivity extends AppCompatActivity implements View.OnClickListener{
    //TODO: pause anim on exit as the app keeps running
    String mRegion;
    Queue<Question> mQuestions;
    TextView mQuestionTextView, mProgressTextView, mScoreTextView;
    LinearLayout mFirstRow, mSecondRow;
    ProgressBar mTimerProgressBar;
    ObjectAnimator mAnimation;
    Question mCurrentQuestion;
    Button[] mButtons;
    Button mPressedButton;

    int mNumCorrect;
    int mScore;
    int mProgress;
    boolean mFinished = false;
    boolean mRestPeriod = false;
    boolean mIncorrectAnswer = false;
    int mButtonDefaultColor;

    final int mMaxTimerValue = 100;
    final int mNumQuestions = 10;
    final int mNumCapitals = 4;
    final int mMaxQuestionDuration = 10000;
    final int mEndAnimationDuration = 500;
    final int mRightAnswerDisplayDuration = 250;
    final int mBaseScore = 100;
    final Handler timerHandler = new Handler();

    //This is what gets executed after the "rest period" between questions
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            mPressedButton.getBackground().setColorFilter(mButtonDefaultColor, PorterDuff.Mode.MULTIPLY);
            updateProgressText();
            setupNextQuestion();
            mRestPeriod = false;
            mIncorrectAnswer = false;
        }
    };
    
    Runnable outOfTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgressText();
            setupNextQuestion();
            mRestPeriod = false;
        }
    };
    
    class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat -1f);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_inprogress);

        mQuestionTextView = findViewById(R.id.quiz_inprogress_header);
        mFirstRow = findViewById(R.id.first_button_row);
        mSecondRow = findViewById(R.id.second_button_row);
        mTimerProgressBar = findViewById(R.id.timer);
        mProgressTextView = findViewById(R.id.progress_text);
        mScoreTextView = findViewById(R.id.quiz_score_text);
        mButtonDefaultColor = ContextCompat.getColor(this, R.color.colorQuizButtonDefault);

        mButtons = new Button[mSecondRow.getChildCount() + mFirstRow.getChildCount()];

        // Get the Drawable custom_progressbar
        Drawable draw=getResources().getDrawable(R.drawable.custom_progressbar);
        // set the drawable as progress drawable
        mTimerProgressBar.setProgressDrawable(draw);

        //Get the region chosen by user
        Intent previousActivityIntent = getIntent();
        if(previousActivityIntent.hasExtra("region")){
            mRegion = previousActivityIntent.getStringExtra("region");
        }
        mQuestions = setupQuestions();
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
        setupTimerAnimation();
        setupNextQuestion();
        updateProgressText();
        mScoreTextView.setText(String.valueOf(mScore));
        mAnimation.start();
        AudioUtil.stopMusic();
        AudioUtil.playMusic(this, AudioUtil.quizMusic);
    }

    @Override
    public void onClick(View view) {
        //Prevents the user from clicking an additional answer to a question
        if(!mRestPeriod){
            int colorTo;
            final int colorFrom = mButtonDefaultColor;
            mPressedButton = (Button)view;
            //Answer is right
            if(mCurrentQuestion.isCorrectAnswer(mPressedButton.getText().toString())){
                AudioUtil.playSound(this, R.raw.correct_sound);
                mNumCorrect++;
                colorTo = Color.GREEN;
                //update score
                calculateScore(mTimerProgressBar.getProgress());
                mScoreTextView.setText(String.valueOf(mScore));
            }
            //answer is wrong
            else{
                AudioUtil.playSound(this, R.raw.incorrect_sound);
                colorTo = Color.RED;
                mIncorrectAnswer = true;
            }
            ValueAnimator clickedButtonAnim = colorAnimation(mPressedButton, colorFrom, colorTo, mEndAnimationDuration, 0);
            mRestPeriod = true;
            clickedButtonAnim.start();
            clickedButtonAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) { }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if(mIncorrectAnswer){
                        Button rightAnswerButton = findRightAnswerButton();
                        int delay = mRightAnswerDisplayDuration * 2;
                        colorAnimation(rightAnswerButton, mButtonDefaultColor, Color.GREEN, mRightAnswerDisplayDuration, 1).start();
                        timerHandler.postDelayed(timerRunnable, delay);
                    }
                    else{
                        timerHandler.postDelayed(timerRunnable, 0);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) { }

                @Override
                public void onAnimationRepeat(Animator animator) { }
            });
            //Wait
            mAnimation.pause();

        }
    }

    private Button findRightAnswerButton() {
        for(Button button : mButtons){
            if(mCurrentQuestion.isCorrectAnswer(button.getText().toString())){
                return button;
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFinished = true;
        AudioUtil.stopMusic();
        AudioUtil.playMusic(this, AudioUtil.menuMusic);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFinished = true;
        finish();
    }


    private ValueAnimator colorAnimation(View view, int colorFrom, int colorTo, int duration, int repeatCount){
        ValueAnimator animation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        animation.setDuration(duration);
        animation.setRepeatCount(repeatCount);
        animation.setInterpolator(new ReverseInterpolator());
        final View _view = view;
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                _view.getBackground().setColorFilter((int) valueAnimator.getAnimatedValue(), PorterDuff.Mode.MULTIPLY);
            }
        });
       return animation;

    }

    private void calculateScore(int timePassed) {
        timePassed /= 100;
        //10 is subtracted to make the score more attuned to the progress
        mScore += (mBaseScore - (timePassed - 10));
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

    private void setupTimerAnimation(){
        //Is multiplied by 100 to ensure for a smooth animation, going up by 1 makes it look choppy
        mTimerProgressBar.setMax(mMaxTimerValue*100);
        mAnimation = ObjectAnimator.ofInt(mTimerProgressBar, "progress", 0, mMaxTimerValue * 100);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setDuration(mMaxQuestionDuration);
        mAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!mFinished){
                    //On android 7 the animation reset when finishes so setupTimerAnimation() has to be called again
                    setupTimerAnimation();
                    //if this happens, then the user has not clicked any option in time
                    //start next question and mark this one is wrong
                    mRestPeriod = true;
                    colorAnimation(findRightAnswerButton(), mButtonDefaultColor, Color.GREEN, mRightAnswerDisplayDuration, 1).start();
                    timerHandler.postDelayed(outOfTimeRunnable, mRightAnswerDisplayDuration * 2);

                }
            }

            @Override
            public void onAnimationCancel(Animator animator) { }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        });
    }

    private void setupNextQuestion(){
        Question nextQuestion = mQuestions.poll();
        if(nextQuestion ==  null){
            //End of quiz
            Intent intent = new Intent(QuizInProgressActivity.this, QuizEndActivity.class);
            //String score = mNumCorrect + "/" + mNumQuestions;
            intent.putExtra("score", String.valueOf(mScore));
            intent.putExtra("region", mRegion);
            mFinished = true;
            AudioUtil.stopMusic();
            startActivity(intent);
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        }
        else{
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
