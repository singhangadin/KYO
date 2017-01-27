package as.knowyouropinion;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import as.knowyouropinion.data.QuestionContract;
import as.knowyouropinion.data.QuestionDBHelper;

import static as.knowyouropinion.data.QuestionContract.QuestionEntry.COLUMN_ANS1;
import static as.knowyouropinion.data.QuestionContract.QuestionEntry.COLUMN_ANS1V;
import static as.knowyouropinion.data.QuestionContract.QuestionEntry.COLUMN_ANS2;
import static as.knowyouropinion.data.QuestionContract.QuestionEntry.COLUMN_ANS2V;
import static as.knowyouropinion.data.QuestionContract.QuestionEntry.COLUMN_ANS3;
import static as.knowyouropinion.data.QuestionContract.QuestionEntry.COLUMN_ANS3V;
import static as.knowyouropinion.data.QuestionContract.QuestionEntry.COLUMN_ANS4;
import static as.knowyouropinion.data.QuestionContract.QuestionEntry.COLUMN_ANS4V;
import static as.knowyouropinion.data.QuestionContract.QuestionEntry.COLUMN_CHOICE;
import static as.knowyouropinion.data.QuestionContract.QuestionEntry.COLUMN_QNO;
import static as.knowyouropinion.data.QuestionContract.QuestionEntry.COLUMN_QUES;

/**<p>
 * Created by localhost on 25/1/17.
 * </p>
 */

public class QuizActivity extends AppCompatActivity {
    private FloatingActionButton markFab;
    private RadioButton option;
    private RadioGroup options;
    private AppCompatRadioButton A, B, C, D;
    private AppCompatTextView Ques;
    private String question;
    private String Sa = "",Sb = "",Sc = "",Sd = "";
    private long Ia,Ib,Ic,Id, total = 0;
    private boolean answered=false;
    private int qno;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        qno = getIntent().getExtras().getInt("qno");

        Ques = (AppCompatTextView)findViewById(R.id.question);
        A = (AppCompatRadioButton)findViewById(R.id.A);
        B = (AppCompatRadioButton)findViewById(R.id.B);
        C = (AppCompatRadioButton)findViewById(R.id.C);
        D = (AppCompatRadioButton)findViewById(R.id.D);

        markFab = (FloatingActionButton) findViewById(R.id.markFab);
        markFab.hide();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("QuizActivity");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        options = (RadioGroup) findViewById(R.id.options);
        options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                markFab.show();
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference answer = database.getReference("Answers").child(qno+"");
        DatabaseReference questn = database.getReference("Questions").child(qno+"");
        DatabaseReference optn = database.getReference("Options").child(qno+"");
        DatabaseReference image = database.getReference("Images").child(qno+"");

        markFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!answered) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(QuizActivity.this);
                    String email = preferences.getString("EMAIL","Email");
                    String userID = email.split("@")[0].replace(".",",");
                    DatabaseReference user = database.getReference("Users").child(userID);
                    int id = options.getCheckedRadioButtonId();
                    option = (RadioButton) findViewById(id);
                    Toast.makeText(getBaseContext(), option.getText().toString(), Toast.LENGTH_SHORT).show();
                    answered = true;
                    switch (id) {
                        case R.id.A:
                            incrementCounter(answer.child("a"),1);
                            user.child(qno+"").setValue("a");
                            break;

                        case R.id.B:
                            incrementCounter(answer.child("b"),2);
                            user.child(qno+"").setValue("b");
                            break;

                        case R.id.C:
                            incrementCounter(answer.child("c"),3);
                            user.child(qno+"").setValue("c");
                            break;

                        case R.id.D:
                            incrementCounter(answer.child("d"),4);
                            user.child(qno+"").setValue("d");
                            break;
                    }

                    markFab.hide();

                }
                else
                {
                }
            }
        });

        questn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                question = dataSnapshot.getValue().toString();
                Ques.setText(question);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        optn.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> HM = (HashMap<String, String>) dataSnapshot.getValue();
                Sa = HM.get("a");
                Sb = HM.get("b");
                Sc = HM.get("c");
                Sd = HM.get("d");
                A.setText(Sa+(answered?" "+((Ia*100)/total)+"%":""));
                B.setText(Sb+(answered?" "+((Ib*100)/total)+"%":""));
                C.setText(Sc+(answered?" "+((Ic*100)/total)+"%":""));
                D.setText(Sd+(answered?" "+((Id*100)/total)+"%":""));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        answer.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Long> HM = (HashMap<String, Long>) dataSnapshot.getValue();
                Ia = HM.get("a");
                Ib = HM.get("b");
                Ic = HM.get("c");
                Id = HM.get("d");
                total = Ia + Ib + Ic + Id;
                if(total == 0)
                {   total = 1;
                }
                A.setText(Sa+(answered?" "+((Ia*100)/total)+"%":""));
                B.setText(Sb+(answered?" "+((Ib*100)/total)+"%":""));
                C.setText(Sc+(answered?" "+((Ic*100)/total)+"%":""));
                D.setText(Sd+(answered?" "+((Id*100)/total)+"%":""));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void incrementCounter(DatabaseReference reference, final int option) {
        reference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.d("TAG","Firebase counter increment failed.");
                }
                else {
                    ContentValues questionValues = new ContentValues();
                    questionValues.put(COLUMN_QNO, qno);
                    questionValues.put(COLUMN_QUES, question);
                    questionValues.put(COLUMN_ANS1, Sa);
                    questionValues.put(COLUMN_ANS2, Sb);
                    questionValues.put(COLUMN_ANS3, Sc);
                    questionValues.put(COLUMN_ANS4, Sd);
                    questionValues.put(COLUMN_ANS1V, Ia);
                    questionValues.put(COLUMN_ANS2V, Ib);
                    questionValues.put(COLUMN_ANS3V, Ic);
                    questionValues.put(COLUMN_ANS4V, Id);
                    questionValues.put(COLUMN_CHOICE, (option));

                    Cursor cursor = getContentResolver().query(
                            QuestionContract.QuestionEntry.buildQuestionNo(Integer.toString(qno)),
                            new String[]{COLUMN_QNO},
                            null,
                            null,
                            null);
                    if(cursor!=null&&cursor.getCount()==0)
                    {   QuestionDBHelper dbHelper = new QuestionDBHelper(QuizActivity.this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues);

                    }
                    else
                    {   getContentResolver().update(
                            QuestionContract.QuestionEntry.buildQuestionNo(qno+""),
                            questionValues,
                            null,
                            null);
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    Log.d("TAG","Firebase counter increment succeeded.");
                }
            }
        });
    }
}
