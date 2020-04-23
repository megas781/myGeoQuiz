package GGhost.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";

    //State Saving Constants
    private static final String CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY";
    private static final String SCORE_COUNT_KEY = "SCORE_COUNT_KEY";
    private static final String IS_CHEATER_KEY = "IS_CHEATER_KEY";
    private static final String HINT_COUNT_KEY = "HINT_COUNT_KEY";

    //Константа, определяющая отношение QuizActivity -> CheatActivity
    private static final int CHEAT_ACTIVITY_REQUEST_CODE = 0;

    private Statement[] mStatementArray = new Statement[]{
            new Statement(R.string.statement_1, false),
            new Statement(R.string.statement_4, true),
            new Statement(R.string.statement_2, false),
            new Statement(R.string.statement_3, true)
    };
    private int mCurrentIndex = 0;
    private int mScoreCount = 0;
    private boolean mIsCheater = false;
    private int mHintCount = 3;
    public int getHintCount() {
        return mHintCount;
    }

    private TextView mStatementTextView;
    private TextView mApiTextView;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //INITIALIZING WIDGETS
        this.mStatementTextView = findViewById(R.id.statement_text_view_id);
        this.mApiTextView = findViewById(R.id.api_level_text_view_id);
        this.mTrueButton = findViewById(R.id.true_button_id);
        this.mFalseButton = findViewById(R.id.false_button_id);
        this.mNextButton = findViewById(R.id.next_button_id);
        this.mCheatButton = findViewById(R.id.cheat_button_id);

        //CHECKING FOR SAVED STATE
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(QuizActivity.CURRENT_INDEX_KEY, 0);
            mScoreCount = savedInstanceState.getInt(QuizActivity.SCORE_COUNT_KEY);
            mIsCheater = savedInstanceState.getBoolean(IS_CHEATER_KEY);
            mHintCount = savedInstanceState.getInt(HINT_COUNT_KEY);
        }

        //SETTING UP INITIAL WIDGET STATES
        updateStatement();
        mApiTextView.setText("API level " + Build.VERSION.SDK_INT);

        System.out.println("onCreate");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(QuizActivity.CURRENT_INDEX_KEY, mCurrentIndex);
        outState.putInt(QuizActivity.SCORE_COUNT_KEY, mScoreCount);
        outState.putInt(QuizActivity.HINT_COUNT_KEY, mHintCount);
        outState.putBoolean(IS_CHEATER_KEY, mIsCheater);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //Back from CheatActivity
            case CHEAT_ACTIVITY_REQUEST_CODE:
                //Handle output from CheatActivity
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        //если данных нет, делать здесь больше нечего
                        return;
                    } else {
                        //Удалось получить данные. ДОСТАЁМ!
                        mIsCheater = CheatActivity.wasAnswerShown(data);
                        if (mIsCheater) {
                            /* Если по возвращении с CheatActivity есть данные, что пользователь использовал
                            позсказку, инкрементируем mCheatCount */
                            mHintCount--;
                        }
                    }
                } else {
                    return;
                }
            default:
                break;
        }

    }




    public void onTrueButtonTapped(View v) {
        checkAnswer(true);
        this.lockButtons();
    }

    public void onFalseButtonTapped(View v) {
        checkAnswer(false);
        this.lockButtons();
    }

    public void onNextButtonTapped(View v) {

//        Log.d(TAG, "NEXT BUTTON TAPPED", new Exception());

        System.out.println("nextButtonTapped");

        if (mCurrentIndex == mStatementArray.length - 1) {
            //that was the last question
            this.lockButtons();
            this.mCheatButton.setVisibility(View.GONE);
            this.mNextButton.setEnabled(false);
            this.mStatementTextView.setText("You got " + mScoreCount + " scores!\nCongratulations!");
        } else {
            mCurrentIndex = (mCurrentIndex + 1) % this.mStatementArray.length;
            this.updateStatement();
            this.unlockButtons();
        }

        //Resign cheater status for next statement
        mIsCheater = false;
    }

    public void onPrevButtonTapped(View v) {
        System.out.println("prevButtontapped");
        if (mCurrentIndex == 0) {
            mCurrentIndex = mStatementArray.length - 1;
        } else {
            mCurrentIndex = (mCurrentIndex - 1) % mStatementArray.length;
        }
        this.updateStatement();
    }

    public void onCheatButtonTapped(View v) {
        //Let's eventually display some other activities
        Intent cheatIntent = CheatActivity.createIntentForQuizActivity(this, this.mStatementArray[mCurrentIndex].getAnswerValue(), getHintCount());
        startActivityForResult(cheatIntent, CHEAT_ACTIVITY_REQUEST_CODE);
    }




    /**
     * Обновляет TextView, опираясь на значение mCurrentIndex.
     */
    protected void updateStatement() {
        this.mStatementTextView.setText((mCurrentIndex + 1) + ") " + getResources().getString(mStatementArray[mCurrentIndex].getTextResId()));
    }

    /**
     * Процедурный метод, проверяющий ответ и выводящий сообщение
     *
     * @param userAnswer Какую кнопку нажал пользователь
     */
    protected void checkAnswer(boolean userAnswer) {
        if (mIsCheater) {
            showMessage(R.string.judgement_toast_text);
        } else {
            boolean result = userAnswer == this.mStatementArray[mCurrentIndex].getAnswerValue();
            if (result) {
                this.mScoreCount++;
            }
            int messageResId = result ? R.string.correct_toast : R.string.incorrect_toast;
            showMessage(messageResId);
        }
    }

    /**
     * Метод для показа сообщений с помощью Тостов
     *
     * @param strResource Индекс ресурса
     */
    protected void showMessage(int strResource) {
        Toast t = Toast.makeText(QuizActivity.this, strResource, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP, 0, 60);
        t.show();
    }

    protected void lockButtons() {
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
    }

    protected void unlockButtons() {
        mTrueButton.setEnabled(true);
        mFalseButton.setEnabled(true);
    }
}
