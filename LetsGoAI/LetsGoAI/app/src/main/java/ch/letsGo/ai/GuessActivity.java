package ch.letsGo.ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class GuessActivity extends AppCompatActivity {

    boolean isHuman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        Intent intent = getIntent();
        isHuman = intent.getBooleanExtra("isHuman", false);
    }

    public void onHumanButtonClick(View view) {
        Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
        intent.putExtra("win", isHuman);
        startActivity(intent);
    }

    public void onAIButtonClick(View view) {
        Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
        intent.putExtra("win", !isHuman);
        startActivity(intent);
    }
}