package as.knowyouropinion;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import as.knowyouropinion.data.QuestionContract;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        qno = getIntent().getExtras().getInt("qno");

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
            int optionAV = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS1V));
            int optionBV = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS2V));
            int optionCV = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS3V));
            int optionDV = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS4V));
            int choice = data.getInt(data.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_CHOICE));

            Log.e("DATA",Sa+":"+Sb+":"+Sc+":"+Sd);

            Ques.setText(question);

            total = optionAV + optionBV + optionCV + optionDV;
            if(total == 0)
            {   total = 1;
            }
            A.setText(Sa+" "+((optionAV*100)/total)+"%");
            B.setText(Sb+" "+((optionBV*100)/total)+"%");
            C.setText(Sc+" "+((optionCV*100)/total)+"%");
            D.setText(Sd+" "+((optionDV*100)/total)+"%");

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
            A.setClickable(false);
            B.setClickable(false);
            C.setClickable(false);
            D.setClickable(false);
            data.close();
        }
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> HM = (HashMap<String, String>) dataSnapshot.getValue();
                Sa = HM.get("a");
                Sb = HM.get("b");
                Sc = HM.get("c");
                Sd = HM.get("d");
                A.setText(Sa+" "+((Ia*100)/total)+"%");
                B.setText(Sb+" "+((Ib*100)/total)+"%");
                C.setText(Sc+" "+((Ic*100)/total)+"%");
                D.setText(Sd+" "+((Id*100)/total)+"%");
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
                A.setText(Sa+" "+((Ia*100)/total)+"%");
                B.setText(Sb+" "+((Ib*100)/total)+"%");
                C.setText(Sc+" "+((Ic*100)/total)+"%");
                D.setText(Sd+" "+((Id*100)/total)+"%");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
