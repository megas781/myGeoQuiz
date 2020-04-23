package GGhost.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private TextView mInfoTextView;
    private TextView mAnswerTextView;
    private TextView mHintCountTextView;
    private Button mShowAnswerButton;

    private static final String EXTRA_ANSWER_VALUE_KEY = "EXTRA_ANSWER_VALUE_KEY";
    private static final String EXTRA_ANSWER_SHOWN_KEY = "EXTRA_ANSWER_SHOWN_KEY";
    private static final String EXTRA_HINT_COUNT_KEY = "EXTRA_HINT_COUNT_KEY";
    private static final String EXTRA_FIRST_VISITED_KEY = "EXTRA_FIRST_VISITED_KEY";

    private boolean mAnswerValue;
    private boolean mWasAnswerShown = false;

    private int mHintCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mInfoTextView = findViewById(R.id.info_text_view_id);
        mAnswerTextView = findViewById(R.id.answer_text_view_id);
        mShowAnswerButton = findViewById(R.id.show_answer_button_id);
        mHintCountTextView = findViewById(R.id.hint_count_text_view_id);

        mAnswerValue = getIntent().getBooleanExtra(EXTRA_ANSWER_VALUE_KEY,false);
        mHintCount = getIntent().getIntExtra(EXTRA_HINT_COUNT_KEY, 0);

        //Если bundle не пустой, значит там есть значение для
        if (savedInstanceState != null) {
            mWasAnswerShown = savedInstanceState.getBoolean(EXTRA_ANSWER_SHOWN_KEY);
            mHintCount = savedInstanceState.getInt(EXTRA_HINT_COUNT_KEY, 0);
        }

        mHintCountTextView.setText(getResources().getString(R.string.hint_count_text_view_label) +": " + mHintCount);

        if (mHintCount <= 0) {
            if (!mWasAnswerShown) {
                //Здесь мы должны указать пользователю его место у параши
                mInfoTextView.setText(R.string.you_used_all_hints_label);
                mShowAnswerButton.setEnabled(false);
            }
        }
        if (mWasAnswerShown) {
            //Показать ответ
            mAnswerTextView.setText(this.mAnswerValue ? R.string.true_button : R.string.false_button);
            mAnswerTextView.setVisibility(View.VISIBLE);
            mShowAnswerButton.setVisibility(View.INVISIBLE);
            //Не забываем восстановить Result для QuizActivity
            this.setAnswerShownResult(mWasAnswerShown);
        }
    }

    /**
     * Метод, вызываемый при повороте. Здесь нужно сохранить состояние в bundle'e outState
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_ANSWER_SHOWN_KEY, mWasAnswerShown);
    }


    public void showAnswerButtonTapped(View v) {
        mAnswerTextView.setText(this.mAnswerValue ? R.string.true_button : R.string.false_button);
        mAnswerTextView.setVisibility(View.VISIBLE);
        mWasAnswerShown = true;
        this.setAnswerShownResult(mWasAnswerShown);

        //Отключаем кнопку после нажатия
        v.setEnabled(false);

        //Не забываем обновлять отображение количества hint'ов
        mHintCount--;

        mHintCountTextView.setText(getResources().getString(R.string.hint_count_text_view_label) +": " + mHintCount);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = mShowAnswerButton.getWidth() / 2;
            int cy = mShowAnswerButton.getHeight() / 2;
            float radius = mShowAnswerButton.getWidth();

            Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();
        }
    }


    /**
     * Метод для установки Result'a для QuizAcitivity
     * @param isAnswerShown
     */
    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent responseData = new Intent();
        responseData.putExtra(EXTRA_ANSWER_SHOWN_KEY, isAnswerShown);
        setResult(RESULT_OK,responseData);
    }


    /**
     * External use only
     * @param context
     * @param answerValue
     * @return
     */
    public static Intent createIntentForQuizActivity(Context context, Boolean answerValue, int hintCount) {
        Intent i = new Intent(context, CheatActivity.class);
        i.putExtra(EXTRA_ANSWER_VALUE_KEY, answerValue);
        i.putExtra(EXTRA_HINT_COUNT_KEY, hintCount);
        i.putExtra(EXTRA_FIRST_VISITED_KEY, true);
        return i;
    }
    /**
     * This static method is only external use for this activity's parent.
     * @param backIntent Intent, gotten by parent activity from CheatActivity
     * @return Answer
     */
    public static boolean wasAnswerShown(Intent backIntent) {
        boolean wasAnswerShown = backIntent.getBooleanExtra(EXTRA_ANSWER_SHOWN_KEY, false);
        return wasAnswerShown;
    }
}
