package as.knowyouropinion.model;

import android.graphics.Color;

/**<p>
 * Created by Angad on 25/1/17.
 * </p>
 */

public class HomeQuestionData
{   private String question, imgUrl;
    private int quesNo;
    private long peeps;
    private int color;

    public HomeQuestionData() {
        this.color = Color.parseColor("#616161");
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public long getPeeps() {
        return peeps;
    }

    public void setPeeps(long peeps) {
        this.peeps = peeps;
    }

    public int getQuesNo() {
        return quesNo;
    }

    public void setQuesNo(int quesNo) {
        this.quesNo = quesNo;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
