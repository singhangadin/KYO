package as.knowyouropinion.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static java.lang.Long.parseLong;

/**<p>
 * Created by Angad on 23/1/17.
 * </p>
 */

public class QuestionContract {
    public static final String CONTENT_AUTHORITY = "as.knowyouropinion";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_QUESTION = "question";

    public static final class QuestionEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUESTION).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUESTION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUESTION;

        public static final String TABLE_NAME = "question";

        public static final String COLUMN_QNO = "question_no";

        public static final String COLUMN_QUES = "question";

        public static final String COLUMN_ANS1 = "ans1";
        public static final String COLUMN_ANS2 = "ans2";
        public static final String COLUMN_ANS3 = "ans3";
        public static final String COLUMN_ANS4 = "ans4";

        public static final String COLUMN_ANS1V = "ans1v";
        public static final String COLUMN_ANS2V = "ans2v";
        public static final String COLUMN_ANS3V = "ans3v";
        public static final String COLUMN_ANS4V = "ans4v";

        public static final String COLUMN_CHOICE = "choice";

        public static Uri buildQuestionUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildQuestionNo(String quesNo) {
            return CONTENT_URI.buildUpon().appendPath(quesNo).build();
        }

        public static int getQuestionNoFromURI(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

}
