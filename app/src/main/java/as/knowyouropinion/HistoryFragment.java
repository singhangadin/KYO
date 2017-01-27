package as.knowyouropinion;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import as.knowyouropinion.data.QuestionContract;
import as.knowyouropinion.data.QuestionContract.QuestionEntry;
import as.knowyouropinion.model.HistoryCursorAdapter;

/**<p>
 * Created by Angad on 23/1/17.
 * </p>
 */

public class HistoryFragment extends Fragment implements
    LoaderManager.LoaderCallbacks<Cursor> {
    private Context context;
    private HistoryCursorAdapter mCursorAdapter;
    private static final int HISTORY_LOADER = 0;

    private String PROJECTION_MATRIX[] = {
            QuestionEntry._ID,
            QuestionEntry.COLUMN_QNO,
            QuestionEntry.COLUMN_QUES,
            QuestionEntry.COLUMN_ANS1V,
            QuestionEntry.COLUMN_ANS2V,
            QuestionEntry.COLUMN_ANS3V,
            QuestionEntry.COLUMN_ANS4V,
            QuestionEntry.COLUMN_CHOICE
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_history,container,false);
        RecyclerView recyclerView = (RecyclerView)view. findViewById(R.id.historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mCursorAdapter = new HistoryCursorAdapter(context, null);
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
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                QuestionContract.QuestionEntry.CONTENT_URI,
                PROJECTION_MATRIX,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
//        if (mPosition != ListView.INVALID_POSITION) {
//            mListView.smoothScrollToPosition(mPosition);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(HISTORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
}
