package as.knowyouropinion;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import as.knowyouropinion.data.QuestionContract;

/**<p>
 * Created by Angad on 23/1/17.
 * </p>
 */

public class HistoryFragment extends Fragment {
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_history,container,false);
        Cursor questionCursor = context.getContentResolver().query(
                QuestionContract.QuestionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        while(questionCursor.moveToNext())
        {   int quesNo = questionCursor.getInt(1);
            String question = questionCursor.getString(2);
            Log.e("TAG", quesNo+":"+question);
        }
        questionCursor.close();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
