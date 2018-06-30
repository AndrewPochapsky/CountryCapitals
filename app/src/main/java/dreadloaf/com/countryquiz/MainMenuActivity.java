package dreadloaf.com.countryquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    Button mStartButton, mOptionsButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mStartButton = findViewById(R.id.main_menu_start);
        mOptionsButton = findViewById(R.id.main_menu_options);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, ChooseRegionActivity.class);
                startActivity(intent);
            }
        });
    }


}
