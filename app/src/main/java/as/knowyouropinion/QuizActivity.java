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

import com.github.angads25.graphs.BarGraphView;
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
import as.knowyouropinion.utils.Utility;

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

/**
 * <p>
 * Created by localhost on 25/1/17.
 * </p>
 */

public class QuizActivity extends AppCompatActivity {
    private FloatingActionButton markFab;
    private RadioButton option;
    private RadioGroup options;
    private AppCompatRadioButton a, b, c, d;
    private AppCompatTextView ques;
    private String question;
    private String sA = "", sB = "", sC = "", sD = "";
    private long iA, iB, iC, iD, total = 0;
    private boolean answered = false;
    private int qno;
    private BarGraphView barGraphView;
    private int choice = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        barGraphView = (BarGraphView) findViewById(R.id.barGraph);
        qno = getIntent().getExtras().getInt("qno");

        ques = (AppCompatTextView) findViewById(R.id.question);
        a = (AppCompatRadioButton) findViewById(R.id.A);
        b = (AppCompatRadioButton) findViewById(R.id.B);
        c = (AppCompatRadioButton) findViewById(R.id.C);
        d = (AppCompatRadioButton) findViewById(R.id.D);

        markFab = (FloatingActionButton) findViewById(R.id.markFab);
        markFab.hide();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.label_qno) + qno);
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
                if (!answered) {
                    markFab.show();
                }
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference answer = database.getReference("Answers").child(qno + "");
        DatabaseReference questn = database.getReference("Questions").child(qno + "");
        DatabaseReference optn = database.getReference("Options").child(qno + "");

        markFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(QuizActivity.this);
                    String email = preferences.getString("EMAIL", "Email");
                    String userID = email.split("@")[0].replace(".", ",");
                    DatabaseReference user = database.getReference("Users").child(userID);
                    int id = options.getCheckedRadioButtonId();
                    option = (RadioButton) findViewById(id);
                    if (option.getText().toString().equals("")) {
                        Toast.makeText(getBaseContext(), R.string.err_server_conn, Toast.LENGTH_SHORT).show();
                    } else {
                        answered = true;
                        a.setClickable(false);
                        b.setClickable(false);
                        c.setClickable(false);
                        d.setClickable(false);
                        option.setEnabled(true);
                        switch (id) {
                            case R.id.A:
                                choice = 1;
                                incrementCounter(answer.child("a"), choice);
                                user.child(qno + "").setValue("a");
                                if (barGraphView != null) {
                                    barGraphView.setAnswer(1);
                                }
                                break;

                            case R.id.B:
                                choice = 2;
                                incrementCounter(answer.child("b"), choice);
                                user.child(qno + "").setValue("b");
                                if (barGraphView != null) {
                                    barGraphView.setAnswer(2);
                                }
                                break;

                            case R.id.C:
                                choice = 3;
                                incrementCounter(answer.child("c"), choice);
                                user.child(qno + "").setValue("c");
                                if (barGraphView != null) {
                                    barGraphView.setAnswer(3);
                                }
                                break;

                            case R.id.D:
                                choice = 4;
                                incrementCounter(answer.child("d"), choice);
                                user.child(qno + "").setValue("d");
                                if (barGraphView != null) {
                                    barGraphView.setAnswer(4);
                                }
                                break;
                        }
                        markFab.hide();
                        if (barGraphView != null) {
                            barGraphView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        questn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                question = dataSnapshot.getValue().toString();
                ques.setText(question);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        optn.addValueEventListener(new ValueEventListener() {
            @Override
            @SuppressLint("SetTextI18n")
            @SuppressWarnings("unchecked")
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> HM = (HashMap<String, String>) dataSnapshot.getValue();
                sA = HM.get("a");
                sB = HM.get("b");
                sC = HM.get("c");
                sD = HM.get("d");
                a.setText(sA + (answered ? " " + ((iA * 100) / total) + "%" : ""));
                b.setText(sB + (answered ? " " + ((iB * 100) / total) + "%" : ""));
                c.setText(sC + (answered ? " " + ((iC * 100) / total) + "%" : ""));
                d.setText(sD + (answered ? " " + ((iD * 100) / total) + "%" : ""));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        answer.addValueEventListener(new ValueEventListener() {
            @Override
            @SuppressLint("SetTextI18n")
            @SuppressWarnings("unchecked")
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Long> HM = (HashMap<String, Long>) dataSnapshot.getValue();
                iA = HM.get("a");
                iB = HM.get("b");
                iC = HM.get("c");
                iD = HM.get("d");
                total = iA + iB + iC + iD;
                if (total == 0) {
                    total = 1;
                }
                short percA = (short) ((iA * 100) / total);
                short percB = (short) ((iB * 100) / total);
                short percC = (short) ((iC * 100) / total);
                short percD = (short) ((iD * 100) / total);
                a.setText(sA + (answered ? " " + percA + "%" : ""));
                b.setText(sB + (answered ? " " + percB + "%" : ""));
                c.setText(sC + (answered ? " " + percC + "%" : ""));
                d.setText(sD + (answered ? " " + percD + "%" : ""));
                if (barGraphView != null) {
                    barGraphView.setPerc(percA, percB, percC, percD);
                }
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
                    Log.d("TAG", "Firebase counter increment failed.");
                } else {
                    ContentValues questionValues = new ContentValues();
                    questionValues.put(COLUMN_QNO, qno);
                    questionValues.put(COLUMN_QUES, question);
                    questionValues.put(COLUMN_ANS1, sA);
                    questionValues.put(COLUMN_ANS2, sB);
                    questionValues.put(COLUMN_ANS3, sC);
                    questionValues.put(COLUMN_ANS4, sD);
                    questionValues.put(COLUMN_ANS1V, iA);
                    questionValues.put(COLUMN_ANS2V, iB);
                    questionValues.put(COLUMN_ANS3V, iC);
                    questionValues.put(COLUMN_ANS4V, iD);
                    questionValues.put(COLUMN_CHOICE, (option));

                    Cursor cursor = getContentResolver().query(
                            QuestionContract.QuestionEntry.buildQuestionNo(Integer.toString(qno)),
                            new String[]{COLUMN_QNO},
                            null,
                            null,
                            null);
                    if (cursor != null && cursor.getCount() == 0) {
                        QuestionDBHelper dbHelper = new QuestionDBHelper(QuizActivity.this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues);

                    } else {
                        getContentResolver().update(
                                QuestionContract.QuestionEntry.buildQuestionNo(qno + ""),
                                questionValues,
                                null,
                                null);
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    Utility.updateWidget(QuizActivity.this);
                    Log.d("TAG", "Firebase counter increment succeeded.");
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (answered) {
            outState.putBoolean("answered", true);
            outState.putInt("choice", choice);
            outState.putLong("iA", iA);
            outState.putLong("iB", iB);
            outState.putLong("iC", iC);
            outState.putLong("iD", iD);
            outState.putString("sA", sA);
            outState.putString("sB", sB);
            outState.putString("sC", sC);
            outState.putString("sD", sD);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("RECREATE", "Bool:" + (savedInstanceState != null));
        if (savedInstanceState != null) {
            answered = savedInstanceState.getBoolean("answered");
            Log.e("RECREATE", "Ans:" + answered);
            if (answered) {
                choice = savedInstanceState.getInt("choice");
                markFab.hide();
                iA = savedInstanceState.getLong("iA");
                iB = savedInstanceState.getLong("iB");
                iC = savedInstanceState.getLong("iC");
                iD = savedInstanceState.getLong("iD");
                sA = savedInstanceState.getString("sA");
                sB = savedInstanceState.getString("sB");
                sC = savedInstanceState.getString("sC");
                sD = savedInstanceState.getString("sD");
                total = iA + iB + iC + iD;
                if (total == 0) {
                    total = 1;
                }
                short percA = (short) ((iA * 100) / total);
                short percB = (short) ((iB * 100) / total);
                short percC = (short) ((iC * 100) / total);
                short percD = (short) ((iD * 100) / total);
                a.setText(sA + " " + percA + getString(R.string.symbol_percentage));
                b.setText(sB + " " + percB + getString(R.string.symbol_percentage));
                c.setText(sC + " " + percC + getString(R.string.symbol_percentage));
                d.setText(sD + " " + percD + getString(R.string.symbol_percentage));
                if (barGraphView != null) {
                    barGraphView.setVisibility(View.VISIBLE);
                    barGraphView.setAnswer(choice);
                    barGraphView.setPerc(percA, percB, percC, percD);
                }
            }
        }
    }
}
