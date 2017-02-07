package as.knowyouropinion;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.angads25.graphs.BarGraphView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import as.knowyouropinion.data.QuestionContract;
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
 * Created by Angad on 6/2/17.
 * </p>
 */

public class ResultActivity extends AppCompatActivity {
    private AppCompatRadioButton a, b, c, d;
    private AppCompatTextView ques;
    private String question;
    private String sA = "", sB = "", sC = "", sD = "";
    private long iA, iB, iC, iD, total = 0;
    private int qno;
    private BarGraphView barGraphView;
    private InterstitialAd mInterstitialAd;
    private int choice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        qno = getIntent().getExtras().getInt("qno");

        barGraphView = (BarGraphView) findViewById(R.id.barGraph);
        if (barGraphView != null) {
            barGraphView.setVisibility(View.VISIBLE);
        }
        ques = (AppCompatTextView) findViewById(R.id.question);
        a = (AppCompatRadioButton) findViewById(R.id.A);
        b = (AppCompatRadioButton) findViewById(R.id.B);
        c = (AppCompatRadioButton) findViewById(R.id.C);
        d = (AppCompatRadioButton) findViewById(R.id.D);

        FloatingActionButton markFab = (FloatingActionButton) findViewById(R.id.markFab);
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

        Cursor data = getContentResolver().query(
                QuestionContract.QuestionEntry.buildQuestionNo(qno + ""),
                null,
                null,
                null,
                null);

        if (data != null) {
            data.moveToFirst();
            String question = data.getString(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_QUES));
            sA = data.getString(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS1));
            sB = data.getString(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS2));
            sC = data.getString(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS3));
            sD = data.getString(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS4));
            iA = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS1V));
            iB = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS2V));
            iC = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS3V));
            iD = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS4V));
            choice = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_CHOICE));

            ques.setText(question);
            total = iA + iB + iC + iD;
            if (total == 0) {
                total = 1;
            }
            a.setText(sA + " " + ((iA * 100) / total) + getString(R.string.symbol_percentage));
            b.setText(sB + " " + ((iB * 100) / total) + getString(R.string.symbol_percentage));
            c.setText(sC + " " + ((iC * 100) / total) + getString(R.string.symbol_percentage));
            d.setText(sD + " " + ((iD * 100) / total) + getString(R.string.symbol_percentage));

            switch (choice) {
                case 1:
                    a.performClick();
                    break;

                case 2:
                    b.performClick();
                    break;

                case 3:
                    c.performClick();
                    break;

                case 4:
                    d.performClick();
                    break;
            }
            if (barGraphView != null) {
                barGraphView.setAnswer(choice);
            }
            a.setClickable(false);
            b.setClickable(false);
            c.setClickable(false);
            d.setClickable(false);
            data.close();
        }


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("TEST_DEVICE_ID")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference answer = database.getReference("Answers").child(qno + "");
        DatabaseReference questn = database.getReference("Questions").child(qno + "");
        DatabaseReference optn = database.getReference("Options").child(qno + "");

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
                a.setText(sA + " " + ((iA * 100) / total) + getString(R.string.symbol_percentage));
                b.setText(sB + " " + ((iB * 100) / total) + getString(R.string.symbol_percentage));
                c.setText(sC + " " + ((iC * 100) / total) + getString(R.string.symbol_percentage));
                d.setText(sD + " " + ((iD * 100) / total) + getString(R.string.symbol_percentage));
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
                a.setText(sA + " " + percA + getString(R.string.symbol_percentage));
                b.setText(sB + " " + percB + getString(R.string.symbol_percentage));
                c.setText(sC + " " + percC + getString(R.string.symbol_percentage));
                d.setText(sD + " " + percD + getString(R.string.symbol_percentage));
                if (barGraphView != null) {
                    barGraphView.setPerc(percA, percB, percC, percD);
                }
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
                questionValues.put(COLUMN_CHOICE, (choice));
                getContentResolver().update(
                        QuestionContract.QuestionEntry.buildQuestionNo(qno + ""),
                        questionValues,
                        null,
                        null);
                Utility.updateWidget(ResultActivity.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mInterstitialAd.isLoaded() && !Utility.adShown) {
            mInterstitialAd.show();
            Utility.adShown = true;
        }
    }

    @Override
    protected void onDestroy() {
        mInterstitialAd.setAdListener(null);
        super.onDestroy();
    }
}
