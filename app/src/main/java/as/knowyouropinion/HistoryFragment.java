package as.knowyouropinion;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private HistoryCursorAdapter mCursorAdapter;
    private Cursor questionCursor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_history,container,false);
        RecyclerView recyclerView = (RecyclerView)view. findViewById(R.id.historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        questionCursor = context.getContentResolver().query(
                QuestionContract.QuestionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        mCursorAdapter = new HistoryCursorAdapter(context, questionCursor);
        recyclerView.setAdapter(mCursorAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        questionCursor.close();
    }
}
