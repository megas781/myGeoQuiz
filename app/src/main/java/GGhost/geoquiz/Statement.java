package GGhost.geoquiz;

public class Statement {

    private int mTextResId;

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    private boolean mAnswerValue;
    public boolean getAnswerValue() {
        return mAnswerValue;
    }
    public void setAnswerValue(boolean answerValue) {
        mAnswerValue = answerValue;
    }

    public Statement(int textResId, boolean answerValue) {
        this.mTextResId = textResId;
        this.mAnswerValue = answerValue;
    }
}
