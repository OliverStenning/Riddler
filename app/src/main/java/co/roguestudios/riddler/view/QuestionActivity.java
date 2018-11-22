package co.roguestudios.riddler.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.muddzdev.styleabletoast.StyleableToast;

import co.roguestudios.riddler.R;
import co.roguestudios.riddler.classes.Player;
import co.roguestudios.riddler.classes.Question;

public class QuestionActivity extends AppCompatActivity {

    private static final int QUESTIONS_BETWEEN_ADS = 1;

    private QuestionModel questionModel;

    private ProgressBar questionProgress;
    private TextView questionProgressText;
    private TextView questionText;
    private Button skipButton;
    private Button hintButton;
    private EditText answerInput;
    private TextView hintText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_question);

        questionProgress = findViewById(R.id.questionProgress);
        questionProgressText = findViewById(R.id.questionProgressText);
        questionText = findViewById(R.id.questionText);
        skipButton = findViewById(R.id.skipButton);
        hintButton = findViewById(R.id.hintButton);
        answerInput = findViewById(R.id.answerInput);
        hintText = findViewById(R.id.hintText);


        //Setup view model
        questionModel = ViewModelProviders.of(this).get(QuestionModel.class);
        questionModel.loadPlayer(this);
        questionModel.loadQuestions(this);

        final Context context = this;
        final Observer<Player> playerObserver = player -> {
            if (player.getPosition() > questionModel.getQuestions().size()) {
                startActivity(new Intent(context, CorrectActivity.class));
            } else {
                updateUI(player);
            }
        };
        questionModel.getPlayer().observe(this, playerObserver);

        //Override keyboard enter method
        answerInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) clickEnter(null);
            return true;
        });

    }

    public void clickBack(View view) {
        finish();
    }
    public void clickSkip(View view) {
        SkipDialog skipDialog = new SkipDialog();
        skipDialog.show(getSupportFragmentManager(), "SkipDialogFragment");

        skipDialog.setSkipDialogListener(dialog -> {
            //TODO watch ad and reward player
        });

    }
    public void clickHint(View view) {
        hideKeyboard();
        Player player = questionModel.getPlayer().getValue();
        if (player.getHints() > 0) {
            if (player.getQuestionHintsUsed() < questionModel.getQuestions().get(player.getPosition()).getHint().length) {
                questionModel.useHint();
            } else {
                StyleableToast.makeText(this, getString(R.string.hints_expended), R.style.infoToast).show();
            }
        } else {
            HintDialog hintDialog = new HintDialog();
            hintDialog.show(getSupportFragmentManager(), "HintDialogFragment");

            hintDialog.setHintDialogListener(dialog -> {
                //TODO watch and and reward player
            });

        }
    }
    public void clickEnter(View view) {
        hideKeyboard();
        //TODO make checks more generous
        if (!questionModel.guess(answerInput.getText().toString())) {
            //Answer is wrong
            answerInput.setAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
        } else {
            //Answer is correct
            answerInput.setText("");
            startActivity(new Intent(this, CorrectActivity.class));
            overridePendingTransition(0, 0);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(answerInput.getWindowToken(), 0);
    }
    private void updateUI(Player player) {

        Question question = questionModel.getQuestions().get(player.getPosition());

        questionProgress.setProgress(player.getPosition());
        questionProgress.setMax(questionModel.getQuestions().size());
        questionProgressText.setText((player.getPosition() + 1) + "/" + questionModel.getQuestions().size());

        questionText.setText(question.getQuestion());
        hintButton.setText(this.getString(R.string.hints_button) + "  " + player.getHints());

        String hints = "";
        for (int i = 0; i < player.getQuestionHintsUsed(); i++) {
            hints += question.getHint()[i] + "\n" + "\n";
        }
        hintText.setText(hints);

    }

    @Override
    public void onPause() {
        questionModel.savePlayer(this);
        super.onPause();
    }

}
