package as.knowyouropinion.data.model;

/**<p>
 * Created by localhost on 25/1/17.
 * </p>
 */

public class HomeQuestionData
{   private String question, imgUrl;
    private int quesNo;
    private long peeps;

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
}
