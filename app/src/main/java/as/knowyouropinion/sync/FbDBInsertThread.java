package as.knowyouropinion.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import as.knowyouropinion.data.QuestionContract;
import as.knowyouropinion.data.QuestionDBHelper;
import as.knowyouropinion.utils.Utility;

/**
 * <p>
 * Created by Angad on 28/1/17.
 * </p>
 */

class FbDBInsertThread extends Thread {
    private ContentValues questionValues;
    private int i;
    private String answer;
    private FirebaseDatabase database;
    private SQLiteDatabase db;
    private Context context;

    FbDBInsertThread(Context context, int i, String answer, FirebaseDatabase database) {
        questionValues = new ContentValues();
        QuestionDBHelper dbHelper = new QuestionDBHelper(context);
        db = dbHelper.getWritableDatabase();
        this.i = i;
        this.answer = answer;
        this.database = database;
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_QNO, i);
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE, (((int) answer.charAt(0) - (int) 'a') + 1));
        final boolean[] ques = {false};
        final boolean[] ans = {false};
        final boolean[] opt = {false};
        DatabaseReference questions = database.getReference("Questions").child(i + "");
        DatabaseReference options = database.getReference("Options").child(i + "");
        DatabaseReference answers = database.getReference("Answers").child(i + "");
        questions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String question = (String) dataSnapshot.getValue();
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_QUES, question);
                ques[0] = true;
                if (opt[0] && ans[0]) {
                    db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues);
                    Utility.updateWidget(context);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        options.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> HM = (HashMap<String, String>) dataSnapshot.getValue();
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS1, HM.get("a"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS2, HM.get("b"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS3, HM.get("c"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS4, HM.get("d"));
                opt[0] = true;
                if (ques[0] && ans[0]) {
                    db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues);
                    Utility.updateWidget(context);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        answers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Long> HM = (HashMap<String, Long>) dataSnapshot.getValue();
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS1V, HM.get("a"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS2V, HM.get("b"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS3V, HM.get("c"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS4V, HM.get("d"));

                ans[0] = true;
                if (ques[0] && opt[0]) {
                    db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues);
                    Utility.updateWidget(context);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

