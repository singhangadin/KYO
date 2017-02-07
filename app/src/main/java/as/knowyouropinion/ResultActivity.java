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

/**<p>
 * Created by Angad on 6/2/17.
 * </p>
 */

public class ResultActivity extends AppCompatActivity {
    private AppCompatRadioButton A, B, C, D;
    private AppCompatTextView Ques;
    private String question;
    private String Sa = "",Sb = "",Sc = "",Sd = "";
    private long Ia,Ib,Ic,Id, total = 0;
    private int qno;
    private BarGraphView barGraphView;
    private InterstitialAd mInterstitialAd;
    private int choice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        qno = getIntent().getExtras().getInt("qno");

        barGraphView = (BarGraphView)findViewById(R.id.barGraph);
        if(barGraphView!=null)
        {   barGraphView.setVisibility(View.VISIBLE);
        }
        Ques = (AppCompatTextView)findViewById(R.id.question);
        A = (AppCompatRadioButton)findViewById(R.id.A);
        B = (AppCompatRadioButton)findViewById(R.id.B);
        C = (AppCompatRadioButton)findViewById(R.id.C);
        D = (AppCompatRadioButton)findViewById(R.id.D);

        FloatingActionButton markFab = (FloatingActionButton) findViewById(R.id.markFab);
        markFab.hide();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.label_qno)+qno);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Cursor data = getContentResolver().query(
            QuestionContract.QuestionEntry.buildQuestionNo(qno+""),
            null,
            null,
            null,
            null);

        if(data!=null) {
            data.moveToFirst();
            String question = data.getString(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_QUES));
            Sa = data.getString(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS1));
            Sb = data.getString(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS2));
            Sc = data.getString(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS3));
            Sd = data.getString(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS4));
            Ia = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS1V));
            Ib = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS2V));
            Ic = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS3V));
            Id = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS4V));
            choice = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_CHOICE));

            Ques.setText(question);
            total = Ia + Ib + Ic + Id;
            if(total == 0)
            {   total = 1;
            }
            A.setText(Sa+" "+((Ia*100)/total)+getString(R.string.symbol_percentage));
            B.setText(Sb+" "+((Ib*100)/total)+getString(R.string.symbol_percentage));
            C.setText(Sc+" "+((Ic*100)/total)+getString(R.string.symbol_percentage));
            D.setText(Sd+" "+((Id*100)/total)+getString(R.string.symbol_percentage));

            switch (choice) {
                case 1: A.performClick();
                        break;

                case 2: B.performClick();
                        break;

                case 3: C.performClick();
                        break;

                case 4: D.performClick();
                        break;
            }
            if(barGraphView!=null) {
                barGraphView.setAnswer(choice);
            }
            A.setClickable(false);
            B.setClickable(false);
            C.setClickable(false);
            D.setClickable(false);
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
        DatabaseReference answer = database.getReference("Answers").child(qno +"");
        DatabaseReference questn = database.getReference("Questions").child(qno +"");
        DatabaseReference optn = database.getReference("Options").child(qno +"");

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
            @Override
            @SuppressLint("SetTextI18n")
            @SuppressWarnings("unchecked")
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> HM = (HashMap<String, String>) dataSnapshot.getValue();
                Sa = HM.get("a");
                Sb = HM.get("b");
                Sc = HM.get("c");
                Sd = HM.get("d");
                A.setText(Sa+" "+((Ia*100)/total)+getString(R.string.symbol_percentage));
                B.setText(Sb+" "+((Ib*100)/total)+getString(R.string.symbol_percentage));
                C.setText(Sc+" "+((Ic*100)/total)+getString(R.string.symbol_percentage));
                D.setText(Sd+" "+((Id*100)/total)+getString(R.string.symbol_percentage));
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
                Ia = HM.get("a");
                Ib = HM.get("b");
                Ic = HM.get("c");
                Id = HM.get("d");
                total = Ia + Ib + Ic + Id;
                if(total == 0)
                {   total = 1;
                }
                short percA = (short) ((Ia*100)/total);
                short percB = (short) ((Ib*100)/total);
                short percC = (short) ((Ic*100)/total);
                short percD = (short) ((Id*100)/total);
                A.setText(Sa+" "+percA+getString(R.string.symbol_percentage));
                B.setText(Sb+" "+percB+getString(R.string.symbol_percentage));
                C.setText(Sc+" "+percC+getString(R.string.symbol_percentage));
                D.setText(Sd+" "+percD+getString(R.string.symbol_percentage));
                if(barGraphView!=null)
                {   barGraphView.setPerc(percA, percB, percC, percD);
                }
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
                questionValues.put(COLUMN_CHOICE, (choice));
                getContentResolver().update(
                        QuestionContract.QuestionEntry.buildQuestionNo(qno+""),
                        questionValues,
                        null,
                        null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mInterstitialAd.isLoaded()&& !Utility.adShown) {
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
