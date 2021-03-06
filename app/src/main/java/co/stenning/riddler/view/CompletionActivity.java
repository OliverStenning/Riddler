package co.stenning.riddler.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import co.stenning.riddler.R;

public class CompletionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_completion);

        int score = getIntent().getIntExtra(QuestionActivity.SCORE_EXTRA, 0);
        TextView scoreText = findViewById(R.id.totalScoreText);
        scoreText.setText(getString(R.string.score_label) + " " + Integer.toString(score));
    }

    public void clickContinue(View view) {
        goToMenu();
    }

    @Override
    public void onBackPressed() {
        goToMenu();
    }

    public void goToMenu() {
        Intent intent = new Intent(this,MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
