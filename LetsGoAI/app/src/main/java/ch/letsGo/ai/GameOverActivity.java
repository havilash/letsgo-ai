package ch.letsGo.ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;

import java.util.Objects;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtWinTitle;
    TextView txtLoseTitle;
    TextView txtWin;
    TextView txtLose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        //Weist die Elemente aus dem xml, der jeweiligen passenden Variable zu
        txtWinTitle = (TextView) findViewById(R.id.txtWinTitle);
        txtLoseTitle = (TextView) findViewById(R.id.txtLoseTitle);
        txtWin = (TextView) findViewById(R.id.txtWin);
        txtLose = (TextView) findViewById(R.id.txtLose);

        Intent intent = getIntent();

        Boolean extraValue = intent.getBooleanExtra("win",false);
        //Validiert ob der Spieler mit seinem Tipp richtig lag
        if(intent.hasExtra("win") && extraValue.equals(true)){
            txtWinTitle.setVisibility(View.VISIBLE);
            txtWin.setVisibility(View.VISIBLE);
            txtLoseTitle.setVisibility(View.GONE);
            txtLose.setVisibility(View.GONE);
        }
        else if(intent.hasExtra("win") && extraValue.equals(false)){
            txtWinTitle.setVisibility(View.GONE);
            txtWin.setVisibility(View.GONE);
            txtLoseTitle.setVisibility(View.VISIBLE);
            txtLose.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
    }

}