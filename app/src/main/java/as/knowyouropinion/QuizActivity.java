package as.knowyouropinion;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**<p>
 * Created by localhost on 25/1/17.
 * </p>
 */

public class QuizActivity extends AppCompatActivity {
    private FloatingActionButton markFab;
    private RadioButton option;
    private RadioGroup options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
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

        markFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = options.getCheckedRadioButtonId();
                option = (RadioButton) findViewById(id);
                Toast.makeText(getBaseContext(),option.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public void incrementCounter() {
//        firebase.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(final MutableData currentData) {
//                if (currentData.getValue() == null) {
//                    currentData.setValue(1);
//                } else {
//                    currentData.setValue((Long) currentData.getValue() + 1);
//                }
//
//                return Transaction.success(currentData);
//            }
//
//            @Override
//            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
//                if (firebaseError != null) {
//                    Log.d("Firebase counter increment failed.");
//                } else {
//                    Log.d("Firebase counter increment succeeded.");
//                }
//            }
//        });
//    }
}
