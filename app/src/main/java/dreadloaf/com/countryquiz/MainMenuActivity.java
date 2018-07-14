package dreadloaf.com.countryquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import dreadloaf.com.countryquiz.util.AudioUtil;

public class MainMenuActivity extends AppCompatActivity {

    Button mStartButton;
    //This boolean is used to determine when the music should be paused
    //if the value is true, then the user has pressed a button and therefore the music should continue
    //if the value is false, then the user has exited another way and music should pause
    boolean mPressedButton = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mStartButton = findViewById(R.id.main_menu_start);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPressedButton = true;
                Intent intent = new Intent(MainMenuActivity.this, ChooseRegionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        AudioUtil.playMusic(this, AudioUtil.menuMusic);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPressedButton = false;
        AudioUtil.resumeMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!mPressedButton)
            AudioUtil.pauseMusic();
    }
}
