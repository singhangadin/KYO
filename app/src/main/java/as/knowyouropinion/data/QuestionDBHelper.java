package as.knowyouropinion.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import as.knowyouropinion.data.QuestionContract.QuestionEntry;

/**<p>
 * Created by Angad on 23/1/17.
 * </p>
 */

public class QuestionDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "questions.db";

    public QuestionDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_QUESTION_TABLE = "CREATE TABLE " + QuestionEntry.TABLE_NAME + " (" +
                QuestionEntry._ID + " INTEGER PRIMARY KEY," +
                QuestionEntry.COLUMN_QNO + " INTEGER UNIQUE NOT NULL, " +
                QuestionEntry.COLUMN_QUES+ " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_ANS1+ " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_ANS2+ " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_ANS3+ " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_ANS4+ " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_ANS1V + " INTEGER NOT NULL, " +
                QuestionEntry.COLUMN_ANS2V + " INTEGER NOT NULL, " +
                QuestionEntry.COLUMN_ANS3V + " INTEGER NOT NULL, " +
                QuestionEntry.COLUMN_ANS4V + " INTEGER NOT NULL, " +
                QuestionEntry.COLUMN_CHOICE + " INTEGER NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_QUESTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuestionEntry.TABLE_NAME);
        onCreate(db);
    }
}
